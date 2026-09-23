package com.luppo.farmacia.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardDto {
    private Long totalMasterProducts;
    private Long totalPharmacyProducts;
    private Long totalPharmacies;
    private Long totalScrapingExecutions;
    private List<ScrapingStatusDto> latestScrapingStatus;
}
