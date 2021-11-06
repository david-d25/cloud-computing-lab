package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.mapping

import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.StyleDto
import space.davids_digital.cloud_computing_lab.backend.model.Agent

fun Agent.toStyleDto() = StyleDto(
    id = id,
    name = name,
    sensitive = sensitive
)