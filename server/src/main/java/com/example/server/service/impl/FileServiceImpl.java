package com.example.server.service.impl;

import com.example.server.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public void upload(MultipartFile file) {

    }

    @Override
    public void download(Long fileId) {

    }
}
