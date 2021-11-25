package space.davids_digital.cloud_computing_lab.backend.api.rest.controller.manage

import org.springframework.web.bind.annotation.*
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.ServerConfigRequestManageDto
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.ServerConfigResponseManageDto
import space.davids_digital.cloud_computing_lab.backend.service.ServerConfigService

@RestController
@RequestMapping("/manage/config")
class ServerConfigController(
    private val configService: ServerConfigService
) {
    @GetMapping
    fun getServerConfig() = ServerConfigResponseManageDto(configService.getConfig())

    @PostMapping
    fun setServerConfig(@RequestBody request: ServerConfigRequestManageDto) {
        configService.setConfig(request.config)
    }
}