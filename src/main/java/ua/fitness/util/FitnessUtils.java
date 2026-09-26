package ua.fitness.util;

import ua.fitness.model.MembershipPass; 
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class FitnessUtils {

    static final List<String> VALID_SPECIALTIES = List.of("YOGA", "CROSSFIT", "PILATES", "GYM");
    static final List<String> VALID_PASS_TYPES = List.of("STANDARD", "PREMIUM", "VIP", "STUDENT");

    private FitnessUtils() {
    }

    public static void requireNotNull(Object obj, String fieldName) {
        ValidationHelper.requireNotNull(obj, fieldName);
    }

    public static void requireNotBlank(String str, String fieldName) {
        ValidationHelper.requireNotBlank(str, fieldName);
    }

    public static void requirePositive(double value, String fieldName) {
        ValidationHelper.requirePositive(value, fieldName);
    }

    public static void requireInRange(int value, int min, int max,String fieldName) {
        ValidationHelper.requireInRange(value, min, max,fieldName);
    }

    public static void requireAtLeastAge(LocalDate birthDate, int minAge, String fieldName) {
        ValidationHelper.requireAtLeastAge(birthDate, minAge, fieldName);
    }

    public static void requireStrictlyAfter(LocalDate target, LocalDate reference, String targetName, String refName) {
        ValidationHelper.requireStrictlyAfter(target, reference, targetName, refName);
    }

    public static void requireDateInBounds(LocalDate date, LocalDate start, LocalDate end, String fieldName) {
        ValidationHelper.requireDateInBounds(date, start, end, fieldName);
    }

    public static void requireValidSpecialty(String specialty) {
        if (!VALID_SPECIALTIES.contains(specialty)) {
            throw new IllegalArgumentException("Specialty expected one of "+VALID_SPECIALTIES+", got: "+specialty);
        }
    }

    public static void requireValidPassType(String type) {
        if (!VALID_PASS_TYPES.contains(type)) {
            throw new IllegalArgumentException("Pass type expected one of "+VALID_PASS_TYPES+", got: "+type);
        }
    }

    public static String normalize(String str) {
        return FormatHelper.normalize(str);
    }

    public static long passDurationDays(MembershipPass pass) {
        requireNotNull(pass, "MembershipPass");
        return ChronoUnit.DAYS.between(pass.getStartDate(), pass.getEndDate());
    }

    public static double pricePerDay(MembershipPass pass) {
        requireNotNull(pass, "MembershipPass");
        long days =passDurationDays(pass);
        if (days==0) {
            return pass.getPrice(); 
        }
        return pass.getPrice()/days;
    }
}