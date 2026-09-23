-- Habilitar extensión para UUID y soporte de vectores
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Tabla de Farmacias
CREATE TABLE IF NOT EXISTS pharmacy (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    base_url VARCHAR(255) NOT NULL,
    logo_url VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla de Catálogo Maestro de Productos
CREATE TABLE IF NOT EXISTS master_product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    active_ingredient VARCHAR(255) NOT NULL,
    concentration VARCHAR(100),
    pharmaceutical_form VARCHAR(100),
    administration_route VARCHAR(100),
    brand VARCHAR(100),
    laboratory VARCHAR(100),
    health_registration VARCHAR(100),
    chm VARCHAR(50),
    presentation VARCHAR(255),
    quantity INTEGER,
    unit VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_master_product_active_ing ON master_product(active_ingredient);
CREATE INDEX idx_master_product_name ON master_product(name);
CREATE INDEX idx_master_product_brand ON master_product(brand);

-- 3. Tabla de Productos por Farmacia
CREATE TABLE IF NOT EXISTS pharmacy_product (
    id BIGSERIAL PRIMARY KEY,
    pharmacy_id BIGINT NOT NULL REFERENCES pharmacy(id) ON DELETE CASCADE,
    master_product_id BIGINT REFERENCES master_product(id) ON DELETE SET NULL,
    external_id VARCHAR(100) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    original_description TEXT,
    brand VARCHAR(100),
    presentation VARCHAR(255),
    url VARCHAR(500) NOT NULL,
    image_url VARCHAR(500),
    current_price DECIMAL(10, 2),
    current_offer_price DECIMAL(10, 2),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    content_hash VARCHAR(64),
    last_scraped_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_pharmacy_external_id UNIQUE(pharmacy_id, external_id)
);
CREATE INDEX idx_pharmacy_product_master ON pharmacy_product(master_product_id);
CREATE INDEX idx_pharmacy_product_name ON pharmacy_product(original_name);

-- 4. Histórico Inmutable de Precios
CREATE TABLE IF NOT EXISTS price_history (
    id BIGSERIAL PRIMARY KEY,
    pharmacy_product_id BIGINT NOT NULL REFERENCES pharmacy_product(id) ON DELETE CASCADE,
    price DECIMAL(10, 2) NOT NULL,
    offer_price DECIMAL(10, 2),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    checked_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_price_history_product_date ON price_history(pharmacy_product_id, checked_at DESC);

-- 5. Tabla de Referencia Regulatoria SRS (Superintendencia de Regulación Sanitaria)
CREATE TABLE IF NOT EXISTS srs_product (
    id BIGSERIAL PRIMARY KEY,
    master_product_id BIGINT REFERENCES master_product(id) ON DELETE SET NULL,
    health_registration VARCHAR(100) NOT NULL UNIQUE,
    chm VARCHAR(50),
    product_name VARCHAR(255) NOT NULL,
    active_ingredient VARCHAR(255) NOT NULL,
    concentration VARCHAR(100),
    pharmaceutical_form VARCHAR(100),
    presentation VARCHAR(255),
    laboratory VARCHAR(100),
    source_url VARCHAR(500),
    last_verified_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 6. Precios Regulados SRS (PVMP)
CREATE TABLE IF NOT EXISTS srs_price (
    id BIGSERIAL PRIMARY KEY,
    srs_product_id BIGINT NOT NULL REFERENCES srs_product(id) ON DELETE CASCADE,
    pvmp DECIMAL(10, 2) NOT NULL,
    pvmp_unit DECIMAL(10, 2),
    pvmp_presentation DECIMAL(10, 2),
    market_price DECIMAL(10, 2),
    pvmp_type VARCHAR(100),
    effective_date DATE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 7. Emparejamiento de Productos (Matching)
CREATE TABLE IF NOT EXISTS product_match (
    id BIGSERIAL PRIMARY KEY,
    pharmacy_product_id BIGINT NOT NULL REFERENCES pharmacy_product(id) ON DELETE CASCADE,
    master_product_id BIGINT NOT NULL REFERENCES master_product(id) ON DELETE CASCADE,
    match_score DECIMAL(5, 4) NOT NULL,
    match_method VARCHAR(50) NOT NULL, -- EXACT, EMBEDDING, AI, MANUAL
    match_status VARCHAR(50) NOT NULL DEFAULT 'CONFIRMED', -- PENDING, CONFIRMED, REJECTED
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 8. Auditoría de Ejecuciones del Scraper
CREATE TABLE IF NOT EXISTS scraping_execution (
    id BIGSERIAL PRIMARY KEY,
    pharmacy_id BIGINT REFERENCES pharmacy(id) ON DELETE SET NULL,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(50) NOT NULL, -- RUNNING, COMPLETED, FAILED, TIMEOUT
    records_found INTEGER NOT NULL DEFAULT 0,
    records_created INTEGER NOT NULL DEFAULT 0,
    records_updated INTEGER NOT NULL DEFAULT 0,
    records_failed INTEGER NOT NULL DEFAULT 0,
    error_message TEXT
);
CREATE INDEX idx_scraping_execution_date ON scraping_execution(started_at DESC);

-- 9. Auditoría y Registro de Procesamiento con IA
CREATE TABLE IF NOT EXISTS ai_processing_log (
    id BIGSERIAL PRIMARY KEY,
    pharmacy_product_id BIGINT REFERENCES pharmacy_product(id) ON DELETE SET NULL,
    provider VARCHAR(50) NOT NULL, -- GEMINI, OPENAI, HEURISTIC
    model VARCHAR(100) NOT NULL,
    prompt TEXT NOT NULL,
    response TEXT NOT NULL,
    confidence_score DECIMAL(5, 4),
    tokens_used INTEGER,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
