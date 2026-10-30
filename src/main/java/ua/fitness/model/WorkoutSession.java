package ua.fitness.model;

import ua.fitness.util.FitnessUtils;
import java.time.LocalDate;

public record WorkoutSession(Trainer trainer, MembershipPass pass, LocalDate sessionDate, int durationMinutes) {
    public WorkoutSession {
        FitnessUtils.requireNotNull(trainer, "trainer");
        FitnessUtils.requireNotNull(pass, "pass");
        FitnessUtils.requireNotNull(sessionDate, "sessionDate");
        
        if (sessionDate != null && pass != null) {
            if (sessionDate.isBefore(pass.startDate()) || sessionDate.isAfter(pass.endDate())) {
                throw new IllegalArgumentException("sessionDate expected between pass dates, got: " + sessionDate);
            }
        }
        FitnessUtils.requireInRange(durationMinutes, 30, 180, "durationMinutes");
    }

    public static WorkoutSession of(Trainer trainer, MembershipPass pass, LocalDate sessionDate, int durationMinutes) {
        return new WorkoutSession(trainer, pass, sessionDate, durationMinutes);
    }
}    