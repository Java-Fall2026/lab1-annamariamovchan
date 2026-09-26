package ua.fitness.model;

import ua.common.BaseEntity;
import ua.fitness.util.FitnessUtils;
import java.time.LocalDate;
import java.util.Objects;

public class MembershipPass extends BaseEntity {

    private final String passCode; 
    private final Member member;
    private final String type;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double price;

    private MembershipPass(String passCode, Member member, String type, LocalDate startDate, LocalDate endDate, double price) {
        super();
        FitnessUtils.requireNotBlank(passCode, "passCode");
        FitnessUtils.requireNotNull(member, "member");
        
        String normalizedType = FitnessUtils.normalize(type);
        FitnessUtils.requireValidPassType(normalizedType);
        
        FitnessUtils.requireStrictlyAfter(endDate, startDate, "endDate", "startDate");
        FitnessUtils.requirePositive(price, "price");

        this.passCode = passCode;
        this.member = member;
        this.type = normalizedType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
    }

    public static MembershipPass of(String passCode, Member member, String type, LocalDate startDate, LocalDate endDate, double price) {
        return new MembershipPass(passCode, member, type, startDate, endDate, price);
    }

    public String getPassCode() {
        return passCode;
    }

    public Member getMember() {
        return member;
    }

    public String getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o==null || getClass() != o.getClass()) return false;
        MembershipPass that = (MembershipPass) o;
        return Objects.equals(passCode, that.passCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passCode);
    }

    @Override
    public String toString() {
        return "MembershipPass{"+"passCode='"+passCode+'\''+", member=" +member.getTaxId() + 
                ", type='" + type + '\'' +
                ", startDate=" +startDate+
                ", endDate=" +endDate+
                ", price=" +price+
                ", createdAt=" + createdAt +
                '}';
    }
}