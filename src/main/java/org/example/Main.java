package org.example;

import org.example.Exception.DeadLineExceedException;

public class Main {

    public static void main(String[] args) throws DeadLineExceedException {
        final Server server = new Server();
        server.setPort(7878);
        server.start();
    }
}