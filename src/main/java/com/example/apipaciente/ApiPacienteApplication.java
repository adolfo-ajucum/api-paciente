package com.example.apipaciente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiPacienteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiPacienteApplication.class, args);
		System.out.println("\n*****************************************");
		System.out.println("*** API de Pacientes Iniciada ***");
		System.out.println(" Accede desde: http://localhost:8080/pacientes");
		System.out.println("*****************************************");
	}

}
