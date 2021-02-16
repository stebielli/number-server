package it.stebielli.numberserver;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static it.stebielli.numberserver.Arguments.*;

public class NumberServerMainTestIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberServerMainTestIT.class);
    private static final int REQUIREMENT_FOR_2M_NUMBERS = 10_000;
    private static final int _2M_NUMBERS = 2_000_000;

    private NumberServerMain main;

    @BeforeEach
    public void setUp() {
        main = new NumberServerMain();
        main.launch(DEFAULT_PORT, DEFAULT_MAX_CONNECTIONS, DEFAULT_LOG_FILE, DEFAULT_REPORT_PERIOD);
        LOGGER.info("Started server with port: {}, maxConnections: {}, logFile: {}, reportPeriod: {}",
                DEFAULT_PORT, DEFAULT_MAX_CONNECTIONS, DEFAULT_LOG_FILE, DEFAULT_REPORT_PERIOD);
    }

    @AfterEach
    public void tearDown() throws IOException {
        main.shutdown();
        Files.deleteIfExists(Path.of(DEFAULT_LOG_FILE));
        LOGGER.info("Server stopped");
    }

    @Test
    public void stressTestSingleSocket() throws IOException {
        var printStream = newSocketPrintStream();

        long start = System.currentTimeMillis();

        printRange(printStream, 0, _2M_NUMBERS);

        long finish = System.currentTimeMillis();

        assertTimeRequirement(finish - start);
        assertFileSize();
    }

    @Test
    public void stressTestMaxConcurrentSocketWithHighDuplication() throws IOException, ExecutionException, InterruptedException {
        List<PrintStream> printStreams = newPrintStreamsMaxConnections();
        var service = Executors.newFixedThreadPool(DEFAULT_MAX_CONNECTIONS);

        long start = System.currentTimeMillis();

        waitAll(duplicatedPrints(printStreams, service));

        long finish = System.currentTimeMillis();

        service.shutdown();
        assertTimeRequirement(finish - start);
        assertFileSize();
    }

    @Test
    public void stressTestMaxConcurrentSocketHighNoDuplication() throws IOException, ExecutionException, InterruptedException {
        List<PrintStream> printStreams = newPrintStreamsMaxConnections();
        var service = Executors.newFixedThreadPool(DEFAULT_MAX_CONNECTIONS);

        long start = System.currentTimeMillis();

        waitAll(nonDuplicatedPrints(printStreams, service));

        long finish = System.currentTimeMillis();
        service.shutdown();

        assertTimeRequirement(finish - start);
        assertFileSize();
    }

    private List<PrintStream> newPrintStreamsMaxConnections() throws IOException {
        List<PrintStream> printStreams = new ArrayList<>();
        for (int i = 0; i < DEFAULT_MAX_CONNECTIONS; i++) {
            printStreams.add(newSocketPrintStream());
        }
        return printStreams;
    }

    private PrintStream newSocketPrintStream() throws IOException {
        var socket = new Socket("localhost", DEFAULT_PORT);
        return new PrintStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    private List<Future<?>> duplicatedPrints(List<PrintStream> printStreams, ExecutorService service) {
        List<Future<?>> futures = new ArrayList<>();
        for (PrintStream p : printStreams) {
            futures.add(submitPrintRange(service, p, 0, _2M_NUMBERS));
        }
        return futures;
    }

    private List<Future<?>> nonDuplicatedPrints(List<PrintStream> printStreams, ExecutorService service) {
        List<Future<?>> futures = new ArrayList<>();

        int initial = 0;
        var range = _2M_NUMBERS / DEFAULT_MAX_CONNECTIONS;
        int last = range;

        for (var p : printStreams) {
            futures.add(submitPrintRange(service, p, initial, last));

            initial = last;
            last = last + range;
        }

        return futures;
    }

    private Future<?> submitPrintRange(ExecutorService service, PrintStream p, int initial, int last) {
        return service.submit(() -> printRange(p, initial, last));
    }

    private void printRange(PrintStream printStream, int from, int to) {
        try (printStream) {
            for (int num = from; num < to; num++) {
                printStream.printf("%09d%n", num);
            }
            printStream.flush();
        }
    }

    private void waitAll(List<Future<?>> futures) throws InterruptedException, ExecutionException {
        for (var f : futures) {
            f.get();
        }
    }

    private void assertFileSize() throws IOException {
        var size = Files.readAllLines(Path.of(DEFAULT_LOG_FILE)).size();
        Assertions.assertThat(size).isEqualTo(_2M_NUMBERS);
    }

    private void assertTimeRequirement(long elapsed) {
        Assertions.assertThat(elapsed).isLessThan(REQUIREMENT_FOR_2M_NUMBERS);
        LOGGER.info("Elapsed time to write 2M unique numbers: {} millis", elapsed);
    }
}