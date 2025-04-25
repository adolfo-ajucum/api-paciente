package com.example.apipaciente.service;

import com.example.apipaciente.model.Paciente;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; // Importar @Value
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource; // Importar Resource
import org.springframework.core.io.UrlResource; // Importar UrlResource
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Paths; // Importar Paths
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    private static final Logger log = LoggerFactory.getLogger(PacienteService.class);
    private List<Paciente> listaPacientes = new ArrayList<>();
    private final ObjectMapper objectMapper;

    // Inyecta el valor de la propiedad 'app.pacientes.json.path'
    // Si no se define, no asignará nada (null o vacío dependiendo del tipo)
    @Value("${app.pacientes.json.path:#{null}}") // Usamos SpEL para default null si no existe
    private String externalJsonPath;

    public PacienteService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        if (!this.objectMapper.getRegisteredModuleIds().contains(JavaTimeModule.class.getName())) {
            this.objectMapper.registerModule(new JavaTimeModule());
        }
    }

    @PostConstruct
    public void cargarDatosDesdeJson() {
        Resource resource = null;
        boolean loadedFromExternal = false;

        // 1. Intentar cargar desde la ruta externa si está definida
        if (externalJsonPath != null && !externalJsonPath.trim().isEmpty()) {
            try {
                log.info("Intentando cargar pacientes desde ruta externa: {}", externalJsonPath);
                // Usamos UrlResource para manejar prefijos como file: o classpath:
                // o simplemente rutas de sistema de archivos. Paths.get asegura que sea una ruta de archivo válida.
                resource = new UrlResource(Paths.get(externalJsonPath).toUri());
                if (resource.exists() && resource.isReadable()) {
                    log.info("Archivo JSON externo encontrado en: {}", externalJsonPath);
                    loadedFromExternal = true;
                } else {
                    log.warn("Archivo JSON externo NO encontrado o no legible en: {}", externalJsonPath);
                    resource = null; // Resetear para intentar fallback
                }
            } catch (MalformedURLException e) {
                log.error("Ruta externa mal formada '{}': {}", externalJsonPath, e.getMessage());
                resource = null; // Resetear para intentar fallback
            } catch (Exception e) { // Captura más amplia por si acaso (permisos, etc.)
                log.error("Error inesperado al acceder a la ruta externa '{}': {}", externalJsonPath, e.getMessage());
                resource = null; // Resetear para intentar fallback
            }
        } else {
            log.info("No se especificó ruta externa (app.pacientes.json.path), intentando fallback a classpath.");
        }


        // 2. Si no se cargó desde externo, intentar cargar desde el classpath (fallback)
        if (resource == null) {
            try {
                log.info("Intentando cargar pacientes desde classpath: pacientes.json");
                resource = new ClassPathResource("pacientes.json");
                if (!resource.exists()) {
                    log.error("Fallback fallido: Archivo pacientes.json NO encontrado en el classpath.");
                    this.listaPacientes = Collections.emptyList(); // No hay datos que cargar
                    return; // Salir si no hay archivo interno tampoco
                }
                log.info("Archivo JSON interno (classpath) encontrado.");
            } catch (Exception e) {
                log.error("Error al intentar acceder al recurso del classpath pacientes.json: {}", e.getMessage());
                this.listaPacientes = Collections.emptyList();
                return;
            }
        }

        // 3. Parsear el JSON desde el recurso seleccionado (externo o interno)
        try (InputStream inputStream = resource.getInputStream()) {
            this.listaPacientes = objectMapper.readValue(inputStream, new TypeReference<List<Paciente>>() {});
            String source = loadedFromExternal ? externalJsonPath : "classpath:pacientes.json";
            log.info(">>> {} pacientes cargados correctamente desde {}", listaPacientes.size(), source);

            if (!listaPacientes.isEmpty()) {
                log.info(">>> DPI de ejemplo para buscar: {}", listaPacientes.get(0).getDpi());
            }

        } catch (FileNotFoundException e) { // Específico por si isReadable falló sutilmente
            log.error("Error: Archivo de recurso no encontrado al intentar leer: {}", resource.getDescription());
            this.listaPacientes = Collections.emptyList();
        }
        catch (Exception e) {
            log.error("!!! Error al parsear JSON desde {}: {}", resource.getDescription(), e.getMessage(), e);
            this.listaPacientes = Collections.emptyList();
        }
    }

    // --- Métodos findAll, findByCodigo, findByDpi (sin cambios) ---
    public List<Paciente> findAll() {
        return listaPacientes;
    }

    public Optional<Paciente> findByCodigo(String codigo) {
        return listaPacientes.stream()
                .filter(p -> p.getCodigo().equalsIgnoreCase(codigo))
                .findFirst();
    }

    public Optional<Paciente> findByDpi(String dpi) {
        if (dpi == null || dpi.trim().isEmpty()) {
            return Optional.empty();
        }
        return listaPacientes.stream()
                .filter(p -> p.getDpi() != null && p.getDpi().equals(dpi))
                .findFirst();
    }
}