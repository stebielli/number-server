package it.stebielli.numberserver.socketservice;

import it.stebielli.numberserver.MockitoTest;
import it.stebielli.numberserver.reader.NumberReader;
import it.stebielli.numberserver.reader.NumberReaderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import static org.mockito.Mockito.*;

class NumberSocketHandlerTest extends MockitoTest {

    private static final int MAX_CONNECTIONS = 1;

    @Mock
    NumberReaderFactory readerFactory;
    @Mock
    NumberReader reader;
    @Mock
    InputStream stream;

    @BeforeEach
    public void setUp() {
        super.setUp();
        when(readerFactory.newReader()).thenReturn(reader);
    }

    private Socket mockSocket() throws IOException {
        var socket = mock(Socket.class);
        withInputStream(socket);
        return socket;
    }

    private NumberSocketHandler newNumberSocketHandler() {
        return new NumberSocketHandler(readerFactory, MAX_CONNECTIONS);
    }

    @Test
    void socketInputPassedToReaderThenClose() throws IOException {
        var socket = mockSocket();
        var handler = newNumberSocketHandler();

        handler.handle(socket);

        var inOrder = inOrder(reader, socket);
        inOrder.verify(reader, timeout(TIMEOUT)).read(stream);
        inOrder.verify(socket, timeout(TIMEOUT)).close();
    }

    @Test
    void rejectSocketIfReachedMaxConnections() throws IOException {
        var socket = mockSocket();
        var socketToReject = mockSocket();
        var handler = newNumberSocketHandler();

        handler.handle(socket);
        handler.handle(socketToReject);

        verifyIsRejected(socketToReject);
        verifyIsClosed(socketToReject);
        verifyIsClosed(socket);
    }

    @Test
    void rejectSocketIfHandlerIsClosed() throws IOException {
        var socket = mockSocket();
        var handler = newNumberSocketHandler();

        handler.close();
        handler.handle(socket);

        verifyIsRejected(socket);
        verifyIsClosed(socket);
    }

    @Test
    void closeClosesActiveSockets() throws IOException {
        var socket = mockSocket();
        var handler = newNumberSocketHandler();
        readerIsReading();


        handler.handle(socket);
        handler.close();

        verifyIsClosed(socket);
    }

    @Test
    void verifyIOExceptionWhileClosingIsHandled() throws IOException {
        var socket = mockSocket();
        doThrow(new IOException()).when(socket).close();

        var handler = newNumberSocketHandler();

        handler.handle(socket);

        // socket is closed after being handled
        // this test fails if the exception is not handled
    }

    private void readerIsReading() {
        doAnswer(invocation -> {
            Thread.sleep(TIMEOUT * 10);
            return null;
        }).when(reader).read(stream);
    }

    private void withInputStream(Socket socket) throws IOException {
        when(socket.getInputStream()).thenReturn(stream);
    }

    private void verifyIsClosed(Socket socket) throws IOException {
        verify(socket, timeout(TIMEOUT)).close();
    }

    private void verifyIsRejected(Socket socketToReject) throws IOException {
        verify(socketToReject, never()).getInputStream();
    }

}