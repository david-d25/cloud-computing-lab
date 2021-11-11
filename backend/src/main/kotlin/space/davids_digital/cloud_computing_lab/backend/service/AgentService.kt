package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import space.davids_digital.cloud_computing_lab.backend.model.AgentModel
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import space.davids_digital.cloud_computing_lab.backend.orm.entity.mapping.toEntity
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

    fun editAgent(agent: AgentModel) { // todo this should use separate edit request model
        agentRepository.save(agent.toEntity())
    }

    fun createAgent(agent: AgentModel) { // todo this should use separate create request model
        agentRepository.save(
            agent.toEntity().copy(
                id = null,
                status = AgentStatusEntityEnum.UNINITIALIZED,
                lastUpdateTimestamp = null,
                memory = mutableMapOf()
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