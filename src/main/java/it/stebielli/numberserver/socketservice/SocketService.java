package it.stebielli.numberserver.socketservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

import static it.stebielli.numberserver.utils.ExecutorsUtils.newSingleThreadExecutor;
import static it.stebielli.numberserver.utils.ExecutorsUtils.shutdown;

public class SocketService implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketService.class);

    private final ServerSocket serverSocket;
    private final SocketHandler socketHandler;
    private final ExecutorService executorService;
    private volatile boolean running;

    public SocketService(ServerSocket serverSocket, SocketHandler socketHandler) {
        this.serverSocket = serverSocket;
        this.socketHandler = socketHandler;
        this.executorService = newSingleThreadExecutor();
        this.running = true;

        startThread();
    }

    @Override
    public void close() {
        running = false;
        shutdown(executorService);
    }

    private void startThread() {
        executorService.execute(this::service);
    }

    private void service() {
        while (running) {
            handleSockets();
        }
    }

    private void handleSockets() {
        try {
            var socket = serverSocket.accept();
            socketHandler.handle(socket);
        } catch (SocketException e) {
            LOGGER.warn("ServerSocket is closing");
        } catch (IOException e) {
            LOGGER.error("Error accepting new Socket", e);
        }
    }

}
