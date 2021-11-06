package space.davids_digital.cloud_computing_lab.backend.api.rest.controller.public

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.mapping.toStyleDto
import space.davids_digital.cloud_computing_lab.backend.model.AgentStatus
import space.davids_digital.cloud_computing_lab.backend.service.AgentService

@RestController("/style")
class StyleController(
    private val agentService: AgentService
) {
    @GetMapping
    fun getStyles() = agentService.getAgents()
        .filter { it.visible && it.status != AgentStatus.ERROR }
        .map { it.toStyleDto() }
}