package com.joojoo.api.user.infrastructure.image;

import com.joojoo.api.user.domain.provider.fileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class fileServiceImpl implements fileService {

    @Value("${STORAGE_ENDPOINT}")
    private String endPoint;

    @Value("${BUCKET_NAME}")
    private String bucket;

    @Override
    public String getFullUrl(String fileName) {
        return endPoint + bucket + fileName;
    }
}
