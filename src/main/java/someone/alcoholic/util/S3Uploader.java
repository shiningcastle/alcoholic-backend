package someone.alcoholic.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
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

//    @Value("${cloud.aws.s3.prefix-url}")
//    private String s3PrefixUrl;

    public String uploadFile(MultipartFile multipartFile, String fileName) {
        validateFileExists(multipartFile);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        log.info("{} 파일 S3 서버 등록 시작", fileName);
        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            log.error("{} 파일 S3 서버 등록 실패 : {}", fileName, e.getMessage());
            throw new CustomRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionEnum.FILE_SAVE_FAIL);
        }
        String savedUrl = amazonS3Client.getUrl(bucket, fileName).toString();
        log.info("{} 파일 S3 서버 등록 완료 - URL : {}", fileName, savedUrl);
        return savedUrl;
    }

    private void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            log.info("업로드 요청 파일이 존재하지 않습니다.");
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.FILE_NOT_EXISTS);
        }
    }

}
