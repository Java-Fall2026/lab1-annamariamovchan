package ua.fitness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.fitness.model.*;
import ua.fitness.util.TrainerLoader;

import java.time.LocalDate;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting Lab 3 execution");

        System.out.println("1. Читання файлу (з коректними та некоректними рядками)");
        var trainers = TrainerLoader.loadTrainers("data/trainers.csv");
        System.out.println("Успішно завантажено: " + trainers.size());

        System.out.println("\n2. Спроба прочитати відсутній файл ");
        var missingTrainers = TrainerLoader.loadTrainers("data/not_exists.csv");

        System.out.println("\n3. Некоректне значення в сеттері змінюваного класу");
        try {
            Trainer dummyTrainer = new Trainer("T-1", "Test", Specialty.GYM);
            Member dummyMember = new Member("M-1", "Test Member", LocalDate.of(2000, 1, 1));
            MembershipPass dummyPass = new MembershipPass(
                "PASS-1", dummyMember, PassType.values()[0], 
                LocalDate.now(), LocalDate.now().plusDays(30), 1000.0
            );

            WorkoutSession session = new WorkoutSession(dummyTrainer, dummyPass, LocalDate.now().plusDays(5), 60);
            log.info("Session created. Attempting to set invalid duration...");
            
            session.setDurationMinutes(-15); 
            
        } catch (IllegalArgumentException e) {
            log.error("Setter validation failed: {}", e.getMessage());
            System.out.println("Перехоплено помилку сеттера: " + e.getMessage());
        }

        System.out.println("\n4. Логування всіх трьох рівнів продемонстровано вище");
        log.info("Lab 3 execution finished successfully");
    }
}