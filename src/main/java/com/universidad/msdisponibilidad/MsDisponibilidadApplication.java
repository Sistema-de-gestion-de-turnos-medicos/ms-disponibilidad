package com.universidad.msdisponibilidad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class MsDisponibilidadApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsDisponibilidadApplication.class, args);
    }
}
