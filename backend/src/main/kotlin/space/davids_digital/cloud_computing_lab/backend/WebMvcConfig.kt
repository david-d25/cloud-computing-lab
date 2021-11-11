package space.davids_digital.cloud_computing_lab.backend

import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

class WebMvcConfig: WebMvcConfigurationSupport() {
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>?>) {
        converters.add(MappingJackson2HttpMessageConverter())
        converters.add(StringHttpMessageConverter())
    }
}