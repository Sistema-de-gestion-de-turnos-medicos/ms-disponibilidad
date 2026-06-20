package com.universidad.msdisponibilidad.config;

import com.universidad.msdisponibilidad.model.Disponibilidad;
import com.universidad.msdisponibilidad.repository.DisponibilidadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DisponibilidadRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            log.info(">>> Disponibilidades ya cargadas. Se omite inicialización.");
            return;
        }
        log.info(">>> Cargando disponibilidades iniciales...");

        repository.save(new Disponibilidad(null, 1L, "LUNES",     "08:00", "12:00", 30, true));
        repository.save(new Disponibilidad(null, 1L, "MIERCOLES", "08:00", "12:00", 30, true));


        repository.save(new Disponibilidad(null, 2L, "MARTES",   "09:00", "13:00", 20, true));
        repository.save(new Disponibilidad(null, 2L, "JUEVES",   "09:00", "13:00", 20, true));


        repository.save(new Disponibilidad(null, 3L, "LUNES",    "14:00", "18:00", 60, true));
        repository.save(new Disponibilidad(null, 3L, "VIERNES",  "14:00", "18:00", 60, true));


        repository.save(new Disponibilidad(null, 4L, "MIERCOLES","08:00", "11:00", 15, true));
        repository.save(new Disponibilidad(null, 4L, "VIERNES",  "08:00", "11:00", 15, false));
        log.info(">>> 8 disponibilidades cargadas OK.");
    }
}
