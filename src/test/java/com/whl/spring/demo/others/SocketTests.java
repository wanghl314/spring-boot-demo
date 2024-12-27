package com.whl.spring.demo.others;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class SocketTests {
    private static Logger logger = LoggerFactory.getLogger(SocketTests.class);

    @Test
    public void singleSocketConnectionTest() throws IOException {
        long cycle = 1000L;
        int socketTimeout = 10000;
        Socket socket = SSLSocketFactory.getDefault().createSocket("qyapi.weixin.qq.com", 443);
        socket.setSoTimeout(socketTimeout);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter write = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

            while (true) {
                String traceId = UUID.randomUUID().toString().replace("-", "");
                String request = "GET /cgi-bin/gettoken HTTP/1.1\r\nHost: qyapi.weixin.qq.com\r\nUser-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:124.0) Gecko/20100101 Firefox/124.0\r\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8\r\nAccept-Encoding: gzip, deflate, br\r\nConnection: keep-alive\r\n\r\n";
                System.out.println(LocalDateTime.now().format(formatter) + " " + traceId);
                System.out.println(request);
                write.write(request);
                write.flush();
                StringBuilder builder = new StringBuilder();

                try {
                    int contentLength = 0;

                    for (int i = 0; ; i++) {
                        String line = reader.readLine();

                        if (i == 0) {
                            System.out.println(LocalDateTime.now().format(formatter) + " " + traceId);
                        }

                        if (line.startsWith("Content-Length")) {
                            contentLength = Integer.parseInt(line.split(":")[1].trim());
                        }
                        builder.append(line);
                        builder.append("\r\n");

                        if (builder.toString().endsWith("\r\n\r\n")) {
                            break;
                        }
                    }
                    System.out.println(builder.toString());

                    if (contentLength > 0) {
                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                            while (baos.size() < contentLength) {
                                baos.write(reader.read());
                            }
                            System.out.println(baos.toString(StandardCharsets.UTF_8));
                        }
                    }
                } catch (Exception e) {
                    System.out.println(LocalDateTime.now().format(formatter) + " " + traceId);
                    logger.error(e.getMessage(), e);
                }

                try {
                    Thread.sleep(cycle);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

}
