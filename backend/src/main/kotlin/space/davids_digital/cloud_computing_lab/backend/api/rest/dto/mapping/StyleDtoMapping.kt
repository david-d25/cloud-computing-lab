package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.mapping

import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.StyleDto
import space.davids_digital.cloud_computing_lab.backend.model.AgentModel

fun AgentModel.toStyleDto() = StyleDto(
    id = id,
    title = name,
    sensitive = sensitive
)