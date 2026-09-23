package com.luppo.farmacia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "master_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "active_ingredient", nullable = false)
    private String activeIngredient;

    private String concentration;

    @Column(name = "pharmaceutical_form")
    private String pharmaceuticalForm;

    @Column(name = "administration_route")
    private String administrationRoute;

    private String brand;
    private String laboratory;

    @Column(name = "health_registration")
    private String healthRegistration;

    private String chm;
    private String presentation;
    private Integer quantity;
    private String unit;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();
}
