package com.example.server.domain.entity;

import lombok.Data;

/**
 * 用于保存系统的设置
 */

@Data
public class Settings {
    private Integer sid;
    private String keyName;
    private String value;
}
