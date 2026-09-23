package com.luppo.farmacia.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScrapingStatusDto {
    private Long executionId;
    private Long pharmacyId;
    private String pharmacyName;
    private String pharmacyCode;
    private String status;
    private Integer recordsFound;
    private Integer recordsCreated;
    private Integer recordsUpdated;
    private Integer recordsFailed;
    private String errorMessage;
    private OffsetDateTime startedAt;
    private OffsetDateTime finishedAt;
    private Long durationSeconds;
}
