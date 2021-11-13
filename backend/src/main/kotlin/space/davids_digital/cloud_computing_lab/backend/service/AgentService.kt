package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import space.davids_digital.cloud_computing_lab.backend.model.CreateAgentRequestModel
import space.davids_digital.cloud_computing_lab.backend.model.EditAgentRequestModel
import space.davids_digital.cloud_computing_lab.backend.orm.entity.AgentEntity
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import space.davids_digital.cloud_computing_lab.backend.orm.entity.mapping.toModel
import space.davids_digital.cloud_computing_lab.backend.orm.repository.AgentRepository
import javax.transaction.Transactional

@Service
class AgentService @Autowired constructor(
    private val agentRepository: AgentRepository,
    private val agentExecutionService: AgentExecutionService
) {
    fun getAgents() = agentRepository.findAll().map { it.toModel() }

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
        agentRepository.delete(entity)
    }
}