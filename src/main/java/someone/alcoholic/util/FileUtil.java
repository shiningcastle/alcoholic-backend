package someone.alcoholic.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;

import java.util.UUID;

@Slf4j
public class FileUtil {
    private static final String DOT = ".";
    private static final String SLASH = "/";

    public static String buildFilePath(MultipartFile multipartFile, String domainName, Long seq) {
        String originalFileName = multipartFile.getOriginalFilename();
        log.info("{} 파일 경로 생성 시작", originalFileName);
        UUID uuid = UUID.randomUUID();
        log.info("{} 파일 - UUID : {}", originalFileName, uuid);
        String saveFileName = domainName + SLASH + seq + SLASH + uuid + getFileExtension(multipartFile);
        log.info("{} 파일 경로 생성완료 - Path : {}", originalFileName, saveFileName);
        return saveFileName;
    }

    public static String buildDefaultFilePath(String domainName, String fileName) {
        log.info("기본 파일경로 생성 시작 - domainName : {}, fileName : {}", domainName, fileName);
        String saveFileName = domainName + SLASH + fileName;
        log.info("기본 파일경로 생성 완료 - result : {}", saveFileName);
        return saveFileName;
    }

    public static String getFileExtension(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        String extension = fileName.substring(multipartFile.getOriginalFilename().lastIndexOf(DOT));
        return extension;
    }

    public static void validateFile(MultipartFile multipartFile) {
        log.info("업로드 요청 파일 체크 시작");
        if (multipartFile.isEmpty()) {
            log.info("업로드 요청 파일 누락");
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.FILE_NOT_EXISTS);
        }
        String fileName = multipartFile.getOriginalFilename();
        String extension = getFileExtension(multipartFile);
        log.info("{} 파일 - 크기 : {}, 확장자 : {}", fileName, multipartFile.getSize(), extension);
        if (!extension.equals(".jpeg") && !extension.equals(".png")) {
            log.info("{} 파일 - 허용되지 않는 확장자 파일 요청으로 등록 실패", fileName);
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.FILE_EXTENSION_NOT_SUPPORTED);
        }
        log.info("{} 업로드 요청 파일 체크 완료", fileName);
    }
}
