package com.luppo.farmacia.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SrsReferenceDto {
    private String healthRegistration;
    private String chm;
    private String productName;
    private String activeIngredient;
    private String concentration;
    private String pharmaceuticalForm;
    private String presentation;
    private String laboratory;
    private BigDecimal pvmp;
    private BigDecimal pvmpUnit;
    private BigDecimal pvmpPresentation;
    private BigDecimal marketPrice;
    private String pvmpType;
    private LocalDate effectiveDate;
    private String sourceUrl;
    private OffsetDateTime lastVerifiedAt;
}
