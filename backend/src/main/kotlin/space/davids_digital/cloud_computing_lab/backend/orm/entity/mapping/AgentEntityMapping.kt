package space.davids_digital.cloud_computing_lab.backend.orm.entity.mapping

import space.davids_digital.cloud_computing_lab.backend.model.Agent
import space.davids_digital.cloud_computing_lab.backend.model.AgentStatus
import space.davids_digital.cloud_computing_lab.backend.orm.entity.AgentEntity
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import java.sql.Timestamp

fun AgentEntity.toModel() = Agent(
    id = id!!,
    type = type!!,
    name = name!!,
    status = when(status) {
        AgentStatusEntityEnum.UNINITIALIZED -> AgentStatus.UNINITIALIZED
        AgentStatusEntityEnum.RUNNING -> AgentStatus.RUNNING
        AgentStatusEntityEnum.SLEEPING -> AgentStatus.SLEEPING
        AgentStatusEntityEnum.ERROR -> AgentStatus.ERROR
        else -> throw IllegalStateException("Wrong 'status' field in AgentEntity")
    },
    updatePeriodSeconds = updatePeriodSeconds,
    visible = visible!!,
    lastUpdateTimestamp = lastUpdateTimestamp!!.toInstant(),
    sensitive = sensitive!!,
    properties = properties,
    memory = memory
)

fun Agent.toEntity() = AgentEntity(
    id = id,
    type = type,
    name = name,
    status = when(status) {
        AgentStatus.UNINITIALIZED -> AgentStatusEntityEnum.UNINITIALIZED
        AgentStatus.RUNNING -> AgentStatusEntityEnum.RUNNING
        AgentStatus.SLEEPING -> AgentStatusEntityEnum.SLEEPING
        AgentStatus.ERROR -> AgentStatusEntityEnum.ERROR
    },
    updatePeriodSeconds = updatePeriodSeconds,
    visible = visible,
    lastUpdateTimestamp = Timestamp.from(lastUpdateTimestamp),
    sensitive = sensitive,
    properties = properties,
    memory = memory
)