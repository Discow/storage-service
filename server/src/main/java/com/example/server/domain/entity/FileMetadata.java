package com.example.server.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件元数据
 */

@Data
@Builder
public class FileMetadata {
    private Long fid; //文件id
    private String fileName; //文件名
    private Long fileSize; //文件大小（byte）
    private String fileHash; //文件hash值（md5）
    private String contentType; //文件类型
    private LocalDateTime uploadTime; //上传时间
    private Long uid; //用户id（外键）
}
