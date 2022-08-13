package someone.alcoholic.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    @Value("${cloud.aws.s3.suffix-url}")
    private String s3suffixUrl;

    @Value("${cloud.aws.s3.default-image-path}")
    private String defaultImagePath;

    public String s3PrefixUrl() {
        return String.format("https://%s.%s/", bucket, s3suffixUrl);
    }

    public String uploadFile(MultipartFile multipartFile, Long seq, String savedFilePath, String imageDirectory, String profileDirectory) {
        String fileName = multipartFile.getOriginalFilename();
        log.info("{} 파일 - S3서버 등록 시작", fileName);
        validateFile(multipartFile);
        // 기존 파일 삭제
        deleteFile(savedFilePath);
        // 새 파일 저장
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        String filePath = FileUtil.buildFileName(multipartFile, imageDirectory, profileDirectory, seq);
        log.info("{} 파일 - S3서버 저장 예정 경로 : {}", fileName, filePath);
        try (InputStream inputStream = multipartFile.getInputStream()) {
            log.info("{} 파일 - S3서버 {} 경로 업로드 시도", fileName, filePath);
            amazonS3Client.putObject(new PutObjectRequest(bucket, filePath, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            log.error("{} 파일 - S3서버 {} 경로 등록 실패 : {}", fileName, filePath, e.getMessage());
            e.printStackTrace();
            throw new CustomRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionEnum.FILE_SAVE_FAIL);
        }
        String savedUrl = amazonS3Client.getUrl(bucket, filePath).toString();
        log.info("{} 파일 - S3서버 {} 경로 등록 완료 - URL : {}", fileName, filePath, savedUrl);
        return filePath;
    }

    // 파일 삭제
    public boolean deleteFile(String savedFilePath){
        log.info("{} 경로 파일 S3서버 삭제 시작", savedFilePath);
        // 기본 이미지면 삭제안함
        if (savedFilePath.equals(defaultImagePath)) {
            log.info("파일 저장경로 {} -> 기본 이미지 파일로 미삭제", savedFilePath);
            return false;
        }
        try {
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, savedFilePath));
        } catch (Exception e) {
            log.error("{} 경로 파일 - S3서버 삭제 오류 : {}", savedFilePath, e.getMessage());
            e.printStackTrace();
            throw new CustomRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionEnum.FILE_REMOVE_FAIL);
        }
        log.info("{} 경로 파일 S3서버 삭제 완료", savedFilePath);
        return true;
    }

    private void validateFile(MultipartFile multipartFile) {
        log.info("업로드 요청 파일 체크 시작");
        if (multipartFile.isEmpty()) {
            log.info("업로드 요청 파일 누락");
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.FILE_NOT_EXISTS);
        }
        String fileName = multipartFile.getOriginalFilename();
        String extension = FileUtil.getFileExtension(multipartFile);
        long size = multipartFile.getSize();
        log.info("{} 파일 - 크기 : {}, 확장자 : {}", fileName, size, extension);
        if (!extension.equals(".jpeg") && !extension.equals(".png")) {
            log.info("{} 파일 - 허용되지 않는 확장자 파일 요청으로 등록 실패", fileName);
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.FILE_EXTENSION_NOT_SUPPORTED);
        }
        log.info("{} 업로드 요청 파일 체크 완료", fileName);
    }

}
