package com.example.server.config;

import com.example.server.mapper.SettingsMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.example.server.util.Constants.Settings;

/**
 * 启动时初始化
 */

@Component
public class InitializerConfig implements CommandLineRunner {
    @Resource
    SettingsMapper settingsMapper;
    @Value("${spring.storage.default-path}")
    String defaultPath;

    @Override
    public void run(String... args) throws Exception {
        //检查配置项
        String storagePath = settingsMapper.getValue(Settings.STORAGE_PATH_KEY);
        String os = settingsMapper.getValue(Settings.OS_KEY);
        String currentOS = System.getProperty("os.name");
        //初始化配置（如果配置项不存在则使用默认值）
        if (!StringUtils.hasText(storagePath)) {
            settingsMapper.addKey(Settings.STORAGE_PATH_KEY, defaultPath);
            //创建默认存储路径
            Path path = Paths.get(defaultPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path); //可递归创建父目录
            }
        }
        if (!StringUtils.hasText(os)) {
            settingsMapper.addKey(Settings.OS_KEY, currentOS);
        } else {
            settingsMapper.updateValue(Settings.OS_KEY, currentOS);
        }
    }
}
