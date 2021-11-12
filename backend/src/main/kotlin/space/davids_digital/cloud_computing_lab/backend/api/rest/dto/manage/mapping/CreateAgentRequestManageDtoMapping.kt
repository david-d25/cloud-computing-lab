package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.mapping

import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.CreateAgentRequestManageDto
import space.davids_digital.cloud_computing_lab.backend.model.AgentModel
import space.davids_digital.cloud_computing_lab.backend.model.AgentStatusModel
import space.davids_digital.cloud_computing_lab.backend.model.CreateAgentRequestModel

fun CreateAgentRequestManageDto.toModel() = CreateAgentRequestModel(
    name = name!!,
    type = type!!,
    sensitive = sensitive,
    visible = visible,
    updatePeriodSeconds = updatePeriodSeconds,
    parameters = parameters
)