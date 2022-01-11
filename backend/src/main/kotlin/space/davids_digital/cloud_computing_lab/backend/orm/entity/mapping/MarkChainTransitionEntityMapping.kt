package space.davids_digital.cloud_computing_lab.backend.orm.entity.mapping

import space.davids_digital.cloud_computing_lab.backend.model.MarkChainTransitionModel
import space.davids_digital.cloud_computing_lab.backend.orm.entity.MarkChainTransitionEntity

fun MarkChainTransitionEntity.toModel() = MarkChainTransitionModel(
    agentId = agentId!!,
    beginning = beginning,
    continuation = continuation,
    transitionCount = transitionCount
)
