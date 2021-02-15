package it.stebielli.numberserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;

import static it.stebielli.numberserver.Arguments.*;
import static org.assertj.core.api.Assertions.*;

public class NumberServerTest {

    private static final int TIMEOUT = 10;
    private static final int CLOSED = -1;
    private static final String HOST = "localhost";

    private NumberServer numberServer;

    @BeforeEach
    public void setUp() {
        numberServer = new NumberServer(DEFAULT_PORT, DEFAULT_MAX_CONNECTIONS, DEFAULT_LOG_FILE, DEFAULT_REPORT_PERIOD);
    }

    @AfterEach
    public void tearDown() throws IOException {
        numberServer.close();
        Files.deleteIfExists(Path.of(DEFAULT_LOG_FILE));
    }

    @Test
    public void applicationStart() throws StartupException, IOException {
        numberServer.start();

        assertIsConnected(newSocketClient());
    }

    @Test
    public void applicationAcceptAtMostMaxConnections() throws StartupException, IOException {
        numberServer.start();
        for (int i = 0; i < DEFAULT_MAX_CONNECTIONS; i++) {
            assertIsConnected(newSocketClient());
        }

        // without closing the sockets the 6th connection has to fail
        assertIsDisconnected(newSocketClient());
    }

    @Test
    public void openingAndClosingSocketWithMoreThenMaxConnections() throws StartupException, IOException {
        numberServer.start();
        // in this way the application never reach the limit
        // therefore has to accept all the connections
        for (int i = 0; i < DEFAULT_MAX_CONNECTIONS * 2; i++) {
            Socket s = newSocketClient();
            assertIsConnected(s);
            s.close();
        }
    }

    @Test
    public void throwsAServerStartExceptionIfNotAbleToStart() throws StartupException {
        numberServer.start();
        var serverThatFail = new NumberServer(DEFAULT_PORT, DEFAULT_MAX_CONNECTIONS, DEFAULT_LOG_FILE, DEFAULT_REPORT_PERIOD);
        assertThatExceptionOfType(StartupException.class).isThrownBy(serverThatFail::start);
    }

    private void assertIsConnected(Socket s) {
        // the socket reach the read timeout because the server is not sending data
        // it means that the connection was successfully established
        assertThatThrownBy(() -> s.getInputStream().read())
                .isInstanceOf(SocketTimeoutException.class);
    }

    private void assertIsDisconnected(Socket s) throws IOException {
        // reading a int '-1' means connection closed
        assertThat(s.getInputStream().read()).isEqualTo(CLOSED);
    }

    private Socket newSocketClient() throws IOException {
        Socket s = new Socket(HOST, DEFAULT_PORT);

        // the timeout is set to test a successful connection during the read
        s.setSoTimeout(TIMEOUT);

        return s;
    }

}
