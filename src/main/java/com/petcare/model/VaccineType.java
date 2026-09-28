package com.petcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "vaccine_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccineType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Vaccine name is required")
    @Column(nullable = false, unique = true)
    private String name; // e.g. "Rabies", "DHPP 5-in-1", "Deworming (Broad Spectrum)"

    @NotNull(message = "Standard interval in days is required")
    @Min(value = 1, message = "Interval must be at least 1 day")
    @Column(nullable = false)
    private Integer standardIntervalDays; // e.g. 365 days for annual, 90 days for quarterly deworming

    private String targetSpecies; // "DOG", "CAT", "ALL"

    @Column(length = 1000)
    private String description;

    @Builder.Default
    private Boolean isMandatory = true;
}
