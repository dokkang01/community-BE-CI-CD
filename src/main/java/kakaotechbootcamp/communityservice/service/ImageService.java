package kakaotechbootcamp.communityservice.service;

import org.springframework.beans.factory.annotation.Value; //
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Service
public class ImageService {
    private final S3Presigner presigner;
    private final String bucket;

    public ImageService(S3Presigner presigner, @Value("${app.s3.bucket}") String bucket) {
        this.presigner = presigner;
        this.bucket = bucket;
    }

    public UploadUrlResponse createUploadUrl(String kind, String filename, String contentType, Long userId) {
        // kind 에 따라 디렉토리 다르게 (PROFILE / POST 등)
        String folder = "etc";
        if ("PROFILE".equalsIgnoreCase(kind)) {
            folder = "profiles";
        } else if ("POST".equalsIgnoreCase(kind)) {
            folder = "posts";
        }

        // 파일 이름 sanitize
        String safeName = (filename == null || filename.isBlank())
                ? "image"
                : filename.replaceAll("[^a-zA-Z0-9._-]", "_");

        // 예: profiles/12/1732787123123_avatar.png
        String objectKey = String.format(
                "%s/%d/%d_%s",
                folder,
                userId != null ? userId : 0L,
                System.currentTimeMillis(),
                safeName
        );

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType != null ? contentType : "application/octet-stream")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(10)) // URL 10분 유효
                .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(presignRequest);

        return new UploadUrlResponse(presigned.url().toString(), objectKey);
    }

    // 프론트가 기대하는 응답 형태: { "uploadUrl": "...", "key": "..." }
    public record UploadUrlResponse(String uploadUrl, String key) {}
}