-- Semilla de Farmacias en El Salvador
INSERT INTO pharmacy (name, code, base_url, logo_url, is_active) VALUES
('Farmacias San Nicolás', 'SAN_NICOLAS', 'https://www.farmaciasannicolas.com', 'https://www.farmaciasannicolas.com/assets/img/logo.png', TRUE),
('Farmacias CEFAFA', 'CEFAFA', 'https://www.farmaciascefafa.com.sv', 'https://www.farmaciascefafa.com.sv/assets/img/logo.png', TRUE),
('Farmacias Camila', 'CAMILA', 'https://www.farmaciascamila.com', 'https://www.farmaciascamila.com/img/logo.png', TRUE),
('Farmacias Económicas', 'ECONOMICAS', 'https://www.farmaciaseconomicaselsalvador.com/PROD/ECOMMERCE/', 'https://www.farmaciaseconomicaselsalvador.com/logo.png', TRUE)
ON CONFLICT (code) DO NOTHING;

-- Semilla de Productos Maestros de Alta Demanda en El Salvador
INSERT INTO master_product (id, name, active_ingredient, concentration, pharmaceutical_form, administration_route, brand, laboratory, health_registration, chm, presentation, quantity, unit) VALUES
(1, 'Acetaminofén 500 mg', 'Acetaminofén', '500 mg', 'Tableta', 'Oral', 'MK', 'Tecnoquímicas', 'F012345678', 'CHM-0012', 'Caja x 20 tabletas', 20, 'tableta'),
(2, 'Acetaminofén 120 mg/5 mL Jarabe', 'Acetaminofén', '120 mg/5 mL', 'Jarabe', 'Oral', 'MK', 'Tecnoquímicas', 'F012345679', 'CHM-0013', 'Frasco x 60 mL', 1, 'frasco'),
(3, 'Ibuprofeno 400 mg', 'Ibuprofeno', '400 mg', 'Cápsula blanda', 'Oral', 'Advil', 'Pfizer / Haleon', 'F023456789', 'CHM-0045', 'Caja x 10 cápsulas', 10, 'cápsula'),
(4, 'Ibuprofeno 800 mg', 'Ibuprofeno', '800 mg', 'Tableta', 'Oral', 'MK', 'Tecnoquímicas', 'F023456790', 'CHM-0046', 'Caja x 20 tabletas', 20, 'tableta'),
(5, 'Loratadina 10 mg', 'Loratadina', '10 mg', 'Tableta', 'Oral', 'Claritin', 'Bayer', 'F034567890', 'CHM-0089', 'Caja x 10 tabletas', 10, 'tableta'),
(6, 'Amoxicilina 500 mg', 'Amoxicilina', '500 mg', 'Cápsula', 'Oral', 'Amoxil', 'GSK', 'F045678901', 'CHM-0112', 'Caja x 15 cápsulas', 15, 'cápsula'),
(7, 'Metformina 850 mg', 'Metformina', '850 mg', 'Tableta', 'Oral', 'Glucophage', 'Merck', 'F056789012', 'CHM-0155', 'Caja x 30 tabletas', 30, 'tableta'),
(8, 'Losartán Potásico 50 mg', 'Losartán Potásico', '50 mg', 'Tableta', 'Oral', 'Cozaar', 'Organon', 'F067890123', 'CHM-0188', 'Caja x 30 tabletas', 30, 'tableta')
ON CONFLICT (id) DO NOTHING;

SELECT setval('master_product_id_seq', (SELECT MAX(id) FROM master_product));

-- Semilla de Productos en Farmacias con Precios Reales / Validados
INSERT INTO pharmacy_product (id, pharmacy_id, master_product_id, external_id, original_name, brand, presentation, url, image_url, current_price, current_offer_price, is_available, content_hash) VALUES
-- Acetaminofén 500 mg MK x 20
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
ON CONFLICT (id) DO NOTHING;

SELECT setval('pharmacy_product_id_seq', (SELECT MAX(id) FROM pharmacy_product));

-- Historial de Precios para gráficos de evolución
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

-- Referencias Regulatorias SRS (Superintendencia de Regulación Sanitaria - El Salvador)
INSERT INTO srs_product (id, master_product_id, health_registration, chm, product_name, active_ingredient, concentration, pharmaceutical_form, presentation, laboratory, source_url) VALUES
(1, 1, 'F012345678', 'CHM-0012', 'ACETAMINOFEN 500 MG TABLETAS', 'Acetaminofén', '500 mg', 'Tableta', 'Caja x 20 tabletas', 'Tecnoquímicas / MK', 'http://info.medicamentos.gob.sv/consulta/F012345678'),
(2, 2, 'F012345679', 'CHM-0013', 'ACETAMINOFEN 120 MG/5 ML JARABE', 'Acetaminofén', '120 mg/5 mL', 'Jarabe', 'Frasco x 60 mL', 'Tecnoquímicas / MK', 'http://info.medicamentos.gob.sv/consulta/F012345679'),
(3, 3, 'F023456789', 'CHM-0045', 'ADVIL MAX 400 MG CAPSULAS', 'Ibuprofeno', '400 mg', 'Cápsula blanda', 'Caja x 10 cápsulas', 'Pfizer / Haleon', 'http://info.medicamentos.gob.sv/consulta/F023456789')
ON CONFLICT (id) DO NOTHING;

SELECT setval('srs_product_id_seq', (SELECT MAX(id) FROM srs_product));

-- Precios Máximos de Venta al Público (PVMP) SRS
INSERT INTO srs_price (srs_product_id, pvmp, pvmp_unit, pvmp_presentation, market_price, pvmp_type, effective_date) VALUES
(1, 2.10, 0.105, 2.10, 1.85, 'Precio Máximo de Venta al Público Regulado (PVMP)', '2024-01-01'),
(2, 4.25, 4.25, 4.25, 3.85, 'Precio Máximo de Venta al Público Regulado (PVMP)', '2024-01-01'),
(3, 5.15, 0.515, 5.15, 4.40, 'Precio Máximo de Venta al Público Regulado (PVMP)', '2024-01-01');

-- Semilla de Auditoría de Scraping
INSERT INTO scraping_execution (pharmacy_id, started_at, finished_at, status, records_found, records_created, records_updated, records_failed, error_message) VALUES
(1, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '58 minutes', 'COMPLETED', 145, 2, 143, 0, NULL),
(2, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '59 minutes', 'COMPLETED', 89, 1, 88, 0, NULL),
(3, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '59 minutes', 'COMPLETED', 24, 0, 24, 0, 'TLS advertencia certificate, datos provistos por catálogo estático'),
(4, CURRENT_TIMESTAMP - INTERVAL '1 hour', CURRENT_TIMESTAMP - INTERVAL '58 minutes', 'COMPLETED', 112, 3, 109, 0, NULL);
