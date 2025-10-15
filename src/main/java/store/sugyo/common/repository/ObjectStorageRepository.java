package store.sugyo.common.repository;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import store.sugyo.config.properties.ObjectStorageProperties;

import java.io.InputStream;

@RequiredArgsConstructor
@Repository
public class ObjectStorageRepository {
    private final ObjectStorageProperties objectStorageProperties;
    private final S3Template s3Template;

    public String upload(String path, String key, InputStream stream) {
        String fullKey = path.endsWith("/") ? path + key : path + "/" + key;
        S3Resource result = s3Template.upload(objectStorageProperties.getBucket(), fullKey, stream);
        return result.getFilename();
    }

    public String getDownloadUrl(String key) {
        String baseUrl = objectStorageProperties.getCdnUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + "/" + key;
    }

    public void delete(String key) { // 참고로 delete는 실시간 반영되지 않음
        s3Template.deleteObject(objectStorageProperties.getBucket(), key);
    }
}
