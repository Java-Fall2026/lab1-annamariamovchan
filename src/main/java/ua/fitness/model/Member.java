package ua.fitness.model;

import ua.fitness.util.FitnessUtils;
import java.time.LocalDate;

public record Member(String taxId, String fullName, LocalDate birthDate) {
    public Member {
        FitnessUtils.requireNotBlank(taxId, "taxId");
        FitnessUtils.requireNotBlank(fullName, "fullName");
        FitnessUtils.requireNotNull(birthDate, "birthDate");
        
        if (birthDate!=null && birthDate.plusYears(16).isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("birthDate expected at least 16 years old, got: "+birthDate);
        }
    }
}
