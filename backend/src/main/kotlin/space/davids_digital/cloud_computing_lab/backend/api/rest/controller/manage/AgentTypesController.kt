package space.davids_digital.cloud_computing_lab.backend.api.rest.controller.manage

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.AgentTypesResponseDto

@RestController
@RequestMapping("/manage/agent-type")
class AgentTypesController(
    private val agentExecutorsTypeToNameMap: Map<String, String>
) {
    @GetMapping
    fun getAgentTypes() = agentExecutorsTypeToNameMap.entries.map { AgentTypesResponseDto(it.key, it.value) }
}