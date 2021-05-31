package com.example.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.demo.dto.FileDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ofPattern;

@Slf4j
@Service
public class MinioServiceUsingAmazonS3SDKImpl implements MinioServiceUsingAmazonS3SDK {


    private final Object lock = new Object();
    private AmazonS3 minioClient;
    @Value("${storage.bucketNamePrefix}")
    private String bucketNamePrefix;

    MinioServiceUsingAmazonS3SDKImpl(AmazonS3 minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public byte[] getFile(String fileName) throws Exception {
        String bucketName = FilenameUtils.getPathNoEndSeparator(fileName);
        fileName = FilenameUtils.getName(fileName);
        if (!minioClient.doesObjectExist(bucketName, fileName)) {
            return new byte[0];
        }
        try {
            S3Object s3Object = minioClient.getObject(bucketName, fileName);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        } catch (Exception ex) {
            throw new Exception("error.system", ex);
        }
    }

    @Override
    public FileDTO storeFile(String filename, String contentType, byte[] contents) throws Exception {
        String fileName = StringUtils.cleanPath(filename);
        if (fileName.contains("..")) {
            throw new Exception("Sorry! Filename contains invalid path sequence " + fileName);
        }
        fileName = String.format("%s%s_%s", RandomStringUtils.randomAlphabetic(8), System.currentTimeMillis(), fileName);
        String bucketName = bucketNamePrefix + LocalDateTime.now().format(ofPattern("yyyyMMddHH"));
        try {
            if (!minioClient.doesBucketExistV2(bucketName)) {
                synchronized (lock) {
                    if (!minioClient.doesBucketExistV2(bucketName)) {
                        minioClient.createBucket(bucketName);
                    }
                }
            }
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            try (InputStream inputStream = new ByteArrayInputStream(contents)) {
                minioClient.putObject(bucketName, fileName, inputStream, metadata);
                String fileUrl = UriUtils.encodePath(String.format("/%s/%s", bucketName, fileName), StandardCharsets.UTF_8);
                return new FileDTO(bucketName, fileName, fileUrl, metadata.getContentType(), LocalDateTime.now());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new Exception("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public void renameFile(String sourceFile, String desFile) {
        throw new UnsupportedOperationException("implement me");
    }

    @Override
    public void deleteFile(String file) throws Exception {
        String bucketName = FilenameUtils.getPathNoEndSeparator(file);
        String fileName = FilenameUtils.getName(file);
        if (!minioClient.doesObjectExist(bucketName, fileName)) {
            throw new Exception("file.notFound");
        }
        try {
            minioClient.deleteObject(bucketName, fileName);
        } catch (Exception ex) {
            throw new Exception("error.system", ex);
        }
    }
}
