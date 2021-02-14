package it.stebielli.numberserver.socketservice;

import it.stebielli.numberserver.MockitoTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.*;

class SocketServiceTest extends MockitoTest {

    public static final int TWICE = 2;

    @Mock
    private ServerSocket serverSocket;
    @Mock
    private SocketHandler socketHandler;

    @Test
    void acceptedSocketsAreHandled() throws IOException {
        var socket = mock(Socket.class);
        when(serverSocket.accept()).thenReturn(socket);

        new SocketService(serverSocket, socketHandler);

        verify(serverSocket, timeout(TIMEOUT).atLeastOnce()).accept();
        verify(socketHandler, timeout(TIMEOUT).atLeastOnce()).handle(socket);
    }

    @Test
    void assertIOExceptionIsNotAStopper() throws IOException {
        var socket = mock(Socket.class);
        when(serverSocket.accept()).thenThrow(new IOException()).thenReturn(socket);

        new SocketService(serverSocket, socketHandler);

        verify(serverSocket, timeout(TIMEOUT).atLeast(TWICE)).accept();
        verify(socketHandler, timeout(TIMEOUT).atLeastOnce()).handle(socket);
    }

    @Test
    void closeStopsAcceptingSocket() throws InterruptedException {
        var socketService = new SocketService(serverSocket, socketHandler);

        socketService.close();

        verifyNoAsyncInteraction(serverSocket);
    }

}