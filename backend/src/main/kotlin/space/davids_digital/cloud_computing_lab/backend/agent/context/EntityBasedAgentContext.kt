package space.davids_digital.cloud_computing_lab.backend.agent.context

import space.davids_digital.cloud_computing_lab.agent.AgentStatus
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext
import space.davids_digital.cloud_computing_lab.backend.orm.entity.AgentEntity
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum

class EntityBasedAgentContext(
    val entity: AgentEntity
): AgentContext() {
    override fun getMemory() = entity.memory.toMap()

    override fun getParameters() = entity.parameters.toMap()

    override fun getParameter(key: String) = entity.parameters[key]

    override fun getMemoryEntry(key: String) = entity.memory[key]

    override fun setMemoryEntry(key: String, value: String) {
        entity.memory[key] = value
    }

    override fun getStatus() = when(entity.status!!) {
        AgentStatusEntityEnum.UNINITIALIZED -> AgentStatus.UNINITIALIZED
        AgentStatusEntityEnum.RUNNING -> AgentStatus.RUNNING
        AgentStatusEntityEnum.READY -> AgentStatus.SLEEPING
        AgentStatusEntityEnum.ERROR -> AgentStatus.ERROR
    }

    override fun setStatus(status: AgentStatus) {
        entity.status = when(status) {
            AgentStatus.UNINITIALIZED -> AgentStatusEntityEnum.UNINITIALIZED
            AgentStatus.RUNNING -> AgentStatusEntityEnum.RUNNING
            AgentStatus.SLEEPING -> AgentStatusEntityEnum.READY
            AgentStatus.ERROR -> AgentStatusEntityEnum.ERROR
        }
    }

    override fun submitData(data: String) {
        entity.data[entity.data.keys.maxOf { it } + 1] = data
    }

    override fun clearData() {
        entity.data.clear()
    }
}