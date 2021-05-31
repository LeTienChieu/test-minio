package com.example.demo.service;

import com.example.demo.dto.FileDTO;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface MinioServiceUsingAmazonS3SDK {
    byte[] getFile(String fileName) throws Exception;

    FileDTO storeFile(String filename, String contentType, byte[] contents) throws Exception;

    void renameFile(String sourceFile, String desFile);

    void deleteFile(String file) throws Exception;

    default byte[] downloadTemplate(String fileName) throws Exception {
        ClassPathResource resource = new ClassPathResource("template/" + fileName);
        if (!resource.exists()){
            throw new FileNotFoundException("file.download.notFound");
        }
        try (InputStream defaultSign = resource.getInputStream()) {
            return IOUtils.toByteArray(defaultSign);
        } catch (IOException e) {
            //todo: handle exception
            e.printStackTrace();
            throw new Exception(e);
        }
    }
}
