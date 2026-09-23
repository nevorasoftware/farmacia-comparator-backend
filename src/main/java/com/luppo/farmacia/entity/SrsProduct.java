package com.luppo.farmacia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "srs_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SrsProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_product_id")
    private MasterProduct masterProduct;

    @Column(name = "health_registration", nullable = false, unique = true)
    private String healthRegistration;

    private String chm;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "active_ingredient", nullable = false)
    private String activeIngredient;

    private String concentration;

    @Column(name = "pharmaceutical_form")
    private String pharmaceuticalForm;

    private String presentation;
    private String laboratory;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    @Column(name = "last_verified_at", nullable = false)
    @Builder.Default
    private OffsetDateTime lastVerifiedAt = OffsetDateTime.now();

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}
