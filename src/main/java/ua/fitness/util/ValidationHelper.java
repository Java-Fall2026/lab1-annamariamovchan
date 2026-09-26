package ua.fitness.util;

import java.time.LocalDate;
import java.time.Period;


class ValidationHelper {
    private ValidationHelper() {
    }

    static void requireNotNull(Object obj, String fieldName) {
        if (obj==null) {
            throw new IllegalArgumentException(fieldName+" expected to be not null");
        }
    }

    static void requireNotBlank(String str, String fieldName) {
        if (str==null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName+ " expected to be not blank, got: '" +str+ "'");
        }
    }

    static void requirePositive(double value, String fieldName) {
        if (value<=0) {
            throw new IllegalArgumentException(fieldName+" expected strictly greater than 0, got: " +value);
        }
    }

    static void requireInRange(int value, int min, int max, String fieldName) {
        if (value<min || value>max) {
            throw new IllegalArgumentException(fieldName +" expected between " +min+" and " +max +", got: "+value);
        }
    }

    static void requireAtLeastAge(LocalDate birthDate, int minAge, String fieldName) {
        requireNotNull(birthDate, fieldName);
        int age =Period.between(birthDate, LocalDate.now()).getYears();
        if (age<minAge) {
            throw new IllegalArgumentException(fieldName+ " expected at least "+minAge+" years old, got: " + age);
        }
    }

    static void requireStrictlyAfter(LocalDate target, LocalDate reference, String targetName, String refName) {
        requireNotNull(target, targetName);
        requireNotNull(reference, refName);
        if (!target.isAfter(reference)) {
            throw new IllegalArgumentException(targetName+" expected strictly after "+refName + ", got: " +target);
        }
    }

    static void requireDateInBounds(LocalDate date, LocalDate start, LocalDate end, String fieldName) {
        requireNotNull(date, fieldName);
        if (date.isBefore(start) || date.isAfter(end)) {
            throw new IllegalArgumentException(fieldName+" expected between "+start+" and "+end+", got: " + date);
        }
    }
}
