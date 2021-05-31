package com.example.demo.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "storage")
public class MinioStorageProperties {
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private long expire = 600;
    private String bucketNamePrefix = "chieult-";
}

