package com.whl.spring.demo.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

}
