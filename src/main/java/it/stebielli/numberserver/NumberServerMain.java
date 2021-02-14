package it.stebielli.numberserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberServerMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberServerMain.class);

    public static final int DEFAULT_PORT = 4000;
    public static final int DEFAULT_MAX_CONNECTIONS = 5;
    public static final String DEFAULT_LOG_FILE = "numbers.log";
    public static final int DEFAULT_REPORT_PERIOD = 10_000;

    private NumberServer numberServer;

    public static void main(String[] args) {

        var port = DEFAULT_PORT;
        var maxConnections = DEFAULT_MAX_CONNECTIONS;
        var logFile = DEFAULT_LOG_FILE;
        var reportPeriod = DEFAULT_REPORT_PERIOD;

        for (String arg : args) {
            var param = arg.split("=");
            switch (param[0]) {
                case "port":
                    port = Integer.parseInt(param[1]);
                    break;
                case "maxConnections":
                    maxConnections = Integer.parseInt(param[1]);
                    break;
                case "logFile":
                    logFile = param[1];
                    break;
                case "reportPeriod":
                    reportPeriod = Integer.parseInt(param[1]);
                default:
                    break;
            }
        }

        new NumberServerMain().launch(port, maxConnections, logFile, reportPeriod);
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
