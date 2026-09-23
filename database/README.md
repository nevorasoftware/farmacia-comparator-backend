# 🗄️ Guía de Base de Datos PostgreSQL en Railway

Esta carpeta contiene todos los scripts necesarios para inicializar y gestionar la base de datos PostgreSQL del **Comparador de Medicamentos — El Salvador** tanto de forma manual como automática.

---

## 📄 Archivos Disponibles

1. **[`railway_full_init.sql`](./railway_full_init.sql)**:
   - **Script consolidado todo-en-uno**.
   - Contiene la creación de tablas, índices, llaves foráneas y datos semilla con farmacias reales (San Nicolás, CEFAFA, Camila, Económicas), medicamentos populares de El Salvador, precios de mercado y referencias oficiales de la **Superintendencia de Regulación Sanitaria (SRS / PVMP)**.
   - Es idempotente (`IF NOT EXISTS` y `ON CONFLICT DO UPDATE`), por lo que puedes ejecutarlo múltiples veces sin errores.

2. **[`migrations/V1__init_schema.sql`](./migrations/V1__init_schema.sql)**:
   - Esquema DDL para Flyway.
3. **[`seeds/V2__seed_initial_data.sql`](./seeds/V2__seed_initial_data.sql)**:
   - Datos semilla para Flyway.

---

## 🚀 Cómo crear y cargar la base de datos en Railway

### Opción A: Ejecución desde el Query Editor de Railway (Más Rápido y Fácil)

1. En tu panel de [Railway.app](https://railway.app/), crea un nuevo servicio: **New** -> **Database** -> **Add PostgreSQL**.
2. Una vez aprovisionada, haz clic en el servicio de PostgreSQL y ve a la pestaña **"Data"** o **"Query"**.
3. Abre el archivo [`railway_full_init.sql`](./railway_full_init.sql), copia todo su contenido y pégalo en el editor de consultas.
4. Presiona **Run Query**. ¡Listo! Todas las tablas, relaciones y datos iniciales quedarán creados.

---

### Opción B: Ejecución mediante línea de comandos (psql)

Copia la variable `DATABASE_URL` desde la pestaña **Variables** o **Connect** de tu servicio PostgreSQL en Railway y ejecuta en tu terminal:

```bash
psql "postgresql://postgres:PASSWORD@HOST:PORT/railway" -f database/railway_full_init.sql
```

---

### Opción C: Migración Automática con Flyway (Spring Boot)

Si conectas el **backend** a la base de datos de Railway configurando las variables de entorno en el servicio del backend:
- `SPRING_DATASOURCE_URL`: `jdbc:postgresql://HOST:PORT/railway`
- `SPRING_DATASOURCE_USERNAME`: `postgres`
- `SPRING_DATASOURCE_PASSWORD`: `<tu-contraseña-railway>`

Spring Boot ejecutará automáticamente las migraciones `V1` y `V2` al arrancar el contenedor.

---

## 📊 Tablas Creadas

- `pharmacy`: Farmacias monitoreadas (San Nicolás, CEFAFA, Camila, Económicas).
- `master_product`: Catálogo unificado de medicamentos (principios activos, concentraciones, forma farmacéutica, laboratorios).
- `pharmacy_product`: Precios actuales, links directos y disponibilidad por farmacia.
- `price_history`: Registro histórico inmutable de precios para generar gráficas de tendencia.
- `srs_product`: Registro sanitario y código CHM de la Superintendencia de Regulación Sanitaria.
- `srs_price`: Precios Máximos de Venta al Público (PVMP) y precios de mercado regulados.
- `product_match`: Auditoría de emparejamientos mediante reglas e IA (Gemini).
- `scraping_execution`: Auditoría y métricas de scrapers (tiempos, registros creados/fallidos).
- `ai_processing_log`: Auditoría de consumo de tokens y costos de IA.
