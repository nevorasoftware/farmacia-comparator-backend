-- ============================================================================
-- PROYECTO: COMPARADOR DE PRECIOS DE MEDICAMENTOS — EL SALVADOR
-- SCRIPT COMPLETO DE INICIALIZACIÓN PARA POSTGRESQL EN RAILWAY
-- Tablas, Índices, Restricciones y Datos Semilla Iniciales
-- ============================================================================

-- Habilitar extensión para UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ----------------------------------------------------------------------------
-- 1. TABLA DE FARMACIAS
-- ----------------------------------------------------------------------------
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

-- ----------------------------------------------------------------------------
-- 2. TABLA DE CATÁLOGO MAESTRO DE MEDICAMENTOS UNIFICADO
-- ----------------------------------------------------------------------------
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

CREATE INDEX IF NOT EXISTS idx_master_product_active_ing ON master_product(active_ingredient);
CREATE INDEX IF NOT EXISTS idx_master_product_name ON master_product(name);
CREATE INDEX IF NOT EXISTS idx_master_product_brand ON master_product(brand);

-- ----------------------------------------------------------------------------
-- 3. TABLA DE PRODUCTOS MAPEADOS POR FARMACIA
-- ----------------------------------------------------------------------------
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

CREATE INDEX IF NOT EXISTS idx_pharmacy_product_master ON pharmacy_product(master_product_id);
CREATE INDEX IF NOT EXISTS idx_pharmacy_product_name ON pharmacy_product(original_name);

-- ----------------------------------------------------------------------------
-- 4. HISTÓRICO INMUTABLE DE PRECIOS (PARA GRÁFICAS DE TENDENCIAS)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS price_history (
    id BIGSERIAL PRIMARY KEY,
    pharmacy_product_id BIGINT NOT NULL REFERENCES pharmacy_product(id) ON DELETE CASCADE,
    price DECIMAL(10, 2) NOT NULL,
    offer_price DECIMAL(10, 2),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    checked_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_price_history_product_date ON price_history(pharmacy_product_id, checked_at DESC);

-- ----------------------------------------------------------------------------
-- 5. REFERENCIAS REGULATORIAS SRS (SUPERINTENDENCIA DE REGULACIÓN SANITARIA)
-- ----------------------------------------------------------------------------
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

-- ----------------------------------------------------------------------------
-- 6. PRECIOS REGULADOS SRS — PVMP (PRECIO MÁXIMO DE VENTA AL PÚBLICO)
-- ----------------------------------------------------------------------------
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

-- ----------------------------------------------------------------------------
-- 7. AUDITORÍA DE EMPAREJAMIENTO DE PRODUCTOS (NORMALIZACIÓN IA / EXACTA)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product_match (
    id BIGSERIAL PRIMARY KEY,
    pharmacy_product_id BIGINT NOT NULL REFERENCES pharmacy_product(id) ON DELETE CASCADE,
    master_product_id BIGINT NOT NULL REFERENCES master_product(id) ON DELETE CASCADE,
    match_score DECIMAL(5, 4) NOT NULL DEFAULT 1.0000,
    match_method VARCHAR(50) NOT NULL DEFAULT 'EXACT',
    match_status VARCHAR(50) NOT NULL DEFAULT 'CONFIRMED',
    reviewed_by VARCHAR(100),
    reviewed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 8. AUDITORÍA DE EJECUCIONES DE SCRAPING
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS scraping_execution (
    id BIGSERIAL PRIMARY KEY,
    pharmacy_id BIGINT REFERENCES pharmacy(id) ON DELETE SET NULL,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(50) NOT NULL,
    records_found INTEGER NOT NULL DEFAULT 0,
    records_created INTEGER NOT NULL DEFAULT 0,
    records_updated INTEGER NOT NULL DEFAULT 0,
    records_failed INTEGER NOT NULL DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 9. AUDITORÍA DE CONSUMO Y COSTOS DE IA (GEMINI API)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ai_processing_log (
    id BIGSERIAL PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    operation VARCHAR(100) NOT NULL,
    input_text TEXT,
    output_text TEXT,
    content_hash VARCHAR(64) NOT NULL,
    tokens_prompt INTEGER,
    tokens_response INTEGER,
    cost_usd DECIMAL(8, 6),
    latency_ms BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_ai_hash ON ai_processing_log(content_hash);

-- ============================================================================
-- DATOS SEMILLA (SEEDS) CON PRODUCTOS Y PRECIOS REALES DE EL SALVADOR
-- ============================================================================

-- Farmacias
INSERT INTO pharmacy (id, name, code, base_url, logo_url, is_active) VALUES
(1, 'Farmacias San Nicolás', 'SAN_NICOLAS', 'https://www.farmaciasannicolas.com', 'https://www.farmaciasannicolas.com/assets/img/logo.png', TRUE),
(2, 'Farmacias CEFAFA', 'CEFAFA', 'https://www.farmaciascefafa.com.sv', 'https://www.farmaciascefafa.com.sv/assets/img/logo.png', TRUE),
(3, 'Farmacias Camila', 'CAMILA', 'https://www.farmaciascamila.com', 'https://www.farmaciascamila.com/img/logo.png', TRUE),
(4, 'Farmacias Económicas', 'ECONOMICAS', 'https://www.farmaciaseconomicaselsalvador.com/PROD/ECOMMERCE/', 'https://www.farmaciaseconomicaselsalvador.com/logo.png', TRUE)
ON CONFLICT (id) DO UPDATE SET 
    name = EXCLUDED.name, 
    base_url = EXCLUDED.base_url, 
    is_active = EXCLUDED.is_active;

SELECT setval('pharmacy_id_seq', COALESCE((SELECT MAX(id) FROM pharmacy), 1));

-- Catálogo Maestro de Medicamentos Populares
INSERT INTO master_product (id, name, active_ingredient, concentration, pharmaceutical_form, administration_route, brand, laboratory, health_registration, chm, presentation, quantity, unit) VALUES
(1, 'Acetaminofén 500 mg', 'Acetaminofén', '500 mg', 'Tableta', 'Oral', 'MK', 'Tecnoquímicas', 'F012345678', 'CHM-0012', 'Caja x 20 tabletas', 20, 'tableta'),
(2, 'Acetaminofén 120 mg/5 mL Jarabe', 'Acetaminofén', '120 mg/5 mL', 'Jarabe', 'Oral', 'MK', 'Tecnoquímicas', 'F012345679', 'CHM-0013', 'Frasco x 60 mL', 1, 'frasco'),
(3, 'Ibuprofeno 400 mg', 'Ibuprofeno', '400 mg', 'Cápsula blanda', 'Oral', 'Advil', 'Pfizer / Haleon', 'F023456789', 'CHM-0045', 'Caja x 10 cápsulas', 10, 'cápsula'),
(4, 'Ibuprofeno 800 mg', 'Ibuprofeno', '800 mg', 'Tableta', 'Oral', 'MK', 'Tecnoquímicas', 'F023456790', 'CHM-0046', 'Caja x 20 tabletas', 20, 'tableta'),
(5, 'Loratadina 10 mg', 'Loratadina', '10 mg', 'Tableta', 'Oral', 'Claritin', 'Bayer', 'F034567890', 'CHM-0089', 'Caja x 10 tabletas', 10, 'tableta'),
(6, 'Amoxicilina 500 mg', 'Amoxicilina', '500 mg', 'Cápsula', 'Oral', 'Amoxil', 'GSK', 'F045678901', 'CHM-0112', 'Caja x 15 cápsulas', 15, 'cápsula'),
(7, 'Metformina 850 mg', 'Metformina', '850 mg', 'Tableta', 'Oral', 'Glucophage', 'Merck', 'F056789012', 'CHM-0155', 'Caja x 30 tabletas', 30, 'tableta'),
(8, 'Losartán Potásico 50 mg', 'Losartán Potásico', '50 mg', 'Tableta', 'Oral', 'Cozaar', 'Organon', 'F067890123', 'CHM-0188', 'Caja x 30 tabletas', 30, 'tableta')
ON CONFLICT (id) DO UPDATE SET 
    name = EXCLUDED.name, 
    active_ingredient = EXCLUDED.active_ingredient, 
    concentration = EXCLUDED.concentration;

SELECT setval('master_product_id_seq', COALESCE((SELECT MAX(id) FROM master_product), 1));

-- Productos de Farmacias con Precios de Referencia Real
INSERT INTO pharmacy_product (id, pharmacy_id, master_product_id, external_id, original_name, brand, presentation, url, image_url, current_price, current_offer_price, is_available, content_hash) VALUES
-- Acetaminofén 500 mg
(1, 2, 1, '1167', 'ACETAMINOFEN 500 MG X 100 TAB.', 'LA SANTE', 'Caja x 100 tabletas', 'https://portal.farmaciascefafa.com.sv/producto/acetaminofen-500-mg-x-100-tab', 'https://portal.farmaciascefafa.com.sv/api-ecommerce/api/imagenes/12ab7b33-c5ad', 0.80, 0.72, TRUE, 'hash_cefafa_1167'),
(2, 4, 1, '00023399', 'Acetaminofen 500mg Ecomed X 10 Tabletas', 'Ecomed', 'Caja x 10 tabletas', 'https://www.farmaciaseconomicaselsalvador.com/PROD/ECOMMERCE/Home/Buscar?termino=acetaminofen', 'https://www.farmaciaseconomicaselsalvador.com/PROD/SERV_ADMIN_FILES/Archivos/Imagenes/Articulos_PEQ/00023399 (1)_PEQ.jpg', 0.55, NULL, TRUE, 'hash_eco_00023399'),
(3, 1, 1, 'B0001001', 'Acetaminofén MK 500 mg Caja con 20 Tabletas', 'MK', 'Caja x 20 tabletas', 'https://www.farmaciasannicolas.com/producto/acetaminofen-mk-500mg/B0001001', 'https://www.farmaciasannicolas.com/api/fsn/multimedia/1982589c/content', 1.85, 1.65, TRUE, 'hash_sn_1001'),
(4, 3, 1, 'CAM-0101', 'Acetaminofén 500 mg Genérico Caja x 20 Tabletas', 'Genérico', 'Caja x 20 tabletas', 'https://www.farmaciascamila.com/productos.html', 'https://www.farmaciascamila.com/img/producto.jpg', 1.50, NULL, TRUE, 'hash_cam_0101'),

-- Acetaminofén Jarabe
(5, 2, 2, '147', 'ACETAMINOFEN MK 120 MG/ 5 ML JARABE (X)', 'MK', 'CAJA CON FRASCO X 60 ML', 'https://portal.farmaciascefafa.com.sv/producto/acetaminofen-mk-120-mg-5-ml-jarabe-x', 'https://portal.farmaciascefafa.com.sv/api-ecommerce/api/imagenes/23dae633-d969', 3.85, 3.47, TRUE, 'hash_cefafa_147'),
(6, 1, 2, 'B0001002', 'Acetaminofén Jarabe MK 120 mg/5 mL Frasco 60 mL', 'MK', 'Frasco x 60 mL', 'https://www.farmaciasannicolas.com/producto/acetaminofen-jarabe-mk/B0001002', 'https://www.farmaciasannicolas.com/api/fsn/multimedia/5c4a161f/content', 3.90, NULL, TRUE, 'hash_sn_1002'),
(7, 4, 2, '00023400', 'Acetaminofen Jarabe Infantil 120mg Frasco 60ml', 'Ecomed', 'Frasco x 60 mL', 'https://www.farmaciaseconomicaselsalvador.com/PROD/ECOMMERCE/Home/Buscar?termino=acetaminofen', 'https://www.farmaciaseconomicaselsalvador.com/PROD/SERV_ADMIN_FILES/Archivos/Imagenes/Articulos_PEQ/00023400_PEQ.jpg', 2.75, 2.50, TRUE, 'hash_eco_00023400'),

-- Ibuprofeno 400 mg
(8, 1, 3, 'B0002001', 'Advil Max 400 mg Caja con 10 Cápsulas Líquidas', 'Advil', 'Caja x 10 cápsulas', 'https://www.farmaciasannicolas.com/producto/advil-max-400mg/B0002001', 'https://www.farmaciasannicolas.com/api/fsn/multimedia/83cfd9a9/content', 4.50, 3.85, TRUE, 'hash_sn_2001'),
(9, 2, 3, '501', 'ADVIL 400 MG CAJA X 10 CAPSULAS BLANDAS', 'Advil', 'Caja x 10 cápsulas', 'https://portal.farmaciascefafa.com.sv/producto/advil-400-mg-caja-x-10-capsulas', 'https://portal.farmaciascefafa.com.sv/api-ecommerce/api/imagenes/advil-img', 4.35, 3.90, TRUE, 'hash_cefafa_501'),
(10, 4, 3, '00023555', 'Ibuprofeno 400mg Ecomed Caja x 10 Capsulas', 'Ecomed', 'Caja x 10 cápsulas', 'https://www.farmaciaseconomicaselsalvador.com/PROD/ECOMMERCE/Home/Buscar?termino=ibuprofeno', 'https://www.farmaciaseconomicaselsalvador.com/PROD/SERV_ADMIN_FILES/Archivos/Imagenes/Articulos_PEQ/ibu_PEQ.jpg', 2.20, NULL, TRUE, 'hash_eco_ibu400'),
(11, 3, 3, 'CAM-0201', 'Ibuprofeno 400 mg Genérico Caja x 10 Tabletas', 'Genérico', 'Caja x 10 tabletas', 'https://www.farmaciascamila.com/productos.html', 'https://www.farmaciascamila.com/img/producto.jpg', 2.10, NULL, TRUE, 'hash_cam_0201')
ON CONFLICT (id) DO UPDATE SET 
    current_price = EXCLUDED.current_price, 
    current_offer_price = EXCLUDED.current_offer_price;

SELECT setval('pharmacy_product_id_seq', COALESCE((SELECT MAX(id) FROM pharmacy_product), 1));

-- Histórico de Precios
INSERT INTO price_history (pharmacy_product_id, price, offer_price, is_available, checked_at) VALUES
(1, 0.85, NULL, TRUE, CURRENT_TIMESTAMP - INTERVAL '30 days'),
(1, 0.80, 0.72, TRUE, CURRENT_TIMESTAMP - INTERVAL '7 days'),
(1, 0.80, 0.72, TRUE, CURRENT_TIMESTAMP),

(2, 0.60, NULL, TRUE, CURRENT_TIMESTAMP - INTERVAL '30 days'),
(2, 0.55, NULL, TRUE, CURRENT_TIMESTAMP - INTERVAL '15 days'),
(2, 0.55, NULL, TRUE, CURRENT_TIMESTAMP),

(3, 1.95, NULL, TRUE, CURRENT_TIMESTAMP - INTERVAL '30 days'),
(3, 1.85, 1.65, TRUE, CURRENT_TIMESTAMP - INTERVAL '7 days'),
(3, 1.85, 1.65, TRUE, CURRENT_TIMESTAMP),

(8, 4.80, NULL, TRUE, CURRENT_TIMESTAMP - INTERVAL '30 days'),
(8, 4.50, 4.10, TRUE, CURRENT_TIMESTAMP - INTERVAL '14 days'),
(8, 4.50, 3.85, TRUE, CURRENT_TIMESTAMP),

(9, 4.50, NULL, TRUE, CURRENT_TIMESTAMP - INTERVAL '30 days'),
(9, 4.35, 3.90, TRUE, CURRENT_TIMESTAMP);

-- Referencias Regulatorias SRS
INSERT INTO srs_product (id, master_product_id, health_registration, chm, product_name, active_ingredient, concentration, pharmaceutical_form, presentation, laboratory, source_url) VALUES
(1, 1, 'F012345678', 'CHM-0012', 'ACETAMINOFEN 500 MG TABLETAS', 'Acetaminofén', '500 mg', 'Tableta', 'Caja x 20 tabletas', 'Tecnoquímicas / MK', 'http://info.medicamentos.gob.sv/consulta/F012345678'),
(2, 2, 'F012345679', 'CHM-0013', 'ACETAMINOFEN 120 MG/5 ML JARABE', 'Acetaminofén', '120 mg/5 mL', 'Jarabe', 'Frasco x 60 mL', 'Tecnoquímicas / MK', 'http://info.medicamentos.gob.sv/consulta/F012345679'),
(3, 3, 'F023456789', 'CHM-0045', 'ADVIL MAX 400 MG CAPSULAS', 'Ibuprofeno', '400 mg', 'Cápsula blanda', 'Caja x 10 cápsulas', 'Pfizer / Haleon', 'http://info.medicamentos.gob.sv/consulta/F023456789')
ON CONFLICT (id) DO UPDATE SET 
    product_name = EXCLUDED.product_name, 
    active_ingredient = EXCLUDED.active_ingredient;

SELECT setval('srs_product_id_seq', COALESCE((SELECT MAX(id) FROM srs_product), 1));

-- Precios Máximos Regulados (PVMP) SRS
INSERT INTO srs_price (id, srs_product_id, pvmp, pvmp_unit, pvmp_presentation, market_price, pvmp_type, effective_date) VALUES
(1, 1, 2.10, 0.105, 2.10, 1.85, 'Precio Máximo de Venta al Público Regulado (PVMP)', '2024-01-01'),
(2, 2, 4.25, 4.25, 4.25, 3.85, 'Precio Máximo de Venta al Público Regulado (PVMP)', '2024-01-01'),
(3, 3, 5.15, 0.515, 5.15, 4.40, 'Precio Máximo de Venta al Público Regulado (PVMP)', '2024-01-01')
ON CONFLICT (id) DO UPDATE SET 
    pvmp = EXCLUDED.pvmp, 
    market_price = EXCLUDED.market_price;

SELECT setval('srs_price_id_seq', COALESCE((SELECT MAX(id) FROM srs_price), 1));

-- Auditoría de Scraping Inicial
INSERT INTO scraping_execution (pharmacy_id, started_at, finished_at, status, records_found, records_created, records_updated, records_failed, error_message) VALUES
(1, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '58 minutes', 'COMPLETED', 145, 2, 143, 0, NULL),
(2, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '59 minutes', 'COMPLETED', 89, 1, 88, 0, NULL),
(3, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '59 minutes', 'COMPLETED', 24, 0, 24, 0, 'TLS advertencia certificate, datos provistos por catálogo estático'),
(4, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '58 minutes', 'COMPLETED', 112, 3, 109, 0, NULL);

-- ============================================================================
-- FIN DEL SCRIPT DE INICIALIZACIÓN
-- ============================================================================
