package ua.fitness.util;

import ua.fitness.model.MembershipPass;
import ua.fitness.model.PassType;
import ua.fitness.model.Specialty;
import java.time.temporal.ChronoUnit;

public class FitnessUtils {

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

    public static void requireInRange(int value, int min, int max, String fieldName) {
        ValidationHelper.requireInRange(value, min, max, fieldName);
    }

    public static long passDurationDays(MembershipPass pass) {
        requireNotNull(pass, "pass");
        return ChronoUnit.DAYS.between(pass.startDate(), pass.endDate());
    }

    public static double pricePerDay(MembershipPass pass) {
        long days=passDurationDays(pass);
        return days== 0?0 : pass.price()/days;
    }

    public static String getTrainerZone(Specialty specialty) {
        requireNotNull(specialty, "specialty");
        return switch (specialty) {
            case YOGA, PILATES -> "Mind & Body Studio";
            case CROSSFIT -> "Crossfit Arena";
            case GYM -> "Main Gym Area";
        };
    }

    public static String getAllowedAccessHours(PassType type) {
        requireNotNull(type, "type");
        return switch (type) {
            case VIP, PREMIUM -> "24/7 Unlimited Access";
            case STANDARD -> "08:00 - 22:00";
            case STUDENT -> {
                System.out.println("Applying student off-peak restrictions");
                yield "08:00 - 16:00 (Off-peak)";
            }
        };
    }
}