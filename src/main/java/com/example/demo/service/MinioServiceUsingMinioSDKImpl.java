package com.example.demo.service;

import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.ofPattern;

@Service
@Slf4j
public class MinioServiceUsingMinioSDKImpl implements MinioServiceUsingMinioSDK {
    @Value("${project.demo.minio.default.bucket}")
    private String DEFAULT_BUCKET;

    @Value("${project.demo.minio.host}")
    private String MINIO_HOST;

    @Value("${project.demo.minio.bucket}")
    private String BUCKET;

    @Value("${storage.bucketNamePrefix}")
    private String bucketNamePrefix;

    private final Object lock = new Object();

    private final MinioClient minioClient;

    public MinioServiceUsingMinioSDKImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void createBucket(String bucketName) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                //create bucket name
                synchronized (lock) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                }
            }
        } catch (Exception e) {
            log.error("Check exists bucket", e);
        }
    }

    @Override
    public String uploadTempFile(MultipartFile fileInput) throws Exception {
        if (fileInput.isEmpty()) {
            return "";
        }
        String originFileName = fileInput.getOriginalFilename();
        String objectName = StringUtils.cleanPath(Objects.requireNonNull(originFileName));
        if (objectName.contains("..")) {
            throw new Exception("Sorry! Filename contains invalid path sequence " + objectName);
        }
        objectName = String.format("%s%s_%s", RandomStringUtils.randomAlphabetic(8), System.currentTimeMillis(), objectName);
        String bucketName = bucketNamePrefix + LocalDateTime.now().format(ofPattern("yyyyMMddHH"));
        //check bucket name exists
        this.createBucket(bucketName);
        String randomNameForTempFile = "";
        String fileUrl = "";
        try {
            //store temp file in local storage server
            randomNameForTempFile = RandomStringUtils.random(10, true, false);
            FileUtils.writeByteArrayToFile(new File(randomNameForTempFile), fileInput.getBytes());
            //Upload file with minio SDK
            ObjectWriteResponse writeResponse = minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(randomNameForTempFile)
                            .contentType(fileInput.getContentType())
                            .build());
            fileUrl = UriUtils.encodePath(String.format("/%s/%s", bucketName, objectName), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("UploadFile error occur");
        } finally {
            File handleFileTemp = new File(randomNameForTempFile);
            if (handleFileTemp.exists()) {
                handleFileTemp.delete();
            }
        }
        return fileUrl;
    }

    @Override
    public byte[] getFileStore(String filePath) {
        String bucketName = FilenameUtils.getPathNoEndSeparator(filePath);
        String fileName = FilenameUtils.getName(filePath);
        //get InputStream file
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build())) {
            // Read data from stream
            return IOUtils.toByteArray(stream);
        } catch (Exception e) {
            //case1: file not exist
            //case2:permission has change
            //...
            //todo: handle  exception
        }
        return new byte[0];
    }

    @Override
    public List<String> getAllFileNameInBucket(String bucketName) {
                //get all file info in bucket
        List<String> allFileName = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(BUCKET).build());
            //show all file name in bucket
            for (Result<Item> result : results) {
                allFileName.add(result.get().objectName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allFileName;
    }

    @Override
    public boolean removeOnCloud(String bucketName, String fileName) {
        // Remove object.
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
            return true;
        } catch (Exception e) {
            //todo: handle case can not delete
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeBucket(String bucketName) {
//         Remove object
//        if bucket is not empty --> can not remove bucket
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
