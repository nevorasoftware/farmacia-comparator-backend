package com.luppo.farmacia.scraper.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luppo.farmacia.scraper.model.ScrapedItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Component
@Slf4j
public class EconomicasClient {

    private static final String API_URL = "https://www.farmaciaseconomicaselsalvador.com/PROD/ECOMMERCE/API/Articulo/ObtenerArticulosPorNombre";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ScrapedItem> search(String query) {
        List<ScrapedItem> results = new ArrayList<>();
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("ArticuloNombre", query);
            payload.put("Pagina", 1);
            payload.put("TamanoPagina", 15);
            payload.put("Ordenamiento", 0);

            String jsonBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json, text/plain, */*")
                    .timeout(Duration.ofSeconds(8))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                if (root.path("IsSuccessful").asBoolean(false)) {
                    JsonNode articulos = root.path("Data").path("PaginaItems");
                    if (articulos.isArray()) {
                        for (JsonNode item : articulos) {
                            String id = item.path("ID").asText();
                            String nombre = item.path("Nombre").asText();
                            BigDecimal price = BigDecimal.valueOf(item.path("Precio").asDouble(0.0));
                            String imageUrl = item.path("URLImagen").asText();
                            String categoria = item.path("Categoria").path("Nombre").asText();

                            String url = "https://www.farmaciaseconomicaselsalvador.com/PROD/ECOMMERCE/Home/Buscar?termino=" + java.net.URLEncoder.encode(query, "UTF-8");

                            if (price.compareTo(BigDecimal.ZERO) > 0) {
                                results.add(ScrapedItem.builder()
                                        .externalId("ECON-" + id)
                                        .originalName(nombre)
                                        .price(price)
                                        .imageUrl(imageUrl)
                                        .url(url)
                                        .isAvailable(true)
                                        .presentation(categoria)
                                        .build());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Búsqueda en vivo Económicas falló para '{}': {}", query, e.getMessage());
        }
        return results;
    }
}
