package com.petcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccinationResponseDto {
    private Long id;
    private Long petId;
    private String petName;
    private String species;
    private String breed;
    private String ownerName;
    private String ownerPhone;

    private Long vaccineTypeId;
    private String vaccineName;
    private Integer standardIntervalDays;

    private LocalDate dateAdministered;
    private LocalDate nextDueDate;
    private long daysRemaining; // positive if in future, negative if overdue
    private String status; // "UP_TO_DATE", "DUE_SOON", "OVERDUE"

    private String batchNumber;
    private String clinicOrVetName;
    private String notes;
}
