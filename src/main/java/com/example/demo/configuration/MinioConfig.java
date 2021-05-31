package com.example.demo.configuration;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${project.demo.minio.host}")
    private String MINIO_HOST;

    @Value("${project.demo.minio.accesst.key}")
    private String MINIO_ACCESS_KEY;

    @Value("${project.demo.minio.secret.key}")
    private String MINIO_SECRET_KEY;

    @Bean
    public MinioClient configMinio() {
                return MinioClient.builder()
                        .endpoint(MINIO_HOST)
                        .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
                        .build();
    }
}
