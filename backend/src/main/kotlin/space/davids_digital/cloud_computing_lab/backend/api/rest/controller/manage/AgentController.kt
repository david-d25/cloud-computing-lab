package space.davids_digital.cloud_computing_lab.backend.api.rest.controller.manage

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.AgentResponseManageDto
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.CreateAgentRequestManageDto
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.DeleteAgentRequestManageDto
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.UpdateAgentRequestManageDto
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.mapping.toModel
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.mapping.toResponseManageDto
import space.davids_digital.cloud_computing_lab.backend.service.AgentService

@RestController
@RequestMapping("/manage/agent")
class AgentController @Autowired constructor(
    private val agentService: AgentService
) {
    @PutMapping
    fun createAgent(@RequestBody agent: CreateAgentRequestManageDto) {
        agentService.createAgent(agent.toModel())
    }

    @PostMapping
    fun updateAgent(@RequestBody update: UpdateAgentRequestManageDto) {
        // todo
    }

    @DeleteMapping
    fun deleteAgent(@RequestBody @Validated request: DeleteAgentRequestManageDto) {
        agentService.deleteAgent(request.id!!)
    }

    @GetMapping(produces = ["application/json"])
    fun getAgents(): List<AgentResponseManageDto> = agentService.getAgents().map { it.toResponseManageDto() }
}