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
    private int totalUniquesNumbers;
    private final ScheduledExecutorService service;

    public NumberReporter(int periodInMillis) {
        this.printStream = System.out;
        this.periodInMillis = periodInMillis;
        this.uniquesNumbers = new AtomicInteger(0);
        this.duplicatesNumbers = new AtomicInteger(0);
        this.totalUniquesNumbers = 0;
        this.service = ExecutorsUtils.newSingleThreadScheduledExecutor();

        startSchedule();
    }

    public void incrementUniques() {
        uniquesNumbers.incrementAndGet();
    }

    public void incrementDuplicates() {
        duplicatesNumbers.incrementAndGet();
    }

    private void startSchedule() {
        service.scheduleAtFixedRate(this::print, periodInMillis, periodInMillis, TimeUnit.MILLISECONDS);
    }

    public void print() {
        var uniques = uniquesNumbers.getAndSet(0);
        var duplicates = duplicatesNumbers.getAndSet(0);
        totalUniquesNumbers = totalUniquesNumbers + uniques;
        printStream.println(report(uniques, duplicates, totalUniquesNumbers));
        printStream.flush();
    }

    private String report(int uniques, int duplicates, int totalUniques) {
        return "Received " + uniques
                + " unique numbers, " + duplicates
                + " duplicates. Unique total: " + totalUniques;
    }

    @Override
    public void close() {
        ExecutorsUtils.shutdown(service);
    }

}
