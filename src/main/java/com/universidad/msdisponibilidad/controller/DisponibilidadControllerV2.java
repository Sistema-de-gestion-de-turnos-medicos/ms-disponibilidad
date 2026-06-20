package com.universidad.msdisponibilidad.controller;

import com.universidad.msdisponibilidad.dto.DisponibilidadRequestDTO;
import com.universidad.msdisponibilidad.dto.DisponibilidadResponseDTO;
import com.universidad.msdisponibilidad.service.DisponibilidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador documentado de disponibilidad.
 * <p>
 * Expone exactamente las mismas operaciones que {@link DisponibilidadController},
 * pero enriquecidas con anotaciones de OpenAPI/Swagger (resúmenes, ejemplos,
 * esquemas y posibles respuestas) para facilitar su consumo desde Swagger UI.
 */
@RestController
@RequestMapping("/api/v2/disponibilidades")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "Disponibilidad",
        description = "Operaciones para la gestión de la disponibilidad horaria de los médicos: " +
                "creación, consulta, actualización y eliminación de bloques de disponibilidad."
)
public class DisponibilidadControllerV2 {

    private final DisponibilidadService disponibilidadService;

    @Operation(
            summary = "Listar todas las disponibilidades",
            description = "Obtiene el listado completo de bloques de disponibilidad registrados, " +
                    "enriquecidos con datos del médico obtenidos vía Feign desde ms-medicos."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Listado de disponibilidades obtenido exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = DisponibilidadResponseDTO.class))
                    )
            )
    })
    @GetMapping
    public List<DisponibilidadResponseDTO> obtenerTodas() {
        log.info("GET /api/v2/disponibilidades - Listar todas las disponibilidades");
        return disponibilidadService.obtenerTodas();
    }

    @Operation(
            summary = "Listar disponibilidades activas",
            description = "Obtiene únicamente los bloques de disponibilidad marcados como activos."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Disponibilidades activas obtenidas",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DisponibilidadResponseDTO.class)))
            )
    })
    @GetMapping("/activas")
    public List<DisponibilidadResponseDTO> obtenerActivas() {
        log.info("GET /api/v2/disponibilidades/activas");
        return disponibilidadService.obtenerActivas();
    }

    @Operation(
            summary = "Obtener una disponibilidad por su ID",
            description = "Busca y retorna un bloque de disponibilidad específico a partir de su identificador único."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Disponibilidad encontrada",
                    content = @Content(schema = @Schema(implementation = DisponibilidadResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No existe una disponibilidad con el ID indicado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadResponseDTO> obtenerPorId(
            @Parameter(description = "Identificador único de la disponibilidad", example = "1", required = true)
            @PathVariable Long id) {
        log.info("GET /api/v2/disponibilidades/{}", id);
        return disponibilidadService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Listar disponibilidades de un médico",
            description = "Obtiene todos los bloques de disponibilidad asociados a un médico específico."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Disponibilidades del médico obtenidas",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DisponibilidadResponseDTO.class)))
            )
    })
    @GetMapping("/medico/{medicoId}")
    public List<DisponibilidadResponseDTO> porMedico(
            @Parameter(description = "Identificador del médico", example = "3", required = true)
            @PathVariable Long medicoId) {
        log.info("GET /api/v2/disponibilidades/medico/{}", medicoId);
        return disponibilidadService.obtenerPorMedico(medicoId);
    }

    @Operation(
            summary = "Listar disponibilidades por día de la semana",
            description = "Filtra los bloques de disponibilidad según el día de la semana indicado."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Disponibilidades del día obtenidas",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DisponibilidadResponseDTO.class)))
            )
    })
    @GetMapping("/dia")
    public List<DisponibilidadResponseDTO> porDia(
            @Parameter(
                    description = "Nombre del día de la semana",
                    example = "LUNES",
                    required = true,
                    schema = @Schema(allowableValues = {
                            "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"
                    })
            )
            @RequestParam String nombre) {
        log.info("GET /api/v2/disponibilidades/dia?nombre={}", nombre);
        return disponibilidadService.obtenerPorDia(nombre);
    }

    @Operation(
            summary = "Listar disponibilidades de un médico en un día específico",
            description = "Combina el filtro por médico y por día de la semana."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Disponibilidades del médico en el día obtenidas",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DisponibilidadResponseDTO.class)))
            )
    })
    @GetMapping("/medico/{medicoId}/dia")
    public List<DisponibilidadResponseDTO> porMedicoYDia(
            @Parameter(description = "Identificador del médico", example = "3", required = true)
            @PathVariable Long medicoId,
            @Parameter(
                    description = "Nombre del día de la semana",
                    example = "LUNES",
                    required = true,
                    schema = @Schema(allowableValues = {
                            "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"
                    })
            )
            @RequestParam String nombre) {
        log.info("GET /api/v2/disponibilidades/medico/{}/dia?nombre={}", medicoId, nombre);
        return disponibilidadService.obtenerPorMedicoYDia(medicoId, nombre);
    }

    @Operation(
            summary = "Registrar un nuevo bloque de disponibilidad",
            description = "Crea un nuevo bloque de disponibilidad para un médico, indicando el " +
                    "día de la semana, el rango horario y la duración de cada turno."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Disponibilidad creada exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DisponibilidadResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "DisponibilidadCreada",
                                    value = """
                                            {
                                              "id": 1,
                                              "medicoId": 3,
                                              "medicoNombre": "María",
                                              "medicoApellido": "González",
                                              "medicoEspecialidad": "Cardiología",
                                              "diaSemana": "LUNES",
                                              "horaInicio": "09:00",
                                              "horaFin": "13:00",
                                              "intervaloMinutos": 30,
                                              "activa": true
                                            }"""
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos de la solicitud inválidos (validación fallida)"
            )
    })
    @PostMapping
    public ResponseEntity<DisponibilidadResponseDTO> crear(
            @RequestBody(
                    description = "Datos necesarios para registrar un nuevo bloque de disponibilidad",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DisponibilidadRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "NuevaDisponibilidad",
                                    value = """
                                            {
                                              "medicoId": 3,
                                              "diaSemana": "LUNES",
                                              "horaInicio": "09:00",
                                              "horaFin": "13:00",
                                              "intervaloMinutos": 30
                                            }"""
                            )
                    )
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody DisponibilidadRequestDTO dto) {
        log.info("POST /api/v2/disponibilidades - Crear nueva disponibilidad");
        return ResponseEntity.status(HttpStatus.CREATED).body(disponibilidadService.guardar(dto));
    }

    @Operation(
            summary = "Actualizar un bloque de disponibilidad",
            description = "Actualiza los datos de un bloque de disponibilidad existente identificado por su ID."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Disponibilidad actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = DisponibilidadResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Datos de la solicitud inválidos (validación fallida)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No existe una disponibilidad con el ID indicado"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadResponseDTO> actualizar(
            @Parameter(description = "Identificador único de la disponibilidad", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody(
                    description = "Nuevos datos del bloque de disponibilidad",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = DisponibilidadRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "ActualizarDisponibilidad",
                                    value = """
                                            {
                                              "medicoId": 3,
                                              "diaSemana": "MARTES",
                                              "horaInicio": "10:00",
                                              "horaFin": "14:00",
                                              "intervaloMinutos": 20
                                            }"""
                            )
                    )
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody DisponibilidadRequestDTO dto) {
        log.info("PUT /api/v2/disponibilidades/{}", id);
        return disponibilidadService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Eliminar un bloque de disponibilidad",
            description = "Elimina de forma permanente un bloque de disponibilidad del sistema."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "Disponibilidad eliminada exitosamente"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No existe una disponibilidad con el ID indicado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador único de la disponibilidad", example = "1", required = true)
            @PathVariable Long id) {
        log.warn("DELETE /api/v2/disponibilidades/{}", id);
        disponibilidadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
