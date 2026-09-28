package com.petcare.service;

import com.petcare.exception.ResourceNotFoundException;
import com.petcare.model.VaccineType;
import com.petcare.repository.VaccineTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VaccineTypeService {

    private final VaccineTypeRepository vaccineTypeRepository;

    public List<VaccineType> getAllVaccineTypes() {
        return vaccineTypeRepository.findAll();
    }

    public VaccineType getVaccineTypeById(Long id) {
        return vaccineTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaccine Type not found with ID: " + id));
    }

    public VaccineType createVaccineType(VaccineType vaccineType) {
        return vaccineTypeRepository.save(vaccineType);
    }

    public List<VaccineType> getVaccinesBySpecies(String species) {
        if (species == null || species.isBlank() || "ALL".equalsIgnoreCase(species)) {
            return vaccineTypeRepository.findAll();
        }
        return vaccineTypeRepository.findByTargetSpeciesIgnoreCaseOrTargetSpeciesIgnoreCase(species.trim(), "ALL");
    }
}
