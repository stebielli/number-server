package it.stebielli.numberserver.reader;

import it.stebielli.numberserver.NumberServerTerminator;
import it.stebielli.numberserver.logger.NumberLogger;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

public class NumberReader {

    public static final String TERMINATION_INPUT = "terminate";
    private static final Pattern LOG_INPUT = Pattern.compile("^[\\d]{9}$");

    private final NumberServerTerminator terminator;
    private final NumberLogger numberLogger;

    public NumberReader(NumberReaderFactory factory) {
        this.terminator = factory.getTerminator();
        this.numberLogger = factory.getNumberLogger();
    }

    public void read(InputStream stream) {
        var scanner = new Scanner(stream);
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();

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
