package space.davids_digital.cloud_computing_lab.agent.context

import space.davids_digital.cloud_computing_lab.agent.AgentStatus

abstract class AgentContext {
    abstract fun getParameter(key: String): String?
    abstract fun getParameters(): Map<String, String>
    abstract fun getMemory(): Map<String, String>
    abstract fun getMemoryEntry(key: String): String?
    abstract fun setMemoryEntry(key: String, value: String)
    abstract fun getStatus(): AgentStatus
    abstract fun setStatus(status: AgentStatus)

    abstract fun submitData(data: String)
    abstract fun clearData()
}