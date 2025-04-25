# API de Pacientes con Spring Boot 🏥

¡Hola! 👋 Este es un proyecto de ejemplo que implementa una API REST simple para gestionar información demográfica básica de pacientes, utilizando Java y Spring Boot.

## Descripción

La API permite consultar una lista de pacientes predefinida, buscar pacientes individuales por su código único o por su número de DPI. Una característica clave es que los datos iniciales de los pacientes se cargan desde un archivo `pacientes.json`, el cual puede ser proporcionado externamente al ejecutar la aplicación en un contenedor Docker, permitiendo flexibilidad en la gestión de los datos sin necesidad de reconstruir la imagen.

## Características Principales ✨

* Listar todos los pacientes registrados.
* Obtener los detalles de un paciente específico mediante su código.
* Buscar un paciente específico mediante su número de DPI.
* Carga de datos iniciales desde un archivo `pacientes.json`.
* Soporte para ejecución en contenedores Docker.
* Configuración para cargar el archivo JSON desde fuera del contenedor Docker.

## Tecnologías Utilizadas 🛠️

* **Lenguaje:** Java 22
* **Framework:** Spring Boot 3.x.x (Ajusta la versión según tu `pom.xml`)
* **Build Tool:** Maven
* **Contenerización:** Docker
* **Servidor Embebido:** Tomcat (incluido en Spring Boot Web)

## Prerrequisitos 📋

Para construir y ejecutar este proyecto localmente, necesitarás:

* JDK (Java Development Kit) versión 22 o superior.
* Apache Maven.
* Docker y Docker Desktop (Opcional, solo si quieres ejecutarlo en contenedor).
* Un editor de código o IDE (como IntelliJ IDEA, VS Code, Eclipse).
* Git (para clonar el repositorio).

## Cómo Empezar 🚀

### 1. Clonar el Repositorio

```bash
git clone <URL_DEL_REPOSITORIO_GITHUB>
cd api-paciente # O el nombre de tu directorio
2. Construir el Proyecto (con Maven)
Esto compilará el código y creará el archivo JAR ejecutable en el directorio target/.

Bash

./mvnw clean package
# o si no usas el wrapper y tienes Maven instalado globalmente:
# mvn clean package
3. Ejecutar Localmente (con Maven)
La forma más sencilla de ejecutar la aplicación localmente. Usará el archivo src/main/resources/pacientes.json por defecto.

Bash

./mvnw spring-boot:run
# o:
# mvn spring-boot:run
La API estará disponible en http://localhost:8080.

4. Ejecutar con Docker 🐳
Esto permite ejecutar la aplicación en un contenedor aislado y proporciona la flexibilidad de cargar un archivo pacientes.json externo.

a) Construir la Imagen Docker:

Asegúrate de estar en el directorio raíz del proyecto (donde está el Dockerfile) y ejecuta:

Bash

docker build -t mi-api-pacientes .
b) Preparar el JSON Externo:

Crea o copia tu archivo pacientes.json personalizado en un directorio accesible en tu máquina host (fuera del proyecto si quieres).

c) Ejecutar el Contenedor:

Abre una terminal CMD o PowerShell en Windows (Git Bash puede dar problemas al interpretar rutas y variables para Docker en Windows, como descubrimos).

Bash

# Detiene y elimina un contenedor anterior con el mismo nombre (si existe)
docker stop contenedor-pacientes || true
docker rm contenedor-pacientes || true

# Ejecuta el nuevo contenedor
docker run ^
  -p 8080:8080 ^
  --name contenedor-pacientes ^
  -e APP_PACIENTES_JSON_PATH="/app/data/pacientes.json" ^
  -v "/RUTA/ABSOLUTA/EN/TU/HOST/pacientes.json:/app/data/pacientes.json:ro" ^
  mi-api-pacientes

# NOTAS para el comando anterior:
# - Reemplaza "/RUTA/ABSOLUTA/EN/TU/HOST/pacientes.json" con la ruta real a tu archivo.
#   Ejemplos:
#     Linux/Mac: "/home/user/data/mi_pacientes.json"
#     Windows:   "/c/Users/Usuario/Documents/data/mi_pacientes.json" o "/d/Downloads/json/pacientes.json"
# - Usamos `^` (en CMD/PowerShell) para dividir el comando en varias líneas por legibilidad. En Linux/Mac usarías `\`.
# - Quitamos `-d` para ver los logs directamente. Añádelo si quieres que corra en segundo plano.
La API estará disponible en http://localhost:8080. Los logs de la aplicación te indicarán si cargó el archivo JSON desde la ruta externa (/app/data/pacientes.json) o desde el classpath interno.

Estructura del Proyecto 📁
.
├── .mvn/                   # Maven Wrapper
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ejemplo/apipacientes/ # Paquete base
│   │   │       ├── controller/          # Controladores REST (PacienteController)
│   │   │       ├── model/               # Clases de Modelo (Paciente)
│   │   │       ├── service/             # Lógica de negocio (PacienteService)
│   │   │       └── ApiPacientesApplication.java # Clase principal
│   │   └── resources/
│   │       ├── application.properties   # Configuración de Spring Boot
│   │       └── pacientes.json           # Datos de ejemplo (usado como fallback)
│   └── test/                    # Pruebas unitarias/integración
├── .gitignore
├── Dockerfile               # Instrucciones para construir la imagen Docker
├── mvnw                     # Maven Wrapper (Linux/Mac)
├── mvnw.cmd                 # Maven Wrapper (Windows)
├── pom.xml                  # Dependencias y configuración de Maven
└── README.md                # ¡Este archivo!
Endpoints de la API 🧭
La URL base es http://localhost:8080/api/pacientes

1. Listar Todos los Pacientes
Método: GET
Ruta: /api/pacientes
Respuesta Exitosa (200 OK): Un array JSON con todos los objetos Paciente.
JSON

[
  {
    "codigo": "P001",
    "dpi": "2900000000001",
    "nombres": "Ana María",
    "apellidos": "García López",
    "fechaNacimiento": "1990-05-15",
    "sexo": "Femenino"
  },
  {
    "codigo": "P002",
    // ... más pacientes
  }
]
2. Obtener Paciente por Código
Método: GET
Ruta: /api/pacientes/{codigo}
Ejemplo: /api/pacientes/P001
Respuesta Exitosa (200 OK): Un objeto JSON con los datos del paciente solicitado.
JSON

{
  "codigo": "P001",
  "dpi": "2900000000001",
  "nombres": "Ana María",
  "apellidos": "García López",
  "fechaNacimiento": "1990-05-15",
  "sexo": "Femenino"
}
Respuesta si no se encuentra (404 Not Found): Cuerpo vacío.
3. Buscar Paciente por DPI
Método: GET
Ruta: /api/pacientes/buscar?dpi={numero_dpi}
Ejemplo: /api/pacientes/buscar?dpi=2900000000001
Respuesta Exitosa (200 OK): Un objeto JSON con los datos del paciente solicitado. (Similar al de buscar por código).
Respuesta si no se encuentra (404 Not Found): Cuerpo vacío.
Configuración ⚙️
La aplicación está configurada para buscar un archivo pacientes.json externo usando la variable de entorno APP_PACIENTES_JSON_PATH.

Dentro del código (PacienteService), la anotación @Value("${app.pacientes.json.path:#{null}}") lee esta configuración.
Al ejecutar con Docker, debes pasar esta variable usando -e APP_PACIENTES_JSON_PATH="/ruta/interna/en/contenedor/pacientes.json". La ruta estándar interna es /app/data/pacientes.json.
Si la variable de entorno no se define o el archivo externo no se encuentra en la ruta especificada, la aplicación usará como fallback el archivo src/main/resources/pacientes.json incluido en el JAR.
Contribuciones
Por ahora, este es un proyecto de ejemplo, pero si tienes ideas o mejoras, ¡siéntete libre de abrir un Issue o un Pull Request!

Licencia 📄
Este proyecto puede ser utilizado libremente.