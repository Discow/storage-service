package com.example.server.service.impl;

import com.example.server.domain.dto.FileDTO;
import com.example.server.domain.entity.FileMetadata;
import com.example.server.exception.GeneralException;
import com.example.server.mapper.FileMetadataMapper;
import com.example.server.mapper.SettingsMapper;
import com.example.server.service.FileService;
import com.example.server.util.HashUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.server.util.Constants.Settings;
import static com.example.server.util.Constants.User;

@Service
public class FileServiceImpl implements FileService {
    private final FileMetadataMapper fileMetadataMapper;
    private final String basePath;

    @Autowired
    public FileServiceImpl(FileMetadataMapper fileMetadataMapper, SettingsMapper settingsMapper) {
        this.fileMetadataMapper = fileMetadataMapper;
        this.basePath = settingsMapper.getValue(Settings.STORAGE_PATH_KEY);
    }

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

    @SneakyThrows
    @Override
    public FileDTO download(Long fileId) {
        FileMetadata fileMetadata = fileMetadataMapper.findById(fileId);
        if (fileMetadata == null) {
            throw new GeneralException("文件不存在");
        }
        String fileName = fileMetadata.getFileName(); //文件名
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".")); //后缀
        String fileHash = fileMetadata.getFileHash(); //文件hash
        Path path = Paths.get(basePath + "/" + fileHash + fileSuffix); //文件在文件系统的路径
        return FileDTO.builder()
                .fileName(fileName)
                .fileBytes(Files.readAllBytes(path))
                .build();
    }

    @Override
    public boolean isFileExist(String fileHash) {
        return fileMetadataMapper.existByHash(fileHash);
    }

    //抽取方法-保存到文件系统（basePath/fileMD5.txt）
    void saveToFS(MultipartFile file, String fileMD5) throws IOException {
        Path path = Path.of(basePath
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
