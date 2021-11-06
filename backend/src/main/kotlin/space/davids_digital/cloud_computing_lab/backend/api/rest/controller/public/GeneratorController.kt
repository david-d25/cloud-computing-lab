package space.davids_digital.cloud_computing_lab.backend.api.rest.controller.public

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import space.davids_digital.cloud_computing_lab.backend.service.GeneratorService

@RestController
class GeneratorController(
    private val generatorService: GeneratorService
) {
    @GetMapping("/generate")
    fun generate(
        @RequestParam(name = "text") text: String,
        @RequestParam(name = "styles") styles: Array<Int>
    ) = generatorService.generate(text, styles)
}