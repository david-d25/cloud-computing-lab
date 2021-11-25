package space.davids_digital.cloud_computing_lab.backend.api.rest.controller.manage

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.AgentTypeResponseDto
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.manage.mapping.toDto
import space.davids_digital.cloud_computing_lab.backend.model.AgentExecutorMetaInfo

@RestController
@RequestMapping("/manage/agent/type")
class AgentTypesController(
    @Qualifier("agentExecutorsMetaInfo")
    private val metaInfo: Map<String, AgentExecutorMetaInfo>
) {
    @GetMapping
    fun getAgentTypesInfo() = metaInfo.values.map { it.toDto() }
}