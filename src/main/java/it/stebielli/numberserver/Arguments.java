package it.stebielli.numberserver;

public class Arguments {
    public static final int DEFAULT_PORT = 4000;
    public static final int DEFAULT_MAX_CONNECTIONS = 5;
    public static final String DEFAULT_LOG_FILE = "numbers.log";
    public static final int DEFAULT_REPORT_PERIOD = 10_000;

    private int port = DEFAULT_PORT;
    private int maxConnections = DEFAULT_MAX_CONNECTIONS;
    private String logFile = DEFAULT_LOG_FILE;
    private int reportPeriod = DEFAULT_REPORT_PERIOD;

    public Arguments(String... args) {
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
    }

    public int getPort() {
        return port;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public String getLogFile() {
        return logFile;
    }

    public int getReportPeriod() {
        return reportPeriod;
    }

}
