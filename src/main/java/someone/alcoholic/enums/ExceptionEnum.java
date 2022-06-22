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

    EMAIL_SEND_FAIL("email-001", "인증 이메일 전송 실패."),
    EMAIL_NOT_CERTIFIED("email-002", "해당 이메일은 인증되지 않은 이메일입니다."),
    EMAIL_ALREADY_EXIST("email-003", "이미 인증이 완료된 이메일 입니다."),
    EMAIL_ALREADY_REQUEST("email-004", "만료기간 내 해당 이메일로 이미 인증 요청되었습니다."),
    EMAIL_CHECK_UNKNOWN("email-005", "인증 요청된 이메일 주소가 아닙니다."),
    EMAIL_CHECK_DISCORD("email-006", "이메일 인증번호가 일치하지 않습니다."),
    EMAIL_ID_NOT_EXIST("email-007", "해당 이메일과 아이디로 가입된 회원이 존재하지 않습니다."),
    EMAIL_EXIST("email-008", "해당 이메일로 가입된 회원이 존재합니다."),
    EMAIL_NOT_EXIST("email-009", "해당 이메일로 가입된 회원이 존재하지 않습니다."),

    REDIS_NOT_EXIST("redis-001", "해당 키의 값이 존재하지 않습니다."),

    VALUE_NOT_FOUND("common-001", "값이 비어 있다."),
    CATEGORY_NOT_FOUND("common-002", "없는 카테고리이다."),
    PAGE_NOT_FOUND("common-003", "없는 페이지이다."),

    FILE_OVER_SIZE("file-001", "파일 업로드 용량 초과입니다."),

    SERVER_ERROR("server-001", "서버 에러 발생");

    private final String code;
    private final String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
