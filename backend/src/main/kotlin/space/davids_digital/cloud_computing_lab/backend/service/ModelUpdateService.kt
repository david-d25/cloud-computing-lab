package space.davids_digital.cloud_computing_lab.backend.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import space.davids_digital.cloud_computing_lab.backend.util.GlobalConstraints.MODEL_UPDATE_DELAY_MS
import java.util.concurrent.Callable
import java.util.concurrent.Executors

@Service
class ModelUpdateService(
    private val modelControlService: ModelControlService,
    private val agentService: AgentService
) {
    companion object {
        private const val MAX_DATA_ENTRIES_BY_EXECUTION = 100
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val threadPool = Executors.newCachedThreadPool()

    @Scheduled(fixedDelay = MODEL_UPDATE_DELAY_MS)
    fun updateModels() {
        val updatableAgents = agentService.getAgentNeedingModelUpdateIds()
        threadPool.invokeAll(updatableAgents.map { agentId ->
            Callable {
                val lastApplied = agentService.getAgentLastAppliedDataEntryId(agentId)
                val start = (lastApplied ?: -1) + 1
                val end = agentService.getAgentMaxDataEntryId(agentId)?.coerceAtMost(start + MAX_DATA_ENTRIES_BY_EXECUTION - 1)

                if (end == null || lastApplied == end)
                    return@Callable

                logger.info(
                    "Updating chain model of agent id {}: last applied data id is {}, will apply up to {}",
                    agentId, lastApplied, end
                )

                for (dataId in start..end) {
                    logger.info("Updating chain model of agent id {}: applying data id {}/{}", agentId, dataId, end)
                    modelControlService.applyDataset(agentId, dataId)
                }

                logger.info("Chain model of agent id {} updated (last applied data id is {})", agentId, end)
            }
        })
    }
}