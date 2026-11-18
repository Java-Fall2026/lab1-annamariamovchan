package ua.fitness.model;

import ua.fitness.util.FitnessUtils;
import java.time.LocalDate;

public class WorkoutSession {
    private final Trainer trainer;
    private final MembershipPass pass;
    private final LocalDate sessionDate;
    private int durationMinutes; 

    public WorkoutSession(Trainer trainer, MembershipPass pass, LocalDate sessionDate, int durationMinutes) {
        FitnessUtils.requireNotNull(trainer, "trainer");
        FitnessUtils.requireNotNull(pass, "pass");
        FitnessUtils.requireNotNull(sessionDate, "sessionDate");
        
        if (sessionDate != null && pass != null) {
            if (sessionDate.isBefore(pass.startDate()) || sessionDate.isAfter(pass.endDate())) {
                throw new IllegalArgumentException("sessionDate expected between pass dates, got: " + sessionDate);
            }
        }
        
        this.trainer =trainer;
        this.pass =pass;
        this.sessionDate =sessionDate;
        
        setDurationMinutes(durationMinutes);
    }

    public void setDurationMinutes(int durationMinutes) {
        FitnessUtils.requireInRange(durationMinutes, 30, 180, "durationMinutes");
        this.durationMinutes = durationMinutes;
    }

    public Trainer getTrainer() {return trainer; }
    public MembershipPass getPass() {return pass; }
    public LocalDate getSessionDate() {return sessionDate; }
    public int getDurationMinutes() {return durationMinutes; }
}