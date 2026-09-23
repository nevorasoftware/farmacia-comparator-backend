package com.luppo.farmacia.controller;

import com.luppo.farmacia.dto.AdminDashboardDto;
import com.luppo.farmacia.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Administración", description = "Estadísticas y monitor de scraping")
public class AdminController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    @Operation(summary = "Obtener estadísticas del dashboard administrativo")
    public ResponseEntity<AdminDashboardDto> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
}
