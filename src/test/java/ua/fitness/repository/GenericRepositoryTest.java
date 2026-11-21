package ua.fitness.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.fitness.exception.DuplicateIdentityException;
import ua.fitness.model.Member;
import ua.fitness.model.Specialty;
import ua.fitness.model.Trainer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GenericRepositoryTest {

    private GenericRepository<Trainer> repo;
    private Trainer ivan;

    @BeforeEach
    void setUp() {
        repo = new GenericRepository<>("trainers", Trainer::trainerId);
        ivan = new Trainer("TR-01", "Ivan Ivanenko", Specialty.GYM);
    }

    @Test
    @DisplayName("1. add + findByIdentity finds the added item")
    void addAndFind() {
        repo.add(ivan);

        Optional<Trainer> found = repo.findByIdentity("TR-01");

        assertTrue(found.isPresent());
        assertEquals(ivan, found.get());
    }

    @Test
    @DisplayName("2. findByIdentity returns empty Optional for missing identity")
    void findMissingReturnsEmpty() {
        assertTrue(repo.findByIdentity("NOPE").isEmpty());
    }

    @Test
    @DisplayName("3. Repeated add throws DuplicateIdentityException")
    void duplicateAddThrows() {
        repo.add(ivan);

        DuplicateIdentityException e = assertThrows(DuplicateIdentityException.class,
                () -> repo.add(ivan));

        assertTrue(e.getMessage().contains("TR-01"));
        assertTrue(e.getMessage().contains("trainers"));
    }

    @Test
    @DisplayName("4. addIfAbsent returns false and does not change size")
    void addIfAbsentOnDuplicate() {
        assertTrue(repo.addIfAbsent(ivan));

        assertFalse(repo.addIfAbsent(ivan));
        assertEquals(1, repo.size());
    }

    @Test
    @DisplayName("5. removeByIdentity works for existing and missing items")
    void removeExistingAndMissing() {
        repo.add(ivan);

        assertTrue(repo.removeByIdentity("TR-01"));
        assertEquals(0, repo.size());
        assertFalse(repo.removeByIdentity("TR-01"));
    }

    @Test
    @DisplayName("6. Modifying the findAll() result does not affect the repository")
    void findAllIsProtected() {
        repo.add(ivan);

        List<Trainer> all = repo.findAll();

        assertThrows(UnsupportedOperationException.class, all::clear);
        assertEquals(1, repo.size());
    }

    @Test
    @DisplayName("7. The same repository class works with two different types")
    void worksWithDifferentTypes() {
        GenericRepository<Member> members = new GenericRepository<>("members", Member::taxId);
        Member anna = new Member("M-1", "Anna", LocalDate.of(2000, 1, 1));

        repo.add(ivan);
        members.add(anna);

        assertEquals(ivan, repo.findByIdentity("TR-01").orElseThrow());
        assertEquals(anna, members.findByIdentity("M-1").orElseThrow());
    }

    @Test
    @DisplayName("8. add(null) and blank identity throw IllegalArgumentException")
    void nullAndBlankIdentityRejected() {
        assertThrows(IllegalArgumentException.class, () -> repo.add(null));

        GenericRepository<String> strings = new GenericRepository<>("strings", s -> s);
        assertThrows(IllegalArgumentException.class, () -> strings.add("  "));
    }
}