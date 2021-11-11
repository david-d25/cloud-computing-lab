package space.davids_digital.cloud_computing_lab.backend.service

class ServiceException(override val message: String): RuntimeException(message)