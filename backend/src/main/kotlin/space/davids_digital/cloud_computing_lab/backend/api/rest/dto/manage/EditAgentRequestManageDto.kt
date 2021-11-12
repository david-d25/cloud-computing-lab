package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage

import com.fasterxml.jackson.annotation.JsonProperty
import space.davids_digital.cloud_computing_lab.backend.model.AgentStatusModel
import javax.validation.constraints.NotNull

data class EditAgentRequestManageDto(
    @JsonProperty
    @NotNull
    val id: Int? = null,

    @JsonProperty("type")
    val type: String? = null,

    @JsonProperty("name")
    val name: String? = null,

    @JsonProperty("visible")
    val visible: Boolean? = null,

    @JsonProperty("update_period_seconds")
    val updatePeriodSeconds: Long? = null,

    @JsonProperty("sensitive")
    val sensitive: Boolean? = null,

    @JsonProperty("parameters")
    val parameters: Map<String, String> = mapOf(),
)
