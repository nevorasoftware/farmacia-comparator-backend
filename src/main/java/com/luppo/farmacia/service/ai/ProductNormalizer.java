package com.luppo.farmacia.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductNormalizer {

    @Value("${ai.enabled:true}")
    private boolean aiEnabled;

    private final GeminiAiProvider geminiAiProvider;

    public NormalizedProduct normalize(String rawName, String rawDescription) {
        if (rawName == null || rawName.trim().isEmpty()) {
            return NormalizedProduct.builder()
                    .name("Desconocido")
                    .activeIngredient("Genérico")
                    .confidence(0.0)
                    .build();
        }

        // 1. Intentar normalización por Inteligencia Artificial (Gemini) si está disponible
        if (aiEnabled && geminiAiProvider.isAvailable()) {
            try {
                NormalizedProduct aiResult = geminiAiProvider.normalize(rawName, rawDescription);
                if (aiResult != null && aiResult.getName() != null && !aiResult.getName().isEmpty()) {
                    if (aiResult.getActiveIngredient() == null || aiResult.getActiveIngredient().isEmpty()) {
                        aiResult.setActiveIngredient(aiResult.getName());
                    }
                    log.info("Normalización exitosa con Gemini AI para '{}': {}", rawName, aiResult.getName());
                    return aiResult;
                }
            } catch (Exception e) {
                log.warn("Fallo temporal en Gemini AI, recurriendo a motor heurístico: {}", e.getMessage());
            }
        }

        // 2. Motor Heurístico Determinístico local
        return normalizeDeterministic(rawName);
    }

    public NormalizedProduct normalizeDeterministic(String text) {
        String upper = text.toUpperCase();
        String activeIng = extractActiveIngredient(upper);
        String concentration = extractConcentration(upper);
        String form = extractPharmaceuticalForm(upper);
        String brand = extractBrand(upper);
        int qty = extractQuantity(upper);

        String cleaned = cleanProductName(text);

        String canonicalName;
        if (upper.contains("ENSURE")) {
            canonicalName = cleaned;
            if (activeIng == null) activeIng = "Fórmula Polimérica Nutricional / Suplemento Alimenticio Completo";
            if ("Tableta".equals(form)) form = upper.contains("220") || upper.contains("BOTE") ? "Líquido" : "Polvo / Lata";
            brand = "Abbott";
        } else if (upper.contains("GLUCERNA")) {
            canonicalName = cleaned;
            if (activeIng == null) activeIng = "Fórmula Nutricional Especializada para Diabetes";
            brand = "Abbott";
        } else if (upper.contains("ENTEREX")) {
            canonicalName = cleaned;
            if (activeIng == null) activeIng = "Suplemento Nutricional Proteico";
            brand = "Victus";
        } else if (upper.contains("PEDIASURE")) {
            canonicalName = cleaned;
            if (activeIng == null) activeIng = "Fórmula Nutricional Infantil Especializada";
            brand = "Abbott";
        } else if (activeIng != null) {
            canonicalName = activeIng + (concentration != null && !concentration.isEmpty() ? " " + concentration : "") + " " + form;
        } else {
            canonicalName = cleaned;
            activeIng = cleaned;
        }

        return NormalizedProduct.builder()
                .name(canonicalName)
                .activeIngredient(activeIng)
                .concentration(concentration != null ? concentration : "")
                .pharmaceuticalForm(form)
                .brand(brand)
                .laboratory(brand.equals("Abbott") ? "Abbott Laboratories" : "Comercial / Laboratorio")
                .quantity(qty > 0 ? qty : 1)
                .unit(form.toLowerCase())
                .confidence(upper.contains("ENSURE") || activeIng != null ? 0.90 : 0.70)
                .build();
    }

    private String extractActiveIngredient(String text) {
        if (text.contains("ENSURE")) return "Fórmula Polimérica Nutricional / Suplemento Alimenticio Completo";
        if (text.contains("GLUCERNA")) return "Fórmula Nutricional Especializada para Diabetes";
        if (text.contains("ENTEREX")) return "Suplemento Nutricional Proteico";
        if (text.contains("PEDIASURE")) return "Fórmula Nutricional Infantil Especializada";
        if (text.contains("ELECTROLIT") || text.contains("SUEROX")) return "Electrolitos Orales";
        if (text.contains("BEBELAC") || text.contains("ENFAGROW") || text.contains("NAN") || text.contains("NIDO")) return "Fórmula Láctea Infantil";

        if (text.contains("ACETAMINOFEN") || text.contains("PARACETAMOL")) return "Acetaminofén";
        if (text.contains("IBUPROFENO") || text.contains("ADVIL")) return "Ibuprofeno";
        if (text.contains("AMOXICILINA")) return "Amoxicilina";
        if (text.contains("LORATADINA") || text.contains("CLARITIN")) return "Loratadina";
        if (text.contains("METFORMINA")) return "Metformina";
        if (text.contains("LOSARTAN") || text.contains("LOSARTÁN")) return "Losartán Potásico";
        if (text.contains("OMEPRAZOL")) return "Omeprazol";
        if (text.contains("AZITROMICINA")) return "Azitromicina";
        if (text.contains("CIPROFLOXACINA") || text.contains("CIPROFLOXACINO")) return "Ciprofloxacina";
        if (text.contains("ASPIRINA") || text.contains("ACIDO ACETILSALICILICO") || text.contains("CARDIOASPIRINA")) return "Ácido Acetilsalicílico";
        if (text.contains("ATORVASTATINA")) return "Atorvastatina";
        if (text.contains("ENALAPRIL")) return "Enalapril";
        if (text.contains("AMLODIPINA") || text.contains("AMLODIPINO")) return "Amlodipina";
        if (text.contains("SALBUTAMOL")) return "Salbutamol";
        if (text.contains("DEXAMETASONA")) return "Dexametasona";
        if (text.contains("CETIRIZINA")) return "Cetirizina";
        if (text.contains("DICLOFENAC") || text.contains("DICLOFENACO")) return "Diclofenaco";
        if (text.contains("CLOPIDOGREL")) return "Clopidogrel";
        return null;
    }

    private String extractConcentration(String text) {
        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?\\s*(MG|G|GR|GRS|GRAMOS|MCG|ML|%|UI|LB)(/\\d*\\s*ML)?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim().toUpperCase();
        }
        return "";
    }

    private String extractPharmaceuticalForm(String text) {
        if (text.contains("LATA") || text.contains("POLVO")) return "Polvo / Lata";
        if (text.contains("BOTE") || text.contains("BOTELLA") || text.contains("220ML") || text.contains("220 ML")) return "Líquido / Botella";
        if (text.contains("JARABE") || text.contains("JBE")) return "Jarabe";
        if (text.contains("CAPSULA") || text.contains("CÁPSULA") || text.contains("CAP")) return "Cápsula";
        if (text.contains("TABLETA") || text.contains("TAB") || text.contains("COMPRIMIDO")) return "Tableta";
        if (text.contains("SUSPENSION") || text.contains("SUSPENSIÓN")) return "Suspensión";
        if (text.contains("GOTAS")) return "Gotas";
        if (text.contains("CREMA")) return "Crema";
        if (text.contains("GEL")) return "Gel";
        if (text.contains("INYECTABLE") || text.contains("AMPOLLA")) return "Inyectable";
        return "Tableta";
    }

    private String extractBrand(String text) {
        if (text.contains("ENSURE") || text.contains("GLUCERNA") || text.contains("PEDIASURE") || text.contains("ABBOTT")) return "Abbott";
        if (text.contains("MK")) return "MK";
        if (text.contains("ECOMED")) return "Ecomed";
        if (text.contains("LA SANTE")) return "La Santé";
        if (text.contains("VIJOSA")) return "Vijosa";
        if (text.contains("LAB.SUIZOS") || text.contains("SUIZOS")) return "Laboratorios Suizos";
        if (text.contains("BAYER")) return "Bayer";
        if (text.contains("PFIZER")) return "Pfizer";
        if (text.contains("ADVIL")) return "Advil";
        if (text.contains("TYLENOL")) return "Tylenol";
        return "Comercial";
    }

    private int extractQuantity(String text) {
        Pattern pattern = Pattern.compile("X\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {}
        }
        return 1;
    }

    private String cleanProductName(String text) {
        return text.replaceAll("(?i)(caja|frasco|blister|x\\s*\\d+|tab|cap|\\bde\\b|\\bcon\\b).*", "").trim();
    }
}
