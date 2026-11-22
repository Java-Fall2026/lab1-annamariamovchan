package ua.fitness.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.fitness.model.MembershipPass;
import ua.fitness.model.PassType;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MembershipPassRepository extends GenericRepository<MembershipPass> {
    private static final Logger log = LoggerFactory.getLogger(MembershipPassRepository.class);

    public MembershipPassRepository() {
        super("passes", MembershipPass::passCode);
    }


    public List<MembershipPass> findAllSortedByPrice() {
        List<MembershipPass> result = findAllSorted(Comparator.comparingDouble(MembershipPass::price));
        log.info("Sorted passes by price ASC: {} items", result.size());
        return result;
    }

    public List<MembershipPass> findAllSortedByEndDateThenPriceDesc() {
        List<MembershipPass> result = findAllSorted(
                Comparator.comparing(MembershipPass::endDate)
                        .thenComparing(Comparator.comparingDouble(MembershipPass::price).reversed()));
        log.info("Sorted passes by endDate ASC, price DESC: {} items", result.size());
        return result;
    }


    public List<MembershipPass> findByOwnerNameContaining(String part) {
        if (part == null || part.isBlank()) {
            throw new IllegalArgumentException("search text must not be blank");
        }
        String needle = part.toLowerCase();
        List<MembershipPass> result = stream()
                .filter(p -> p.member().fullName().toLowerCase().contains(needle))
                .sorted()
                .toList();
        logSearch("owner name contains '" + part + "'", result.size());
        return result;
    }

    public List<MembershipPass> findByPriceBetween(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("invalid price range: " + min + " .. " + max);
        }
        List<MembershipPass> result = stream()
                .filter(p -> p.price() >= min && p.price() <= max)
                .sorted()
                .toList();
        logSearch("price in " + min + " .. " + max, result.size());
        return result;
    }

    public List<MembershipPass> findByTypeAndMinPrice(PassType type, double minPrice) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        List<MembershipPass> result = stream()
                .filter(p -> p.type() == type)
                .filter(p -> p.price() >= minPrice)
                .sorted()
                .toList();
        logSearch("type " + type + " and price >= " + minPrice, result.size());
        return result;
    }

    public Optional<MembershipPass> findFirstExpiringBefore(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        Optional<MembershipPass> result = stream()
                .filter(p -> p.endDate().isBefore(date))
                .sorted(Comparator.comparing(MembershipPass::endDate))
                .findFirst();
        logSearch("first pass expiring before " + date, result.isPresent() ? 1 : 0);
        return result;
    }

    public List<MembershipPass> findCheapest(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive, got: " + n);
        }
        List<MembershipPass> result = stream()
                .sorted(Comparator.comparingDouble(MembershipPass::price))
                .limit(n)
                .toList();
        logSearch("cheapest " + n, result.size());
        return result;
    }

    public List<String> findDistinctOwnerNames() {
        List<String> result = stream()
                .map(p -> p.member().fullName())
                .distinct()
                .sorted()
                .toList();
        logSearch("distinct owner names", result.size());
        return result;
    }


    public Map<PassType, List<MembershipPass>> groupByType() {
        Map<PassType, List<MembershipPass>> result = stream()
                .collect(Collectors.groupingBy(
                        MembershipPass::type,
                        () -> new EnumMap<PassType, List<MembershipPass>>(PassType.class),
                        Collectors.toList()));
        log.info("Grouped passes by type: {} groups", result.size());
        return result;
    }

    public Map<PassType, Double> averagePriceByType() {
        Map<PassType, Double> result = stream()
                .collect(Collectors.groupingBy(
                        MembershipPass::type,
                        () -> new EnumMap<PassType, Double>(PassType.class),
                        Collectors.averagingDouble(MembershipPass::price)));
        log.info("Average price by type: {} groups", result.size());
        return result;
    }

    public List<MembershipPass> findAllSortedNaturally() {
    List<MembershipPass> result = findAllSorted(Comparator.naturalOrder());
    log.info("Sorted passes by natural order (price, passCode, ...): {} items", result.size());
    return result;
    }


    public double totalPrice() {
        double total = stream().mapToDouble(MembershipPass::price).sum();
        log.info("Total price of passes: {}", total);
        return total;
    }

    private static void logSearch(String criterion, int found) {
        if (found == 0) {
            log.warn("Search by {} found nothing", criterion);
        } else {
            log.info("Search by {} found {} items", criterion, found);
        }
    }
}