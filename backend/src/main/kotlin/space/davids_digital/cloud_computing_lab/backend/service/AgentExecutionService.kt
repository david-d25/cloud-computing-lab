package space.davids_digital.cloud_computing_lab.backend.service

interface AgentExecutionService {
    fun enqueueExecution(id: Int)
}