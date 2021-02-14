package it.stebielli.numberserver.reader;

import it.stebielli.numberserver.MockitoTest;
import it.stebielli.numberserver.NumberServerTerminator;
import it.stebielli.numberserver.logger.NumberLogger;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class NumberReaderTest extends MockitoTest {

    public static final String ENTER = "\n";
    public static final String VALID_INPUT = "000000000";
    public static final String INVALID_INPUT = "invalid";

    @Mock
    NumberServerTerminator terminator;
    @Mock
    NumberLogger numberLogger;

    @Test
    void validInputIsPassedToNumberLogger() {
        var stream = streamOf(VALID_INPUT + ENTER);
        var reader = new NumberReaderFactory(numberLogger, terminator).newReader();

        reader.read(stream);

        verify(numberLogger).log(Integer.parseInt(VALID_INPUT));
        verify(numberLogger).flush();
    }

    @Test
    void invalidInputIsNotPassedToNumberLoggerAndStopReading() {
        var stream = streamOf(INVALID_INPUT + ENTER + VALID_INPUT + ENTER);

        var reader = new NumberReaderFactory(numberLogger, terminator).newReader();

        reader.read(stream);

        verify(numberLogger).flush();

        verifyNoMoreInteractions(numberLogger);
    }

    @Test
    void terminateInputCallsTermination() {
        var stream = streamOf(NumberReader.TERMINATION_INPUT + ENTER);

        var reader = new NumberReaderFactory(numberLogger, terminator).newReader();

        reader.read(stream);

        verify(terminator).terminate();
        verify(numberLogger).flush();
    }


    private ByteArrayInputStream streamOf(String input) {
        return new ByteArrayInputStream(input.getBytes());
    }
}