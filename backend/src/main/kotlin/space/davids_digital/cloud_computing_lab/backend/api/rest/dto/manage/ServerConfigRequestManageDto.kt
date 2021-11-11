package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

data class ServerConfigRequestManageDto(
    @JsonProperty("config")
    @NotNull
    val config: Map<String, String>
)
