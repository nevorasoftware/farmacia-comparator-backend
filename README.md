# farmacia-comparator-backend

Microservicio de API REST para el Comparador de Precios de Medicamentos en El Salvador.

## Tecnologías
- Java 17 LTS
- Spring Boot 3.3.x
- Spring Data JPA
- PostgreSQL & Flyway
- Spring Security (preparado con roles y CORS)
- OpenAPI / Swagger UI (springdoc)
- Maven

## Variables de Entorno
- `SPRING_DATASOURCE_URL`: URL JDBC de PostgreSQL (ej. `jdbc:postgresql://localhost:5432/farmacia_db`)
- `SPRING_DATASOURCE_USERNAME`: Usuario de BD
- `SPRING_DATASOURCE_PASSWORD`: Contraseña de BD
- `SERVER_PORT`: Puerto HTTP (default: 8080)

## Ejecución Local
```bash
mvn clean spring-boot:run
```
Swagger UI disponible en: `http://localhost:8080/swagger-ui.html`
