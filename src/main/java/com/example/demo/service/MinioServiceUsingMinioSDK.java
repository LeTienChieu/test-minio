package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MinioServiceUsingMinioSDK {

    void createBucket(String bucketName);

    String uploadTempFile(MultipartFile file) throws Exception;

    byte[] getFileStore(String filePath);

    List<String> getAllFileNameInBucket(String bucketName);

    boolean removeOnCloud(String bucketName, String fileName);

    boolean removeBucket(String bucketName);
}
