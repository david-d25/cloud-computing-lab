package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.mapping

import space.davids_digital.cloud_computing_lab.agent.AgentExecutorParameterType
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.AgentTypeParameterResponseDto
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.AgentTypeParameterTypeResponseDto
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.AgentTypeResponseDto
import space.davids_digital.cloud_computing_lab.backend.model.AgentExecutorMetaInfo

fun AgentExecutorMetaInfo.toDto() = AgentTypeResponseDto(
    type = this.type,
    title = this.title,
    parameters = this.parameters.map {
        AgentTypeParameterResponseDto(
            name = it.name,
            title = it.title,
            type = when(it.type) {
                AgentExecutorParameterType.NUMBER -> AgentTypeParameterTypeResponseDto.NUMBER
                AgentExecutorParameterType.STRING -> AgentTypeParameterTypeResponseDto.STRING
            },
            required = it.required
        )
    }
)