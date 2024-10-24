package com.example.server.domain.entity;

import lombok.Data;

/**
 * 用户信息
 */

@Data
public class User {
    private Long uid; //用户id
    private String email; //邮箱地址
    private String username; //用户名
    private String password; //密码
}
