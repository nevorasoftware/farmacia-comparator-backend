package com.luppo.farmacia.scraper.model;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapedItem {
    private String externalId;
    private String originalName;
    private String originalDescription;
    private String brand;
    private String presentation;
    private BigDecimal price;
    private BigDecimal offerPrice;
    private String url;
    private String imageUrl;
    @Builder.Default
    private Boolean isAvailable = true;
}
