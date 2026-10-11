package ua.fitness;

import ua.fitness.model.Member;
import ua.fitness.model.MembershipPass;
import ua.fitness.model.PassType;
import ua.fitness.model.Specialty;
import ua.fitness.model.Trainer;
import ua.fitness.util.FitnessUtils;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        System.out.println("1. Створення record та звернення до компонентів");
        Member member = new Member("TAX123", "Anna Motin", LocalDate.of(2005, 5, 15));
        System.out.println("Member TaxID: " + member.taxId());

        System.out.println("\n2. Робота з Enum");
        System.out.print("Усі типи абонементів: ");
        for (PassType type : PassType.values()) {
            System.out.print(type + "(factor: " +type.getPriceFactor() + ")");
        }
        System.out.println();

        System.out.println("\nEnum: valueOf з помилкою (try/catch) ");
        try {
            PassType wrongType = PassType.valueOf("SUPER_VIP");
        } catch (IllegalArgumentException e) {
            System.out.println("Спіймано помилку Enum: " + e.getMessage());
        }

        System.out.println("\n3. Робота switch-виразів ");
        System.out.println("Зона для CROSSFIT: " +FitnessUtils.getTrainerZone(Specialty.CROSSFIT));
        System.out.println("Години доступу для STUDENT: " +FitnessUtils.getAllowedAccessHours(PassType.STUDENT));

        System.out.println("\n4. Record з невалідними даними ");
        try {
            Member invalidMember =new Member("TAX999", "Young Boy", LocalDate.of(2020, 1, 1));
        } catch (IllegalArgumentException e) {
            System.out.println("Спіймано помилку конструктора record: " +e.getMessage());
        }

        System.out.println("\n5. Порівняння record-об'єктів (equals /hashCode) ");
        Member member1 = new Member("TAX777", "Ivan", LocalDate.of(2000, 1, 1));
        Member member2 = new Member("TAX777", "Ivan", LocalDate.of(2000, 1, 1));
        
        System.out.println("member1 == member2 (різні об'єкти в пам'яті): " +(member1 == member2));
        System.out.println("member1.equals(member2) (однакові дані): " +member1.equals(member2));
        System.out.println("member1.hashCode() == member2.hashCode(): " +(member1.hashCode() == member2.hashCode()));

        System.out.println("\n6. Вивід record через згенерований toString() ");
        Trainer trainer = new Trainer("TR-01", "Max Power", Specialty.GYM);
        MembershipPass pass = MembershipPass.of("PASS-001", member, PassType.VIP, 
                LocalDate.now(), LocalDate.now().plusMonths(1), 1500.0);
        
        System.out.println(member);
        System.out.println(trainer);
        System.out.println(pass);
    }
}