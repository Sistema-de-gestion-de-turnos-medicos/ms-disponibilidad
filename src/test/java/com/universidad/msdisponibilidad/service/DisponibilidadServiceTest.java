package com.universidad.msdisponibilidad.service;

import com.universidad.msdisponibilidad.client.MedicoClient;
import com.universidad.msdisponibilidad.dto.DisponibilidadRequestDTO;
import com.universidad.msdisponibilidad.dto.DisponibilidadResponseDTO;
import com.universidad.msdisponibilidad.dto.MedicoResponseDTO;
import com.universidad.msdisponibilidad.model.Disponibilidad;
import com.universidad.msdisponibilidad.repository.DisponibilidadRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisponibilidadServiceTest {

    @Mock
    private DisponibilidadRepository disponibilidadRepository;

    @Mock
    private MedicoClient medicoClient;

    @InjectMocks
    private DisponibilidadService disponibilidadService;

    private Disponibilidad disponibilidad;
    private DisponibilidadRequestDTO requestDTO;
    private MedicoResponseDTO medicoResponseDTO;

    @BeforeEach
    void setUp() {
        disponibilidad = new Disponibilidad(1L, 100L, "LUNES", "08:00", "12:00", 30, true);

        requestDTO = new DisponibilidadRequestDTO(100L, "lunes", "08:00", "12:00", 30);

        medicoResponseDTO = new MedicoResponseDTO(
                100L, "Laura", "Fernández", "Dermatología", "M-456",
                "laura@correo.com", "999111222", true);
    }



    @Test
    void testObtenerTodas_exitoso() {

        when(disponibilidadRepository.findAll()).thenReturn(List.of(disponibilidad));
        when(medicoClient.obtenerPorId(100L)).thenReturn(medicoResponseDTO);

        List<DisponibilidadResponseDTO> resultado = disponibilidadService.obtenerTodas();


        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Laura", resultado.get(0).getMedicoNombre());
    }

    @Test
    void testObtenerTodas_medicoServiceCaido_usaDatosPorDefecto() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/medicos/100",
                Collections.emptyMap(), null, new RequestTemplate());
        when(disponibilidadRepository.findAll()).thenReturn(List.of(disponibilidad));
        when(medicoClient.obtenerPorId(100L))
                .thenThrow(new FeignException.NotFound("Not Found", request, null, null));


        List<DisponibilidadResponseDTO> resultado = disponibilidadService.obtenerTodas();

        assertNotNull(resultado);
        assertEquals("Médico no disponible", resultado.get(0).getMedicoNombre());
    }



    @Test
    void testObtenerPorId_exitoso() {
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(disponibilidad));
        when(medicoClient.obtenerPorId(100L)).thenReturn(medicoResponseDTO);

        Optional<DisponibilidadResponseDTO> resultado = disponibilidadService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }

    @Test
    void testObtenerPorId_noExiste_devuelveOptionalVacio() {
        when(disponibilidadRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<DisponibilidadResponseDTO> resultado = disponibilidadService.obtenerPorId(99L);


        assertTrue(resultado.isEmpty());
    }



    @Test
    void testObtenerPorMedico() {
        when(disponibilidadRepository.findByMedicoId(100L)).thenReturn(List.of(disponibilidad));
        when(medicoClient.obtenerPorId(100L)).thenReturn(medicoResponseDTO);

        List<DisponibilidadResponseDTO> resultado = disponibilidadService.obtenerPorMedico(100L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }



    @Test
    void testGuardar_exitoso() {
        when(medicoClient.obtenerPorId(100L)).thenReturn(medicoResponseDTO);
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenReturn(disponibilidad);

        DisponibilidadResponseDTO resultado = disponibilidadService.guardar(requestDTO);


        assertNotNull(resultado);
        assertEquals("LUNES", resultado.getDiaSemana());
        assertEquals("Laura", resultado.getMedicoNombre());
        verify(disponibilidadRepository, times(1)).save(any(Disponibilidad.class));
    }

    @Test
    void testGuardar_medicoInactivo_lanzaExcepcion() {
        medicoResponseDTO.setActivo(false);
        when(medicoClient.obtenerPorId(100L)).thenReturn(medicoResponseDTO);


        assertThrows(RuntimeException.class, () -> disponibilidadService.guardar(requestDTO));
        verify(disponibilidadRepository, never()).save(any(Disponibilidad.class));
    }

    @Test
    void testGuardar_medicoNoExiste_lanzaExcepcion() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/medicos/100",
                Collections.emptyMap(), null, new RequestTemplate());
        when(medicoClient.obtenerPorId(100L))
                .thenThrow(new FeignException.NotFound("Not Found", request, null, null));

        assertThrows(RuntimeException.class, () -> disponibilidadService.guardar(requestDTO));
        verify(disponibilidadRepository, never()).save(any(Disponibilidad.class));
    }



    @Test
    void testActualizar_exitoso() {
        when(medicoClient.obtenerPorId(100L)).thenReturn(medicoResponseDTO);
        when(disponibilidadRepository.findById(1L)).thenReturn(Optional.of(disponibilidad));
        when(disponibilidadRepository.save(any(Disponibilidad.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<DisponibilidadResponseDTO> resultado = disponibilidadService.actualizar(1L, requestDTO);

        assertTrue(resultado.isPresent());
        assertEquals("LUNES", resultado.get().getDiaSemana());
    }

    @Test
    void testActualizar_noExiste_devuelveOptionalVacio() {
        when(medicoClient.obtenerPorId(100L)).thenReturn(medicoResponseDTO);
        when(disponibilidadRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<DisponibilidadResponseDTO> resultado = disponibilidadService.actualizar(99L, requestDTO);

        assertTrue(resultado.isEmpty());
        verify(disponibilidadRepository, never()).save(any(Disponibilidad.class));
    }



    @Test
    void testEliminar_exitoso() {
        disponibilidadService.eliminar(1L);

        verify(disponibilidadRepository, times(1)).deleteById(1L);
    }
}
