package space.davids_digital.cloud_computing_lab.backend.api.rest.controller.public

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import space.davids_digital.cloud_computing_lab.backend.api.rest.dto.GenerateResultDto
import space.davids_digital.cloud_computing_lab.backend.service.TextGeneratorService

@RestController
class GeneratorController(
    private val textGeneratorService: TextGeneratorService
) {
    @GetMapping("/generate")
    fun generate(
        @RequestParam(name = "text") text: String,
        @RequestParam(name = "styles") styles: List<Int> = listOf()
    ) = GenerateResultDto(
        beginning = text,
        generated = textGeneratorService.generate(text, styles)
    )
}