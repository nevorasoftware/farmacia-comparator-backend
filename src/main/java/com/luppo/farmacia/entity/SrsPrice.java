package com.luppo.farmacia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "srs_price")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SrsPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "srs_product_id", nullable = false)
    private SrsProduct srsProduct;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pvmp;

    @Column(name = "pvmp_unit", precision = 10, scale = 2)
    private BigDecimal pvmpUnit;

    @Column(name = "pvmp_presentation", precision = 10, scale = 2)
    private BigDecimal pvmpPresentation;

    @Column(name = "market_price", precision = 10, scale = 2)
    private BigDecimal marketPrice;

    @Column(name = "pvmp_type")
    private String pvmpType;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
