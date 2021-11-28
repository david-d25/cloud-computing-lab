package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.mapping

import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.EditAgentRequestManageDto
import space.davids_digital.cloud_computing_lab.backend.model.EditAgentRequestModel

fun EditAgentRequestManageDto.toModel() = EditAgentRequestModel(
    id = id!!,
    name = name,
    type = type,
    visible = visible,
    sensitive = sensitive,
    updatePeriodSeconds = updatePeriodSeconds,
    parameters = parameters
)