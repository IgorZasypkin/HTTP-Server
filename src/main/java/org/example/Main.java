package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class Main {
    public static void main(String[] args) {

        try (
                final ServerSocket serverSocket = new ServerSocket(9999);
        ) {
            while (true) {
                try {
                //блокирующий вызов
                final Socket socket = serverSocket.accept();
                   handleClient(socket);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
            // если не удалось запустить сервер
        }
    }

    private static void handleClient(final Socket socket) throws IOException {
        try (
                socket;
                final OutputStream out = socket.getOutputStream();
                final InputStream in = socket.getInputStream();

        ) {

            System.out.println(socket.getInetAddress());
            out.write("Enter command\n".getBytes(StandardCharsets.UTF_8));

            final String message = readMessage(in);
            System.out.println("message: " + message);

        }
    }

    private static String readMessage(final InputStream in) throws IOException {
        final byte[] buffer = new byte[4096];
        int offset = 0;
        int length = buffer.length;
        while (true) {
            final int read = in.read(buffer, offset, length);
            offset += read;
            length = buffer.length - offset;
//                        final String message = new String(buffer, 0, read, StandardCharsets.UTF_8);
//                        System.out.println("message= " + message);
            final byte lastByte = buffer[offset - 1];

            if (lastByte == '\n') {
                break;
            }
        }
//        final String message = new String(buffer, 0, buffer.length - length, StandardCharsets.UTF_8)
//                .trim();
//
//        return message;


        return new String(buffer, 0, buffer.length - length, StandardCharsets.UTF_8)
                .trim();

    }
}