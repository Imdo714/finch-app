package com.joojoo.api.user.infrastructure.image;

import com.joojoo.api.user.application.port.out.FilePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class FileStorageAdapter implements FilePort {

    @Value("${STORAGE_ENDPOINT}")
    private String endPoint;

    @Value("${BUCKET_NAME}")
    private String bucket;

    @Override
    public String getFullUrl(String fileName) {
        return endPoint + bucket + fileName;
    }
}
