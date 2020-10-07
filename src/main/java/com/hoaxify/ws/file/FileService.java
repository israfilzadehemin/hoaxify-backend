package com.hoaxify.ws.file;

import com.hoaxify.ws.Configuration.AppConfiguration;
import com.hoaxify.ws.user.User;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FileService {

    private final AppConfiguration appConfiguration;
    private final FileAttachmentRepository fileAttachmentRepository;


    @SneakyThrows
    public String writeBase64EncodedStringToFile(String img) {

        String filename = generateRandomName();
        File file = new File(String.format("%s/%s", appConfiguration.getProfileStorage(), filename));
        OutputStream os = new FileOutputStream(file);

        byte[] base64encoded = Base64.getDecoder().decode(img);

        os.write(base64encoded);
        os.close();
        return filename;

    }

    public String generateRandomName() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public void deleteProfileImage(String oldImage) {
        if (oldImage == null) {
            return;
        }
        deleteFile(Paths.get(appConfiguration.getProfileStoragePath(), oldImage));
    }

    public String detectType(byte[] arr) {
        return new Tika().detect(arr);
    }

    public String detectType(String value) {
        return new Tika().detect(Base64.getDecoder().decode(value));
    }

    @SneakyThrows
    public FileAttachment saveHoaxAttachment(MultipartFile multipartFile) {
        Tika tika = new Tika();
        String fileName = generateRandomName();
        File target = new File(String.format("%s/%s", appConfiguration.getAttachmentStoragePath(), fileName));
        OutputStream os = new FileOutputStream(target);
        os.write(multipartFile.getBytes());
        os.close();

        FileAttachment fa = new FileAttachment();
        fa.setName(fileName);
        fa.setDate(new Date());
        fa.setFileType(detectType(multipartFile.getBytes()));
        return fileAttachmentRepository.save(fa);
    }

    public void deleteAttachmentImage(String oldImage) {
        if (oldImage == null) {
            return;
        }
        deleteFile(Paths.get(appConfiguration.getAttachmentStoragePath(), oldImage));
    }

    @SneakyThrows
    private void deleteFile(Path path) {
        Files.deleteIfExists(path);
    }

    public void deleteRelatedFiles(User user) {
        deleteProfileImage(user.getImage());
        fileAttachmentRepository.findByHoaxUser(user).forEach(file -> deleteAttachmentImage(file.getName()));
    }
}
