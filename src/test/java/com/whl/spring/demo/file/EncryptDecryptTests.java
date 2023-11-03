package com.whl.spring.demo.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EncryptDecryptTests {
    private static Logger logger = LoggerFactory.getLogger(EncryptDecryptTests.class);

    private static String aesKey;

    private static String originalFile;

    private static String encryptFile;

    private static String decryptFile;

    private static String content;

    @BeforeAll
    public static void init() {
        String baseDir = "C:\\Users\\Administrator\\Desktop";
        aesKey = RandomStringUtils.randomAlphanumeric(16);
        originalFile = baseDir + File.separator + "nginx.txt";
        encryptFile = baseDir + File.separator + "nginx-new.txt.enc";
        decryptFile = baseDir + File.separator + "nginx-new.txt";
    }

    @Test
    @Order(1)
    public void testEncrypt() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        try (InputStream is = new FileInputStream(originalFile)) {
            content = IOUtils.toString(is, StandardCharsets.UTF_8);
            logger.info("file content: {}", content);
        }

        try (InputStream is = new FileInputStream(originalFile);
             CipherOutputStream cos = new CipherOutputStream(
                     new FileOutputStream(encryptFile),
                     this.buildAesEncryptCipher(aesKey))) {
            logger.info("encrypt aesKey: {}", aesKey);
            IOUtils.copyLarge(is, cos);
        }
        assertTrue(new File(encryptFile).length() > 0);
    }

    @Test
    @Order(2)
    public void testDecrypt() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        try (OutputStream os = new FileOutputStream(decryptFile);
             CipherInputStream cis = new CipherInputStream(
                     new FileInputStream(encryptFile),
                     this.buildAesDecryptCipher(aesKey))) {
            logger.info("decrypt aesKey: {}", aesKey);
            IOUtils.copyLarge(cis, os);
        }
        String newContent = null;

        try (InputStream is = new FileInputStream(decryptFile)) {
            newContent = IOUtils.toString(is, StandardCharsets.UTF_8);
            logger.info("file content: {}", newContent);
        }
        assertEquals(content, newContent);
    }

    private Cipher buildAesEncryptCipher(String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher;
    }

    private Cipher buildAesDecryptCipher(String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher;
    }

    @Test
    public void testWrite() throws IOException {
        String json = "{\"a\":\"b\"}";
        this.write(json.getBytes(StandardCharsets.UTF_8), new FileOutputStream(encryptFile));
    }

    @Test
    public void testRead() throws IOException {
        byte[] data = this.read(new FileInputStream(encryptFile));
        System.out.println(new String(data, StandardCharsets.UTF_8));
    }

    private void write(byte[] data, OutputStream os) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(os)) {
            int dataLength = data.length;
            System.out.println("dataLength=" + dataLength);

            if (dataLength > 0) {
                // 生成随机索引数量
                Random random = new Random();
                int randomCount = random.nextInt(dataLength);

                if (randomCount > 256) {
                    randomCount = 256;
                }
                // 构造随机索引
                long[] randoms;
                System.out.println("randomCount=" + randomCount);

                if (randomCount > 0) {
                    randoms = new long[randomCount];
                    Set<Long> cache = new HashSet<Long>();

                    for (int i = 0; i < randoms.length; i++) {
                        long r;

                        do {
                            r = random.nextLong(dataLength);
                        } while (cache.contains(r));
                        cache.add(r);
                        randoms[i] = r;
                    }
                    Arrays.sort(randoms);
                    System.out.println("randomIndexes=" + Arrays.toString(randoms));
                } else {
                    randoms = new long[0];
                }

                // 写入数据
                dos.writeInt(randomCount);

                for (long r : randoms) {
                    dos.writeLong(r);
                }
                int i = 0; // 随机字符插入位置数组游标

                for (int j = 0; j < dataLength; j++) {
                    if (i < randoms.length && j == randoms[i]) {
                        dos.writeUTF(RandomStringUtils.randomAlphanumeric(random.nextInt(10)));
                        i++;
                    }
                    dos.writeByte(data[j]);
                }
            }
        }
    }

    private byte[] read(InputStream is) throws IOException {
        try (DataInputStream dis = new DataInputStream(is);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int randomCount = dis.readInt();

            if (randomCount > 0) {
                long[] randoms = new long[randomCount];

                for (int i = 0; i < randomCount; i++) {
                    randoms[i] = dis.readLong();
                }
                int i = 0; // 随机索引游标
                int j = 0; // 原始数据游标

                while (true) {
                    if (i < randoms.length && j == randoms[i]) {
                        try {
                            dis.readUTF();
                        } catch (EOFException e) {
                            break;
                        }
                        i++;
                    }
                    int k = dis.read();

                    if (k == -1) {
                        break;
                    } else {
                        baos.write(k);
                    }
                    j++;
                }
                return baos.toByteArray();
            }
            IOUtils.copyLarge(dis, baos);
            return baos.toByteArray();
        }
    }

}
