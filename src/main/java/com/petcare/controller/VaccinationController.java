package com.petcare.controller;

import com.petcare.dto.VaccinationRecordRequest;
import com.petcare.dto.VaccinationResponseDto;
import com.petcare.service.VaccinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccinations")
@RequiredArgsConstructor
@Tag(name = "Vaccination Tracking", description = "Endpoints for logging immunizations and monitoring schedules")
public class VaccinationController {

    private final VaccinationService vaccinationService;

    @PostMapping
    @Operation(summary = "Core Features 2 & 3: Log a vaccination event and auto-calculate next due date based on fixed interval")
    public ResponseEntity<VaccinationResponseDto> logVaccination(@Valid @RequestBody VaccinationRecordRequest request) {
        VaccinationResponseDto response = vaccinationService.logVaccination(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/due-soon")
    @Operation(summary = "Core Feature 4: List all pets with vaccinations due in the next 7 days (or customizable days)")
    public ResponseEntity<List<VaccinationResponseDto>> getVaccinationsDueSoon(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(vaccinationService.getVaccinationsDueInNextDays(days));
    }

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Core Feature 5: View a pet's complete vaccination history")
    public ResponseEntity<List<VaccinationResponseDto>> getPetHistory(@PathVariable Long petId) {
        return ResponseEntity.ok(vaccinationService.getPetVaccinationHistory(petId));
    }

    @GetMapping
    @Operation(summary = "Get all vaccination records across all pets")
    public ResponseEntity<List<VaccinationResponseDto>> getAllRecords() {
        return ResponseEntity.ok(vaccinationService.getAllRecords());
    }
}
