package someone.alcoholic.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class FileUtil {
    private static final String SLASH = "/";
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    public static String buildFileName(MultipartFile multipartFile, String domainName, String categoryName, Long seq) {
        String originalFileName = multipartFile.getOriginalFilename();
        log.info("파일 이름 생성 시작 - originalFileName : {}", originalFileName);
        String saveFileName = domainName + SLASH + categoryName + SLASH + seq + SLASH + seq + getFileExtension(multipartFile);
        log.info("파일 이름 생성 완료 - originalFileName : {} -> result : {}", originalFileName, saveFileName);
        return saveFileName;
    }

    public static String buildDefaultFilePath(String domainName, String categoryName, String fileName) {
        log.info("기본 파일경로 생성 시작 - domainName : {}, categoryName : {}, fileName : {}", domainName, categoryName, fileName);
        String saveFileName = domainName + SLASH + categoryName + SLASH + fileName;
        log.info("기본 파일경로 생성 완료 - result : {}", saveFileName);
        return saveFileName;
    }

    public static String getFileExtension(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        String extension = fileName.substring(multipartFile.getOriginalFilename().lastIndexOf(FILE_EXTENSION_SEPARATOR));
        return extension;
    }
}
