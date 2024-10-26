package com.example.server.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@Component
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
