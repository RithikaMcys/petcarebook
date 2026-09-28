package com.petcare.controller;

import com.petcare.model.Owner;
import com.petcare.repository.OwnerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
@Tag(name = "Pet Owners", description = "Endpoints for managing pet owner accounts")
public class OwnerController {

    private final OwnerRepository ownerRepository;

    @GetMapping
    @Operation(summary = "Get all pet owners")
    public ResponseEntity<List<Owner>> getAllOwners() {
        return ResponseEntity.ok(ownerRepository.findAll());
    }

    @PostMapping
    @Operation(summary = "Register a new pet owner")
    public ResponseEntity<Owner> createOwner(@Valid @RequestBody Owner owner) {
        return new ResponseEntity<>(ownerRepository.save(owner), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get owner by ID")
    public ResponseEntity<Owner> getOwnerById(@PathVariable Long id) {
        return ownerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
