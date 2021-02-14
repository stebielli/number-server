package it.stebielli.numberserver.logger;

import it.stebielli.numberserver.reporter.NumberReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NumberLogger implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberLogger.class);

    private final FileWriter fileWriter;
    private final Set<Integer> indexedNumbers;
    private final String logFile;
    private final NumberReporter reporter;

    public NumberLogger(String logFile, NumberReporter reporter) throws NumberLoggerInitializationException {
        this.logFile = logFile;
        this.reporter = reporter;
        this.indexedNumbers = Collections.synchronizedSet(new HashSet<>());
        this.fileWriter = newFileWriter();
    }

    public void log(int number) {
        if (isNotIndexed(number)) {
            print(number);
            reporter.incrementUniques();
        } else {
            reporter.incrementDuplicates();
        }
    }

    private void print(int number) {
        try {
            fileWriter.write(number + System.lineSeparator());
        } catch (IOException e) {
            LOGGER.error("A problem occurred writing to file " + logFile, e);
        }
    }

    public void flush() {
        try {
            fileWriter.flush();
        } catch (IOException e) {
            LOGGER.warn("A problem occurred flushing to file " + logFile, e);
        }
    }

    @Override
    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            LOGGER.warn("A problem occurred closing the fileWriter", e);
        }
    }

    private FileWriter newFileWriter() throws NumberLoggerInitializationException {
        try {
            return new FileWriter(logFile, false);
        } catch (IOException e) {
            throw new NumberLoggerInitializationException("Error creating a new " + logFile, e);
        }
    }

    private boolean isNotIndexed(int number) {
        return indexedNumbers.add(number);
    }
}
