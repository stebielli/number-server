package it.stebielli.numberserver.reporter;

import it.stebielli.numberserver.MockitoTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.PrintStream;

import static org.mockito.Mockito.*;

class NumberReporterTest extends MockitoTest {

    @Mock
    PrintStream printStream;

    @Test
    void startAndCloseReporting() throws InterruptedException {
        var reporter = new NumberReporter(printStream, 50);

        verify(printStream, timeout(TIMEOUT)).println("Received 0 unique numbers, 0 duplicates. Unique total: 0");
        reporter.close();

        verifyNoMoreAsyncInteraction(printStream);
    }

    @Test
    void reportUniqueNumber() {
        var reporter = new NumberReporter(printStream, 50);

        reporter.incrementUniques();

        verify(printStream, timeout(TIMEOUT)).println("Received 1 unique numbers, 0 duplicates. Unique total: 1");

        reporter.close();
    }

    @Test
    void reportDuplicatedNumber() {
        var reporter = new NumberReporter(printStream, 50);

        reporter.incrementDuplicates();

        verify(printStream, timeout(TIMEOUT)).println("Received 0 unique numbers, 1 duplicates. Unique total: 0");

        reporter.close();
    }

    @Test
    void resetReportPeriodically() throws InterruptedException {
        var reporter = new NumberReporter(printStream, 50);

        reporter.incrementUniques();

        Thread.sleep(100);

        reporter.incrementUniques();

        var inOrder = inOrder(printStream);
        inOrder.verify(printStream, timeout(TIMEOUT * 2)).println("Received 1 unique numbers, 0 duplicates. Unique total: 1");
        inOrder.verify(printStream, timeout(TIMEOUT * 2)).println("Received 1 unique numbers, 0 duplicates. Unique total: 2");

        reporter.close();
    }
}