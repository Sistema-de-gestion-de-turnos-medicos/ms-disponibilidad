package com.universidad.msdisponibilidad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadResponseDTO {
    private Long id;
    private Long medicoId;
    // Datos enriquecidos desde ms-medicos via Feign
    private String medicoNombre;
    private String medicoApellido;
    private String medicoEspecialidad;
    // Datos propios de disponibilidad
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private int intervaloMinutos;
    private boolean activa;
}
