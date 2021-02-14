package it.stebielli.numberserver.logger;

import it.stebielli.numberserver.MockitoTest;
import it.stebielli.numberserver.reporter.NumberReporter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

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
import static org.mockito.Mockito.verify;

class NumberLoggerTest extends MockitoTest {

    public static final int NUMBER = 0;
    public static final String LOG_FILE = "numbers.log";

    @Mock
    NumberReporter numberReporter;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
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
        return Path.of(LOG_FILE);
    }

    @Test
    void constructorCreatesNewLogFile() throws IOException, NumberLoggerInitializationException {
        Files.writeString(numbersLog(), "something");

        var logger = new NumberLogger(LOG_FILE, numberReporter);

        logger.close();

        assertThat(Files.exists(numbersLog())).isTrue();
        assertThat(numbersLog().toFile().length()).isEqualTo(0);

    }

    @Test
    void logNumber() throws IOException, NumberLoggerInitializationException {
        var logger = new NumberLogger(LOG_FILE, numberReporter);

        logger.log(NUMBER);
        logger.flush();
        logger.close();

        assertThat(Files.lines(numbersLog()).count()).isEqualTo(1);
        assertThat(Files.lines(numbersLog()).findFirst().get()).isEqualTo(String.valueOf(NUMBER));
        verify(numberReporter).incrementUniques();

    }

    @Test
    void loggedNumbersAreUnique() throws NumberLoggerInitializationException, IOException {
        var logger = new NumberLogger(LOG_FILE, numberReporter);

        logger.log(NUMBER);
        logger.log(NUMBER);
        logger.flush();
        logger.close();

        assertThat(Files.lines(numbersLog()).count()).isEqualTo(1);
        assertThat(Files.lines(numbersLog()).findFirst().get()).isEqualTo(String.valueOf(NUMBER));
        verify(numberReporter).incrementUniques();
        verify(numberReporter).incrementDuplicates();

    }

    @Test
    void logSameNumbersFromSeparateThreads() throws NumberLoggerInitializationException, ExecutionException, InterruptedException, IOException {
        var numbers = randomHundredNumbers();

        var logger = new NumberLogger(LOG_FILE, numberReporter);

        var future0 = Executors.newSingleThreadExecutor().submit(() -> numbers.forEach(logger::log));
        var future1 = Executors.newSingleThreadExecutor().submit(() -> numbers.forEach(logger::log));

        future0.get();
        future1.get();

        logger.flush();
        logger.close();

        var writtenNumbers = Files.lines(numbersLog())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        assertThat(writtenNumbers).containsExactlyInAnyOrderElementsOf(numbers);
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