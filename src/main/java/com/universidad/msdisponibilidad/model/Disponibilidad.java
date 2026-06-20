package com.universidad.msdisponibilidad.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "disponibilidades")
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "medico_id", nullable = false)
    private Long medicoId;


    @Column(name = "dia_semana", nullable = false, length = 15)
    private String diaSemana;


    @Column(name = "hora_inicio", nullable = false, length = 5)
    private String horaInicio;


    @Column(name = "hora_fin", nullable = false, length = 5)
    private String horaFin;


    @Column(name = "intervalo_minutos", nullable = false)
    private int intervaloMinutos;


    @Column(nullable = false)
    private boolean activa = true;
}
