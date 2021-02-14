package it.stebielli.numberserver.logger;

import java.io.IOException;

public class NumberLoggerInitializationException extends Throwable {
    public NumberLoggerInitializationException(String message, Throwable e) {
        super(message, e);
    }
}
