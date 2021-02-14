package it.stebielli.numberserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberServerMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberServerMain.class);

    public static void main(String[] args) {
        new NumberServerMain().launch();
    }

    public void launch() {
        var numberServer = new NumberServer(4000, 5, "numbers.log", 10_000);
        try {
            numberServer.start();
        } catch (StartupException e) {
            LOGGER.error("A problem occurred starting the server", e);
        }
    }

}
