-- V3: Semilla de Ensure Advance y Fórmulas Nutricionales de Alta Demanda

-- 1. Insertar Productos Maestros Ensure y Aspirina
INSERT INTO master_product (id, name, active_ingredient, concentration, pharmaceutical_form, administration_route, brand, laboratory, health_registration, chm, presentation, quantity, unit) VALUES
(9, 'Ensure Advance Sabor Café 400g', 'Fórmula Polimérica Nutricional / Suplemento Alimenticio Completo', '400 g', 'Polvo / Lata', 'Oral', 'Abbott', 'Abbott Laboratories', 'F078901234', 'CHM-0220', 'Lata x 400 g', 1, 'lata'),
(10, 'Ensure Advance Vainilla 400g', 'Fórmula Polimérica Nutricional / Suplemento Alimenticio Completo', '400 g', 'Polvo / Lata', 'Oral', 'Abbott', 'Abbott Laboratories', 'F078901235', 'CHM-0221', 'Lata x 400 g', 1, 'lata'),
(11, 'Aspirina 500 mg', 'Ácido Acetilsalicílico', '500 mg', 'Tableta', 'Oral', 'Bayer', 'Bayer', 'F089012345', 'CHM-0310', 'Caja x 20 tabletas', 20, 'tableta')
ON CONFLICT (id) DO NOTHING;

SELECT setval('master_product_id_seq', (SELECT MAX(id) FROM master_product));

-- 2. Insertar Productos en Farmacias (San Nicolás, Económicas, CEFAFA, Camila)
INSERT INTO pharmacy_product (id, pharmacy_id, master_product_id, external_id, original_name, brand, presentation, url, image_url, current_price, current_offer_price, is_available, content_hash) VALUES
-- Ensure Advance Sabor Café 400g
(12, 1, 9, 'SN-B0003150LATAX1', 'Ensure Advance Sabor Cafe Lata X 400 Gramos', 'Abbott', 'Lata x 400 g', 'https://www.farmaciasannicolas.com/producto/Ensure-Advance-Sabor-Cafe-Lata-X-400-Gramos/B0003150LATAX1', 'https://fsn-api-multimedia.azurewebsites.net/api/fsn/multimedia/fc0f626a-091a-42fb-a264-557c54095bc5/content', 33.57, 31.89, TRUE, 'hash_sn_ensure_cafe'),
(13, 2, 9, 'CEF-ENS-CAFE', 'ENSURE ADVANCE SABOR CAFE 400 GR', 'Abbott', 'Lata x 400 g', 'https://portal.farmaciascefafa.com.sv/producto/ensure-advance-cafe', 'https://portal.farmaciascefafa.com.sv/api-ecommerce/api/imagenes/ensure-cafe', 33.13, 31.50, TRUE, 'hash_cef_ensure_cafe'),
(14, 4, 9, 'ECO-ENS-CAFE', 'Ensure Advance Cafe 400gr', 'Abbott', 'Lata x 400 g', 'https://www.farmaciaseconomicaselsalvador.com/PROD/ECOMMERCE/Home/Buscar?termino=ensure', 'https://www.farmaciaseconomicaselsalvador.com/PROD/SERV_ADMIN_FILES/Archivos/Imagenes/Articulos_PEQ/ensure_PEQ.jpg', 33.57, 31.89, TRUE, 'hash_eco_ensure_cafe'),
(15, 3, 9, 'CAM-ENS-CAFE', 'Ensure Advance Café Lata 400g', 'Abbott', 'Lata x 400 g', 'https://www.farmaciascamila.com', 'https://www.farmaciascamila.com/img/producto.jpg', 33.00, NULL, TRUE, 'hash_cam_ensure_cafe'),

-- Ensure Advance Vainilla 400g
(16, 1, 10, 'SN-A2790LATAX1', 'Ensure Advance Hmb Vainilla 400 Gramos', 'Abbott', 'Lata x 400 g', 'https://www.farmaciasannicolas.com/producto/Ensure-Advance-Hmb-Vainilla-400-Gramos/A2790LATAX1', 'https://fsn-api-multimedia.azurewebsites.net/api/fsn/multimedia/fc0f626a-091a-42fb-a264-557c54095bc5/content', 33.57, 31.89, TRUE, 'hash_sn_ensure_vai'),
(17, 2, 10, 'CEF-ENS-VAI', 'ENSURE ADVANCE VAINILLA 400 GR', 'Abbott', 'Lata x 400 g', 'https://portal.farmaciascefafa.com.sv/producto/ensure-advance-vainilla-400-gr', 'https://portal.farmaciascefafa.com.sv/api-ecommerce/api/imagenes/ensure-vai', 33.13, NULL, TRUE, 'hash_cef_ensure_vai'),
(18, 4, 10, 'ECO-ENS-VAI', 'Ensure Advance Vainilla 400gr', 'Abbott', 'Lata x 400 g', 'https://www.farmaciaseconomicaselsalvador.com/PROD/ECOMMERCE/Home/Buscar?termino=ensure', 'https://www.farmaciaseconomicaselsalvador.com/PROD/SERV_ADMIN_FILES/Archivos/Imagenes/Articulos_PEQ/ensure_vai.jpg', 33.57, NULL, TRUE, 'hash_eco_ensure_vai'),

-- Aspirina 500 mg
(19, 1, 11, 'SN-ASP500', 'Aspirina 500 mg Caja con 20 Tabletas', 'Bayer', 'Caja x 20 tabletas', 'https://www.farmaciasannicolas.com', 'https://www.farmaciasannicolas.com/api/fsn/multimedia/aspirina.jpg', 2.80, 2.50, TRUE, 'hash_sn_asp500'),
(20, 2, 11, 'CEF-ASP500', 'ASPIRINA 500 MG BAYER CAJA X 20 TAB', 'Bayer', 'Caja x 20 tabletas', 'https://portal.farmaciascefafa.com.sv', 'https://portal.farmaciascefafa.com.sv/api-ecommerce/api/imagenes/aspirina', 2.65, 2.45, TRUE, 'hash_cef_asp500'),
(21, 4, 11, 'ECO-ASP500', 'Aspirina Bayer 500mg X 20 Tabletas', 'Bayer', 'Caja x 20 tabletas', 'https://www.farmaciaseconomicaselsalvador.com', 'https://www.farmaciaseconomicaselsalvador.com/img/aspirina.jpg', 2.60, NULL, TRUE, 'hash_eco_asp500')
ON CONFLICT (id) DO NOTHING;

SELECT setval('pharmacy_product_id_seq', (SELECT MAX(id) FROM pharmacy_product));

-- 3. Histórico de Precios para Ensure
INSERT INTO price_history (pharmacy_product_id, price, offer_price, is_available, checked_at) VALUES
(12, 33.57, 31.89, TRUE, CURRENT_TIMESTAMP - INTERVAL '7 days'),
(12, 33.57, 31.89, TRUE, CURRENT_TIMESTAMP),
(13, 33.13, 31.50, TRUE, CURRENT_TIMESTAMP - INTERVAL '7 days'),
(13, 33.13, 31.50, TRUE, CURRENT_TIMESTAMP),
(14, 33.57, 31.89, TRUE, CURRENT_TIMESTAMP);

-- 4. Referencias Regulatorias SRS
INSERT INTO srs_product (id, master_product_id, health_registration, chm, product_name, active_ingredient, concentration, pharmaceutical_form, presentation, laboratory, source_url) VALUES
(4, 9, 'F078901234', 'CHM-0220', 'ENSURE ADVANCE SABOR CAFE 400G', 'Fórmula Polimérica Nutricional / Suplemento Alimenticio Completo', '400 g', 'Polvo / Lata', 'Lata x 400 g', 'Abbott Laboratories', 'http://info.medicamentos.gob.sv/consulta/F078901234'),
(5, 10, 'F078901235', 'CHM-0221', 'ENSURE ADVANCE VAINILLA 400G', 'Fórmula Polimérica Nutricional / Suplemento Alimenticio Completo', '400 g', 'Polvo / Lata', 'Lata x 400 g', 'Abbott Laboratories', 'http://info.medicamentos.gob.sv/consulta/F078901235'),
(6, 11, 'F089012345', 'CHM-0310', 'ASPIRINA 500 MG TABLETAS', 'Ácido Acetilsalicílico', '500 mg', 'Tableta', 'Caja x 20 tabletas', 'Bayer', 'http://info.medicamentos.gob.sv/consulta/F089012345')
ON CONFLICT (id) DO NOTHING;

SELECT setval('srs_product_id_seq', (SELECT MAX(id) FROM srs_product));

-- 5. Precios Máximos SRS (PVMP)
INSERT INTO srs_price (id, srs_product_id, pvmp, pvmp_unit, pvmp_presentation, market_price, pvmp_type, effective_date) VALUES
(4, 4, 34.50, 34.50, 34.50, 33.57, 'Precio Máximo de Venta al Público Regulado (PVMP)', '2024-01-01'),
(5, 5, 34.50, 34.50, 34.50, 33.57, 'Precio Máximo de Venta al Público Regulado (PVMP)', '2024-01-01'),
(6, 6, 3.20, 0.16, 3.20, 2.80, 'Precio Máximo de Venta al Público Regulado (PVMP)', '2024-01-01')
ON CONFLICT (id) DO NOTHING;

SELECT setval('srs_price_id_seq', (SELECT MAX(id) FROM srs_price));
