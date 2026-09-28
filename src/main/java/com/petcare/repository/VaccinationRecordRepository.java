package com.petcare.repository;

import com.petcare.model.VaccinationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {

    List<VaccinationRecord> findByPetIdOrderByDateAdministeredDesc(Long petId);

    List<VaccinationRecord> findByNextDueDateBetweenOrderByNextDueDateAsc(LocalDate startDate, LocalDate endDate);

    List<VaccinationRecord> findByNextDueDateBeforeOrderByNextDueDateAsc(LocalDate date);

    @Query("SELECT vr FROM VaccinationRecord vr WHERE vr.pet.owner.id = :ownerId ORDER BY vr.nextDueDate ASC")
    List<VaccinationRecord> findByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(vr) FROM VaccinationRecord vr WHERE vr.nextDueDate < :today")
    long countOverdue(@Param("today") LocalDate today);

    @Query("SELECT COUNT(vr) FROM VaccinationRecord vr WHERE vr.nextDueDate BETWEEN :startDate AND :endDate")
    long countDueBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
