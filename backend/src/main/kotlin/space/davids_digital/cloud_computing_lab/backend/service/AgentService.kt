package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.stereotype.Service
import space.davids_digital.cloud_computing_lab.backend.orm.entity.mapping.toModel
import space.davids_digital.cloud_computing_lab.backend.orm.repository.AgentRepository

@Service
class AgentService(
    private val agentRepository: AgentRepository
) {
    fun getAgents() = agentRepository.findAll().map { it.toModel() }

    // fun getAgentAvailableTypes(): Array<String>
    // fun editAgent(agent: Agent)
    // fun createAgent(agent: Agent)
    // fun deleteAgent(id: Int)
    // fun runAgent(id: Int)
}