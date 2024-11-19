package com.example.server.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@Component
@Scope("request") //这里必须设置request作用域，以避免并发请求时资源出现混乱的问题
public class CustomResourceHttpRequestHandler extends ResourceHttpRequestHandler {
    private Resource resource;

    @Override
    protected Resource getResource(@NonNull HttpServletRequest request) {
        return this.resource;
    }

    public void setResource(Path filePath) throws MalformedURLException {
        this.resource = new UrlResource(filePath.toUri());
        setLocations(List.of(this.resource));
    }
}
