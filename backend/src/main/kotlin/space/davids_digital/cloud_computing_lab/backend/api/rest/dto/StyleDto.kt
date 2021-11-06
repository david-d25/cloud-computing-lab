package space.davids_digital.cloud_computing_lab.backend.api.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class StyleDto(
    @JsonProperty("id")
    val id: Int? = null,

    @JsonProperty("name")
    val name: String? = null,

    @JsonProperty("sensitive")
    val sensitive: Boolean? = null
)
