package ua.fitness.repository;

import ua.fitness.model.Specialty;
import ua.fitness.model.Trainer;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

public class SearchBenchmark {
    private static final int SIZE = 100_000;
    private static final int WARMUP_ROUNDS = 5;
    private static final int SAMPLES = 7;

    private static long sink;

    public static void main(String[] args) {

        System.setProperty("logback.configurationFile", "logback-benchmark.xml");

        IdentityExtractor<Trainer> extractor = Trainer::trainerId;
        GenericRepository<Trainer> repo = new GenericRepository<>("benchmark", extractor);
        for (int i = 0; i < SIZE; i++) {
            repo.add(new Trainer("TR-" + i, "Trainer " + i, Specialty.GYM));
        }


        List<Trainer> list = repo.findAll();

        Function<String, Optional<Trainer>> mapSearch = repo::findByIdentity;
        Function<String, Optional<Trainer>> listSearch = id -> linearSearch(list, id, extractor);

        String[] names = {"first", "last", "missing"};
        String[] ids = {"TR-0", "TR-" + (SIZE - 1), "TR-MISSING"};

        System.out.println("| Scenario | Map (ns/op) | List (ns/op) | List / Map |");
        System.out.println("|---|---|---|---|");
        for (int i = 0; i < ids.length; i++) {
            double map = measure(mapSearch, ids[i], 100_000);
            // Linear search over 100k items is slow, so fewer iterations for last/missing
            int listIterations = (i == 0) ? 100_000 : 200;
            double lin = measure(listSearch, ids[i], listIterations);
            System.out.println(String.format(Locale.ROOT, "| %s | %.1f | %.1f | %.1fx |",
                    names[i], map, lin, lin / map));
        }
        System.out.println("(sink=" + sink + ")");
    }


    private static double measure(Function<String, Optional<Trainer>> search, String id, int iterations) {
        for (int w = 0; w < WARMUP_ROUNDS; w++) {
            run(search, id, iterations);
        }
        double[] samples = new double[SAMPLES];
        for (int s = 0; s < SAMPLES; s++) {
            long start = System.nanoTime();
            run(search, id, iterations);
            samples[s] = (System.nanoTime() - start) / (double) iterations;
        }
        Arrays.sort(samples);
        return samples[SAMPLES / 2];
    }

    private static void run(Function<String, Optional<Trainer>> search, String id, int iterations) {
        for (int i = 0; i < iterations; i++) {
            if (search.apply(id).isPresent()) {
                sink++;
            }
        }
    }

    private static <E> Optional<E> linearSearch(
            List<E> items, String identity, IdentityExtractor<E> extractor) {
        for (E item : items) {
            if (identity.equals(extractor.identityOf(item))) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
}