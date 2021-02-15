package it.stebielli.numberserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberServerMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberServerMain.class);

    private NumberServer numberServer;

    public static void main(String[] args) {
        Arguments arguments = new Arguments(args);
        new NumberServerMain()
                .launch(arguments.getPort(),
                        arguments.getMaxConnections(),
                        arguments.getLogFile(),
                        arguments.getReportPeriod());
    }

    public void launch(int port, int maxConnections, String logFile, int reportPeriod) {
        numberServer = new NumberServer(port, maxConnections, logFile, reportPeriod);
        try {
            numberServer.start();
        } catch (StartupException e) {
            LOGGER.error("A problem occurred starting the server", e);
        }
    }

    public void shutdown() {
        numberServer.close();
    }

}
