package com.example.server.service.impl;

import com.example.server.domain.entity.FileMetadata;
import com.example.server.exception.GeneralException;
import com.example.server.mapper.FileMetadataMapper;
import com.example.server.mapper.SettingsMapper;
import com.example.server.service.FileService;
import com.example.server.util.HashUtils;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.server.util.Constants.Settings;
import static com.example.server.util.Constants.User;

@Service
public class FileServiceImpl implements FileService {
    @Resource
    FileMetadataMapper fileMetadataMapper;
    @Resource
    SettingsMapper settingsMapper;

    @SneakyThrows
    @Override
    public void upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new GeneralException("文件为空，请重新上传");
        }
        //获取文件MD5
        String fileMD5 = HashUtils.calculateFileMD5(file.getInputStream());

        // TODO 增加秒传验证（去重）功能

        //保存文件到文件系统（basePath/uid/fileMD5.txt）
        Path path = Path.of(settingsMapper.getValue(Settings.STORAGE_PATH_KEY)
                + "/"
                + User.PUBLIC_USER_ID
                + "/"
                + fileMD5
                + Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")));

        //如果父目录不存在则创建
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        file.transferTo(path);

        //保存文件元数据
        fileMetadataMapper.addMetadata(FileMetadata.builder()
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .fileHash(fileMD5)
                .contentType(file.getContentType())
                .uploadTime(LocalDateTime.now())
                .uid(User.PUBLIC_USER_ID)
                .build());
    }

    @Override
    public void download(Long fileId) {

    }
}
