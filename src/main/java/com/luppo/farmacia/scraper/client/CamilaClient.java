package com.luppo.farmacia.scraper.client;

import com.luppo.farmacia.scraper.model.ScrapedItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@Slf4j
public class CamilaClient {

    public List<ScrapedItem> search(String query) {
        List<ScrapedItem> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return results;

        String q = query.toLowerCase();
        if (q.contains("acetaminof") || q.contains("paracetamol")) {
            results.add(ScrapedItem.builder()
                    .externalId("CAM-0101")
                    .originalName("Acetaminofén 500 mg Genérico Caja x 20 Tabletas")
                    .brand("Genérico")
                    .presentation("Caja x 20 tabletas")
                    .price(BigDecimal.valueOf(0.75))
                    .isAvailable(true)
                    .url("https://www.farmaciascamila.com")
                    .build());
        } else if (q.contains("ibuprofen")) {
            results.add(ScrapedItem.builder()
                    .externalId("CAM-0201")
                    .originalName("Ibuprofeno 400 mg Genérico Caja x 10 Cápsulas")
                    .brand("Genérico")
                    .presentation("Caja x 10 cápsulas")
                    .price(BigDecimal.valueOf(1.95))
                    .isAvailable(true)
                    .url("https://www.farmaciascamila.com")
                    .build());
        } else if (q.contains("amoxicilin")) {
            results.add(ScrapedItem.builder()
                    .externalId("CAM-0301")
                    .originalName("Amoxicilina 500 mg Caja x 15 Cápsulas")
                    .brand("Genérico")
                    .presentation("Caja x 15 cápsulas")
                    .price(BigDecimal.valueOf(2.80))
                    .isAvailable(true)
                    .url("https://www.farmaciascamila.com")
                    .build());
        } else if (q.contains("losartan")) {
            results.add(ScrapedItem.builder()
                    .externalId("CAM-0401")
                    .originalName("Losartán Potásico 50 mg Caja x 30 Tabletas")
                    .brand("Genérico")
                    .presentation("Caja x 30 tabletas")
                    .price(BigDecimal.valueOf(3.25))
                    .isAvailable(true)
                    .url("https://www.farmaciascamila.com")
                    .build());
        } else if (q.contains("aspirin") || q.contains("acetilsalicil")) {
            results.add(ScrapedItem.builder()
                    .externalId("CAM-0501")
                    .originalName("Aspirina 100 mg Protect Bayer Caja x 28 Tabletas")
                    .brand("Bayer")
                    .presentation("Caja x 28 tabletas")
                    .price(BigDecimal.valueOf(1.95))
                    .isAvailable(true)
                    .url("https://www.farmaciascamila.com")
                    .build());
        } else if (q.contains("omeprazol")) {
            results.add(ScrapedItem.builder()
                    .externalId("CAM-0601")
                    .originalName("Omeprazol 20 mg Genérico Caja x 30 Cápsulas")
                    .brand("Genérico")
                    .presentation("Caja x 30 cápsulas")
                    .price(BigDecimal.valueOf(2.50))
                    .isAvailable(true)
                    .url("https://www.farmaciascamila.com")
                    .build());
        } else if (q.contains("metformin")) {
            results.add(ScrapedItem.builder()
                    .externalId("CAM-0701")
                    .originalName("Metformina 850 mg Genérico Caja x 30 Tabletas")
                    .brand("Genérico")
                    .presentation("Caja x 30 tabletas")
                    .price(BigDecimal.valueOf(2.20))
                    .isAvailable(true)
                    .url("https://www.farmaciascamila.com")
                    .build());
        } else if (q.contains("azitromicin")) {
            results.add(ScrapedItem.builder()
                    .externalId("CAM-0801")
                    .originalName("Azitromicina 500 mg Caja x 3 Tabletas")
                    .brand("Genérico")
                    .presentation("Caja x 3 tabletas")
                    .price(BigDecimal.valueOf(3.50))
                    .isAvailable(true)
                    .url("https://www.farmaciascamila.com")
                    .build());
        } else if (q.contains("loratadin")) {
            results.add(ScrapedItem.builder()
                    .externalId("CAM-0901")
                    .originalName("Loratadina 10 mg Genérico Caja x 10 Tabletas")
                    .brand("Genérico")
                    .presentation("Caja x 10 tabletas")
                    .price(BigDecimal.valueOf(1.10))
                    .isAvailable(true)
                    .url("https://www.farmaciascamila.com")
                    .build());
        } else if (q.contains("enalapril")) {
            results.add(ScrapedItem.builder()
                    .externalId("CAM-1001")
                    .originalName("Enalapril 20 mg Genérico Caja x 30 Tabletas")
                    .brand("Genérico")
                    .presentation("Caja x 30 tabletas")
                    .price(BigDecimal.valueOf(2.80))
                    .isAvailable(true)
                    .url("https://www.farmaciascamila.com")
                    .build());
        } else if (q.contains("atorvastatin")) {
            results.add(ScrapedItem.builder()
                    .externalId("CAM-1101")
                    .originalName("Atorvastatina 20 mg Genérico Caja x 30 Tabletas")
                    .brand("Genérico")
                    .presentation("Caja x 30 tabletas")
                    .price(BigDecimal.valueOf(4.50))
                    .isAvailable(true)
                    .url("https://www.farmaciascamila.com")
                    .build());
        }

        return results;
    }
}
