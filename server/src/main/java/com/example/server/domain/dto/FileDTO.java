package com.example.server.domain.dto;

import lombok.Builder;
import lombok.Data;

//用于向控制层传递文件本体数据

@Data
@Builder
public class FileDTO {
    private Long fid;
    private String fileName;
    private byte[] fileBytes;
}
