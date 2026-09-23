package com.luppo.farmacia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "pharmacy_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_product_id")
    private MasterProduct masterProduct;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "original_description", columnDefinition = "TEXT")
    private String originalDescription;

    private String brand;
    private String presentation;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "current_price", precision = 10, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "current_offer_price", precision = 10, scale = 2)
    private BigDecimal currentOfferPrice;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "content_hash", length = 64)
    private String contentHash;

    @Column(name = "last_scraped_at", nullable = false)
    @Builder.Default
    private OffsetDateTime lastScrapedAt = OffsetDateTime.now();

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}
