package space.davids_digital.cloud_computing_lab.backend.api.rest

import org.springframework.core.annotation.AnnotationUtils
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.ErrorDto
import space.davids_digital.cloud_computing_lab.backend.service.ServerConfigService
import space.davids_digital.cloud_computing_lab.backend.service.ServiceException
import space.davids_digital.cloud_computing_lab.backend.util.ServerConfigConstraints.DEFAULT_INTERNAL_SERVER_ERROR_MESSAGE
import javax.servlet.http.HttpServletRequest


@ControllerAdvice
class GlobalDefaultExceptionHandler(
    configService: ServerConfigService
) {
    companion object {
        const val DEFAULT_DEFAULT_ERROR_MESSAGE = "Internal server error"
    }

    private val defaultErrorMessage = configService.getConfig(
        DEFAULT_INTERNAL_SERVER_ERROR_MESSAGE,
        DEFAULT_DEFAULT_ERROR_MESSAGE
    )!!

    @ExceptionHandler(Exception::class)
    fun handle(request: HttpServletRequest, exception: Exception): ErrorDto {
        // If the exception is annotated with @ResponseStatus, rethrow it and let the framework handle it
        if (AnnotationUtils.findAnnotation(exception.javaClass, ResponseStatus::class.java) != null) throw exception

        return ErrorDto(
            error = 500,
            message = if (exception is ServiceException)
                exception.message
            else
                defaultErrorMessage
        )
    }
}