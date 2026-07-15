package com.whl.spring.demo.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ContentDisposition;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileUtils {

    public static String contentDispositionAttachment(String filename) {
        return ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build()
                .toString();
    }

    public static Path resolveWithinBase(Path baseDir, String name) {
        if (baseDir == null || StringUtils.isBlank(name) || name.indexOf('\0') >= 0) {
            return null;
        }
        Path base = baseDir.toAbsolutePath().normalize();
        Path resolved = base.resolve(name).toAbsolutePath().normalize();
        if (!resolved.startsWith(base)) {
            return null;
        }
        return resolved;
    }

}
