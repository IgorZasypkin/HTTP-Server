package org.example;

import com.google.common.primitives.Bytes;
import org.example.Exception.BadRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

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

            switch (message) {
                case "time":
                    final Instant now = Instant.now();
                    out.write(now.toString().getBytes(StandardCharsets.UTF_8));
                    break;
                case "shutdown":
                    out.write("ok".getBytes(StandardCharsets.UTF_8));
                    System.exit(200);
                    break;
                default:
                    out.write("unknown command\n".getBytes(StandardCharsets.UTF_8));
            }
            System.out.println("message: " + message);
        }
    }

    private static String readMessage(final InputStream in) throws IOException {
        final byte[] CLRFCLRF = {'\r', '\n', '\r', '\n'};
        final byte[] buffer = new byte[4096];
        int offset = 0;
        int length = buffer.length;
        while (true) {
            final int read = in.read(buffer, offset, length);
            offset += read;
            length = buffer.length - offset;

            final int headersEndIndex = Bytes.indexOf(buffer, CLRFCLRF);

            if (headersEndIndex != -1) {
                break;
            }


            if (read == 0 || length == 0) {
                throw new BadRequestException("CRLFCRLF not found");
            }
        }

        return new String(buffer,
                0,
                buffer.length - length,
                StandardCharsets.UTF_8)
                .trim(); // тоже, что и строка 80-83.
    }
}