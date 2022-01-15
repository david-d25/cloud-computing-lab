package space.davids_digital.cloud_computing_lab.backend.service

import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import space.davids_digital.cloud_computing_lab.backend.orm.repository.AgentRepository
import space.davids_digital.cloud_computing_lab.backend.orm.repository.MarkChainTransitionRepository
import space.davids_digital.cloud_computing_lab.tokenizer.Tokenizer
import kotlin.math.floor

@Service
@Qualifier("local")
class LocalModelControlService(
    private val markChainTransitionRepository: MarkChainTransitionRepository,
    private val transactionManager: PlatformTransactionManager,
    private val agentRepository: AgentRepository
): ModelControlService {

    override fun applyDataset(agentId: Int, dataId: Long) {
        val data = agentRepository.getDataByKey(agentId, dataId) ?: throw ServiceException("Data id $dataId not found in agent id $agentId")

        val maxWordsPerTransition = floor(markChainTransitionRepository.getRecommendedMaxWordsPerTransition(agentId)).toInt()
        val transitions = Tokenizer.generateTransitions(data, 1, maxWordsPerTransition)

        val status = transactionManager.getTransaction(null)
        try {
            transitions.stream()
                .filter { it.beginning.length < 255 && it.continuation.length < 255 }
                .forEach {
                    markChainTransitionRepository.applyNewTransition(agentId, it.beginning, it.continuation, it.count.toLong())
                }

            agentRepository.findById(agentId).ifPresent {
                it.lastAppliedDataEntry = dataId
                agentRepository.save(it)
            }

            transactionManager.commit(status)
        } catch (e: Exception) {
            transactionManager.rollback(status)
            throw RuntimeException(e)
        }
    }
}