package it.stebielli.numberserver.socketservice;

import it.stebielli.numberserver.reader.NumberReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import static it.stebielli.numberserver.utils.ExecutorsUtils.newFixedThreadPool;
import static it.stebielli.numberserver.utils.ExecutorsUtils.shutdown;

public class NumberSocketHandler implements SocketHandler, Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberSocketHandler.class);

    private final ExecutorService executorService;
    private final NumberReaderFactory readerFactory;
    private final Set<Socket> activeSockets;

    public NumberSocketHandler(NumberReaderFactory readerFactory, int maxConnections) {
        this.executorService = newFixedThreadPool(maxConnections);
        this.readerFactory = readerFactory;
        this.activeSockets = new HashSet<>();
    }

    @Override
    public void handle(Socket socket) {
        try {

            activeSockets.add(socket);
            runAsync(() -> read(socket))
                    .thenRun(() -> closeGracefully(socket));

        } catch (RejectedExecutionException e) {
            closeGracefully(socket);
        }
    }

    @Override
    public void close() {
        activeSockets.forEach(this::closeSocket);
        activeSockets.clear();
        shutdown(executorService);
    }

    private CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executorService);
    }

    private void read(Socket socket) {
        try {
            readerFactory.newReader().read(socket.getInputStream());
        } catch (IOException e) {
            LOGGER.warn("A problem occurred reading from the Socket", e);
        }
    }

    private void closeGracefully(Socket socket) {
        closeSocket(socket);
        activeSockets.remove(socket);
    }

    private void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.warn("A problem occurred closing the Socket", e);
        }
    }

}
