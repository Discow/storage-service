package com.example.server.controller;

import com.example.server.config.CustomResourceHttpRequestHandler;
import com.example.server.domain.dto.FileDTO;
import com.example.server.domain.vo.response.RestBean;
import com.example.server.exception.GeneralException;
import com.example.server.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashMap;

/**
 * 文件api：用于上传与下载文件
 */

@RestController
@RequestMapping("/api/file")
public class FileApi {
    private final FileService fileService;
    // 使用ObjectFactory注入CustomResourceHttpRequestHandler（方法注入）
    private final ObjectFactory<CustomResourceHttpRequestHandler> customResourceHttpRequestHandlerObjectFactory;

    @Autowired
    public FileApi(FileService fileService, ObjectFactory<CustomResourceHttpRequestHandler> customResourceHttpRequestHandlerObjectFactory) {
        this.fileService = fileService;
        this.customResourceHttpRequestHandlerObjectFactory = customResourceHttpRequestHandlerObjectFactory;
    }

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

    //预览文件
    @GetMapping("/preview/{fileId}")
    public ResponseEntity<?> preview(@PathVariable Long fileId, HttpServletRequest request, HttpServletResponse response) {
        try {
            // 解析文件路径
            Path filePath = fileService.getFileFSPath(fileId);
            // 设置响应头，inline 会在浏览器中显示或播放文件
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName() + "\"");
            // 获取（注入）customResourceHttpRequestHandler
            CustomResourceHttpRequestHandler customResourceHttpRequestHandler = customResourceHttpRequestHandlerObjectFactory.getObject();
            // 设置资源路径
            customResourceHttpRequestHandler.setResource(filePath);
            // 让 CustomResourceHttpRequestHandler 处理请求
            customResourceHttpRequestHandler.handleRequest(request, response);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (GeneralException e) {
            return ResponseEntity.ok().body(RestBean.failure(400, e.getMessage()));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
