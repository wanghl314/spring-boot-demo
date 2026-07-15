package com.whl.spring.demo.controller;

import com.whl.spring.demo.util.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FilePathSafetyTests {

    @TempDir
    Path tempDir;

    @Test
    void resolveWithinBase_allowsSimpleFileName() {
        Path resolved = FileUtils.resolveWithinBase(tempDir, "abc-123.txt");
        assertNotNull(resolved);
        assertEquals(tempDir.resolve("abc-123.txt").normalize(), resolved);
    }

    @Test
    void resolveWithinBase_rejectsParentDirectoryTraversal() {
        assertNull(FileUtils.resolveWithinBase(tempDir, "../secret.txt"));
        assertNull(FileUtils.resolveWithinBase(tempDir, "..\\secret.txt"));
        assertNull(FileUtils.resolveWithinBase(tempDir, "sub/../../secret.txt"));
    }

    @Test
    void resolveWithinBase_rejectsAbsolutePath() {
        Path absolute = tempDir.resolveSibling("outside.txt").toAbsolutePath();
        assertNull(FileUtils.resolveWithinBase(tempDir, absolute.toString()));
    }

    @Test
    void resolveWithinBase_rejectsBlankName() {
        assertNull(FileUtils.resolveWithinBase(tempDir, " "));
        assertNull(FileUtils.resolveWithinBase(tempDir, ""));
        assertNull(FileUtils.resolveWithinBase(tempDir, null));
    }

}
