package ua.fitness.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.fitness.model.Member;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MemberRepository extends GenericRepository<Member> {
    private static final Logger log = LoggerFactory.getLogger(MemberRepository.class);

    public MemberRepository() {
        super("members", Member::taxId);
    }

    public List<Member> findAllSortedByName() {
        List<Member> result = findAllSorted(Comparator.comparing(Member::fullName));
        log.info("Sorted members by fullName ASC: {} items", result.size());
        return result;
    }

    public List<Member> findAllSortedNaturally() {
        List<Member> result = findAllSorted(Comparator.naturalOrder());
        log.info("Sorted members by natural order (fullName, taxId, birthDate): {} items", result.size());
        return result;
    }

//fixed
    public List<Member> findAllSortedByBirthDateThenNameDesc() {
        List<Member> result=findAllSorted(
                Comparator.comparing(Member::birthDate)
                        .thenComparing(Comparator.comparing(Member::fullName).reversed()));
        log.info("Sorted members by birthDate ASC, fullName DESC: {} items", result.size());
        return result;
    }


    public List<Member> findByNameContaining(String part) {
        requireText(part);
        String needle = part.toLowerCase();
        List<Member> result = stream()
                .filter(m -> m.fullName().toLowerCase().contains(needle))
                .sorted(Comparator.comparing(Member::fullName))
                .toList();
        logSearch("fullName contains '" + part + "'", result.size());
        return result;
    }

    public List<Member> findByBirthDateBetween(LocalDate from, LocalDate to) {
        if (from == null || to == null || from.isAfter(to)) {
            throw new IllegalArgumentException("invalid date range: " + from + " .. " + to);
        }
        List<Member> result = stream()
                .filter(m -> !m.birthDate().isBefore(from) && !m.birthDate().isAfter(to))
                .sorted(Comparator.comparing(Member::birthDate))
                .toList();
        logSearch("birthDate in " + from + " .. " + to, result.size());
        return result;
    }

    public List<Member> findByNameContainingAndBornBefore(String part, LocalDate date) {
        requireText(part);
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        String needle = part.toLowerCase();
        List<Member> result = stream()
                .filter(m -> m.fullName().toLowerCase().contains(needle))
                .filter(m -> m.birthDate().isBefore(date))
                .sorted()
                .toList();
        logSearch("fullName contains '" + part + "' and birthDate before " + date, result.size());
        return result;
    }

    public Optional<Member> findFirstByNamePrefix(String prefix) {
        requireText(prefix);
        String lower = prefix.toLowerCase();
        Optional<Member> result = stream()
                .filter(m -> m.fullName().toLowerCase().startsWith(lower))
                .sorted(Comparator.comparing(Member::fullName))
                .findFirst();
        logSearch("first fullName starting with '" + prefix + "'", result.isPresent() ? 1 : 0);
        return result;
    }

    private static void requireText(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("search text must not be blank");
        }
    }

    private static void logSearch(String criterion, int found) {
        if (found == 0) {
            log.warn("Search by {} found nothing", criterion);
        } else {
            log.info("Search by {} found {} items", criterion, found);
        }
    }
}