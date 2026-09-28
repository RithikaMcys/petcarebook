package com.petcare.service;

import com.petcare.dto.PetRegistrationRequest;
import com.petcare.exception.ResourceNotFoundException;
import com.petcare.model.Owner;
import com.petcare.model.Pet;
import com.petcare.repository.OwnerRepository;
import com.petcare.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;

    @Transactional
    public Pet registerPet(PetRegistrationRequest request) {
        if (request.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Pet date of birth cannot be in the future.");
        }

        Owner owner = null;
        if (request.getOwnerId() != null) {
            owner = ownerRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + request.getOwnerId()));
        } else if (request.getOwnerName() != null && !request.getOwnerName().isBlank()) {
            owner = Owner.builder()
                    .fullName(request.getOwnerName().trim())
                    .email(request.getOwnerEmail() != null ? request.getOwnerEmail().trim() : null)
                    .phoneNumber(request.getOwnerPhone() != null ? request.getOwnerPhone().trim() : "N/A")
                    .address(request.getOwnerAddress() != null ? request.getOwnerAddress().trim() : null)
                    .build();
            owner = ownerRepository.save(owner);
        }

        Pet pet = Pet.builder()
                .name(request.getName().trim())
                .species(request.getSpecies().trim().toUpperCase())
                .breed(request.getBreed().trim())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender() != null ? request.getGender().trim().toUpperCase() : "UNKNOWN")
                .weightKg(request.getWeightKg())
                .owner(owner)
                .build();

        return petRepository.save(pet);
    }

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + id));
    }

    public List<Pet> searchPets(String keyword, String species) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        String sp = (species != null && !species.isBlank() && !"ALL".equalsIgnoreCase(species)) ? species.trim() : null;
        return petRepository.searchPets(kw, sp);
    }

    public List<Pet> getPetsByOwner(Long ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    @Transactional
    public Pet updatePet(Long id, PetRegistrationRequest request) {
        Pet pet = getPetById(id);
        pet.setName(request.getName().trim());
        pet.setSpecies(request.getSpecies().trim().toUpperCase());
        pet.setBreed(request.getBreed().trim());
        pet.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) pet.setGender(request.getGender().trim().toUpperCase());
        if (request.getWeightKg() != null) pet.setWeightKg(request.getWeightKg());

        if (request.getOwnerId() != null) {
            Owner owner = ownerRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + request.getOwnerId()));
            pet.setOwner(owner);
        }
        return petRepository.save(pet);
    }

    @Transactional
    public void deletePet(Long id) {
        Pet pet = getPetById(id);
        petRepository.delete(pet);
    }
}
