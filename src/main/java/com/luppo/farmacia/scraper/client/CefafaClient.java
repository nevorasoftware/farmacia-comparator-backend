package com.luppo.farmacia.scraper.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luppo.farmacia.scraper.model.ScrapedItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Component
@Slf4j
public class CefafaClient {

    private static final String APP_KEY = "cefafa.app.v1";
    private static final String SECRET = "n3OoYOI7sT59GHHSeTkhN++atzNMda6BneOnal8Kp6k=";
    private static final String BASE_API = "https://portal.farmaciascefafa.com.sv/api-ecommerce/api/";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(6))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ScrapedItem> search(String query) {
        List<ScrapedItem> results = new ArrayList<>();
        try {
            String endpoint = BASE_API + "productos/buscador/" + java.net.URLEncoder.encode(query, StandardCharsets.UTF_8);
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String message = APP_KEY + timestamp;
            String signature = generateHmacSignature(message, SECRET);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .header("Accept", "application/json")
                    .header("X-App-Key", APP_KEY)
                    .header("X-Timestamp", timestamp)
                    .header("X-Signature", signature)
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode products = root.path("products");
                if (products.isArray()) {
                    for (JsonNode p : products) {
                        String id = p.path("id").asText();
                        String name = p.path("name").asText();
                        String branch = p.path("branch").asText();
                        BigDecimal price = BigDecimal.valueOf(p.path("price").asDouble(0.0));
                        String slug = p.path("slug").asText();
                        String image = p.path("image").asText();
                        String imageUrl = image != null && !image.isEmpty() 
                                ? BASE_API + "imagenes/" + image 
                                : null;
                        String url = "https://portal.farmaciascefafa.com.sv/producto/" + slug;

                        if (price.compareTo(BigDecimal.ZERO) > 0) {
                            results.add(ScrapedItem.builder()
                                    .externalId("CEFAFA-" + id)
                                    .originalName(name)
                                    .brand(branch)
                                    .price(price)
                                    .isAvailable(true)
                                    .url(url)
                                    .imageUrl(imageUrl)
                                    .build());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Búsqueda en vivo CEFAFA falló para '{}': {}", query, e.getMessage());
        }
        return results;
    }

    private String generateHmacSignature(String data, String secret) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hmacData = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacData);
    }
}
