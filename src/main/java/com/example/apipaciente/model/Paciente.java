package com.example.apipaciente.model;

import java.time.LocalDate;

public class Paciente {
    private String codigo;
    private String dpi;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String sexo;

    // Constructor vacío (necesario para Jackson/JSON)
    public Paciente() {}

    // Constructor con todos los campos (útil)
    public Paciente(String codigo, String dpi, String nombres, String apellidos, LocalDate fechaNacimiento, String sexo) {
        this.codigo = codigo;
        this.dpi = dpi;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
    }

    // Getters y Setters para todos los campos
    // (O puedes usar Lombok con @Data o @Getter/@Setter en la clase)
    // Ejemplo:
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    // ... (getters/setters para todos los demás campos: dpi, nombres, etc.) ...

    public String getDpi() { return dpi; }
    public void setDpi(String dpi) { this.dpi = dpi; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
}