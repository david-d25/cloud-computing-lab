package space.davids_digital.cloud_computing_lab.backend.api.rest.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GenerateResultDto (
    @JsonProperty("beginning")
    val beginning: String? = null,

    @JsonProperty("generated")
    val generated: String? = null
)