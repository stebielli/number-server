package it.stebielli.numberserver.logger;

import it.stebielli.numberserver.reporter.NumberReporter;

import java.io.Closeable;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NumberLogger implements Closeable {

    private final PrintStream printStream;
    private final Set<Integer> indexedNumbers;
    private final Lock lock;
    private final String logFile;
    private final NumberReporter reporter;

    public NumberLogger(String logFile, NumberReporter reporter) throws NumberLoggerInitializationException {
        this.logFile = logFile;
        this.reporter = reporter;

        this.indexedNumbers = new HashSet<>();
        this.lock = new ReentrantLock();

        this.printStream = newPrintStream();
    }

    public void log(int number) {
        synchronizedLog(number);
    }

    @Override
    public void close() {
        printStream.close();
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
            throw new NumberLoggerInitializationException("Error creating a new " + logFile, e);
        }
    }

    private Path logFile() {
        return Path.of(logFile);
    }

    private void synchronizedLog(int number) {
        lock.lock();
        try {
            doLog(number);
        } finally {
            lock.unlock();
        }
    }

    private void doLog(int number) {
        if (isNotIndexed(number)) {
            logAndIndex(number);
            reporter.incrementUniques();
        } else {
            reporter.incrementDuplicates();
        }
    }

    private boolean isNotIndexed(int number) {
        return !indexedNumbers.contains(number);
    }

    private void logAndIndex(int number) {
        printStream.println(number);
        indexedNumbers.add(number);
    }
}
