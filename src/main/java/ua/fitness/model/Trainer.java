package ua.fitness.model;

import ua.common.BaseEntity;
import ua.fitness.util.FitnessUtils;
import java.util.Objects;

public class Trainer extends BaseEntity {

    private final String trainerId; 
    private final String name;
    private final String specialty;

    public Trainer(String trainerId, String name, String specialty) {
        super();
        FitnessUtils.requireNotBlank(trainerId, "trainerId");
        FitnessUtils.requireNotBlank(name, "name");

        String normalizedSpecialty = FitnessUtils.normalize(specialty);
        FitnessUtils.requireValidSpecialty(normalizedSpecialty);

        this.trainerId = trainerId;
        this.name = name;
        this.specialty = normalizedSpecialty;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o==null|| getClass() !=o.getClass()) return false;
        Trainer trainer=(Trainer) o;
        return Objects.equals(trainerId, trainer.trainerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainerId);
    }

    @Override
    public String toString() {
        return "Trainer{" +
                "trainerId='"+trainerId + '\'' +
                ", name='"+name + '\'' +
                ", specialty='"+specialty + '\'' +
                ", createdAt="+createdAt +
                '}';
    }
}