package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import space.davids_digital.cloud_computing_lab.backend.orm.entity.MarkChainTransitionEntity
import space.davids_digital.cloud_computing_lab.backend.orm.repository.AgentRepository
import space.davids_digital.cloud_computing_lab.backend.orm.repository.MarkChainTransitionRepository
import space.davids_digital.cloud_computing_lab.backend.util.GlobalConstraints.DEFAULT_MAX_WORDS_PER_TRANSITION
import space.davids_digital.cloud_computing_lab.tokenizer.Tokenizer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors

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

        val transitionEntities = ConcurrentHashMap(
            markChainTransitionRepository.findAllByAgentIds(listOf(agentId))
                .associateBy { Pair(it.beginning, it.continuation) }
        )
        val entryIdCount = AtomicInteger((markChainTransitionRepository.getMaxEntryIdByAgentId(agentId) ?: - 1) + 1)

        Tokenizer.generateTransitions(data, DEFAULT_MAX_WORDS_PER_TRANSITION).parallelStream()
            .filter {
                (it.beginning == null || it.beginning!!.length < 255) && (it.continuation == null || it.continuation!!.length < 255) }
            .forEach { transition ->
                transitionEntities.computeIfAbsent(
                    Pair(transition.beginning, transition.continuation)
                ) {
                    MarkChainTransitionEntity(
                        agentId = agentId,
                        entryId = entryIdCount.getAndIncrement(),
                        beginning = transition.beginning,
                        continuation = transition.continuation,
                        transitionCount = 0
                    )
                }.transitionCount += transition.count
            }

        markChainTransitionRepository.saveAll(transitionEntities.values)
    }
}