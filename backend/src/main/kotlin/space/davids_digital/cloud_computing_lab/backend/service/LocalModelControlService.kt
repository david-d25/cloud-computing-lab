package space.davids_digital.cloud_computing_lab.backend.service

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

        val transitions = Tokenizer.generateTransitions(data, floor(markChainTransitionRepository.getRecommendedMaxWordsPerTransition(agentId)).toInt())

        val status = transactionManager.getTransaction(null)
        try {
            transitions.stream()
                .filter {
                    (it.beginning == null || it.beginning!!.length < 255) && (it.continuation == null || it.continuation!!.length < 255) }
                .forEach {
                    if (markChainTransitionRepository.existsByAgentIdAndBeginningAndContinuation(agentId, it.beginning, it.continuation))
                        markChainTransitionRepository.updateExistingTransition(agentId, it.beginning, it.continuation, it.count.toLong())
                    else
                        markChainTransitionRepository.putNewTransition(agentId, it.beginning, it.continuation, it.count.toLong())
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