package com.universidad.msdisponibilidad.controller;

import com.universidad.msdisponibilidad.dto.DisponibilidadRequestDTO;
import com.universidad.msdisponibilidad.dto.DisponibilidadResponseDTO;
import com.universidad.msdisponibilidad.service.DisponibilidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/disponibilidades")
@RequiredArgsConstructor
public class DisponibilidadController {

    private final DisponibilidadService disponibilidadService;


    @GetMapping
    public List<DisponibilidadResponseDTO> obtenerTodas() {
        return disponibilidadService.obtenerTodas();
    }


    @GetMapping("/activas")
    public List<DisponibilidadResponseDTO> obtenerActivas() {
        return disponibilidadService.obtenerActivas();
    }


    @GetMapping("/{id}")
    public ResponseEntity<DisponibilidadResponseDTO> obtenerPorId(@PathVariable Long id) {
        return disponibilidadService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/medico/{medicoId}")
    public List<DisponibilidadResponseDTO> porMedico(@PathVariable Long medicoId) {
        return disponibilidadService.obtenerPorMedico(medicoId);
    }


    @GetMapping("/dia")
    public List<DisponibilidadResponseDTO> porDia(@RequestParam String nombre) {
        return disponibilidadService.obtenerPorDia(nombre);
    }


    @GetMapping("/medico/{medicoId}/dia")
    public List<DisponibilidadResponseDTO> porMedicoYDia(
            @PathVariable Long medicoId,
            @RequestParam String nombre) {
        return disponibilidadService.obtenerPorMedicoYDia(medicoId, nombre);
    }


    @PostMapping
    public ResponseEntity<DisponibilidadResponseDTO> crear(
            @Valid @RequestBody DisponibilidadRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(disponibilidadService.guardar(dto));
    }


    @PutMapping("/{id}")
    public ResponseEntity<DisponibilidadResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DisponibilidadRequestDTO dto) {
        return disponibilidadService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        disponibilidadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
