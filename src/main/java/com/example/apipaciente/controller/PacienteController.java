package com.example.apipaciente.controller;

import com.example.apipaciente.model.Paciente;
import com.example.apipaciente.service.PacienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pacientes") // Ruta base para la API
public class PacienteController {

    private final PacienteService pacienteService;

    // Inyección de dependencias vía constructor
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // Endpoint: GET /api/pacientes -> Devuelve todos los pacientes
    @GetMapping
    public List<Paciente> obtenerTodosLosPacientes() {
        return pacienteService.findAll();
    }

    // Endpoint: GET /api/pacientes/{codigo} -> Devuelve un paciente por su código
    @GetMapping("/{codigo}")
    public ResponseEntity<Paciente> obtenerPacientePorCodigo(@PathVariable String codigo) {
        return pacienteService.findByCodigo(codigo)
                .map(ResponseEntity::ok) // Si lo encuentra -> 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // Si no -> 404 Not Found
    }

    // Endpoint: GET /api/pacientes/buscar?dpi=... -> Devuelve un paciente por su DPI
    @GetMapping("/buscar")
    public ResponseEntity<Paciente> buscarPacientePorDpi(@RequestParam String dpi) {
        return pacienteService.findByDpi(dpi)
                .map(ResponseEntity::ok) // Si lo encuentra -> 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // Si no -> 404 Not Found
    }
}