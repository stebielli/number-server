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
    private final String logFile;
    private final int reportPeriod;

    private ServerSocket serverSocket;
    private SocketService socketService;
    private NumberSocketHandler socketHandler;
    private NumberReporter numberReporter;
    private NumberLogger numberLogger;

    public NumberServer(int port, int maxConnections, String logFile, int reportPeriod) {
        this.port = port;
        this.maxConnections = maxConnections;
        this.logFile = logFile;
        this.reportPeriod = reportPeriod;
    }

    public void start() throws StartupException {
        startServerSocket();
        startNumberReporter();
        startNumberLogger();
        startSocketHandler();
        startSocketService();
    }

    private void startServerSocket() throws StartupException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new StartupException(e);
        }
    }

    private void startNumberReporter() {
        numberReporter = new NumberReporter(reportPeriod);
    }

    private void startNumberLogger() throws StartupException {
        try {
            numberLogger = new NumberLogger(logFile, numberReporter);
        } catch (NumberLoggerInitializationException e) {
            throw new StartupException(e);
        }
    }

    private void startSocketHandler() {
        var readerFactory = new NumberReaderFactory(numberLogger, terminationFunction());
        socketHandler = new NumberSocketHandler(readerFactory, maxConnections);
    }

    private NumberServerTerminator terminationFunction() {
        return this::close;
    }

    private void startSocketService() {
        socketService = new SocketService(serverSocket, socketHandler);
    }

    @Override
    public void close() {
        socketService.close();
        socketHandler.close();
        numberLogger.close();
        numberReporter.close();
        closeServerSocket();
    }

    private void closeServerSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.warn("A problem occurred closing ServerSocket", e);
        }
    }

}
