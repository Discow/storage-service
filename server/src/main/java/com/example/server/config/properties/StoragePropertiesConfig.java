package com.example.server.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "spring.storage")
@Component
public class StoragePropertiesConfig {
    private String defaultPath;
}
