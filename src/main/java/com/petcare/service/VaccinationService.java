package com.petcare.service;

import com.petcare.dto.DashboardStatsDto;
import com.petcare.dto.VaccinationRecordRequest;
import com.petcare.dto.VaccinationResponseDto;
import com.petcare.exception.InvalidVaccinationDateException;
import com.petcare.exception.ResourceNotFoundException;
import com.petcare.model.Pet;
import com.petcare.model.VaccinationRecord;
import com.petcare.model.VaccineType;
import com.petcare.repository.OwnerRepository;
import com.petcare.repository.PetRepository;
import com.petcare.repository.VaccinationRecordRepository;
import com.petcare.repository.VaccineTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaccinationService {

    private final VaccinationRecordRepository recordRepository;
    private final PetRepository petRepository;
    private final VaccineTypeRepository vaccineTypeRepository;
    private final OwnerRepository ownerRepository;

    /**
     * Core Feature 2 & 3: Log a vaccination event with business rules enforced.
     * Business Rule 1: A vaccination record cannot be logged with a future date.
     * Business Rule 2: Each vaccine type has a fixed interval used to compute the next due date.
     */
    @Transactional
    public VaccinationResponseDto logVaccination(VaccinationRecordRequest request) {
        LocalDate today = LocalDate.now();

        // Enforce Business Rule 1: Date cannot be in the future
        if (request.getDateAdministered() == null) {
            throw new IllegalArgumentException("Date administered is required.");
        }
        if (request.getDateAdministered().isAfter(today)) {
            throw new InvalidVaccinationDateException(
                    "Vaccination date cannot be in the future. Provided date: " + request.getDateAdministered() +
                    ", Current system date: " + today
            );
        }

        // Retrieve Pet
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + request.getPetId()));

        // Retrieve VaccineType
        VaccineType vaccineType = vaccineTypeRepository.findById(request.getVaccineTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Vaccine Type not found with ID: " + request.getVaccineTypeId()));

        // Enforce Business Rule 2: Compute next due date using fixed interval
        Integer intervalDays = vaccineType.getStandardIntervalDays();
        if (intervalDays == null || intervalDays <= 0) {
            intervalDays = 365; // Default fallback to 1 year if unassigned
        }
        LocalDate nextDueDate = request.getDateAdministered().plusDays(intervalDays);

        log.info("Logging vaccination for Pet: '{}' (ID: {}), Vaccine: '{}', Given: {}, Next Due: {} (+{} days)",
                pet.getName(), pet.getId(), vaccineType.getName(), request.getDateAdministered(), nextDueDate, intervalDays);

        VaccinationRecord record = VaccinationRecord.builder()
                .pet(pet)
                .vaccineType(vaccineType)
                .dateAdministered(request.getDateAdministered())
                .nextDueDate(nextDueDate)
                .batchNumber(request.getBatchNumber() != null ? request.getBatchNumber().trim() : null)
                .clinicOrVetName(request.getClinicOrVetName() != null ? request.getClinicOrVetName().trim() : null)
                .notes(request.getNotes() != null ? request.getNotes().trim() : null)
                .build();

        VaccinationRecord saved = recordRepository.save(record);
        return mapToDto(saved);
    }

    /**
     * Core Feature 4: List all pets with vaccinations due in the next 7 days.
     */
    public List<VaccinationResponseDto> getVaccinationsDueInNextDays(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);

        List<VaccinationRecord> records = recordRepository.findByNextDueDateBetweenOrderByNextDueDateAsc(today, futureDate);
        return records.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Core Feature 5: Owner can view a pet's complete vaccination history.
     */
    public List<VaccinationResponseDto> getPetVaccinationHistory(Long petId) {
        if (!petRepository.existsById(petId)) {
            throw new ResourceNotFoundException("Pet not found with ID: " + petId);
        }
        List<VaccinationRecord> records = recordRepository.findByPetIdOrderByDateAdministeredDesc(petId);
        return records.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<VaccinationResponseDto> getAllRecords() {
        return recordRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public DashboardStatsDto getDashboardStats() {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);

        long totalPets = petRepository.count();
        long totalOwners = ownerRepository.count();
        long totalVaccineTypes = vaccineTypeRepository.count();
        long totalRecords = recordRepository.count();
        long dueIn7Days = recordRepository.countDueBetween(today, nextWeek);
        long overdue = recordRepository.countOverdue(today);

        return DashboardStatsDto.builder()
                .totalPets(totalPets)
                .totalOwners(totalOwners)
                .totalVaccineTypes(totalVaccineTypes)
                .totalVaccinationsAdministered(totalRecords)
                .vaccinationsDueIn7Days(dueIn7Days)
                .overdueVaccinations(overdue)
                .build();
    }

    private VaccinationResponseDto mapToDto(VaccinationRecord r) {
        LocalDate today = LocalDate.now();
        long daysRemaining = ChronoUnit.DAYS.between(today, r.getNextDueDate());

        String status;
        if (daysRemaining < 0) {
            status = "OVERDUE";
        } else if (daysRemaining <= 7) {
            status = "DUE_SOON";
        } else {
            status = "UP_TO_DATE";
        }

        Pet pet = r.getPet();
        VaccineType vt = r.getVaccineType();

        return VaccinationResponseDto.builder()
                .id(r.getId())
                .petId(pet.getId())
                .petName(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .ownerName(pet.getOwner() != null ? pet.getOwner().getFullName() : "Unassigned")
                .ownerPhone(pet.getOwner() != null ? pet.getOwner().getPhoneNumber() : "N/A")
                .vaccineTypeId(vt.getId())
                .vaccineName(vt.getName())
                .standardIntervalDays(vt.getStandardIntervalDays())
                .dateAdministered(r.getDateAdministered())
                .nextDueDate(r.getNextDueDate())
                .daysRemaining(daysRemaining)
                .status(status)
                .batchNumber(r.getBatchNumber())
                .clinicOrVetName(r.getClinicOrVetName())
                .notes(r.getNotes())
                .build();
    }
}
