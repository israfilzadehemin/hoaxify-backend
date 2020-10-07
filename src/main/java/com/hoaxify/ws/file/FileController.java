package com.hoaxify.ws.file;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@RestController
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/api/1.0/hoax-attachment")
    FileAttachment saveHoaxAttachment(MultipartFile file) {
        return fileService.saveHoaxAttachment(file);
    }
}
