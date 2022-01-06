package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage

import space.davids_digital.cloud_computing_lab.backend.model.AgentStatusModel
import java.time.Instant

data class AgentResponseManageDto(
    var id: Int? = null,
    var type: String? = null,
    var name: String? = null,
    var status: AgentStatusModel? = null,
    var visible: Boolean? = null,
    var updatePeriodSeconds: Long? = null,
    var lastUpdateTimestamp: Instant? = null,
    var lastAppliedDataEntryId: Long? = null,
    var lastDataEntryId: Long? = null,
    var sensitive: Boolean? = null,
    var parameters: Map<String, String> = mapOf(),
    var memory: Map<String, String> = mapOf()
)
