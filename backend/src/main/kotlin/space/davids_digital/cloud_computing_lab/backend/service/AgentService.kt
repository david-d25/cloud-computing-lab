package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import space.davids_digital.cloud_computing_lab.agent.AgentConstants
import space.davids_digital.cloud_computing_lab.backend.model.AgentStatusModel
import space.davids_digital.cloud_computing_lab.backend.model.CreateAgentRequestModel
import space.davids_digital.cloud_computing_lab.backend.model.EditAgentRequestModel
import space.davids_digital.cloud_computing_lab.backend.orm.entity.AgentEntity
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import space.davids_digital.cloud_computing_lab.backend.orm.entity.mapping.toModel
import space.davids_digital.cloud_computing_lab.backend.orm.repository.AgentRepository
import space.davids_digital.cloud_computing_lab.backend.orm.repository.MarkChainTransitionRepository
import space.davids_digital.cloud_computing_lab.backend.util.GlobalConstraints.AGENT_SCHEDULED_EXECUTION_CHECK_DELAY_MS
import java.time.Instant
import javax.annotation.PostConstruct
import javax.transaction.Transactional

@Service
class AgentService @Autowired constructor(
    private val agentRepository: AgentRepository,
    private val agentExecutionService: AgentExecutionService,
    private val markChainTransitionRepository: MarkChainTransitionRepository
) {
    fun getAgents() = agentRepository.findAll().map { it.toModel(agentRepository) }

    fun getAgentNeedingModelUpdateIds() = agentRepository.getAgentNeedingModelUpdateIds()
    fun getAgentMaxDataEntryId(id: Int) = agentRepository.getAgentMaxDataEntryId(id)
    fun getAgentLastAppliedDataEntryId(id: Int) = agentRepository.getAgentLastAppliedDataEntryId(id)

    @PostConstruct
    fun manuallyUpdateAgentsStatus() {
        getAgents().forEach {
            if (it.status == AgentStatusModel.RUNNING) {
                agentRepository.findById(it.id).get().let { entity ->
                    entity.status = AgentStatusEntityEnum.ERROR
                    entity.memory[AgentConstants.ERROR_MESSAGE] = "Сервер выключился до того, как агент завершил работу"
                    agentRepository.save(entity)
                }
            }
        }
    }

    @Scheduled(fixedDelay = AGENT_SCHEDULED_EXECUTION_CHECK_DELAY_MS)
    fun checkAgentsRunSchedule() {
        getAgents().forEach {
            if ((it.status == AgentStatusModel.READY || it.status == AgentStatusModel.UNINITIALIZED) && it.updatePeriodSeconds != null && it.updatePeriodSeconds > 0) {
                if (it.lastUpdateTimestamp == null || (Instant.now().toEpochMilli() - it.lastUpdateTimestamp.toEpochMilli())/1000 > it.updatePeriodSeconds) {
                    runAgent(it.id)
                }
            }
        }
    }

    fun runAgent(id: Int) {
        agentExecutionService.enqueueExecution(id)
    }

    fun editAgent(request: EditAgentRequestModel) {
        val entity = agentRepository.findById(request.id).orElseThrow { ServiceException("Agent id '${request.id}' not found") }
        request.name?.let { entity.name = it }
        request.type?.let { entity.type = it }
        request.sensitive?.let { entity.sensitive = it }
        request.parameters?.let { entity.parameters = it }
        request.updatePeriodSeconds?.let { entity.updatePeriodSeconds = it }
        request.visible?.let { entity.visible = it }
        request.lastAppliedDataEntry?.let { entity.lastAppliedDataEntry = it }
        agentRepository.save(entity)
    }

    fun createAgent(request: CreateAgentRequestModel) {
        agentRepository.save(
            AgentEntity(
                name = request.name,
                type = request.type,
                sensitive = request.sensitive,
                visible = request.visible,
                updatePeriodSeconds = request.updatePeriodSeconds,
                parameters = request.parameters,
                status = AgentStatusEntityEnum.UNINITIALIZED
            )
        )
    }

    @Transactional
    fun deleteAgent(id: Int) {
        val entity = agentRepository.findById(id).orElseThrow { ServiceException("Agent id $id not found") }
        if (entity.status == AgentStatusEntityEnum.RUNNING)
            throw ServiceException("Running agent can't be deleted")
        markChainTransitionRepository.removeByAgentId(id)
        agentRepository.delete(entity)
    }
}