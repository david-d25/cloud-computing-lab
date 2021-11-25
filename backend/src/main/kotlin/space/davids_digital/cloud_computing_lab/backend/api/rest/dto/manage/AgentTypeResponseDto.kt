package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage

data class AgentTypeResponseDto(
    val type: String,
    val title: String,
    val parameters: List<AgentTypeParameterResponseDto>
)
