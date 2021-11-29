package space.davids_digital.cloud_computing_lab.backend.model

data class MarkChainTransitionModel(
    var agentId: Int,
    var entryId: Int,
    var beginning: String?,
    var continuation: String?,
    var transitionCount: Long
)
