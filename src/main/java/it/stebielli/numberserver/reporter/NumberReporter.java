package it.stebielli.numberserver.reporter;

import it.stebielli.numberserver.utils.ExecutorsUtils;

import java.io.Closeable;
import java.io.PrintStream;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NumberReporter implements Closeable {

    private final PrintStream printStream;
    private final int periodInMillis;
    private final AtomicInteger uniquesNumbers;
    private final AtomicInteger duplicatesNumbers;
    private final AtomicInteger totalUniquesNumbers;
    private final ScheduledExecutorService service;

    public NumberReporter(PrintStream printStream, int periodInMillis) {
        this.printStream = printStream;
        this.periodInMillis = periodInMillis;
        this.uniquesNumbers = new AtomicInteger(0);
        this.duplicatesNumbers = new AtomicInteger(0);
        this.totalUniquesNumbers = new AtomicInteger(0);
        this.service = ExecutorsUtils.newSingleThreadScheduledExecutor();

        startSchedule();
    }

    public void incrementUniques() {
        uniquesNumbers.incrementAndGet();
        totalUniquesNumbers.incrementAndGet();
    }

    public void incrementDuplicates() {
        duplicatesNumbers.incrementAndGet();
    }

    private void startSchedule() {
        service.scheduleAtFixedRate(this::print, 0, periodInMillis, TimeUnit.MILLISECONDS);
    }


    public void print() {
        printStream.println(report());
        uniquesNumbers.set(0);
        duplicatesNumbers.set(0);
    }

    private String report() {
        return "Received " + uniquesNumbers.get()
                + " unique numbers, " + duplicatesNumbers.get()
                + " duplicates. Unique total: " + totalUniquesNumbers.get();
    }


    @Override
    public void close() {
        ExecutorsUtils.shutdown(service);
    }
}
