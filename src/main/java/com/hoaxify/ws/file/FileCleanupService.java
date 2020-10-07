package com.hoaxify.ws.file;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
@EnableScheduling
public class FileCleanupService {

    private final FileAttachmentRepository fileAttachmentRepository;
    private final FileService fileService;

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void cleanupStorage() {
        fileAttachmentRepository.findByDateBeforeAndHoaxIsNull(new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000)))
                .forEach(file -> {
                    fileService.deleteAttachmentImage(file.getName());
                    fileAttachmentRepository.deleteById(file.getId());
                });
    }
}
