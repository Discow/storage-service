package com.example.server.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务
 */

public interface FileService {
    //上传文件
    void upload(MultipartFile file);

    //下载文件
    void download(Long fileId);

    //验证文件是否存在
    boolean isFileExist(String fileHash);
}
