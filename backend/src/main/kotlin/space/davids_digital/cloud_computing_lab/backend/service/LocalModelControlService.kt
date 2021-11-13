package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.davids_digital.cloud_computing_lab.backend.orm.entity.MarkChainTransitionEntity
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
    override fun applyDataset(agentId: Int, dataId: Int) {
        val agent = agentRepository.findById(agentId).orElseThrow { ServiceException("Agent id $agentId not found") }
        val data = agent.data[dataId.toLong()] ?: throw ServiceException("Data id $dataId not found in agent id $agentId")

        Tokenizer.generateTransitions(data, DEFAULT_MAX_WORDS_PER_TRANSITION).forEach {
            val transitionEntity = markChainTransitionRepository.findByBeginningAndContinuation(
                it.beginning, it.continuation
            ).orElse(
                MarkChainTransitionEntity(
                    beginning = it.beginning,
                    continuation = it.continuation,
                    transitionCount = 0
                )
            )
            transitionEntity.transitionCount += it.count
            markChainTransitionRepository.save(transitionEntity)
        }
    }
}