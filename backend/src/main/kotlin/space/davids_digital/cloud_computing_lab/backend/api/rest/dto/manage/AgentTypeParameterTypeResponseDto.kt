package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage

import com.fasterxml.jackson.annotation.JsonProperty

enum class AgentTypeParameterTypeResponseDto {
    @JsonProperty("string")
    STRING,

    @JsonProperty("number")
    NUMBER
}