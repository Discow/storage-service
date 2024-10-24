package com.example.server.controller;

import com.example.server.domain.vo.response.RestBean;
import com.example.server.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件api：用于上传与下载文件
 */

@RestController
@RequestMapping("/api/file")
public class FileApi {
    @Resource
    FileService fileService;

    //上传文件
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RestBean<?> upload(MultipartFile file) {
        fileService.upload(file);
        return RestBean.success("文件上传成功");
    }

    //下载文件
    @GetMapping("/download/{fileId}")
    public RestBean<?> download(@PathVariable Long fileId) {
        return RestBean.success();
    }
}
