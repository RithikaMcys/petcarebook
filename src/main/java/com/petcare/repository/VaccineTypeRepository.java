package com.petcare.repository;

import com.petcare.model.VaccineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaccineTypeRepository extends JpaRepository<VaccineType, Long> {

    Optional<VaccineType> findByNameIgnoreCase(String name);

    List<VaccineType> findByTargetSpeciesIgnoreCaseOrTargetSpeciesIgnoreCase(String species1, String species2);
}
