package it.stebielli.numberserver;

import it.stebielli.numberserver.logger.NumberLogger;
import it.stebielli.numberserver.logger.NumberLoggerInitializationException;
import it.stebielli.numberserver.reader.NumberReaderFactory;
import it.stebielli.numberserver.reporter.NumberReporter;
import it.stebielli.numberserver.socketservice.NumberSocketHandler;
import it.stebielli.numberserver.socketservice.SocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;

public class NumberServer implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberServer.class);

    private final int maxConnections;
    private final int port;

    private ServerSocket serverSocket;
    private SocketService socketService;
    private NumberSocketHandler socketHandler;
    private NumberReporter reporter;

    public NumberServer(int port, int maxConnections)  {
        this.port = port;
        this.maxConnections = maxConnections;
    }

    public void start() throws StartupException {
        serverSocket = newServerSocket(port);
        reporter = new NumberReporter(System.out, 10_000);
        var numberReaderFactory = new NumberReaderFactory(newNumberLogger(reporter), terminationFunction());
        socketHandler = new NumberSocketHandler(numberReaderFactory, maxConnections);

        socketService = new SocketService(serverSocket, socketHandler);
    }

    @Override
    public void close() {
        socketService.close();
        socketHandler.close();
        reporter.close();
        closeServerSocket();
    }

    private ServerSocket newServerSocket(int port) throws StartupException {
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            throw new StartupException(e);
        }
    }

    private NumberLogger newNumberLogger(NumberReporter reporter) throws StartupException {
        try {
            return new NumberLogger(reporter);
        } catch (NumberLoggerInitializationException e) {
            throw new StartupException(e);
        }
    }

    private NumberServerTerminator terminationFunction() {
        return this::close;
    }

    private void closeServerSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.warn("A problem occurred closing ServerSocket", e);
        }
    }

}
