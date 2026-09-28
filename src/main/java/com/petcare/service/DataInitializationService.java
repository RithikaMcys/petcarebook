package com.petcare.service;

import com.petcare.model.Owner;
import com.petcare.model.Pet;
import com.petcare.model.VaccinationRecord;
import com.petcare.model.VaccineType;
import com.petcare.repository.OwnerRepository;
import com.petcare.repository.PetRepository;
import com.petcare.repository.VaccinationRecordRepository;
import com.petcare.repository.VaccineTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {

    private final VaccineTypeRepository vaccineTypeRepository;
    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;
    private final VaccinationRecordRepository recordRepository;

    @Override
    public void run(String... args) {
        if (vaccineTypeRepository.count() == 0) {
            log.info("Seeding Standard Veterinary Vaccine Types...");

            VaccineType rabies = VaccineType.builder()
                    .name("Rabies Core Vaccine")
                    .standardIntervalDays(365) // 1 Year
                    .targetSpecies("ALL")
                    .description("Core annual vaccine protecting against the deadly rabies virus.")
                    .isMandatory(true)
                    .build();

            VaccineType dhpp = VaccineType.builder()
                    .name("DHPP 5-in-1 (Distemper, Parvo)")
                    .standardIntervalDays(365) // 1 Year
                    .targetSpecies("DOG")
                    .description("Canine core combo vaccine protecting against Distemper, Hepatitis, Parvovirus, and Parainfluenza.")
                    .isMandatory(true)
                    .build();

            VaccineType fvrcp = VaccineType.builder()
                    .name("FVRCP Feline Core")
                    .standardIntervalDays(365) // 1 Year
                    .targetSpecies("CAT")
                    .description("Feline viral rhinotracheitis, calicivirus, and panleukopenia core vaccine.")
                    .isMandatory(true)
                    .build();

            VaccineType deworming = VaccineType.builder()
                    .name("Broad-Spectrum Deworming")
                    .standardIntervalDays(90) // Quarterly (3 Months)
                    .targetSpecies("ALL")
                    .description("Oral parasitic preventative treating roundworms, hookworms, and tapeworms.")
                    .isMandatory(true)
                    .build();

            VaccineType bordetella = VaccineType.builder()
                    .name("Bordetella (Kennel Cough)")
                    .standardIntervalDays(180) // 6 Months
                    .targetSpecies("DOG")
                    .description("Recommended for socialized dogs entering boarding, daycare, or grooming.")
                    .isMandatory(false)
                    .build();

            vaccineTypeRepository.save(rabies);
            vaccineTypeRepository.save(dhpp);
            vaccineTypeRepository.save(fvrcp);
            vaccineTypeRepository.save(deworming);
            vaccineTypeRepository.save(bordetella);
        }

        if (ownerRepository.count() == 0) {
            log.info("Seeding Sample Pet Owners and Pets...");

            Owner o1 = Owner.builder()
                    .fullName("Rithika Sharma")
                    .email("rithika.s@example.com")
                    .phoneNumber("+91 98401 12345")
                    .address("14 Park Avenue, Chennai")
                    .build();

            Owner o2 = Owner.builder()
                    .fullName("Arun Vijay")
                    .email("arun.v@example.com")
                    .phoneNumber("+91 98402 67890")
                    .address("22 Lake View Road, Coimbatore")
                    .build();

            ownerRepository.save(o1);
            ownerRepository.save(o2);

            Pet p1 = Pet.builder()
                    .name("Milo")
                    .species("DOG")
                    .breed("Golden Retriever")
                    .dateOfBirth(LocalDate.now().minusYears(2).minusMonths(3))
                    .gender("MALE")
                    .weightKg(28.5)
                    .owner(o1)
                    .build();

            Pet p2 = Pet.builder()
                    .name("Luna")
                    .species("CAT")
                    .breed("Persian Longhair")
                    .dateOfBirth(LocalDate.now().minusYears(1).minusMonths(1))
                    .gender("FEMALE")
                    .weightKg(4.2)
                    .owner(o1)
                    .build();

            Pet p3 = Pet.builder()
                    .name("Max")
                    .species("DOG")
                    .breed("German Shepherd")
                    .dateOfBirth(LocalDate.now().minusYears(3).minusMonths(6))
                    .gender("MALE")
                    .weightKg(34.0)
                    .owner(o2)
                    .build();

            petRepository.save(p1);
            petRepository.save(p2);
            petRepository.save(p3);

            // Seed sample vaccination records
            VaccineType rabies = vaccineTypeRepository.findByNameIgnoreCase("Rabies Core Vaccine").orElse(null);
            VaccineType dhpp = vaccineTypeRepository.findByNameIgnoreCase("DHPP 5-in-1 (Distemper, Parvo)").orElse(null);
            VaccineType deworming = vaccineTypeRepository.findByNameIgnoreCase("Broad-Spectrum Deworming").orElse(null);

            if (rabies != null && dhpp != null && deworming != null) {
                // Record 1: Milo's Deworming administered 86 days ago -> nextDueDate is in 4 days! (DUE IN 7 DAYS)
                LocalDate dewormGiven = LocalDate.now().minusDays(86);
                VaccinationRecord r1 = VaccinationRecord.builder()
                        .pet(p1)
                        .vaccineType(deworming)
                        .dateAdministered(dewormGiven)
                        .nextDueDate(dewormGiven.plusDays(deworming.getStandardIntervalDays()))
                        .batchNumber("DWM-2026-B1")
                        .clinicOrVetName("Apollo Paws Clinic")
                        .notes("Quarterly broad-spectrum deworming tablet given with meal.")
                        .build();

                // Record 2: Milo's Rabies vaccine given 350 days ago -> due in 15 days (UP_TO_DATE)
                LocalDate rabiesGiven = LocalDate.now().minusDays(350);
                VaccinationRecord r2 = VaccinationRecord.builder()
                        .pet(p1)
                        .vaccineType(rabies)
                        .dateAdministered(rabiesGiven)
                        .nextDueDate(rabiesGiven.plusDays(rabies.getStandardIntervalDays()))
                        .batchNumber("RAB-2025-V9")
                        .clinicOrVetName("City Veterinary Hospital")
                        .notes("Annual core rabies booster. No adverse reaction.")
                        .build();

                // Record 3: Luna's Rabies vaccine given 360 days ago -> due in 5 days! (DUE IN 7 DAYS)
                LocalDate lunaRabiesGiven = LocalDate.now().minusDays(360);
                VaccinationRecord r3 = VaccinationRecord.builder()
                        .pet(p2)
                        .vaccineType(rabies)
                        .dateAdministered(lunaRabiesGiven)
                        .nextDueDate(lunaRabiesGiven.plusDays(rabies.getStandardIntervalDays()))
                        .batchNumber("RAB-2025-V11")
                        .clinicOrVetName("Feline Care Center")
                        .notes("Annual booster scheduled next week.")
                        .build();

                // Record 4: Max's DHPP vaccine given 375 days ago -> overdue by 10 days! (OVERDUE)
                LocalDate maxDhppGiven = LocalDate.now().minusDays(375);
                VaccinationRecord r4 = VaccinationRecord.builder()
                        .pet(p3)
                        .vaccineType(dhpp)
                        .dateAdministered(maxDhppGiven)
                        .nextDueDate(maxDhppGiven.plusDays(dhpp.getStandardIntervalDays()))
                        .batchNumber("DHP-2025-X2")
                        .clinicOrVetName("VetCare Animal Hospital")
                        .notes("Annual DHPP booster past due. Reminder notification needed.")
                        .build();

                recordRepository.save(r1);
                recordRepository.save(r2);
                recordRepository.save(r3);
                recordRepository.save(r4);
            }
        }
    }
}
