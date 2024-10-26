package com.example.server.service;

import com.example.server.domain.dto.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * 文件服务
 */

public interface FileService {
    //上传文件
    void upload(MultipartFile file);

    //下载文件
    FileDTO download(Long fileId);

    //验证文件是否存在
    boolean isFileExist(String fileHash);

    //通过文件id获取文件在文件系统的路径
    Path getFileFSPath(Long fileId);
}
