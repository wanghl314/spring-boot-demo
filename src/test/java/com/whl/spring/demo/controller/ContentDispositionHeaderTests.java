package com.whl.spring.demo.controller;

import com.whl.spring.demo.util.FileUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContentDispositionHeaderTests {

    @Test
    void asciiFilename_isQuoted() {
        String header = FileUtils.contentDispositionAttachment("report.pdf");
        assertEquals("attachment; filename=\"report.pdf\"; filename*=UTF-8''report.pdf", header);
    }

    @Test
    void spacesAndCommas_areQuotedAndEncoded() {
        String header = FileUtils.contentDispositionAttachment("my file,1.pdf");
        assertTrue(header.startsWith("attachment; "));
        assertTrue(header.contains("filename=\"my file,1.pdf\""));
        assertTrue(header.contains("filename*=UTF-8''my%20file%2C1.pdf"));
    }

    @Test
    void chineseFilename_usesRfc5987FilenameStar() {
        String header = FileUtils.contentDispositionAttachment("中文测试.pdf");
        assertTrue(header.startsWith("attachment; "));
        assertTrue(header.contains("filename*=UTF-8''%E4%B8%AD%E6%96%87%E6%B5%8B%E8%AF%95.pdf"));
        assertFalse(header.contains("filename=中文"), "legacy filename must not embed raw non-ASCII");
    }

    @Test
    void quotesInFilename_areEscaped() {
        String header = FileUtils.contentDispositionAttachment("a\"b.txt");
        assertTrue(header.contains("filename=\"a\\\"b.txt\""));
        assertTrue(header.contains("filename*=UTF-8''a%22b.txt"));
    }

}
