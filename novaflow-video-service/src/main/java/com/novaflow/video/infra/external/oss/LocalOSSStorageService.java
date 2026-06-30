package com.novaflow.video.infra.external.oss;

import com.novaflow.video.domain.model.video.valueobject.OSSStorageInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class LocalOSSStorageService implements OSSStorageService {

    private final Path uploadDir;

    public LocalOSSStorageService(@Value("${app.storage.local-dir:data/uploads/videos}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public OSSStorageInfo uploadVideo(MultipartFile file, String videoId) {
        try {
            Files.createDirectories(uploadDir);
            String filename = file.getOriginalFilename() == null ? "video" : file.getOriginalFilename();
            String extension = filename.contains(".") ? filename.substring(filename.lastIndexOf(".")) : "";
            String objectKey = videoId + extension;
            Path target = uploadDir.resolve(objectKey).normalize();
            file.transferTo(target);
            return OSSStorageInfo.of("local", objectKey, target.toUri().toString(), "local");
        } catch (IOException e) {
            throw new IllegalStateException("本地视频保存失败", e);
        }
    }

    @Override
    public void deleteVideo(String objectKey) {
        try {
            Files.deleteIfExists(uploadDir.resolve(objectKey).normalize());
        } catch (IOException e) {
            throw new IllegalStateException("本地视频删除失败", e);
        }
    }

    @Override
    public String generatePresignedUrl(String objectKey, long expirationSeconds) {
        return uploadDir.resolve(objectKey).normalize().toUri().toString();
    }
}
