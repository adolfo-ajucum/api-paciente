package com.example.apipaciente.model;

// Usaremos Lombok para brevedad, o puedes generar getters/setters manualmente
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok: genera getters, setters, toString, etc.
@NoArgsConstructor // Lombok: genera constructor sin argumentos (necesario para Jackson)
public class LegacyPacienteDTO {

    // Mapean directamente si los nombres coinciden (ignorando mayúsculas/minúsculas a veces)
    private int codigo;
    private int persona;
    private String nombres;
    private String apellidos;

    // Usa @JsonProperty si el nombre del campo Java difiere del JSON
    @JsonProperty("historia_Clinica") // Mapea historia_Clinica del JSON a historiaClinica
    private String historiaClinica;

    private String edad; // La API legacy devuelve edad como String
    private String padre;
    private String madre;
    private String procedencia;

    @JsonProperty("archivo_Fisico") // Mapea archivo_Fisico a archivoFisico
    private boolean archivoFisico;

    // Si no usas Lombok, añade getters y setters manualmente para todos los campos.
}