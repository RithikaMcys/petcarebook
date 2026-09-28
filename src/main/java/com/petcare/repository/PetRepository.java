package com.petcare.repository;

import com.petcare.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByOwnerId(Long ownerId);

    List<Pet> findBySpeciesIgnoreCase(String species);

    @Query("SELECT p FROM Pet p WHERE " +
           "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.breed) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:species IS NULL OR LOWER(p.species) = LOWER(:species))")
    List<Pet> searchPets(@Param("keyword") String keyword, @Param("species") String species);
}
