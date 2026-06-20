package com.universidad.msdisponibilidad.client;

import com.universidad.msdisponibilidad.dto.MedicoResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;


@FeignClient(name = "ms-medicos", url = "${ms.medicos.url}")
public interface MedicoClient {

    // Replica GET /api/medicos/{id}
    @GetMapping("/api/medicos/{id}")
    MedicoResponseDTO obtenerPorId(@PathVariable Long id);

    // Replica GET /api/medicos/activos
    @GetMapping("/api/medicos/activos")
    List<MedicoResponseDTO> obtenerActivos();

    // Replica GET /api/medicos/especialidad?nombre=...
    @GetMapping("/api/medicos/especialidad")
    List<MedicoResponseDTO> buscarPorEspecialidad(@RequestParam String nombre);
}
