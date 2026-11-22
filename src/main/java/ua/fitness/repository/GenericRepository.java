package ua.fitness.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.fitness.exception.DuplicateIdentityException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.Comparator;
import java.util.stream.Stream;

public class GenericRepository<T> {
    private static final Logger log = LoggerFactory.getLogger(GenericRepository.class);

    private final String name;
    private final IdentityExtractor<T> extractor;
    private final Map<String, T> items = new LinkedHashMap<>();

    public GenericRepository(String name, IdentityExtractor<T> extractor) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (extractor == null) {
            throw new IllegalArgumentException("extractor must not be null");
        }
        this.name = name;
        this.extractor = extractor;
    }

    public void add(T item) {
        String id = validatedIdentity(item);
        if (items.containsKey(id)) {
            log.warn("Duplicate rejected in '{}': {}", name, id);
            throw new DuplicateIdentityException(id, name);
        }
        items.put(id, item);
        log.info("Added to '{}': {}", name, id);
    }

    public boolean addIfAbsent(T item) {
        String id = validatedIdentity(item);
        if (items.containsKey(id)) {
            log.warn("Duplicate skipped in '{}': {}", name, id);
            return false;
        }
        items.put(id, item);
        log.info("Added to '{}': {}", name, id);
        return true;
    }


    public void addAll(Collection<T> toAdd) {
        if (toAdd == null) {
            log.error("addAll called with null collection in '{}'", name);
            throw new IllegalArgumentException("items must not be null");
        }
        for (T item : toAdd) {
            add(item);
        }
    }

    public boolean removeByIdentity(String identity) {
        if (items.remove(identity) != null) {
            log.info("Removed from '{}': {}", name, identity);
            return true;
        }
        log.warn("Nothing to remove in '{}': {}", name, identity);
        return false;
    }

    public Optional<T> findByIdentity(String identity) {
        Optional<T> result = Optional.ofNullable(items.get(identity));
        if (result.isPresent()) {
            log.info("Found in '{}': {}", name, identity);
        } else {
            log.warn("Not found in '{}': {}", name, identity);
        }
        return result;
    }

    public List<T> findAll() {
        return List.copyOf(items.values());
    }

    public List<T> findAllSorted(Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }
        return items.values().stream()
                .sorted(comparator)
                .toList();
    }

    public List<T> findAllSortedByIdentity(SortOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("order must not be null");
        }
        Comparator<String> byKey = order == SortOrder.ASC
                ? Comparator.naturalOrder()
                : Comparator.reverseOrder();
        List<T> result = items.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(byKey))
                .map(Map.Entry::getValue)
                .toList();
        log.info("Sorted '{}' by identity {}: {} items", name, order, result.size());
        return result;
    }

    public Stream<T> stream() {
        return items.values().stream();
    }    

    public int size() {
        return items.size();
    }

    private String validatedIdentity(T item) {
        if (item == null) {
            log.error("Null item rejected in '{}'", name);
            throw new IllegalArgumentException("item must not be null");
        }
        String id = extractor.identityOf(item);
        if (id == null || id.isBlank()) {
            log.error("Blank identity rejected in '{}'", name);
            throw new IllegalArgumentException("identity must not be blank");
        }
        return id;
    }
}