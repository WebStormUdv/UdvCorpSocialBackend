package ru.backend.UdvCorpSocialBackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    private final S3Client s3Client;

    @Value("${minio.bucket.posts}")
    private String postsBucketName;

    @Value("${minio.bucket.icons}")
    private String iconsBucketName;

    @Value("${minio.internal.url}")
    private String minioInternalUrl;

    @Value("${minio.external.url}")
    private String minioExternalUrl;

    @Value("${minio.bucket.skill-docs}")
    private String skillDocsBucketName;

    @Value("${minio.bucket.grat-achieves}")
    private String gratAchievsBucketName;

    public FileStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Проверка размера файла
        if (file.getSize() > 10 * 1024 * 1024) { // 10 MB
            throw new IllegalArgumentException("File size exceeds 10 MB");
        }

        // Проверка формата
        String contentType = file.getContentType();
        if (!isImage(contentType)) {
            throw new IllegalArgumentException("Only image files (JPEG, PNG) are allowed");
        }

        // Генерация уникального имени файла
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        // Загрузка в MinIO
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(postsBucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // Формирование публичного URL
        return String.format("%s/%s/%s", minioExternalUrl, postsBucketName, fileName);
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3Client.deleteObject(b -> b.bucket(postsBucketName).key(fileName));
    }

    public String storeIconFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Проверка размера файла
        if (file.getSize() > 10 * 1024 * 1024) { // 10 MB
            throw new IllegalArgumentException("File size exceeds 10 MB");
        }

        // Проверка формата
        String contentType = file.getContentType();
        if (!isImage(contentType)) {
            throw new IllegalArgumentException("Only image files (JPEG, PNG) are allowed");
        }

        // Генерация уникального имени файла
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        // Загрузка в MinIO
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(iconsBucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // Формирование публичного URL
        return String.format("%s/%s/%s", minioExternalUrl, iconsBucketName, fileName);
    }

    public void deleteIconFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3Client.deleteObject(b -> b.bucket(iconsBucketName).key(fileName));
    }

    public String storeSkillDocFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Проверка размера файла
        if (file.getSize() > 10 * 1024 * 1024) { // 10 MB
            throw new IllegalArgumentException("File size exceeds 10 MB");
        }

        // Проверка формата
        String contentType = file.getContentType();
        if (!isValidSkillDoc(contentType)) {
            throw new IllegalArgumentException("Only PDF, DOCX, PNG, or JPEG files are allowed");
        }

        // Генерация уникального имени файла
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        // Загрузка в MinIO
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(skillDocsBucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // Формирование публичного URL
        return String.format("%s/%s/%s", minioExternalUrl, skillDocsBucketName, fileName);
    }

    public void deleteSkillDocFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3Client.deleteObject(b -> b.bucket(skillDocsBucketName).key(fileName));
    }

    public String storeGratAchieveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Проверка размера файла
        if (file.getSize() > 10 * 1024 * 1024) { // 10 MB
            throw new IllegalArgumentException("File size exceeds 10 MB");
        }

        // Проверка формата
        String contentType = file.getContentType();
        if (!isImage(contentType)) {
            throw new IllegalArgumentException("Only image files (JPEG, PNG) are allowed");
        }

        // Генерация уникального имени файла
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        // Загрузка в MinIO
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(gratAchievsBucketName)
                .key(fileName)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // Формирование публичного URL
        return String.format("%s/%s/%s", minioExternalUrl, gratAchievsBucketName, fileName);
    }

    public void deleteGratAchieveFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3Client.deleteObject(b -> b.bucket(gratAchievsBucketName).key(fileName));
    }

    private boolean isImage(String contentType) {
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                        contentType.equals("image/png")
        );
    }

    private boolean isValidSkillDoc(String contentType) {
        return contentType != null && (
                contentType.equals("application/pdf") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/png")
        );
    }
}