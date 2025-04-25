package com.example.apipaciente.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/") // Mapea la ruta raíz
    public String mostrarInstrucciones() {
        // Bloque de texto con HTML actualizado
        return """
               <!DOCTYPE html>
               <html lang="es">
               <head>
                   <meta charset="UTF-8">
                   <meta name="viewport" content="width=device-width, initial-scale=1.0">
                   <title>API de Pacientes - Instrucciones</title>
                   <style>
                       body { font-family: sans-serif; line-height: 1.6; padding: 20px; }
                       h1 { color: #333; }
                       code { background-color: #f4f4f4; padding: 2px 6px; border-radius: 4px; font-family: monospace;}
                       ul { list-style: none; padding: 0; }
                       li { margin-bottom: 10px; }
                   </style>
               </head>
               <body>
                   <h1>API de Pacientes - Instrucciones de Uso</h1>
                   <p>Bienvenido/a a la API de ejemplo para gestionar pacientes.</p>
                   <p>Endpoints disponibles (bajo el subdominio/contexto de la API):</p>
                   <ul>
                       <li>
                           <strong>Listar Todos los Pacientes:</strong><br>
                           <code>GET /pacientes</code> <br>
                           Devuelve un array JSON con todos los pacientes registrados.
                       </li>
                       <li>
                           <strong>Obtener Paciente por Código:</strong><br>
                           <code>GET /pacientes/{codigo_paciente}</code> <br>
                           Reemplaza <code>{codigo_paciente}</code> con el código deseado (ej. <code>P001</code>). Devuelve un objeto JSON con los datos del paciente o un 404 si no se encuentra.
                       </li>
                       <li>
                           <strong>Buscar Paciente por DPI:</strong><br>
                           <code>GET /pacientes/buscar?dpi={numero_dpi}</code> <br>
                           Reemplaza <code>{numero_dpi}</code> con el DPI deseado (ej. <code>2900000000001</code>). Devuelve un objeto JSON con los datos del paciente o un 404 si no se encuentra.
                       </li>
                   </ul>
                   <p><em>Nota: La data inicial se carga desde un archivo JSON al iniciar.</em></p>
               </body>
               </html>
               """;
    }
}