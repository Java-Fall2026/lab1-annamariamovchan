package ua.fitness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.fitness.exception.DuplicateIdentityException;
import ua.fitness.model.*;
import ua.fitness.repository.GenericRepository;
import ua.fitness.util.TrainerLoader;

import java.time.LocalDate;
import java.util.List;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting Lab 4 execution");

   
        System.out.println("1. Три репозиторії різних типів");

        GenericRepository<Trainer> trainerRepo =
                new GenericRepository<>("trainers", Trainer::trainerId);
        GenericRepository<Member> memberRepo =
                new GenericRepository<>("members", m -> m.taxId());

        GenericRepository<WorkoutSession> sessionRepo = new GenericRepository<>("sessions",
                s -> s.getTrainer().trainerId() + "|" + s.getPass().passCode() + "|" + s.getSessionDate());

        Trainer ivan = new Trainer("TR-01", "Ivan Ivanenko", Specialty.GYM);
        Trainer anna = new Trainer("TR-02", "Anna Petrenko", Specialty.YOGA);
        Member member = new Member("M-1", "Olha Koval", LocalDate.of(2000, 5, 17));
        MembershipPass pass = new MembershipPass("PASS-1", member, PassType.STANDARD,
                LocalDate.now(), LocalDate.now().plusDays(30), 1500.0);
        WorkoutSession session = new WorkoutSession(ivan, pass, LocalDate.now().plusDays(3), 60);

        trainerRepo.add(ivan);
        trainerRepo.add(anna);
        memberRepo.add(member);
        sessionRepo.add(session);

        System.out.println("Тренерів: " + trainerRepo.size()
                + ", учасників: " + memberRepo.size()
                + ", тренувань: " + sessionRepo.size());

        
        System.out.println("\n2. Пошук за identity");
        System.out.println("TR-01 -> " + trainerRepo.findByIdentity("TR-01")
                .map(Trainer::name).orElse("не знайдено"));
        System.out.println("TR-99 -> " + trainerRepo.findByIdentity("TR-99")
                .map(Trainer::name).orElse("не знайдено"));
        System.out.println("M-1 -> " + memberRepo.findByIdentity("M-1")
                .map(Member::fullName).orElse("не знайдено"));
        System.out.println("M-404 -> " + memberRepo.findByIdentity("M-404")
                .map(Member::fullName).orElse("не знайдено"));

       
        System.out.println("\n3. Наповнення репозиторію з CSV-файла");
        GenericRepository<Trainer> csvRepo =
                new GenericRepository<>("csv-trainers", Trainer::trainerId);
        List<Trainer> loaded = TrainerLoader.loadTrainers("data/trainers.csv");
        System.out.println("Зчитано коректних рядків: " + loaded.size());

        Trainer csvDuplicate = null;
        for (Trainer t : loaded) {
            try {
                csvRepo.add(t);
            } catch (DuplicateIdentityException e) {
                csvDuplicate = t;
                System.out.println("Дублікат із CSV відхилено: " + e.getMessage());
            }
        }
        System.out.println("У репозиторії після CSV: " + csvRepo.size());

        if (csvDuplicate != null) {
            boolean added = csvRepo.addIfAbsent(csvDuplicate);
            System.out.println("addIfAbsent для того ж дубліката повернув: " + added
                    + " (розмір: " + csvRepo.size() + ")");
        }


        System.out.println("\n4. Дублікат: add проти addIfAbsent");
        try {
            trainerRepo.add(ivan);
            System.out.println("add: дублікат додано (так не має бути)");
        } catch (DuplicateIdentityException e) {
            System.out.println("add: виняток -> " + e.getMessage());
        }
        boolean added = trainerRepo.addIfAbsent(ivan);
        System.out.println("addIfAbsent: повернув " + added + ", виняток не виник");
        System.out.println("Розмір репозиторію тренерів: " + trainerRepo.size());

    
        System.out.println("\n5. Видалення");
        System.out.println("Видалення TR-02 (наявний): " + trainerRepo.removeByIdentity("TR-02"));
        System.out.println("Видалення TR-02 ще раз (відсутній): " + trainerRepo.removeByIdentity("TR-02"));
        System.out.println("Розмір після видалення: " + trainerRepo.size());


        System.out.println("\n6. Логування всіх трьох рівнів");
        System.out.println("INFO і WARN логує репозиторій (додавання, дублікати, пошук, видалення)");
        try {
            trainerRepo.add(null);
        } catch (IllegalArgumentException e) {
  
            System.out.println("ERROR: add(null) -> " + e.getMessage());
        }

    
        System.out.println("\n7. Перевірки з ЛР3");
        TrainerLoader.loadTrainers("data/not_exists.csv");
        try {
            session.setDurationMinutes(-15);
        } catch (IllegalArgumentException e) {
            log.error("Setter validation failed: {}", e.getMessage());
            System.out.println("Перехоплено помилку сеттера: " + e.getMessage());
        }

        log.info("Lab 4 execution finished successfully");
    }
}