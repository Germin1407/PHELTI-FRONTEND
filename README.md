# PHELTI-FRONTEND

Interfaz de escritorio de PHELTI, una plataforma de intercambio de conocimientos, habilidades y servicios mediante phelts. Consume la API REST de PHELTI-APP.

## Tecnologías y estructura

Java 25, Spring Boot 4.1.1, Maven, Spring MVC, Thymeleaf, RestTemplate, HTML, CSS, JavaScript y Bootstrap 5. Los paquetes `configuration`, `Dtos`, `webservicesclients` y `vistas` contienen la configuración, contratos REST, clientes y controladores MVC. Los recursos se organizan en `templates` y `static`.

## Ejecución local

Inicia PHELTI-APP en el puerto 8080. Configura `PHELTI_BACKEND_URL` (por defecto `http://localhost:8080/api`) y `PHELTI_DEMO_KEY` con la misma clave privada del backend. Ejecuta `./mvnw spring-boot:run` o `.\mvnw.cmd spring-boot:run` en Windows. Abre `http://localhost:8081`.

## Uso

Selecciona una cuenta de demostración, configura el perfil y explora las publicaciones. Envía o acepta propuestas, acuerda el horario, consulta reservas y movimientos, confirma el intercambio cuando corresponda y califica la experiencia. La selección de cuentas sirve para la sustentación local y no es autenticación de identidad.

## Opciones predeterminadas

Las habilidades e intereses se seleccionan desde el perfil y las categorías desde las publicaciones. El directorio requiere un nombre o al menos un filtro; el muro requiere una palabra clave o al menos un filtro. Los selectores de búsqueda muestran «Selecciona» cuando no se ha elegido un criterio. El formulario de publicación pide una duración estimada; la fecha y la hora se eligen después de aceptar la propuesta.
