package com.petcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDto {
    private long totalPets;
    private long totalOwners;
    private long totalVaccineTypes;
    private long totalVaccinationsAdministered;
    private long vaccinationsDueIn7Days;
    private long overdueVaccinations;
}
