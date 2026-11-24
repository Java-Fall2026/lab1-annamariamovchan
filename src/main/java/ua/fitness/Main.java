package ua.fitness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.fitness.model.Member;
import ua.fitness.model.MembershipPass;
import ua.fitness.model.PassType;
import ua.fitness.repository.MemberRepository;
import ua.fitness.repository.MembershipPassRepository;
import ua.fitness.repository.SortOrder;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.stream.Stream;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting Lab 5 execution");

        
        System.out.println("1. Наповнення двох доменних репозиторіїв");
        MemberRepository members = new MemberRepository();
        MembershipPassRepository passes = new MembershipPassRepository();

        Member olha = new Member("M-1", "Olha Koval", LocalDate.of(2000, 5, 17));
        Member andrii = new Member("M-2", "Andrii Bondar", LocalDate.of(1995, 3, 10));
        Member olhaM = new Member("M-3", "Olha Melnyk", LocalDate.of(1990, 1, 1));
        members.addAll(List.of(olha, andrii, olhaM));

        passes.addAll(List.of(
                new MembershipPass("P-1", olha, PassType.STANDARD,
                        LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30), 100.2),
                new MembershipPass("P-2", andrii, PassType.PREMIUM,
                        LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30), 100.7),
                new MembershipPass("P-3", olhaM, PassType.STANDARD,
                        LocalDate.of(2026, 2, 1), LocalDate.of(2026, 12, 31), 300.0),
                new MembershipPass("P-4", olha, PassType.VIP,
                        LocalDate.of(2026, 3, 1), LocalDate.of(2026, 9, 30), 100.2)));
        System.out.println("Учасників: " + members.size() + ", абонементів: " + passes.size());


        System.out.println("\n2. Сортування за природним порядком і за доменними критеріями");
        System.out.println("Природний порядок (ціна): ");
        passes.findAllSortedNaturally()
                .forEach(p -> System.out.println("  " + p.passCode() + " " + p.price()));
        System.out.println("За ім'ям (учасники): ");
        members.findAllSortedByName()
                .forEach(m -> System.out.println("  " + m.fullName()));
        System.out.println("Багаторівневе: endDate за зростанням, потім ціна за спаданням:");
        passes.findAllSortedByEndDateThenPriceDesc()
                .forEach(p -> System.out.println("  " + p.passCode() + " " + p.endDate() + " " + p.price()));

       
        System.out.println("\n3. findAllSortedByIdentity в обидва боки");
        System.out.println("ASC: " + passes.findAllSortedByIdentity(SortOrder.ASC)
                .stream().map(MembershipPass::passCode).toList());
        System.out.println("DESC: " + passes.findAllSortedByIdentity(SortOrder.DESC)
                .stream().map(MembershipPass::passCode).toList());

        System.out.println("\n4. TreeSet із компаратором за ім'ям");
        TreeSet<Member> byName = new TreeSet<>(Comparator.comparing(Member::fullName));
        Member twin1 = new Member("M-10", "Same Name", LocalDate.of(1999, 1, 1));
        Member twin2 = new Member("M-11", "Same Name", LocalDate.of(1998, 1, 1));
        byName.add(twin1);
        byName.add(twin2);
        System.out.println("Додано 2 різні об'єкти з однаковим ім'ям, у наборі: " + byName.size());

        System.out.println("\n5. Пошук за кожним критерієм");
        System.out.println("Ім'я містить 'olha': "
                + members.findByNameContaining("olha").stream().map(Member::fullName).toList());
        System.out.println("Народились 1990-01-01..1995-12-31: "
                + members.findByBirthDateBetween(LocalDate.of(1990, 1, 1), LocalDate.of(1995, 12, 31))
                .stream().map(Member::fullName).toList());
        System.out.println("Ім'я містить 'olha' і народилась до 1995: "
                + members.findByNameContainingAndBornBefore("olha", LocalDate.of(1995, 1, 1))
                .stream().map(Member::fullName).toList());
        System.out.println("Власник містить 'koval': "
                + passes.findByOwnerNameContaining("koval").stream().map(MembershipPass::passCode).toList());
        System.out.println("Ціна 100.5..300: "
                + passes.findByPriceBetween(100.5, 300.0).stream().map(MembershipPass::passCode).toList());
        System.out.println("STANDARD і ціна від 200: "
                + passes.findByTypeAndMinPrice(PassType.STANDARD, 200.0)
                .stream().map(MembershipPass::passCode).toList());
        System.out.println("Два найдешевші: "
                + passes.findCheapest(2).stream().map(MembershipPass::passCode).toList());
        System.out.println("Унікальні власники: " + passes.findDistinctOwnerNames());

        System.out.println("\n6. Результат, якого немає (Optional)");
        System.out.println("Префікс 'And': " + members.findFirstByNamePrefix("And")
                .map(Member::fullName).orElse("не знайдено"));
        System.out.println("Префікс 'Zzz': " + members.findFirstByNamePrefix("Zzz")
                .map(Member::fullName).orElse("не знайдено"));
        passes.findFirstExpiringBefore(LocalDate.of(2026, 7, 1))
                .ifPresent(p -> System.out.println("Перший, що спливає до 2026-07-01: " + p.passCode()));
        System.out.println("Спливає до 2025-01-01: " + passes.findFirstExpiringBefore(LocalDate.of(2025, 1, 1))
                .map(MembershipPass::passCode)
                .orElseGet(() -> "немає таких абонементів"));
        try {
            passes.findFirstExpiringBefore(LocalDate.of(2025, 1, 1))
                    .orElseThrow(() -> new NoSuchElementException("No pass expires before 2025-01-01"));
        } catch (NoSuchElementException e) {
            System.out.println("orElseThrow: " + e.getMessage());
        }

        System.out.println("\n7. Групування й агрегація");
        passes.groupByType().forEach((type, list) ->
                System.out.println("  " + type + " -> " + list.stream().map(MembershipPass::passCode).toList()));
        passes.averagePriceByType().forEach((type, avg) ->
                System.out.printf("  середня ціна %s: %.2f%n", type, avg));
        System.out.println("Сума цін: " + passes.totalPrice());

        System.out.println("\n8. Спроба пройти стрім двічі");
        Stream<MembershipPass> stream = passes.stream();
        System.out.println("Перший прохід, кількість: " + stream.count());
        try {
            stream.count();
        } catch (IllegalStateException e) {
            log.error("Stream reuse failed: {}", e.getMessage());
            System.out.println("Перехоплено: " + e.getMessage());
        }

        log.info("Lab 5 execution finished successfully");
    }
}