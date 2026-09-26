package ua.fitness.model;

import ua.common.BaseEntity;
import ua.fitness.util.FitnessUtils;
import java.time.LocalDate;
import java.util.Objects;

public class WorkoutSession extends BaseEntity {

    private final Trainer trainer;
    private final MembershipPass pass;
    private final LocalDate sessionDate;
    private final int durationMinutes;

    private WorkoutSession(Trainer trainer,MembershipPass pass,LocalDate sessionDate,int durationMinutes) {
        super();
        FitnessUtils.requireNotNull(trainer, "trainer");
        FitnessUtils.requireNotNull(pass, "pass");
        
        FitnessUtils.requireDateInBounds(sessionDate, pass.getStartDate(), pass.getEndDate(), "sessionDate");
        
        FitnessUtils.requireInRange(durationMinutes, 30, 180, "durationMinutes");

        this.trainer= trainer;
        this.pass =pass;
        this.sessionDate=sessionDate;
        this.durationMinutes=durationMinutes;
    }

    public static WorkoutSession of(Trainer trainer, MembershipPass pass,LocalDate sessionDate,int durationMinutes) {
        return new WorkoutSession(trainer, pass, sessionDate, durationMinutes);
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public MembershipPass getPass() {
        return pass;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o==null || getClass()!= o.getClass()) return false;
        WorkoutSession that = (WorkoutSession) o;
        return Objects.equals(trainer, that.trainer) &&
               Objects.equals(pass, that.pass) &&
               Objects.equals(sessionDate, that.sessionDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainer, pass, sessionDate);
    }

    @Override
    public String toString() {
        return "WorkoutSession{" +
                "trainer=" + trainer.getTrainerId() +
                ", pass=" + pass.getPassCode() +
                ", sessionDate=" + sessionDate +
                ", durationMinutes=" + durationMinutes +
                ", createdAt=" + createdAt +
                '}';
    }
}