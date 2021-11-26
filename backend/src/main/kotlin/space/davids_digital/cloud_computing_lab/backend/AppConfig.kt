package space.davids_digital.cloud_computing_lab.backend

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.reflections.Reflections
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.hibernate5.HibernateTransactionManager
import org.springframework.orm.hibernate5.LocalSessionFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import space.davids_digital.cloud_computing_lab.agent.annotation.AgentExecutor
import space.davids_digital.cloud_computing_lab.agent.annotation.AgentExecutorParameter
import space.davids_digital.cloud_computing_lab.backend.model.AgentExecutorMetaInfo
import space.davids_digital.cloud_computing_lab.backend.model.AgentExecutorParameterMetaInfo
import java.util.*
import javax.sql.DataSource


@Import(WebMvcConfig::class)
@Configuration
@ComponentScan
@EnableJpaRepositories(entityManagerFactoryRef = "sessionFactory")
@EnableTransactionManagement
class AppConfig {
    @Bean
    fun sessionFactory(): LocalSessionFactoryBean {
        val sessionFactory = LocalSessionFactoryBean()
        sessionFactory.setDataSource(dataSource())
        sessionFactory.setPackagesToScan("space.davids_digital")
        sessionFactory.hibernateProperties = hibernateProperties()
        return sessionFactory
    }

    @Bean
    fun dataSource(): DataSource {
        val requireEnv = { name: String ->
            System.getenv(name) ?: throw IllegalStateException("Please, provide the '$name' environment variable")
        }

        val ds = DriverManagerDataSource()
        ds.setDriverClassName("org.postgresql.Driver")
        ds.url = requireEnv("DB_URL")
        ds.username = requireEnv("DB_USERNAME")
        ds.password = requireEnv("DB_PASSWORD")
        ds.connection.autoCommit = true
        return ds
    }

    @Bean
    fun transactionManager(): PlatformTransactionManager {
        val transactionManager = HibernateTransactionManager()
        transactionManager.sessionFactory = sessionFactory().getObject()
        transactionManager.dataSource = dataSource()
        return transactionManager
    }

    @Bean
    fun hibernateProperties(): Properties {
        val properties = Properties()
        properties["hibernate.hbm2ddl.auto"] = "update"
        properties["hibernate.jdbc.batch_size"] = "500"
        properties["hibernate.dialect"] = "org.hibernate.dialect.PostgreSQLDialect"
        properties["hibernate.show_sql"] = "true"
        return properties
    }

    @Bean
    @Qualifier("agentExecutorsMetaInfo")
    fun agentExecutorsMetaInfo(): Map<String, AgentExecutorMetaInfo> {
        val reflections = Reflections("space.davids_digital")
        return reflections.getTypesAnnotatedWith(AgentExecutor::class.java).associate {
            clazz -> Pair(
                clazz.name,
                AgentExecutorMetaInfo(
                    type = clazz.name,
                    title = clazz.getAnnotation(AgentExecutor::class.java).value,
                    parameters = clazz.getAnnotationsByType(AgentExecutorParameter::class.java).map {
                        with(it) { AgentExecutorParameterMetaInfo(name, title, type, required) }
                    }
                )
            )
        }
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return mapper
    }
}