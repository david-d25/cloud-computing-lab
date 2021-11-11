package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

data class CreateAgentRequestManageDto(
    @NotNull
    @JsonProperty("name")
    val name: String? = null,

    @NotNull
    @JsonProperty("type")
    val type: String? = null,

    @JsonProperty("visible")
    val visible: Boolean = false,

    @JsonProperty("update_period_seconds")
    val updatePeriodSeconds: Long? = null,

    @JsonProperty("sensitive")
    val sensitive: Boolean = false,

    @JsonProperty("parameters")
    val parameters: Map<String, String> = mapOf()
)
