package com.petcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vaccination_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccinationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Pet reference is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id", nullable = false)
    @JsonIgnoreProperties("vaccinationRecords")
    private Pet pet;

    @NotNull(message = "Vaccine type is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vaccine_type_id", nullable = false)
    private VaccineType vaccineType;

    @NotNull(message = "Date administered is required")
    @PastOrPresent(message = "Vaccination date cannot be in the future")
    @Column(nullable = false)
    private LocalDate dateAdministered;

    @Column(nullable = false)
    private LocalDate nextDueDate; // Computed based on VaccineType.standardIntervalDays

    private String batchNumber; // e.g. "RAB-2026-X99"

    private String clinicOrVetName; // e.g. "Paws & Claws Veterinary Clinic"

    @Column(length = 1000)
    private String notes;

    private LocalDateTime createdAt;

    @PrePersist
    public void onPrePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
