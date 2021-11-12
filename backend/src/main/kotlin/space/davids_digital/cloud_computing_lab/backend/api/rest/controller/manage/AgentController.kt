package space.davids_digital.cloud_computing_lab.backend.api.rest.controller.manage

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.AgentResponseManageDto
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.CreateAgentRequestManageDto
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.DeleteAgentRequestManageDto
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.EditAgentRequestManageDto
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.mapping.toModel
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.mapping.toResponseManageDto
import space.davids_digital.cloud_computing_lab.backend.service.AgentService

@RestController
@RequestMapping("/manage/agent")
class AgentController @Autowired constructor(
    private val agentService: AgentService
) {
    @GetMapping
    fun getAgents(): List<AgentResponseManageDto> = agentService.getAgents().map { it.toResponseManageDto() }

    @PutMapping
    fun createAgent(@RequestBody @Validated request: CreateAgentRequestManageDto) {
        agentService.createAgent(request.toModel())
    }

    @PostMapping
    fun editAgent(@RequestBody @Validated edit: EditAgentRequestManageDto) {
        agentService.editAgent(edit.toModel())
    }

    @DeleteMapping
    fun deleteAgent(@RequestBody @Validated request: DeleteAgentRequestManageDto) {
        agentService.deleteAgent(request.id!!)
    }

    @PostMapping("{agentId}/run")
    fun runAgent(@PathVariable agentId: Int) {
        agentService.runAgent(agentId)
    }
}