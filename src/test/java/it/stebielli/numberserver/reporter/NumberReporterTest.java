package it.stebielli.numberserver.reporter;

import it.stebielli.numberserver.MockitoTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.PrintStream;

import static org.mockito.Mockito.*;

class NumberReporterTest extends MockitoTest {

    @Mock
    PrintStream printStream;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        System.setOut(printStream);
    }

    @Test
    void startAndCloseReporting() throws InterruptedException {
        var reporter = new NumberReporter(20);

        verify(printStream, timeout(TIMEOUT)).println("Received 0 unique numbers, 0 duplicates. Unique total: 0");
        verify(printStream, timeout(TIMEOUT)).flush();

        reporter.close();

        verifyNoMoreAsyncInteraction(printStream);
    }

    @Test
    void reportUniqueNumber() {
        var reporter = new NumberReporter(20);

        reporter.incrementUniques();

        verify(printStream, timeout(TIMEOUT)).println("Received 1 unique numbers, 0 duplicates. Unique total: 1");
        verify(printStream, timeout(TIMEOUT)).flush();

        reporter.close();
    }

    @Test
    void reportDuplicatedNumber() {
        var reporter = new NumberReporter(20);

        reporter.incrementDuplicates();

        verify(printStream, timeout(TIMEOUT)).println("Received 0 unique numbers, 1 duplicates. Unique total: 0");
        verify(printStream, timeout(TIMEOUT)).flush();

        reporter.close();
    }

    @Test
    void resetReportPeriodically() throws InterruptedException {
        var reporter = new NumberReporter(20);

        reporter.incrementUniques();
        verify(printStream, timeout(TIMEOUT * 2)).println("Received 1 unique numbers, 0 duplicates. Unique total: 1");

        reporter.incrementUniques();
        verify(printStream, timeout(TIMEOUT * 2)).println("Received 1 unique numbers, 0 duplicates. Unique total: 2");

        reporter.close();
    }
}