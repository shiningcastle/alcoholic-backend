package someone.alcoholic.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;

@Slf4j
public class CommonUtils {
    private static final String SLASH = "/";
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    public static String buildFileName(MultipartFile multipartFile, String domainName, String categoryName, Long seq) {
        String originalFileName = multipartFile.getOriginalFilename();
        log.info("파일 이름 생성 시작 - originalFileName : {}", originalFileName);
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR));
        log.info("{} 파일 - fileExtension : {}", originalFileName, fileExtension);
        if (!(fileExtension.equals(".jpeg") || fileExtension.equals(".png"))) {
            log.info("{} 파일 지원하지 않는 확장자 등록 요청");
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.FILE_EXTENSION_NOT_SUPPORTED);
        }
        String saveFileName = SLASH + domainName + SLASH + categoryName + SLASH + seq + SLASH + seq + fileExtension;
        log.info("파일 이름 생성 완료 - originalFileName : {} -> result : {}", originalFileName, saveFileName);
        return saveFileName;
    }
}
