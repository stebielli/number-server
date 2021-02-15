package it.stebielli.numberserver;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ArgumentsTest {

    @Test
    public void constructorWithDefaults() {
        var arguments = new Arguments();

        assertThat(arguments.getPort()).isEqualTo(Arguments.DEFAULT_PORT);
        assertThat(arguments.getLogFile()).isEqualTo(Arguments.DEFAULT_LOG_FILE);
        assertThat(arguments.getReportPeriod()).isEqualTo(Arguments.DEFAULT_REPORT_PERIOD);
        assertThat(arguments.getMaxConnections()).isEqualTo(Arguments.DEFAULT_MAX_CONNECTIONS);
    }

    @Test
    public void constructorWithTuning() {
        var arguments = new Arguments("port=5000", "logFile=logFile", "reportPeriod=100", "maxConnections=10");

        assertThat(arguments.getPort()).isEqualTo(5000);
        assertThat(arguments.getLogFile()).isEqualTo("logFile");
        assertThat(arguments.getReportPeriod()).isEqualTo(100);
        assertThat(arguments.getMaxConnections()).isEqualTo(10);
    }
}