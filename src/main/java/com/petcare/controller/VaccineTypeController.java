package com.petcare.controller;

import com.petcare.model.VaccineType;
import com.petcare.service.VaccineTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccine-types")
@RequiredArgsConstructor
@Tag(name = "Vaccine Types", description = "Endpoints for managing standard vaccine protocols and intervals")
public class VaccineTypeController {

    private final VaccineTypeService vaccineTypeService;

    @GetMapping
    @Operation(summary = "Get all registered vaccine types and standard intervals")
    public ResponseEntity<List<VaccineType>> getAllVaccineTypes() {
        return ResponseEntity.ok(vaccineTypeService.getAllVaccineTypes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vaccine type by ID")
    public ResponseEntity<VaccineType> getVaccineTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(vaccineTypeService.getVaccineTypeById(id));
    }

    @PostMapping
    @Operation(summary = "Register a new vaccine protocol with standard interval")
    public ResponseEntity<VaccineType> createVaccineType(@Valid @RequestBody VaccineType vaccineType) {
        VaccineType saved = vaccineTypeService.createVaccineType(vaccineType);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/species/{species}")
    @Operation(summary = "Get vaccine types applicable to a specific species (Dog, Cat)")
    public ResponseEntity<List<VaccineType>> getVaccinesBySpecies(@PathVariable String species) {
        return ResponseEntity.ok(vaccineTypeService.getVaccinesBySpecies(species));
    }
}
