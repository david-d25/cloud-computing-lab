package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage

data class AgentTypeParameterResponseDto(
    val name: String,
    val title: String,
    val type: AgentTypeParameterTypeResponseDto,
    val required: Boolean
)
