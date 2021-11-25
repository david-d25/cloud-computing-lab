package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage

import javax.validation.constraints.NotNull

data class EditAgentRequestManageDto(
    @NotNull
    val id: Int? = null,

    val type: String? = null,

    val name: String? = null,

    val visible: Boolean? = null,

    val updatePeriodSeconds: Long? = null,

    val sensitive: Boolean? = null,

    val parameters: Map<String, String> = mapOf(),
)
