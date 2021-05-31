package com.example.demo.controller;

import com.example.demo.dto.FileDTO;
import com.example.demo.service.MinioServiceUsingAmazonS3SDK;
import com.example.demo.service.MinioServiceUsingMinioSDK;;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class Controller {

    private final MinioServiceUsingMinioSDK minioSDK;

    private final MinioServiceUsingAmazonS3SDK amazonS3SDK;

    public Controller(MinioServiceUsingMinioSDK minioSDK, MinioServiceUsingAmazonS3SDK amazonS3SDK) {
        this.minioSDK = minioSDK;
        this.amazonS3SDK = amazonS3SDK;
    }


    @PostMapping("/push-file-minio")
    public String test001(@RequestParam("file") MultipartFile file) throws Exception {
        return this.minioSDK.uploadTempFile(file);
    }

    @PostMapping("/push-file-amazon")
    public String test002(@RequestParam("file") MultipartFile file) throws Exception {
        FileDTO response = this.amazonS3SDK.storeFile(file.getName(), file.getContentType(), file.getBytes());
        return response.getFileUrl();
    }

    @GetMapping("/get-file-minio")
    public byte[] test003(@RequestParam String filePath) {
        return this.minioSDK.getFileStore(filePath);
    }

    @GetMapping("/get-file-amazon")
    public byte[] test004(@RequestParam String filePath) throws Exception {
        return this.amazonS3SDK.getFile(filePath);
    }
}
