package space.davids_digital.cloud_computing_lab.backend.service

interface ModelControlService {
    fun applyDataset(agentId: Int, dataId: Long)
}