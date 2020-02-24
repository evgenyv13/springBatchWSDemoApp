package com.evgenyv13.demoapp.rest.service;

import com.evgenyv13.demoapp.rest.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file) {
        try {
            Path copyLocation = Paths
                    .get(uploadDir + File.separator + System.currentTimeMillis() + StringUtils.cleanPath(file.getOriginalFilename()));
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            return copyLocation.toString();
        } catch (Exception e) {
            throw new FileStorageException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }
    }

    public void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }


}
