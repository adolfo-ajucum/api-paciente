package com.example.apipaciente.controller;

import com.example.apipaciente.model.Paciente;
import com.example.apipaciente.service.PacienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // No usado si quitamos el método
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger; // Añadir import
import org.slf4j.LoggerFactory; // Añadir import
import java.util.Optional; // Añadir import (si no está)



import java.util.List; // No usado si quitamos el método

@RestController
@RequestMapping("/pacientes") // Ruta base sin /api
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // Dentro de la clase PacienteController
    private static final Logger log = LoggerFactory.getLogger(PacienteController.class); // Añadir logger

    @GetMapping("/buscar")
    public ResponseEntity<Paciente> buscarPacientePorDpi(@RequestParam String dpi) {
        // Log al entrar al método
        log.info(">>>>>> PacienteController: Petición recibida para buscar DPI: {}", dpi);
        Optional<Paciente> pacienteOpt = pacienteService.findByDpi(dpi);
        // Log después de llamar al servicio
        log.info("<<<<<< PacienteController: Resultado del servicio para DPI {}: Optional presente = {}", dpi, pacienteOpt.isPresent());
        return pacienteOpt
                .map(paciente -> {
                    log.info("<<<<<< PacienteController: Paciente encontrado, devolviendo 200 OK para DPI: {}", dpi);
                    return ResponseEntity.ok(paciente);
                })
                .orElseGet(() -> {
                    // Log si el Optional está vacío
                    log.warn("<<<<<< PacienteController: Paciente NO encontrado por servicio, devolviendo 404 Not Found para DPI: {}", dpi);
                    return ResponseEntity.notFound().build();
                });
    }

    // --- MÉTODOS DESHABILITADOS ---
    // Estos endpoints ya no funcionarán porque el servicio no los implementa
    // al depender ahora de la API Legacy que no parece tenerlos.

    /*
    // GET /pacientes -> Devuelve todos los pacientes
    @GetMapping
    public List<Paciente> obtenerTodosLosPacientes() {
        // return pacienteService.findAll(); // Método no implementado en servicio
         throw new UnsupportedOperationException("Listar todos los pacientes no está soportado por la API Legacy.");
    }
    */

    /*
    // GET /pacientes/{codigo} -> Devuelve un paciente por su código
    @GetMapping("/{codigo}")
    public ResponseEntity<Paciente> obtenerPacientePorCodigo(@PathVariable String codigo) {
        // return pacienteService.findByCodigo(codigo) // Método no implementado en servicio
        //        .map(ResponseEntity::ok)
        //        .orElseGet(() -> ResponseEntity.notFound().build());
         throw new UnsupportedOperationException("Buscar por código no está soportado por la API Legacy.");
    }
    */
}