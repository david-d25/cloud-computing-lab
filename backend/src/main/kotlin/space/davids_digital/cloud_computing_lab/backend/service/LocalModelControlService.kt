package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.davids_digital.cloud_computing_lab.backend.orm.entity.MarkChainTransitionEntity
import space.davids_digital.cloud_computing_lab.backend.orm.entity.id.MarkChainTransitionId
import space.davids_digital.cloud_computing_lab.backend.orm.repository.AgentRepository
import space.davids_digital.cloud_computing_lab.backend.orm.repository.MarkChainTransitionRepository
import space.davids_digital.cloud_computing_lab.backend.util.GlobalConstraints.DEFAULT_MAX_WORDS_PER_TRANSITION
import space.davids_digital.cloud_computing_lab.tokenizer.Tokenizer

@Service
@Qualifier("local")
class LocalModelControlService(
    private val markChainTransitionRepository: MarkChainTransitionRepository,
    private val agentRepository: AgentRepository
): ModelControlService {
    @Transactional
    override fun applyDataset(agentId: Int, dataId: Long) {
        val agent = agentRepository.findById(agentId).orElseThrow { ServiceException("Agent id $agentId not found") }
        val data = agent.data[dataId] ?: throw ServiceException("Data id $dataId not found in agent id $agentId")

        var entryIdCount = (markChainTransitionRepository.getMaxEntryIdByAgentId(agentId) ?: - 1) + 1
        markChainTransitionRepository.saveAll(
            Tokenizer.generateTransitions(data, DEFAULT_MAX_WORDS_PER_TRANSITION).map { transition ->
                markChainTransitionRepository.findByBeginningAndContinuation(
                    transition.beginning, transition.continuation
                ).orElse(
                    MarkChainTransitionEntity(
                        agentId = agentId,
                        entryId = entryIdCount,
                        beginning = transition.beginning,
                        continuation = transition.continuation,
                        transitionCount = 0
                    )
                ).also { entity ->
                    entity.transitionCount += transition.count
                    entryIdCount++
                }
            }
        )
    }
}