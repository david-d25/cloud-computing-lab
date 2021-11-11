package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage

import com.fasterxml.jackson.annotation.JsonProperty
import space.davids_digital.cloud_computing_lab.backend.model.AgentStatusModel
import java.time.Instant

data class AgentResponseManageDto(
    @JsonProperty("id")
    var id: Int? = null,

    @JsonProperty("type")
    var type: String? = null,

    @JsonProperty("name")
    var name: String? = null,

    @JsonProperty("status")
    var status: AgentStatusModel? = null,

    @JsonProperty("visible")
    var visible: Boolean? = null,

    @JsonProperty("update_period_seconds")
    var updatePeriodSeconds: Long? = null,

    @JsonProperty("last_update_timestamp")
    var lastUpdateTimestamp: Instant? = null,

    @JsonProperty("sensitive")
    var sensitive: Boolean? = null,

    @JsonProperty("parameters")
    var parameters: Map<String, String> = mapOf(),

    @JsonProperty("memory")
    var memory: Map<String, String> = mapOf()
)
