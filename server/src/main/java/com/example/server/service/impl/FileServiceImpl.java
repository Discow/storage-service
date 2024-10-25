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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

        //秒传验证（去重）功能（需要前端配合校验 api: '/api/file/is-exist'）
        boolean isFileHashExist = fileMetadataMapper.existByHash(fileMD5);
        boolean isFileNameExist = fileMetadataMapper.existByName(file.getOriginalFilename());
        //如果文件名和hash值相同则文件已存在，无需任何操作
        if (isFileHashExist && isFileNameExist) {
            throw new GeneralException("文件已存在");
        } else if (!isFileHashExist && isFileNameExist) {
            //如果hash值不同，文件名相同，则重命名文件，然后存入文件系统，再保存元数据
            String renameFile = file.getOriginalFilename() + "(1)";
            saveToFS(file, fileMD5);
            saveMetadata(file, fileMD5, renameFile);
        } else if (isFileHashExist) {
            //如果hash值相同，文件名不同，则只用存储元数据，无需存入文件系统
            saveMetadata(file, fileMD5);
        } else {
            saveToFS(file, fileMD5);
            saveMetadata(file, fileMD5);
        }
    }

    @Override
    public void download(Long fileId) {

    }

    @Override
    public boolean isFileExist(String fileHash) {
        return fileMetadataMapper.existByHash(fileHash);
    }

    //抽取方法-保存到文件系统（basePath/fileMD5.txt）
    void saveToFS(MultipartFile file, String fileMD5) throws IOException {
        Path path = Path.of(settingsMapper.getValue(Settings.STORAGE_PATH_KEY)
                + "/"
                + fileMD5
                + Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")));

        //如果父目录不存在则创建
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        file.transferTo(path);
    }

    //抽取方法-保存元数据到数据库
    void saveMetadata(MultipartFile file, String fileMD5, String fileName) {
        if (!StringUtils.hasText(fileName)) {
            fileName = file.getOriginalFilename();
        }
        fileMetadataMapper.addMetadata(FileMetadata.builder()
                .fileName(fileName)
                .fileSize(file.getSize())
                .fileHash(fileMD5)
                .contentType(file.getContentType())
                .uploadTime(LocalDateTime.now())
                .uid(User.PUBLIC_USER_ID)
                .build());
    }

    void saveMetadata(MultipartFile file, String fileMD5) {
        this.saveMetadata(file, fileMD5, null);
    }
}
