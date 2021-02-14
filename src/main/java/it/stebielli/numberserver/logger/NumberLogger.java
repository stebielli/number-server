package it.stebielli.numberserver.logger;


import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class NumberLogger {

    public static final String LOG_FILE = "numbers.log";
    private final PrintStream printStream;
    private final Set<Integer> indexedNumbers;
    private final ReentrantLock lock;

    public NumberLogger() throws NumberLoggerInitializationException {
        this.printStream = initializePrintStream();
        this.indexedNumbers = Collections.synchronizedSet(new HashSet<>());
        this.lock = new ReentrantLock();
    }

    private PrintStream initializePrintStream() throws NumberLoggerInitializationException {
        try {
            Files.deleteIfExists(logFile());
            return new PrintStream(Files.newOutputStream(logFile()));
        } catch (Exception e) {
            throw new NumberLoggerInitializationException("Error creating a new " + LOG_FILE, e);
        }
    }

    public void log(int number) {
        lock.lock();
        try {
            logNonIndexed(number);
        } finally {
            lock.unlock();
        }
    }

    private void logNonIndexed(int number) {
        if (!indexedNumbers.contains(number)) {
            printStream.println(number);
            indexedNumbers.add(number);
        }
    }

    private Path logFile() {
        return Path.of(LOG_FILE);
    }

}
