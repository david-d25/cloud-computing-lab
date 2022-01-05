package space.davids_digital.cloud_computing_lab.backend.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import space.davids_digital.cloud_computing_lab.backend.model.EditAgentRequestModel
import space.davids_digital.cloud_computing_lab.backend.util.GlobalConstraints.MODEL_UPDATE_DELAY_MS
import javax.transaction.Transactional

@Service
class ModelUpdateService(
    @Qualifier("local")
    private val modelControlService: ModelControlService,
    private val agentService: AgentService,
    private val transactionManager: PlatformTransactionManager
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Scheduled(fixedDelay = MODEL_UPDATE_DELAY_MS)
    fun updateModels() {
        val updatableAgents = agentService.getAgentNeedingModelUpdateIds()
        for (agentId in updatableAgents) {
            val entriesSize = agentService.getAgentDataEntriesSize(agentId)
            val lastApplied = agentService.getAgentLastAppliedDataEntryId(agentId)
            val start = (lastApplied ?: -1) + 1
            val end = entriesSize - 1

            logger.info(
                "Updating chain model of agent id {}: last applied data id is {}, will apply up to {}",
                agentId, lastApplied, end
            )

            for (dataId in start .. end) {
                val status = transactionManager.getTransaction(null)
                logger.info("Updating chain model of agent id {}: applying data id {}/{}", agentId, dataId, end)
                modelControlService.applyDataset(agentId, dataId)
                agentService.editAgent(
                    EditAgentRequestModel(
                        id = agentId,
                        lastAppliedDataEntry = dataId
                    )
                )
                transactionManager.commit(status)
            }

            logger.info("Chain model of agent id {} updated (last applied data id is {})", agentId, end)
        }
    }
}