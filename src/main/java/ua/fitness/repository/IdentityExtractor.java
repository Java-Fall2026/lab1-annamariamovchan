package ua.fitness.repository;

@FunctionalInterface
public interface IdentityExtractor<T> {
    String identityOf(T item);
}