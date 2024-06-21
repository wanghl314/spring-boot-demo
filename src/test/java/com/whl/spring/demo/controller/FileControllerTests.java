package com.whl.spring.demo.controller;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class FileControllerTests {

    @Test
    public void testURLConnection() throws URISyntaxException, IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        URL url = new URI("http://192.168.1.244:8080/file/upload").toURL();
        String boundary = UUID.randomUUID().toString();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(100000000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; chatset=utf-8; boundary=" + boundary);
        conn.setRequestProperty("Server-Id", "1");
        conn.setRequestProperty("Data-Encrypt-Key", "fdcCLsmJg5lK+NkwuvQsTkSwL8f4Et2XbOZiUNL0LhhWGImy1VWKrZmUoHr46WRsnj4G1wjxdcTuoisM74tn8s7mjCdxU+N7tt1hL2jP9VGqQVlEmHb72WKpNMLWLhjfcJI1L5SmOA2RJWIrOmOJ/6rK0bkqNGitrRQxTcBpZwOL9GmN4dJhPpi8F4awItVbg+ALy1R4oUp8KoD8LLx9OSn5xK0+Di9V9WG3Cu62fXeHt7DrwgyB7KO+Oe+IAy+BKyXleH0sFJucjDftlNHNcaPLBUmveBp6d2NfiSjpprALkBkK3qVlFDItvtvWQkeOxGuSbTa+52ydN8EfO3L7WA==");
        conn.connect();

        String aesKey = "KgJicewzetMADQCS";
        SecretKeySpec secretKey = new SecretKeySpec(aesKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        try (InputStream is = new FileInputStream("C:\\Users\\Administrator\\Desktop\\15-火车票2.JPG");
             DataOutputStream dos = new DataOutputStream(new CipherOutputStream(conn.getOutputStream(), cipher))) {
            dos.writeBytes(
                    "--" + boundary + "\r\n" +
                            "Content-Disposition: form-data; name=\"file\"; filename=\"15-火车票2.JPG\"\r\n" +
                            "Content-Type: image/png; charset=utf-8\r\n\r\n");
            IOUtils.copyLarge(is, dos);
            dos.writeBytes("\r\n--" + boundary + "--\r\n");
            dos.flush();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line = null;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

}
