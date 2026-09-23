package com.luppo.farmacia.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyPriceDto {
    private Long pharmacyId;
    private String pharmacyName;
    private String pharmacyCode;
    private String originalName;
    private String presentation;
    private BigDecimal price;
    private BigDecimal offerPrice;
    private Boolean isAvailable;
    private String url;
    private String imageUrl;
    private OffsetDateTime lastUpdated;
    private BigDecimal differenceVsPvmp;
    private BigDecimal percentageVsPvmp;
}
