package com.example.server.mapper;

import com.example.server.domain.entity.FileMetadata;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMetadataMapper {
    //新增文件元数据
    @Insert("insert into file_metadata(file_name,file_size,file_hash,content_type,upload_time,uid) " +
            "values(#{fileName},#{fileSize},#{fileHash},#{contentType},#{uploadTime},#{uid})")
    void addMetadata(FileMetadata fileMetadata);
}
