package com.luppo.farmacia.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquivalentProductDto {
    private Long id;
    private String name;
    private String brand;
    private String laboratory;
    private String activeIngredient;
    private String concentration;
    private String pharmaceuticalForm;
    private String presentation;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal pvmpSrs;
}
