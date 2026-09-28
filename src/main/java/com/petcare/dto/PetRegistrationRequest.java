package com.petcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetRegistrationRequest {

    @NotBlank(message = "Pet name is required")
    private String name;

    @NotBlank(message = "Species is required (e.g. Dog, Cat)")
    private String species;

    @NotBlank(message = "Breed is required")
    private String breed;

    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dateOfBirth;

    private String gender;

    private Double weightKg;

    // Existing Owner ID or New Owner details
    private Long ownerId;

    private String ownerName;

    private String ownerEmail;

    private String ownerPhone;

    private String ownerAddress;
}
