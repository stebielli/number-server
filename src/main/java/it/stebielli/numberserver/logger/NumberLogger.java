package it.stebielli.numberserver.logger;


import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NumberLogger {

    public static final String LOG_FILE = "numbers.log";
    private final PrintStream printStream;
    private final Set<Integer> indexedNumbers;
    private final Lock lock;

    public NumberLogger() throws NumberLoggerInitializationException {
        this.printStream = newPrintStream();
        this.indexedNumbers = new HashSet<>();
        this.lock = new ReentrantLock();
    }

    private PrintStream newPrintStream() throws NumberLoggerInitializationException {
        OutputStream out = clearAndOpenStreamToFile();
        return new PrintStream(out);
    }

    private OutputStream clearAndOpenStreamToFile() throws NumberLoggerInitializationException {
        try {
            Files.deleteIfExists(logFile());
            return Files.newOutputStream(logFile());
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
