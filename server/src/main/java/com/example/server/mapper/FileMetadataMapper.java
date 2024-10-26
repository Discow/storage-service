package com.example.server.mapper;

import com.example.server.domain.entity.FileMetadata;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FileMetadataMapper {
    //新增文件元数据
    @Insert("insert into file_metadata(file_name,file_size,file_hash,content_type,upload_time,uid) " +
            "values(#{fileName},#{fileSize},#{fileHash},#{contentType},#{uploadTime},#{uid})")
    void addMetadata(FileMetadata fileMetadata);

    //查询文件是否存在
    @Select("select count(*) > 0 from file_metadata where file_hash=#{fileHash}")
    boolean existByHash(@Param("fileHash") String fileHash);

    @Select("select count(*) > 0 from file_metadata where file_name=#{fileName}")
    boolean existByName(@Param("fileName") String fileName);

    //根据文件id获取文件metadata
    @Select("select * from file_metadata where fid=#{fid}")
    FileMetadata findById(@Param("fid") Long fid);
}
