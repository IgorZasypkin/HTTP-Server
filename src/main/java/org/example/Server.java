package org.example;

import com.google.common.primitives.Bytes;
import lombok.Setter;
import org.example.Exception.BadRequestException;
import org.example.Exception.DeadLineExceedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Setter
public class Server {

    public static final byte[] CLRFCLRF = {'\r', '\n', '\r', '\n'};
    private int port = 9999;
    private int soTimeout = 30 * 1000;
    private int readTimeout = 60 * 1000;
    private int bufferSize = 4096;

    public void start() throws DeadLineExceedException {

        try (
                final ServerSocket serverSocket = new ServerSocket(port);
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

    private void handleClient(final Socket socket) throws IOException, DeadLineExceedException {

        socket.setSoTimeout(soTimeout);

        try (
                socket;
                final OutputStream out = socket.getOutputStream();
                final InputStream in = socket.getInputStream();
        ) {
            System.out.println(socket.getInetAddress());

            final String message = readMessage(in);

            final String responce =
                    "HTTP/1.1 200 OK\r\n" +
                            "Connection: close\r\n" +
                            "Content-Length: 2\r\n" +
                            "Content-Type: text/html; charset=utf-8\r\n" +
                            "Content-Language: en\r\n" +
                            "\r\n" +
                            "OK";

            out.write(responce.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String readMessage(final InputStream in) throws IOException, DeadLineExceedException {
        final byte[] buffer = new byte[bufferSize];
        int offset = 0;
        int length = buffer.length;

        final Instant deadLine = Instant.now().plusMillis(readTimeout);

        while (true) {

            if(Instant.now().isAfter(deadLine)){
                throw new DeadLineExceedException();
            }

            final int read = in.read(buffer, offset, length);
            offset += read;
            length = buffer.length - offset;

            final int headersEndIndex = Bytes.indexOf(buffer, CLRFCLRF);

            if (headersEndIndex != -1) {
                break;
            }

            if (read == -1)
                throw new BadRequestException(("CLRFCLRF not found, no more data"));

            if (length == 0) {
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