package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage

import com.fasterxml.jackson.annotation.JsonProperty

data class ServerConfigResponseManageDto(
    @get:JsonProperty("config")
    var config: Map<String, String>
)