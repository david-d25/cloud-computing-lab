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
        AgentStatusEntityEnum.READY -> AgentStatusModel.READY
        AgentStatusEntityEnum.ERROR -> AgentStatusModel.ERROR
        else -> throw IllegalStateException("Wrong 'status' field in AgentEntity")
    },
    updatePeriodSeconds = updatePeriodSeconds,
    lastAppliedDataEntry = lastAppliedDataEntry,
    visible = visible!!,
    lastUpdateTimestamp = lastUpdateTimestamp?.toInstant(),
    sensitive = sensitive!!,
    parameters = parameters,
    memory = memory
)

fun AgentModel.toEntity() = AgentEntity(
    id = id,
    type = type,
    name = name,
    status = when(status) {
        AgentStatusModel.UNINITIALIZED -> AgentStatusEntityEnum.UNINITIALIZED
        AgentStatusModel.RUNNING -> AgentStatusEntityEnum.RUNNING
        AgentStatusModel.READY -> AgentStatusEntityEnum.READY
        AgentStatusModel.ERROR -> AgentStatusEntityEnum.ERROR
    },
    updatePeriodSeconds = updatePeriodSeconds,
    lastAppliedDataEntry = lastAppliedDataEntry,
    visible = visible,
    lastUpdateTimestamp = if (lastUpdateTimestamp != null) Timestamp.from(lastUpdateTimestamp) else null,
    sensitive = sensitive,
    parameters = parameters,
    memory = memory.toMutableMap()
)