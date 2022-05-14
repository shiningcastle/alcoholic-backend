package someone.alcoholic.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnum {
    USER_INFO_NOT_FOUND("auth-001", "이 요청은 인증이 필요하다."),
    USER_NOT_EXIST("auth-002", "해당되는 id 없음"),
    BAD_PASSWORD("auth-003", "잘못된 비밀번호"),
    USER_ALREADY_EXIST("auth-004", "이미 존재하는 id이다."),
    BAD_PROVIDER("auth-005", "잘못된 provider이다."),
    NICKNAME_ALREADY_EXIST("auth-006", "이미 존재하는 nickname이다."),
    NOT_ALLOWED_ACCESS("auth-007", "접근 권한이 없다.");

    private String code;
    private String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
