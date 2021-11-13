package space.davids_digital.cloud_computing_lab.backend.api.rest.controller.public

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.StyleDto
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.mapping.toStyleDto
import space.davids_digital.cloud_computing_lab.backend.model.AgentStatusModel
import space.davids_digital.cloud_computing_lab.backend.service.AgentService

@RestController
@RequestMapping("/style")
class StyleController(
    private val agentService: AgentService
) {
    @GetMapping
    fun getStyles(): List<StyleDto> = agentService.getAgents()
        .filter { it.visible && it.status == AgentStatusModel.READY }
        .map { it.toStyleDto() }
}