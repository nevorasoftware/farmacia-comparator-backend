package com.luppo.farmacia.service.ai;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NormalizedProduct {
    private String name;
    private String activeIngredient;
    private String concentration;
    private String pharmaceuticalForm;
    private String administrationRoute;
    private String brand;
    private String laboratory;
    private String healthRegistration;
    private String presentation;
    private Integer quantity;
    private String unit;
    private Double confidence;
}
