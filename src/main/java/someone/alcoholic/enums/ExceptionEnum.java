package someone.alcoholic.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ExceptionEnum {
    USER_INFO_NOT_FOUND("auth-001", "이 요청은 인증이 필요하다."),
    USER_NOT_EXIST("auth-002", "해당되는 id 없음"),
    BAD_PASSWORD("auth-003", "잘못된 비밀번호"),
    USER_ALREADY_EXIST("auth-004", "이미 존재하는 id이다."),
    BAD_PROVIDER("auth-005", "잘못된 provider이다."),
    NICKNAME_ALREADY_EXIST("auth-006", "이미 존재하는 nickname이다."),
    NOT_ALLOWED_ACCESS("auth-007", "접근 권한이 없다."),
    TOKEN_NOT_EXIST("auth-008", "토큰이 존재하지 않는다."),
    TOKEN_NOT_VALID("auth-009", "토큰이 유효하지 않는다."),
    UNSUPPORTED_OAUTH_PROVIDER("auth-010", "지원되지 않는 OAuth Provider 입니다."),
    TMP_USER_NOT_EXIST("auth-011", "임시 회원 id가 없음"),

    VALUE_NOT_FOUND("common-001", "값이 비어 있다."),
    CATEGORY_NOT_FOUND("common-002", "없는 카테고리이다."),
    PAGE_NOT_FOUND("common-003", "없는 페이지이다."),

    FILE_OVER_SIZE("file-001", "파일 업로드 용량 초과입니다.");

    private String code;
    private String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
