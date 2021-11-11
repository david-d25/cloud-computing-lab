package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.mapping

import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.AgentResponseManageDto
import space.davids_digital.cloud_computing_lab.backend.model.AgentModel

fun AgentModel.toResponseManageDto() = AgentResponseManageDto(
    id = id,
    type = type,
    name = name,
    status = status,
    visible = visible,
    updatePeriodSeconds = updatePeriodSeconds,
    lastUpdateTimestamp = lastUpdateTimestamp,
    sensitive = sensitive,
    parameters = parameters,
    memory = memory
)