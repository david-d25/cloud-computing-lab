package space.davids_digital.cloud_computing_lab.backend

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.hibernate5.HibernateTransactionManager
import org.springframework.orm.hibernate5.LocalSessionFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
open class AppConfig {
    @Bean
    open fun sessionFactory(): LocalSessionFactoryBean {
        val sessionFactory = LocalSessionFactoryBean()
        sessionFactory.setDataSource(dataSource())
        sessionFactory.setPackagesToScan("space.davids_digital.cloud_computing_lab.backend")
        sessionFactory.hibernateProperties = hibernateProperties()
        return sessionFactory
    }

    @Bean
    open fun dataSource(): DataSource {
        val requireEnv = { name: String ->
            System.getenv(name) ?: throw IllegalStateException("Please, provide the '$name' environment variable")
        }

        val ds = DriverManagerDataSource()
        ds.setDriverClassName("org.postgresql.Driver")
        ds.url = requireEnv("DB_URL")
        ds.username = requireEnv("DB_USERNAME")
        ds.password = requireEnv("DB_PASSWORD")
        return ds
    }

    @Bean
    open fun transactionManager(): PlatformTransactionManager {
        val transactionManager = HibernateTransactionManager()
        transactionManager.sessionFactory = sessionFactory().getObject()
        return transactionManager
    }

    @Bean
    open fun hibernateProperties(): Properties {
        val properties = Properties()
        properties["hibernate.hbm2ddl.auto"] = "create"
        properties["hibernate.dialect"] = "org.hibernate.dialect.PostgreSQLDialect"
        return properties
    }
}