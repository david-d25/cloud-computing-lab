package space.davids_digital.cloud_computing_lab.backend.orm.entity.mapping

import space.davids_digital.cloud_computing_lab.backend.model.AgentModel
import space.davids_digital.cloud_computing_lab.backend.model.AgentStatusModel
import space.davids_digital.cloud_computing_lab.backend.orm.entity.AgentEntity
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import java.sql.Timestamp

fun AgentEntity.toModel() = AgentModel(
    id = id!!,
    type = type!!,
    name = name!!,
    status = when(status) {
        AgentStatusEntityEnum.UNINITIALIZED -> AgentStatusModel.UNINITIALIZED
        AgentStatusEntityEnum.RUNNING -> AgentStatusModel.RUNNING
        AgentStatusEntityEnum.SLEEPING -> AgentStatusModel.SLEEPING
        AgentStatusEntityEnum.ERROR -> AgentStatusModel.ERROR
        else -> throw IllegalStateException("Wrong 'status' field in AgentEntity")
    },
    updatePeriodSeconds = updatePeriodSeconds,
    visible = visible!!,
    lastUpdateTimestamp = lastUpdateTimestamp!!.toInstant(),
    sensitive = sensitive!!,
    parameters = parameters.entries.associate { entry -> Pair(entry.key, String(entry.value)) },
    memory = memory
)

fun AgentModel.toEntity() = AgentEntity(
    id = id,
    type = type,
    name = name,
    status = when(status) {
        AgentStatusModel.UNINITIALIZED -> AgentStatusEntityEnum.UNINITIALIZED
        AgentStatusModel.RUNNING -> AgentStatusEntityEnum.RUNNING
        AgentStatusModel.SLEEPING -> AgentStatusEntityEnum.SLEEPING
        AgentStatusModel.ERROR -> AgentStatusEntityEnum.ERROR
    },
    updatePeriodSeconds = updatePeriodSeconds,
    visible = visible,
    lastUpdateTimestamp = if (lastUpdateTimestamp != null) Timestamp.from(lastUpdateTimestamp) else null,
    sensitive = sensitive,
    parameters = parameters.entries.associate { entry -> Pair(entry.key, entry.value.toByteArray()) },
    memory = memory.toMutableMap()
)