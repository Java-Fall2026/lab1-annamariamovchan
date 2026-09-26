package ua.fitness.model;

import ua.common.BaseEntity;
import ua.fitness.util.FitnessUtils;
import java.time.LocalDate;
import java.util.Objects;

public class Member extends BaseEntity {

    private final String taxId; 
    private final String fullName;
    private final LocalDate birthDate;

    public Member(String taxId, String fullName, LocalDate birthDate) {
        super();
        FitnessUtils.requireNotBlank(taxId, "taxId");
        FitnessUtils.requireNotBlank(fullName, "fullName");
        FitnessUtils.requireAtLeastAge(birthDate, 16, "birthDate");

        this.taxId= taxId;
        this.fullName =fullName;
        this.birthDate= birthDate;
    }

    public String getTaxId() {
        return taxId;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o ==null || getClass()!= o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(taxId, member.taxId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taxId);
    }

    @Override
    public String toString() {
        return "Member{" +
                "taxId='" + taxId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", birthDate=" + birthDate +
                ", createdAt=" + createdAt +
                '}';
    }
}