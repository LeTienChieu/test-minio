package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO implements Serializable {
    private String path;
    private String filename;
    private String fileCode;
    private String fileUrl;
    @JsonIgnore
    private byte[] contents;
    private String contentType;
    private long size;
    private boolean electronicSign;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonFormat(pattern="dd/MM/yyyy hh:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalDateTime createDate;

    public FileDTO(String path, String filename, String fileUrl, String contentType, LocalDateTime createDate) {
        this.path = path;
        this.filename = filename;
        this.fileUrl = fileUrl;
        this.contentType = contentType;
        this.createDate = createDate;
    }

    public FileDTO(String filename, String fileCode, String fileUrl, boolean electronicSign, LocalDateTime createDate) {
        this.filename = filename;
        this.fileCode = fileCode;
        this.fileUrl = fileUrl;
        this.electronicSign = electronicSign;
        this.createDate = createDate;
    }

    public FileDTO(String filename, String fileCode, byte[] contents, boolean electronicSign, LocalDateTime createDate) {
        this.filename = filename;
        this.fileCode = fileCode;
        this.contents = contents;
        this.electronicSign = electronicSign;
        this.createDate = createDate;
    }
}