package ua.fitness.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.fitness.exception.InvalidDataException;
import ua.fitness.model.Specialty;
import ua.fitness.model.Trainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TrainerLoader {
    private static final Logger log = LoggerFactory.getLogger(TrainerLoader.class);

    public static List<Trainer> loadTrainers(String filePath) {
        List<Trainer> trainers = new ArrayList<>();
        Path path=Path.of(filePath);

        log.info("Starting to load trainers from file: {}", filePath);

        try (BufferedReader br=Files.newBufferedReader(path)) {
            String line=br.readLine(); 
            int lineNumber=1;

            while ((line=br.readLine()) !=null) {
                lineNumber++;
                try {
                    Trainer trainer =parseTrainerFromLine(line, lineNumber);
                    trainers.add(trainer);
                } catch (InvalidDataException e) {
                    log.warn("Skipping invalid row at line {}: {}",lineNumber, e.getMessage());
                }
            }
            log.info("Successfully loaded {} trainers.", trainers.size());

        } catch (IOException e) {
            log.error("Failed to read file. Program continues. File: {}, Error: {}", filePath, e.getMessage());
        } finally {
            log.info("File reading process finished for path: {}", filePath);
        }
        return trainers;
    }

    public static Trainer parseTrainerFromLine(String line, int lineNumber) throws InvalidDataException {
        String[] parts = line.split(",");

        if (parts.length !=3) {
            throw new InvalidDataException("Line " +lineNumber+": Expected 3 columns, got "+parts.length);
        }
        try {
            String id =parts[0].trim();
            String name= parts[1].trim();

            if (name.isEmpty()) {
                throw new IllegalArgumentException("Trainer name cannot be empty");
            }

            Specialty specialty = Specialty.valueOf(parts[2].trim().toUpperCase());

            return new Trainer(id, name,specialty);

        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidDataException("Line " +lineNumber+": Invalid data format", e);
        }
    }
}