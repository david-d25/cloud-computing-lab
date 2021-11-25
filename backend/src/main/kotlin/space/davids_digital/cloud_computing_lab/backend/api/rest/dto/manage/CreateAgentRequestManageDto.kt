package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage

import javax.validation.constraints.NotNull

data class CreateAgentRequestManageDto(
    @NotNull
    val name: String? = null,

    @NotNull
    val type: String? = null,

    val visible: Boolean = false,

    val updatePeriodSeconds: Long? = null,

    val sensitive: Boolean = false,

    val parameters: Map<String, String> = mapOf()
)
