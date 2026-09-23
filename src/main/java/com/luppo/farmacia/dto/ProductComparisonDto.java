package com.luppo.farmacia.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductComparisonDto {
    private Long id;
    private String name;
    private String activeIngredient;
    private String concentration;
    private String pharmaceuticalForm;
    private String administrationRoute;
    private String brand;
    private String laboratory;
    private String healthRegistration;
    private String chm;
    private String presentation;
    private Integer quantity;
    private String unit;

    private BigDecimal lowestPrice;
    private BigDecimal highestPrice;

    private SrsReferenceDto srsReference;
    private List<PharmacyPriceDto> pharmacyPrices;
    private List<EquivalentProductDto> equivalentProducts;
    private List<PriceHistoryPointDto> priceHistory;
}
