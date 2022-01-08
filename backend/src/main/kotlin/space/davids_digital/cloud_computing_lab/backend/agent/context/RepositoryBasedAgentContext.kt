package space.davids_digital.cloud_computing_lab.backend.agent.context

import org.springframework.transaction.annotation.Transactional
import space.davids_digital.cloud_computing_lab.agent.AgentStatus
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import space.davids_digital.cloud_computing_lab.backend.orm.repository.AgentRepository
import space.davids_digital.cloud_computing_lab.backend.service.ServerConfigService

open class RepositoryBasedAgentContext(
    private val agentId: Int,
    private val repository: AgentRepository,
    private val serverConfigService: ServerConfigService
): AgentContext() {
    @Transactional
    override fun getMemory() = repository.findById(agentId).get().memory.toMap()

    @Transactional
    override fun getParameters() = repository.findById(agentId).get().parameters.toMap()

    @Transactional
    override fun getParameter(key: String): String? {
        val result = repository.getParameterByKey(agentId, key) ?: return null
        if (result.startsWith("$"))
            return serverConfigService.getConfig(result.removePrefix("$"))
        return result
    }

    @Transactional
    override fun getMemoryEntry(key: String) = repository.getMemoryByKey(agentId, key)

    @Transactional
    override fun setMemoryEntry(key: String, value: String) = repository.setMemoryByKey(agentId, key, value)

    @Transactional
    override fun getStatus() = when(repository.findById(agentId).get().status!!) {
        AgentStatusEntityEnum.UNINITIALIZED -> AgentStatus.UNINITIALIZED
        AgentStatusEntityEnum.RUNNING -> AgentStatus.RUNNING
        AgentStatusEntityEnum.READY -> AgentStatus.SLEEPING
        AgentStatusEntityEnum.ERROR -> AgentStatus.ERROR
    }

    @Transactional
    override fun setStatus(status: AgentStatus) {
        repository.findById(agentId).get().status = when(status) {
            AgentStatus.UNINITIALIZED -> AgentStatusEntityEnum.UNINITIALIZED
            AgentStatus.RUNNING -> AgentStatusEntityEnum.RUNNING
            AgentStatus.SLEEPING -> AgentStatusEntityEnum.READY
            AgentStatus.ERROR -> AgentStatusEntityEnum.ERROR
        }
    }

    @Transactional
    override fun submitData(data: String) {
        repository.addData(agentId, data)
    }

    @Transactional
    override fun clearData() {
        repository.clearData(agentId)
        repository.clearTransitions(agentId)
        repository.resetLastAppliedEntry(agentId)
    }
}