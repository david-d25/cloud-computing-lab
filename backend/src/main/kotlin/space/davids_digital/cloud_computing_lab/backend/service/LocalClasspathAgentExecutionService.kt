package space.davids_digital.cloud_computing_lab.backend.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import space.davids_digital.cloud_computing_lab.agent.AgentConstants.Companion.ERROR_MESSAGE
import space.davids_digital.cloud_computing_lab.agent.context.AgentContext
import space.davids_digital.cloud_computing_lab.agent.exception.AgentExecutionException
import space.davids_digital.cloud_computing_lab.agent.executor.AbstractAgentExecutor
import space.davids_digital.cloud_computing_lab.backend.agent.context.EntityBasedAgentContext
import space.davids_digital.cloud_computing_lab.backend.orm.entity.enum.AgentStatusEntityEnum
import space.davids_digital.cloud_computing_lab.backend.orm.repository.AgentRepository
import java.sql.Timestamp
import java.time.Instant
import java.util.concurrent.Executors
import javax.persistence.EntityManagerFactory

@Service
@Qualifier("local-classpath")
class LocalClasspathAgentExecutionService @Autowired constructor(
    private val agentRepository: AgentRepository,
    private val entityManagerFactory: EntityManagerFactory
): AgentExecutionService {
    private val executorService = Executors.newCachedThreadPool()

    override fun enqueueExecution(id: Int) {
        val entity = agentRepository.findById(id).orElseThrow { ServiceException("Agent id $id not found") }
        try {
            val executorClass: Class<*> = Class.forName(entity.type)
            if (AbstractAgentExecutor::class.java.isAssignableFrom(executorClass)) {
                val context = EntityBasedAgentContext(entity)
                val constructor = executorClass.getConstructor(AgentContext::class.java)
                val executor = constructor.newInstance(context) as AbstractAgentExecutor

                executorService.execute {
                    val entityManager = entityManagerFactory.createEntityManager()
                    val transaction = entityManager.transaction
                    try {
                        entity.lastUpdateTimestamp = Timestamp.from(Instant.now())
                        entity.status = AgentStatusEntityEnum.RUNNING

                        transaction.begin()
                        executor.execute()
                        transaction.commit()

                        entity.status = AgentStatusEntityEnum.READY
                    } catch (e: AgentExecutionException) {
                        transaction.rollback()
                        entity.status = AgentStatusEntityEnum.ERROR
                        entity.memory[ERROR_MESSAGE] = e.message
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        transaction.rollback()
                        entity.status = AgentStatusEntityEnum.ERROR
                        entity.memory[ERROR_MESSAGE] = "Unknown agent execution error"
                    } finally {
                        agentRepository.save(entity)
                        entityManager.close()
                    }
                }
            } else {
                entity.status = AgentStatusEntityEnum.ERROR
                entity.memory[ERROR_MESSAGE] = "Agent id '${id}' has wrong type"
                agentRepository.save(entity)
            }
        } catch (e: ClassNotFoundException) {
            entity.status = AgentStatusEntityEnum.ERROR
            entity.memory[ERROR_MESSAGE] = "Agent of type '${entity.type}' not found"
            agentRepository.save(entity)
        }
    }
}