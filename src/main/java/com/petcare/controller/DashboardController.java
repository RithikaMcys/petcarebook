package com.petcare.controller;

import com.petcare.dto.DashboardStatsDto;
import com.petcare.service.VaccinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard Statistics", description = "Endpoints for aggregate clinic and pet health KPIs")
public class DashboardController {

    private final VaccinationService vaccinationService;

    @GetMapping("/stats")
    @Operation(summary = "Get overview metrics: total pets, total owners, vaccinations due in 7 days, overdue count")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(vaccinationService.getDashboardStats());
    }
}
