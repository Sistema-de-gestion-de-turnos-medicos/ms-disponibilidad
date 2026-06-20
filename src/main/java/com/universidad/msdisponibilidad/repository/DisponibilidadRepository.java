package com.universidad.msdisponibilidad.repository;

import com.universidad.msdisponibilidad.model.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {


    List<Disponibilidad> findByMedicoId(Long medicoId);


    List<Disponibilidad> findByMedicoIdAndActivaTrue(Long medicoId);


    @Query("SELECT d FROM Disponibilidad d WHERE d.activa = true AND LOWER(d.diaSemana) = LOWER(:dia)")
    List<Disponibilidad> findActivasByDia(@Param("dia") String dia);


    @Query("SELECT d FROM Disponibilidad d WHERE d.medicoId = :medicoId AND LOWER(d.diaSemana) = LOWER(:dia) AND d.activa = true")
    List<Disponibilidad> findByMedicoIdAndDia(@Param("medicoId") Long medicoId, @Param("dia") String dia);


    @Query("SELECT d FROM Disponibilidad d WHERE d.activa = true ORDER BY d.diaSemana, d.horaInicio")
    List<Disponibilidad> findAllActivas();
}
