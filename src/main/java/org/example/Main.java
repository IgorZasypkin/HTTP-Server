package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args){

        int portNumber = 9999;

        try (ServerSocket serverSocket = new ServerSocket(portNumber))
        {
            while (true){
                final Socket socket = serverSocket.accept();
                System.out.println("accepted");
            }

        } catch (IOException e) {
        };

    }
}
