package ua.fitness.model;

import ua.fitness.util.FitnessUtils;

public record Trainer(String trainerId, String name, Specialty specialty) {
    public Trainer {
        FitnessUtils.requireNotBlank(trainerId, "trainerId");
        FitnessUtils.requireNotBlank(name, "name");
        FitnessUtils.requireNotNull(specialty, "specialty");
    }
}
