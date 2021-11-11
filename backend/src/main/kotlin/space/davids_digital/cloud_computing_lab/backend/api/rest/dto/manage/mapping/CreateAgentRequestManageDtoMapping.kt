package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.mapping

import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.CreateAgentRequestManageDto
import space.davids_digital.cloud_computing_lab.backend.model.AgentModel
import space.davids_digital.cloud_computing_lab.backend.model.AgentStatusModel

fun CreateAgentRequestManageDto.toModel() = AgentModel(
    id = 0,
    type = type!!,
    name = name!!,
    status = AgentStatusModel.UNINITIALIZED,
    visible = visible,
    updatePeriodSeconds = updatePeriodSeconds,
    lastUpdateTimestamp = null,
    sensitive = sensitive,
    parameters = parameters,
    memory = mapOf()
)