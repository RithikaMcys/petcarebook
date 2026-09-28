package com.petcare.dto;

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
public class VaccinationRecordRequest {

    @NotNull(message = "Pet ID is required")
    private Long petId;

    @NotNull(message = "Vaccine Type ID is required")
    private Long vaccineTypeId;

    @NotNull(message = "Date administered is required")
    @PastOrPresent(message = "Vaccination date cannot be in the future")
    private LocalDate dateAdministered;

    private String batchNumber;

    private String clinicOrVetName;

    private String notes;
}
