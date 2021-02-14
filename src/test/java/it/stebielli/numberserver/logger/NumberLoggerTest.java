package it.stebielli.numberserver.logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class NumberLoggerTest {

    public static final int NUMBER = 0;

    @BeforeEach
    void setUp() throws IOException {
        clearLogFile();
    }

    @AfterEach
    void tearDown() throws IOException {
        clearLogFile();
    }

    private void clearLogFile() throws IOException {
        Files.deleteIfExists(numbersLog());
    }

    private Path numbersLog() {
        return Path.of(NumberLogger.LOG_FILE);
    }

    @Test
    void constructorCreatesNewLogFile() throws IOException, NumberLoggerInitializationException {
        Files.writeString(numbersLog(), "something");

        new NumberLogger();

        assertThat(Files.exists(numbersLog())).isTrue();
        assertThat(numbersLog().toFile().length()).isEqualTo(0);
    }

    @Test
    void logNumber() throws IOException, NumberLoggerInitializationException {
        var logger = new NumberLogger();

        logger.log(NUMBER);

        assertThat(Files.lines(numbersLog()).count()).isEqualTo(1);
        assertThat(Files.lines(numbersLog()).findFirst().get()).isEqualTo(String.valueOf(NUMBER));
    }

    @Test
    void loggedNumbersAreUnique() throws NumberLoggerInitializationException, IOException {
        var logger = new NumberLogger();

        logger.log(NUMBER);
        logger.log(NUMBER);

        assertThat(Files.lines(numbersLog()).count()).isEqualTo(1);
        assertThat(Files.lines(numbersLog()).findFirst().get()).isEqualTo(String.valueOf(NUMBER));
    }

    @Test
    void logSameNumbersFromSeparateThreads() throws NumberLoggerInitializationException, ExecutionException, InterruptedException, IOException {
        var numbers = randomHundredNumbers();

        var logger = new NumberLogger();

        var future0 = Executors.newSingleThreadExecutor().submit(() -> numbers.forEach(logger::log));
        var future1 = Executors.newSingleThreadExecutor().submit(() -> numbers.forEach(logger::log));

        future0.get();
        future1.get();

        var writtenNumbers = Files.lines(numbersLog())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        assertThat(writtenNumbers).containsExactly(numbers.toArray(new Integer[numbers.size()]));
    }

    private Set<Integer> randomHundredNumbers() {
        Set<Integer> numbers = new HashSet<>();
        Random r = new Random();
        while (numbers.size() < 100) {
            var n = Math.abs(r.nextInt());
            numbers.add(n);
        }
        return numbers;
    }
}