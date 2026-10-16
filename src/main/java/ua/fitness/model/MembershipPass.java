package ua.fitness.model;

import ua.fitness.util.FitnessUtils;
import java.time.LocalDate;

public record MembershipPass(String passCode, Member member, PassType type, LocalDate startDate, LocalDate endDate, double price) {
    public MembershipPass {
        FitnessUtils.requireNotBlank(passCode, "passCode");
        FitnessUtils.requireNotNull(member, "member");
        FitnessUtils.requireNotNull(type, "type");
        FitnessUtils.requireNotNull(startDate, "startDate");
        FitnessUtils.requireNotNull(endDate, "endDate");
        
        if (startDate != null && endDate != null && !endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("endDate expected strictly after startDate, got: " + endDate);
        }
        FitnessUtils.requirePositive(price, "price");
    }

    public static MembershipPass of(String passCode, Member member, PassType type, LocalDate startDate, LocalDate endDate, double price) {
        return new MembershipPass(passCode, member, type, startDate, endDate, price);
    }
}