package com.luppo.farmacia.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "scraping_execution")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScrapingExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;

    @Column(name = "started_at", nullable = false)
    @Builder.Default
    private OffsetDateTime startedAt = OffsetDateTime.now();

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Column(nullable = false)
    private String status;

    @Column(name = "records_found", nullable = false)
    @Builder.Default
    private Integer recordsFound = 0;

    @Column(name = "records_created", nullable = false)
    @Builder.Default
    private Integer recordsCreated = 0;

    @Column(name = "records_updated", nullable = false)
    @Builder.Default
    private Integer recordsUpdated = 0;

    @Column(name = "records_failed", nullable = false)
    @Builder.Default
    private Integer recordsFailed = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
