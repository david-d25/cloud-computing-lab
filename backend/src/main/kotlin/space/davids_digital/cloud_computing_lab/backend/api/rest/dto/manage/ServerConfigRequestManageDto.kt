package space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage

import javax.validation.constraints.NotNull

data class ServerConfigRequestManageDto(
    @NotNull
    val config: Map<String, String>
)
