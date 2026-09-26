package ua.fitness;

import ua.fitness.model.Member;
import ua.fitness.model.MembershipPass;
import ua.fitness.model.Trainer;
import ua.fitness.model.WorkoutSession;
import ua.fitness.util.FitnessUtils;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("1. Створення об'єктів (конструктор і фабрика) та нормалізація");
        
        Member member = new Member("TAX12345", "Anna Motin", LocalDate.of(2005, 5, 15));
        
        Trainer trainer1 = new Trainer("TR-01", "Ivan Olivka", "  yoga ");
        
        MembershipPass pass = MembershipPass.of(
                "PASS-999", member, "vip",
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 11, 1), 1500.0
        );
        
        WorkoutSession session = WorkoutSession.of(trainer1, pass, LocalDate.of(2026, 10, 15), 60);

        System.out.println("Нормалізована спеціальність тренера: "+trainer1.getSpecialty());
        System.out.println("Нормалізований тип абонемента: "+pass.getType());


        System.out.println("\n2. Демонстрація порушення правил (try/catch)");
        
        try {
            new Member("TAX999", "Young Boy", LocalDate.of(2015, 1, 1));
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка віку: " + e.getMessage());
        }

        try {
            new Trainer("TR-02", "Petro", "BOXING");
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка спеціальності: " + e.getMessage());
        }

        try {
            MembershipPass.of("ERR", member, "STANDARD", 
                    LocalDate.of(2026, 10, 1), LocalDate.of(2026, 9, 1), 100);
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка дат: " + e.getMessage());
        }


        System.out.println("\n3. Порівняння об'єктів (== та equals)");
        Trainer trainer2 = new Trainer("TR-01", "Ivan Olijnyk", "YOGA");
        
        System.out.println("trainer1 == trainer2 (різні ділянки пам'яті): "+(trainer1 == trainer2));
        System.out.println("trainer1.equals(trainer2) (однаковий ID): "+trainer1.equals(trainer2));
        System.out.println("trainer1.hashCode() == trainer2.hashCode(): "+(trainer1.hashCode() == trainer2.hashCode()));


        System.out.println("\n4. Виклик обчислюваних методів з Utils");
        System.out.println("Тривалість абонемента (днів): "+FitnessUtils.passDurationDays(pass));
        System.out.println("Вартість одного дня (грн): "+FitnessUtils.pricePerDay(pass));


        System.out.println("\n5. Вивід усіх сутностей через toString()");
        System.out.println(member);
        System.out.println(trainer1);
        System.out.println(pass);
        System.out.println(session);

        // 6. Uncompilable lines (Lab requirement) 
        // Reason 1: ValidationHelper is package-private, so it is inaccessible outside the util package.
        // ua.fitness.util.ValidationHelper.requireNotNull(member, "member");

        // Reason 2: MembershipPass constructor is private, objects can only be created via the static of() method.
        // MembershipPass errorPass = new MembershipPass("CODE", member, "VIP", LocalDate.now(), LocalDate.now().plusDays(1), 100);
    }
}