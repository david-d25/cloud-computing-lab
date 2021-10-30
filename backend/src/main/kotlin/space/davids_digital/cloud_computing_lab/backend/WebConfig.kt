package space.davids_digital.cloud_computing_lab.backend

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer
import javax.servlet.ServletContext


class WebConfig : AbstractAnnotationConfigDispatcherServletInitializer() {
    override fun onStartup(servletContext: ServletContext) {
        val applicationContext = AnnotationConfigWebApplicationContext()
        applicationContext.register(AppConfig::class.java)
        val dispatcherServlet = DispatcherServlet(applicationContext)
        val dispatcher = servletContext.addServlet("dispatcher", dispatcherServlet)
        dispatcher.setLoadOnStartup(1)
        dispatcher.addMapping("/")
    }

    override fun getServletMappings(): Array<String> = arrayOf()
    override fun getRootConfigClasses(): Array<Class<*>> = arrayOf()
    override fun getServletConfigClasses(): Array<Class<*>> = arrayOf()
}