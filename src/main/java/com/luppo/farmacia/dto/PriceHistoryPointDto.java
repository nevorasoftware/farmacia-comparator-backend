package com.luppo.farmacia.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceHistoryPointDto {
    private Long pharmacyId;
    private String pharmacyName;
    private BigDecimal price;
    private BigDecimal offerPrice;
    private Boolean isAvailable;
    private OffsetDateTime checkedAt;
}
