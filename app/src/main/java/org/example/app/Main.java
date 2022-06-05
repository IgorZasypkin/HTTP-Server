package org.example.app;

import org.example.server.exception.DeadLineExceedException;
import org.example.server.Server;

public class Main {

    public static void main(String[] args) throws DeadLineExceedException {
        final Server server = new Server();
        server.setPort(7878);
        server.start();
    }
}