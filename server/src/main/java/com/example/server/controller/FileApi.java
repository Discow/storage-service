package com.example.server.controller;

import com.example.server.domain.dto.FileDTO;
import com.example.server.domain.vo.response.RestBean;
import com.example.server.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

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
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long fileId) {
        FileDTO fileDTO = fileService.download(fileId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileDTO.getFileName());
        return ResponseEntity.ok().headers(headers).body(new ByteArrayResource(fileDTO.getFileBytes()));
    }

    //秒传验证
    @GetMapping("/is-exist")
    public RestBean<?> isFileExist(String fileHash) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("isFileExist", fileService.isFileExist(fileHash));
        return RestBean.success(map);
    }
}
