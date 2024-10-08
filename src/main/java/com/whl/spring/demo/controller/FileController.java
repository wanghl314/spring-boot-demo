package com.whl.spring.demo.controller;

import com.whl.spring.demo.bean.FileInfo;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    @GetMapping("/list")
    public List<FileInfo> list(HttpServletRequest request) throws Exception {
        Path path = this.getFileStorePath();
        File[] files = new File(path.toString()).listFiles();
        List<FileInfo> datas = new ArrayList<FileInfo>();

        if (files != null) {
            for (File file : files) {
                datas.add(this.build(file));
            }
        }
        return datas;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "文件上传接口", description = "上传接口")
    public FileInfo upload(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        String suffix = "";

        if (StringUtils.contains(filename, ".")) {
            suffix = filename.substring(filename.lastIndexOf("."));
        }
        Path path = this.getFileStorePath();
        Path destination = path.resolve(UUID.randomUUID() + suffix);

        if (!Files.exists(destination.getParent())) {
            Files.createDirectories(destination.getParent());
        }
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return this.build(destination.toFile());
    }

    @GetMapping("/download/{name}")
    public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable String name) throws Exception {
        Path path = this.getFileStorePath();
        Path destination = path.resolve(name);

        if (Files.exists(destination)) {
            File file = destination.toFile();
            response.setHeader("Content-Type", this.getContentType(file));
            response.setHeader("Content-Disposition", "attachment; filename=" + name);
            response.setHeader("Content-Length", String.valueOf(file.length()));
            response.setHeader("Cache-Control", "public,max-age=604800");

            try (OutputStream os = response.getOutputStream()) {
                Files.copy(destination, os);
                os.flush();
            }
            return;
        }
        request.getRequestDispatcher("/404").forward(request, response);
    }

    private FileInfo build(File file) {
        return new FileInfo(file.getName(), file.length(), file.lastModified());
    }

    public String getContentType(File file) {
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        if (file != null && file.exists()) {
            try {
                MediaType mediaType = MediaTypeFactory.getMediaType(new FileSystemResource(file)).orElse(MediaType.APPLICATION_OCTET_STREAM);
                contentType = mediaType.toString();
            } catch (Exception ignored) {
            }
        }
        return contentType;
    }

    private Path getFileStorePath() {
        String prefix = System.getProperty("demo.home");

        if (StringUtils.isBlank(prefix)) {
            prefix = System.getProperty("user.dir");
        }
        return Paths.get(prefix , "upload");
    }

}
