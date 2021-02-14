package it.stebielli.numberserver.reader;

import it.stebielli.numberserver.NumberServerTerminator;
import it.stebielli.numberserver.logger.NumberLogger;

public class NumberReaderFactory {

    private final NumberLogger numberLogger;
    private final NumberServerTerminator terminator;

    public NumberReaderFactory(NumberLogger numberLogger, NumberServerTerminator terminator) {
        this.numberLogger = numberLogger;
        this.terminator = terminator;
    }

    public NumberLogger getNumberLogger() {
        return numberLogger;
    }

    public NumberServerTerminator getTerminator() {
        return terminator;
    }

    public NumberReader newReader() {
        return new NumberReader(this);
    }
}
