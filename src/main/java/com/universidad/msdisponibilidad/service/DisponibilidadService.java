package com.universidad.msdisponibilidad.service;

import com.universidad.msdisponibilidad.client.MedicoClient;
import com.universidad.msdisponibilidad.dto.DisponibilidadRequestDTO;
import com.universidad.msdisponibilidad.dto.DisponibilidadResponseDTO;
import com.universidad.msdisponibilidad.dto.MedicoResponseDTO;
import com.universidad.msdisponibilidad.model.Disponibilidad;
import com.universidad.msdisponibilidad.repository.DisponibilidadRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisponibilidadService {

    private final DisponibilidadRepository disponibilidadRepository;


    private final MedicoClient medicoClient;


    private DisponibilidadResponseDTO mapToDTO(Disponibilidad d, MedicoResponseDTO medico) {
        return new DisponibilidadResponseDTO(
                d.getId(),
                d.getMedicoId(),
                medico.getNombre(),
                medico.getApellido(),
                medico.getEspecialidad(),
                d.getDiaSemana(),
                d.getHoraInicio(),
                d.getHoraFin(),
                d.getIntervaloMinutos(),
                d.isActiva()
        );
    }


    private DisponibilidadResponseDTO mapToDTOSinMedico(Disponibilidad d) {
        return new DisponibilidadResponseDTO(
                d.getId(), d.getMedicoId(),
                "Médico no disponible", "", "",
                d.getDiaSemana(), d.getHoraInicio(),
                d.getHoraFin(), d.getIntervaloMinutos(), d.isActiva()
        );
    }


    private MedicoResponseDTO validarMedico(Long medicoId) {
        log.info("Validando existencia y estado del médico con ID: {}", medicoId);
        try {
            MedicoResponseDTO medico = medicoClient.obtenerPorId(medicoId);
            if (!medico.isActivo()) {
                log.warn("El médico con ID {} existe pero no está activo", medicoId);
                throw new RuntimeException("El médico con ID " + medicoId + " no está activo");
            }
            log.info("Médico validado correctamente: {} {}", medico.getNombre(), medico.getApellido());
            return medico;
        } catch (FeignException.NotFound e) {
            log.error("No se encontró ningún médico con ID: {} en ms-medicos", medicoId);
            throw new RuntimeException("No existe ningún médico con ID: " + medicoId);
        }
    }



    public List<DisponibilidadResponseDTO> obtenerTodas() {
        log.info("Consultando todas las disponibilidades");
        List<DisponibilidadResponseDTO> resultado = disponibilidadRepository.findAll().stream()
                .map(d -> {
                    try {
                        MedicoResponseDTO medico = medicoClient.obtenerPorId(d.getMedicoId());
                        return mapToDTO(d, medico);
                    } catch (FeignException e) {
                        log.warn("No se pudo obtener médico con ID {} desde ms-medicos: {}", d.getMedicoId(), e.getMessage());
                        return mapToDTOSinMedico(d);
                    }
                })
                .collect(Collectors.toList());
        log.info("Se retornaron {} disponibilidades", resultado.size());
        return resultado;
    }

    public List<DisponibilidadResponseDTO> obtenerActivas() {
        log.info("Consultando disponibilidades activas");
        List<DisponibilidadResponseDTO> resultado = disponibilidadRepository.findAllActivas().stream()
                .map(d -> {
                    try {
                        MedicoResponseDTO medico = medicoClient.obtenerPorId(d.getMedicoId());
                        return mapToDTO(d, medico);
                    } catch (FeignException e) {
                        log.warn("No se pudo obtener médico con ID {} desde ms-medicos: {}", d.getMedicoId(), e.getMessage());
                        return mapToDTOSinMedico(d);
                    }
                })
                .collect(Collectors.toList());
        log.info("Se retornaron {} disponibilidades activas", resultado.size());
        return resultado;
    }

    public Optional<DisponibilidadResponseDTO> obtenerPorId(Long id) {
        log.info("Buscando disponibilidad con ID: {}", id);
        Optional<DisponibilidadResponseDTO> resultado = disponibilidadRepository.findById(id).map(d -> {
            try {
                MedicoResponseDTO medico = medicoClient.obtenerPorId(d.getMedicoId());
                return mapToDTO(d, medico);
            } catch (FeignException e) {
                log.warn("No se pudo obtener médico con ID {} desde ms-medicos: {}", d.getMedicoId(), e.getMessage());
                return mapToDTOSinMedico(d);
            }
        });
        if (resultado.isEmpty()) {
            log.warn("No se encontró disponibilidad con ID: {}", id);
        }
        return resultado;
    }

    public List<DisponibilidadResponseDTO> obtenerPorMedico(Long medicoId) {
        log.info("Consultando disponibilidades del médico con ID: {}", medicoId);
        List<DisponibilidadResponseDTO> resultado = disponibilidadRepository.findByMedicoId(medicoId).stream()
                .map(d -> {
                    try {
                        MedicoResponseDTO medico = medicoClient.obtenerPorId(d.getMedicoId());
                        return mapToDTO(d, medico);
                    } catch (FeignException e) {
                        log.warn("No se pudo obtener médico con ID {} desde ms-medicos: {}", d.getMedicoId(), e.getMessage());
                        return mapToDTOSinMedico(d);
                    }
                })
                .collect(Collectors.toList());
        log.info("Se encontraron {} disponibilidades para el médico ID: {}", resultado.size(), medicoId);
        return resultado;
    }

    public List<DisponibilidadResponseDTO> obtenerPorDia(String dia) {
        log.info("Consultando disponibilidades activas para el día: {}", dia);
        List<DisponibilidadResponseDTO> resultado = disponibilidadRepository.findActivasByDia(dia).stream()
                .map(d -> {
                    try {
                        MedicoResponseDTO medico = medicoClient.obtenerPorId(d.getMedicoId());
                        return mapToDTO(d, medico);
                    } catch (FeignException e) {
                        log.warn("No se pudo obtener médico con ID {} desde ms-medicos: {}", d.getMedicoId(), e.getMessage());
                        return mapToDTOSinMedico(d);
                    }
                })
                .collect(Collectors.toList());
        log.info("Se encontraron {} disponibilidades activas para el día: {}", resultado.size(), dia);
        return resultado;
    }

    public List<DisponibilidadResponseDTO> obtenerPorMedicoYDia(Long medicoId, String dia) {
        log.info("Consultando disponibilidades del médico ID: {} para el día: {}", medicoId, dia);
        List<DisponibilidadResponseDTO> resultado = disponibilidadRepository.findByMedicoIdAndDia(medicoId, dia).stream()
                .map(d -> {
                    try {
                        MedicoResponseDTO medico = medicoClient.obtenerPorId(d.getMedicoId());
                        return mapToDTO(d, medico);
                    } catch (FeignException e) {
                        log.warn("No se pudo obtener médico con ID {} desde ms-medicos: {}", d.getMedicoId(), e.getMessage());
                        return mapToDTOSinMedico(d);
                    }
                })
                .collect(Collectors.toList());
        log.info("Se encontraron {} disponibilidades para médico ID: {} en día: {}", resultado.size(), medicoId, dia);
        return resultado;
    }

    public DisponibilidadResponseDTO guardar(DisponibilidadRequestDTO dto) {
        log.info("Creando nueva disponibilidad para médico ID: {}, día: {}", dto.getMedicoId(), dto.getDiaSemana());
        MedicoResponseDTO medico = validarMedico(dto.getMedicoId());

        Disponibilidad d = new Disponibilidad(
                null,
                dto.getMedicoId(),
                dto.getDiaSemana().toUpperCase(),
                dto.getHoraInicio(),
                dto.getHoraFin(),
                dto.getIntervaloMinutos(),
                true
        );
        DisponibilidadResponseDTO guardada = mapToDTO(disponibilidadRepository.save(d), medico);
        log.info("Disponibilidad creada con ID: {} para médico {} {}", guardada.getId(), medico.getNombre(), medico.getApellido());
        return guardada;
    }

    public Optional<DisponibilidadResponseDTO> actualizar(Long id, DisponibilidadRequestDTO dto) {
        log.info("Actualizando disponibilidad ID: {} con médico ID: {}", id, dto.getMedicoId());
        MedicoResponseDTO medico = validarMedico(dto.getMedicoId());

        Optional<DisponibilidadResponseDTO> resultado = disponibilidadRepository.findById(id).map(existente -> {
            existente.setMedicoId(dto.getMedicoId());
            existente.setDiaSemana(dto.getDiaSemana().toUpperCase());
            existente.setHoraInicio(dto.getHoraInicio());
            existente.setHoraFin(dto.getHoraFin());
            existente.setIntervaloMinutos(dto.getIntervaloMinutos());
            return mapToDTO(disponibilidadRepository.save(existente), medico);
        });
        if (resultado.isPresent()) {
            log.info("Disponibilidad ID: {} actualizada correctamente", id);
        } else {
            log.warn("No se encontró disponibilidad con ID: {} para actualizar", id);
        }
        return resultado;
    }

    public void eliminar(Long id) {
        log.info("Eliminando disponibilidad con ID: {}", id);
        disponibilidadRepository.deleteById(id);
        log.info("Disponibilidad con ID: {} eliminada correctamente", id);
    }
}
