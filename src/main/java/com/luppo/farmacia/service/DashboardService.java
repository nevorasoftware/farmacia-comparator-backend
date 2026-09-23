package com.luppo.farmacia.service;

import com.luppo.farmacia.dto.AdminDashboardDto;
import com.luppo.farmacia.dto.ScrapingStatusDto;
import com.luppo.farmacia.entity.ScrapingExecution;
import com.luppo.farmacia.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final MasterProductRepository masterProductRepository;
    private final PharmacyProductRepository pharmacyProductRepository;
    private final PharmacyRepository pharmacyRepository;
    private final ScrapingExecutionRepository scrapingExecutionRepository;

    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboardStats() {
        List<ScrapingExecution> latest = scrapingExecutionRepository.findLatestExecutionsPerPharmacy();

        List<ScrapingStatusDto> statusDtos = latest.stream().map(se -> {
            Long duration = null;
            if (se.getFinishedAt() != null) {
                duration = Duration.between(se.getStartedAt(), se.getFinishedAt()).getSeconds();
            }
            return ScrapingStatusDto.builder()
                    .executionId(se.getId())
                    .pharmacyId(se.getPharmacy() != null ? se.getPharmacy().getId() : null)
                    .pharmacyName(se.getPharmacy() != null ? se.getPharmacy().getName() : "General/SRS")
                    .pharmacyCode(se.getPharmacy() != null ? se.getPharmacy().getCode() : "SRS")
                    .status(se.getStatus())
                    .recordsFound(se.getRecordsFound())
                    .recordsCreated(se.getRecordsCreated())
                    .recordsUpdated(se.getRecordsUpdated())
                    .recordsFailed(se.getRecordsFailed())
                    .errorMessage(se.getErrorMessage())
                    .startedAt(se.getStartedAt())
                    .finishedAt(se.getFinishedAt())
                    .durationSeconds(duration)
                    .build();
        }).collect(Collectors.toList());

        return AdminDashboardDto.builder()
                .totalMasterProducts(masterProductRepository.count())
                .totalPharmacyProducts(pharmacyProductRepository.count())
                .totalPharmacies(pharmacyRepository.count())
                .totalScrapingExecutions(scrapingExecutionRepository.count())
                .latestScrapingStatus(statusDtos)
                .build();
    }
}
