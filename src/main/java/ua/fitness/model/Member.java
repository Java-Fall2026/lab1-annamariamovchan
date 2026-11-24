package ua.fitness.model;

import ua.fitness.util.FitnessUtils;
import java.time.LocalDate;
import java.util.Comparator;

public record Member(String taxId, String fullName, LocalDate birthDate) implements Comparable<Member> {
    private static final Comparator<Member> NATURAL = Comparator.comparing(Member::fullName)
            .thenComparing(Member::taxId)
            .thenComparing(Member::birthDate);

    public Member {
        FitnessUtils.requireNotBlank(taxId, "taxId");
        FitnessUtils.requireNotBlank(fullName, "fullName");
        FitnessUtils.requireNotNull(birthDate, "birthDate");

        if (birthDate!=null && birthDate.plusYears(16).isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("birthDate expected at least 16 years old, got: "+birthDate);
        }
    }

    @Override
    public int compareTo(Member other) {
        return NATURAL.compare(this, other);
    }
}