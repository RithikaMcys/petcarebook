package com.petcare.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Pet name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Species is required (e.g. DOG, CAT)")
    @Column(nullable = false)
    private String species;

    @NotBlank(message = "Breed is required")
    @Column(nullable = false)
    private String breed;

    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth cannot be in the future")
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    private String gender; // MALE, FEMALE

    private Double weightKg;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties("pets")
    private Owner owner;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<VaccinationRecord> vaccinationRecords = new ArrayList<>();
}
