package ua.fitness.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.fitness.model.Member;
import ua.fitness.model.MembershipPass;
import ua.fitness.model.PassType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RepositoriesTest {

    private MemberRepository members;
    private MembershipPassRepository passes;

    private Member olha;
    private Member andrii;
    private Member olhaM;
    private MembershipPass p1;
    private MembershipPass p2;
    private MembershipPass p3;
    private MembershipPass p4;

    @BeforeEach
    void setUp() {
        olha = new Member("M-1", "Olha Koval", LocalDate.of(2000, 5, 17));
        andrii = new Member("M-2", "Andrii Bondar", LocalDate.of(1995, 3, 10));
        olhaM = new Member("M-3", "Olha Melnyk", LocalDate.of(1990, 1, 1));

        p1 = new MembershipPass("P-1", olha, PassType.STANDARD,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30), 100.2);
        p2 = new MembershipPass("P-2", andrii, PassType.PREMIUM,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30), 100.7);
        p3 = new MembershipPass("P-3", olhaM, PassType.STANDARD,
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 12, 31), 300.0);
        p4 = new MembershipPass("P-4", olha, PassType.VIP,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 9, 30), 100.2);

        members = new MemberRepository();
        members.addAll(List.of(olha, andrii, olhaM));
        passes = new MembershipPassRepository();
        passes.addAll(List.of(p3, p1, p4, p2));
    }

    @Test
    @DisplayName("1. Collections.sort uses natural order (Comparable)")
    void naturalOrder() {
        List<MembershipPass> list = new ArrayList<>(List.of(p3, p2, p1));
        Collections.sort(list);
        assertEquals(List.of(p1, p2, p3), list);
    }

    @Test
    @DisplayName("2. Multi-level comparator orders equal first keys by the second key")
    void multiLevelComparator() {
        assertEquals(List.of(p2, p1, p4, p3), passes.findAllSortedByEndDateThenPriceDesc());
    }

    @Test
    @DisplayName("3. findAllSortedByIdentity ASC and DESC are mutually reversed")
    void sortedByIdentityBothWays() {
        List<MembershipPass> asc = passes.findAllSortedByIdentity(SortOrder.ASC);
        List<MembershipPass> desc = passes.findAllSortedByIdentity(SortOrder.DESC);
        List<MembershipPass> reversed = new ArrayList<>(asc);
        Collections.reverse(reversed);

        assertEquals("P-1", asc.get(0).passCode());
        assertEquals(reversed, desc);
    }

    @Test
    @DisplayName("4. Prices differing by less than one are sorted correctly")
    void fractionalPrices() {
        assertTrue(p1.compareTo(p2) < 0);
        assertTrue(p2.compareTo(p1) > 0);
        assertEquals(p1, passes.findAllSortedByPrice().get(0));
    }

    @Test
    @DisplayName("5. TreeSet with a comparator inconsistent with equals drops an element")
    void treeSetInconsistentWithEquals() {
        Member a = new Member("M-10", "Same Name", LocalDate.of(1999, 1, 1));
        Member b = new Member("M-11", "Same Name", LocalDate.of(1998, 1, 1));
        TreeSet<Member> set = new TreeSet<>(Comparator.comparing(Member::fullName));

        set.add(a);
        set.add(b);

        assertNotEquals(a, b);
        assertEquals(1, set.size());
    }

    @Test
    @DisplayName("6a. Range search excludes values outside the bounds")
    void rangeSearch() {
        List<MembershipPass> result = passes.findByPriceBetween(100.5, 300.0);
        assertEquals(List.of(p2, p3), result);
    }

    @Test
    @DisplayName("6b. Two-condition search does not return an object matching only one condition")
    void twoConditionSearch() {
        assertEquals(List.of(p3), passes.findByTypeAndMinPrice(PassType.STANDARD, 200.0));
    }

    //!
    @Test
    @DisplayName("6c. Text search is case-insensitive and member birth-date range is inclusive")
    void memberSearches() {
        assertEquals(List.of(olha, olhaM), members.findByNameContaining("OLHA"));
        assertEquals(List.of(andrii),
                members.findByBirthDateBetween(LocalDate.of(1995, 3, 10), LocalDate.of(1995, 3, 10)));
        assertEquals(List.of(olhaM),
                members.findByNameContainingAndBornBefore("olha", LocalDate.of(1995, 1, 1)));
    }

    @Test
    @DisplayName("7. Single-object search returns empty Optional when nothing matches")
    void findFirstEmpty() {
        assertTrue(members.findFirstByNamePrefix("Zzz").isEmpty());
        assertTrue(passes.findFirstExpiringBefore(LocalDate.of(2025, 1, 1)).isEmpty());
    }

    @Test
    @DisplayName("8. groupingBy gives expected number of groups and group size")
    void grouping() {
        Map<PassType, List<MembershipPass>> groups = passes.groupByType();
        assertEquals(3, groups.size());
        assertEquals(2, groups.get(PassType.STANDARD).size());
    }

    @Test
    @DisplayName("9. Aggregation on an empty repository does not fail")
    void aggregationOnEmpty() {
        MembershipPassRepository empty = new MembershipPassRepository();
        assertEquals(0.0, empty.totalPrice());
        assertTrue(empty.averagePriceByType().isEmpty());
        assertTrue(empty.groupByType().isEmpty());
    }

    @Test
    @DisplayName("10. Reusing a consumed stream throws IllegalStateException")
    void streamReuse() {
        Stream<MembershipPass> stream = passes.stream();
        stream.count();
        assertThrows(IllegalStateException.class, stream::count);
    }

    @Test
    @DisplayName("11. Average price per type is computed per group")
    void averageByType() {
        assertEquals(200.1, passes.averagePriceByType().get(PassType.STANDARD), 1e-9);
    }

    @Test
    @DisplayName("12. findByOwnerNameContaining returns the owner's passes in natural order")
    void ownerNameSearch() {
        assertEquals(List.of(p1, p4),passes.findByOwnerNameContaining("KOVAL"));
        assertTrue(passes.findByOwnerNameContaining("zzz").isEmpty());
    }

    @Test
    @DisplayName("13. findCheapest limits and orders the result")
    void cheapest() {
        assertEquals(List.of(p1, p4),passes.findCheapest(2));
        assertEquals(List.of(p1, p4, p2, p3), passes.findCheapest(10));
        assertThrows(IllegalArgumentException.class, () -> passes.findCheapest(0));
    }

    @Test
    @DisplayName("14. findDistinctOwnerNames removes duplicates and sorts")
    void distinctOwnerNames() {
        //Olha Koval owns two passes but must appear onnce
        assertEquals(List.of("Andrii Bondar", "Olha Koval", "Olha Melnyk"),
                passes.findDistinctOwnerNames());
    }

    @Test
    @DisplayName("15. Birth date ascending, then name descending on equal dates")
    void birthDateThenNameDesc() {
        Member anna = new Member("M-20", "Anna", LocalDate.of(2000, 1, 1));
        Member boris = new Member("M-21", "Boris", LocalDate.of(2000, 1, 1));
        Member zoya = new Member("M-22", "Zoya", LocalDate.of(1990, 1, 1));
        MemberRepository repo = new MemberRepository();
        repo.addAll(List.of(anna, boris, zoya));

        //zoya is oldestso she is first; same birthDate is ordered by name DESC
        assertEquals(List.of(zoya, boris, anna), repo.findAllSortedByBirthDateThenNameDesc());
    }
}