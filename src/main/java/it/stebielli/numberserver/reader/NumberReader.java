package it.stebielli.numberserver.reader;

import it.stebielli.numberserver.NumberServerTerminator;
import it.stebielli.numberserver.logger.NumberLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class NumberReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberReader.class);

    public static final String TERMINATION_INPUT = "terminate";
    private static final Pattern LOG_INPUT = Pattern.compile("^[\\d]{9}$");

    private final NumberServerTerminator terminator;
    private final NumberLogger numberLogger;

    public NumberReader(NumberReaderFactory factory) {
        this.terminator = factory.getTerminator();
        this.numberLogger = factory.getNumberLogger();
    }

    public void read(InputStream stream) {
        try (var reader = new BufferedReader(new InputStreamReader(stream))) {
            doRead(reader);
        } catch (IOException e) {
            LOGGER.error("A problem occurred while reading", e);
        } finally {
            numberLogger.flush();
        }
    }

    private void doRead(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {

            if (isLog(line)) {
                numberLogger.log(Integer.parseInt(line));
            } else if (isTermination(line)) {
                terminator.terminate();
            } else {
                break;
            }

        }
    }

    private boolean isTermination(String line) {
        return line.equals(TERMINATION_INPUT);
    }

    private boolean isLog(String line) {
        return LOG_INPUT.matcher(line).matches();
    }

}
