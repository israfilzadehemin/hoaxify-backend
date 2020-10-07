package com.hoaxify.ws.Configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "hoaxify")
public class AppConfiguration {
    private String uploadPath;

    private String profileStorage = "profile";
    private String attachmentStorage = "attachment";

    public String getProfileStoragePath() {
        return String.format("%s/%s", uploadPath, profileStorage);
    }

    public String getAttachmentStoragePath() {
        return String.format("%s/%s", uploadPath, attachmentStorage);
    }
}
