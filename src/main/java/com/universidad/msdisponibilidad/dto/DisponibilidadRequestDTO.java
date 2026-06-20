package com.universidad.msdisponibilidad.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadRequestDTO {

    @NotNull(message = "El medicoId es obligatorio")
    private Long medicoId;

    @NotBlank(message = "El día de la semana es obligatorio")
    private String diaSemana; // LUNES, MARTES, ... VIERNES

    @NotBlank(message = "La hora de inicio es obligatoria")
    private String horaInicio; // formato "HH:mm"

    @NotBlank(message = "La hora de fin es obligatoria")
    private String horaFin;    // formato "HH:mm"

    @Min(value = 1, message = "El intervalo mínimo es 1 minuto")
    private int intervaloMinutos; // duración de cada turno en minutos
}
