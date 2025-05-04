package com.example.apipaciente.service;

import com.example.apipaciente.model.LegacyPacienteDTO; // Importar DTO
import com.example.apipaciente.model.Paciente;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient; // Importar WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException; // Para errores HTTP
import reactor.core.publisher.Mono; // Importar Mono



import java.util.Optional;



@Service
public class PacienteService {

    private static final Logger log = LoggerFactory.getLogger(PacienteService.class);

    /*private final WebClient webClient;

    // Inyectamos la URL base desde application.properties
    @Value("${legacy.api.baseurl}")
    private String legacyApiBaseUrl;

    // Constructor para inicializar WebClient
    // Inyectamos WebClient.Builder que está auto-configurado por Spring Boot
    public PacienteService(WebClient.Builder webClientBuilder) {
        // Creamos una instancia de WebClient configurada para la API legacy
        // Es mejor práctica definir esto como un @Bean en una clase de Configuración,
        // pero para simplificar lo hacemos aquí.
        this.webClient = webClientBuilder.baseUrl(legacyApiBaseUrl).build();
        // NOTA: La baseUrl se inyectará después de la construcción,
        // por lo que es mejor configurar el webClient en un método @PostConstruct
        // o definirlo como un @Bean. Vamos a refinar esto:
    }*/

    // Alternativa/Mejora: Configurar WebClient después de la inyección de propiedades

    @Value("${legacy.api.baseurl}")
    private String legacyApiBaseUrl;

    private final WebClient webClient;

    // Inyecta el bean 'legacyApiWebClient' definido en WebClientConfig
    public PacienteService(WebClient legacyApiWebClient) {
        this.webClient = legacyApiWebClient;
        log.info("PacienteService inyectado con WebClient: {}", webClient);
    }



    // Ya no necesitamos cargar desde JSON
    // @PostConstruct
    // public void cargarDatosDesdeJson() { ... }

    /**
     * Formatea un DPI de 13 dígitos sin espacios al formato "XXXX XXXXX XXXX".
     * @param dpiSinEspacios DPI de 13 dígitos.
     * @return DPI formateado o null si la entrada es inválida.
     */
    private String formatDpiConEspacios(String dpiSinEspacios) {
        if (dpiSinEspacios == null || dpiSinEspacios.length() != 13 || !dpiSinEspacios.matches("\\d+")) {
            log.warn("Intento de formatear DPI inválido: {}", dpiSinEspacios);
            return null; // O lanzar excepción
        }
        // 3593 74514 0801
        return dpiSinEspacios.substring(0, 4) + " " +
                dpiSinEspacios.substring(4, 9) + " " +
                dpiSinEspacios.substring(9, 13);
    }

    // Método findByDpi modificado para llamar a la API legacy
    public Optional<Paciente> findByDpi(String dpiSinEspacios) {
        if (dpiSinEspacios == null || dpiSinEspacios.trim().isEmpty()) {
            return Optional.empty();
        }

        String cuiConEspacios = formatDpiConEspacios(dpiSinEspacios);
        if (cuiConEspacios == null) {
            return Optional.empty(); // DPI inválido para formatear
        }

        log.info("Buscando en API legacy con CUI formateado: {}", cuiConEspacios);

        try {
            // Hacemos la llamada a la API legacy
            // La API legacy devuelve un array, incluso para búsqueda por CUI único
            LegacyPacienteDTO[] legacyResponseArray = webClient.get()
                    .uri("/api/BusquedaLegacy/avanzado/{cui}", cuiConEspacios) // Construye la URL relativa
                    .retrieve() // Ejecuta la petición
                    .bodyToMono(LegacyPacienteDTO[].class) // Espera un array de DTOs
                    .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                        log.warn("API Legacy no encontró CUI: {}", cuiConEspacios);
                        return Mono.just(new LegacyPacienteDTO[0]); // Devuelve array vacío en 404
                    })
                    .onErrorResume(WebClientResponseException.class, ex -> {
                        log.error("Error {} de API Legacy al buscar CUI {}: {}", ex.getStatusCode(), cuiConEspacios, ex.getResponseBodyAsString());
                        return Mono.error(new RuntimeException("Error al llamar API Legacy: " + ex.getStatusCode())); // Propaga otros errores HTTP
                    })
                    .onErrorResume(Exception.class, ex -> {
                        log.error("Error inesperado al llamar API Legacy para CUI {}: {}", cuiConEspacios, ex.getMessage());
                        return Mono.error(new RuntimeException("Error inesperado llamando API Legacy", ex)); // Propaga errores generales
                    })
                    .block(); // Espera el resultado (bloqueante)
            // En una aplicación completamente reactiva, evitaríamos .block()

            if (legacyResponseArray != null && legacyResponseArray.length > 0) {
                // Asumimos que el primero es el correcto para una búsqueda por CUI
                LegacyPacienteDTO legacyDto = legacyResponseArray[0];
                log.info("Respuesta recibida de API legacy para {}: {}", cuiConEspacios, legacyDto);

                // Mapeamos la respuesta DTO a nuestro modelo Paciente
                Paciente paciente = new Paciente();
                paciente.setCodigo(String.valueOf(legacyDto.getCodigo()));
                paciente.setDpi(dpiSinEspacios); // Guardamos el DPI original sin espacios
                paciente.setNombres(legacyDto.getNombres());
                paciente.setApellidos(legacyDto.getApellidos());
                paciente.setEdad(legacyDto.getEdad());
                // Campos no disponibles en la API legacy quedan null por defecto:
                // paciente.setFechaNacimiento(null);
                // paciente.setSexo(null);
                // Decidir qué hacer con 'codigo'. ¿Usamos historiaClinica o lo dejamos null?
                // paciente.setCodigo(legacyDto.getHistoriaClinica());
              //  paciente.setCodigo(null); // O dejarlo null

                return Optional.of(paciente);
            } else {
                // No se encontró en la API legacy (o hubo un 404 manejado)
                log.info("No se encontró paciente en API legacy para CUI {}", cuiConEspacios);
                return Optional.empty();
            }

        } catch (Exception e) {
            // Captura errores propagados por onErrorResume o el .block()
            log.error("Excepción final al procesar búsqueda para CUI {}: {}", cuiConEspacios, e.getMessage());
            return Optional.empty();
        }
    }

    // Estos métodos ya no funcionarán porque no tenemos la lista local
    /*
    public List<Paciente> findAll() {
        // Necesitaría un endpoint en la API legacy para listar todos
        log.warn("findAll() no implementado con API Legacy.");
        return Collections.emptyList();
    }

    public Optional<Paciente> findByCodigo(String codigo) {
        // Necesitaría un endpoint en la API legacy para buscar por 'codigo' o 'historiaClinica'
         log.warn("findByCodigo() no implementado con API Legacy.");
        return Optional.empty();
    }
    */
}