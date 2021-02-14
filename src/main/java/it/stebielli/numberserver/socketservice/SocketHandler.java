package it.stebielli.numberserver.socketservice;

import java.net.Socket;

public interface SocketHandler {

    void handle(Socket socket);

}
