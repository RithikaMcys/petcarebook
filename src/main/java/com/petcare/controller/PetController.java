package com.petcare.controller;

import com.petcare.dto.PetRegistrationRequest;
import com.petcare.dto.VaccinationResponseDto;
import com.petcare.model.Pet;
import com.petcare.service.PetService;
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
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Tag(name = "Pet Management", description = "Endpoints for registering and viewing pet profiles")
public class PetController {

    private final PetService petService;
    private final VaccinationService vaccinationService;

    @PostMapping
    @Operation(summary = "Core Feature 1: Register a new pet (name, species, breed, date of birth, owner)")
    public ResponseEntity<Pet> registerPet(@Valid @RequestBody PetRegistrationRequest request) {
        Pet saved = petService.registerPet(request);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all registered pets")
    public ResponseEntity<List<Pet>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pet profile by ID")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.getPetById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search pets by keyword (name or breed) and species")
    public ResponseEntity<List<Pet>> searchPets(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String species) {
        return ResponseEntity.ok(petService.searchPets(keyword, species));
    }

    @GetMapping("/{petId}/vaccinations")
    @Operation(summary = "Core Feature 5: View a pet's complete vaccination and deworming history")
    public ResponseEntity<List<VaccinationResponseDto>> getPetVaccinationHistory(@PathVariable Long petId) {
        return ResponseEntity.ok(vaccinationService.getPetVaccinationHistory(petId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update pet details")
    public ResponseEntity<Pet> updatePet(@PathVariable Long id, @Valid @RequestBody PetRegistrationRequest request) {
        return ResponseEntity.ok(petService.updatePet(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete pet record")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}
