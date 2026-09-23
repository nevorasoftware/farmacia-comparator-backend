package com.luppo.farmacia.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@Slf4j
public class GeminiAiProvider {

    @Value("${ai.gemini.apiKey:}")
    private String apiKey;

    @Value("${ai.gemini.model:gemini-1.5-flash}")
    private String model;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isAvailable() {
        return apiKey != null && !apiKey.trim().isEmpty() && !apiKey.startsWith("tu_");
    }

    public NormalizedProduct normalize(String rawProductName, String rawDescription) {
        if (!isAvailable()) {
            return null;
        }

        try {
            String prompt = String.format(
                    "Eres un farmacéutico experto en medicamentos de El Salvador. Normaliza el siguiente producto en un JSON estricto:\n" +
                    "Producto: %s\n" +
                    "Descripción: %s\n\n" +
                    "Responde UNICAMENTE con este objeto JSON:\n" +
                    "{\n" +
                    "  \"nombre\": \"Nombre comercial o estandarizado del medicamento\",\n" +
                    "  \"principioActivo\": \"Nombre del principio activo principal (ej. Acetaminofén, Ibuprofeno, Losartán, Amoxicilina)\",\n" +
                    "  \"concentracion\": \"ej. 500 mg, 50 mg, 100 mg\",\n" +
                    "  \"formaFarmaceutica\": \"ej. Tableta, Cápsula, Jarabe, Suspensión\",\n" +
                    "  \"marca\": \"ej. MK, Bayer, Pfizer, Vijosa, Genérico\",\n" +
                    "  \"laboratorio\": \"Laboratorio fabricante si se infiere\",\n" +
                    "  \"cantidad\": 20,\n" +
                    "  \"unidad\": \"tableta\"\n" +
                    "}",
                    rawProductName, rawDescription != null ? rawDescription : ""
            );

            String requestBody = objectMapper.writeValueAsString(new GeminiRequest(prompt));
            String endpoint = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", model, apiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(12))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                String candidateText = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
                candidateText = candidateText.replaceAll("```json", "").replaceAll("```", "").trim();
                JsonNode parsed = objectMapper.readTree(candidateText);

                return NormalizedProduct.builder()
                        .name(parsed.path("nombre").asText(rawProductName))
                        .activeIngredient(parsed.path("principioActivo").asText())
                        .concentration(parsed.path("concentracion").asText())
                        .pharmaceuticalForm(parsed.path("formaFarmaceutica").asText("Tableta"))
                        .brand(parsed.path("marca").asText("Genérico / Comercial"))
                        .laboratory(parsed.path("laboratorio").asText("Comercial"))
                        .quantity(parsed.path("cantidad").asInt(1))
                        .unit(parsed.path("unidad").asText("unidad"))
                        .confidence(0.95)
                        .build();
            } else {
                log.warn("Gemini API retornó código {}", response.statusCode());
            }
        } catch (Exception e) {
            log.warn("Excepción al consultar Gemini API: {}", e.getMessage());
        }

        return null;
    }

    private static class GeminiRequest {
        public Contents[] contents;
        public GeminiRequest(String text) {
            this.contents = new Contents[]{ new Contents(new Part[]{ new Part(text) }) };
        }
    }
    private static class Contents {
        public Part[] parts;
        public Contents(Part[] parts) { this.parts = parts; }
    }
    private static class Part {
        public String text;
        public Part(String text) { this.text = text; }
    }
}
