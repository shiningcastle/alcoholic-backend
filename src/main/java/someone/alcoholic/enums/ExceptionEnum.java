package someone.alcoholic.enums;

import lombok.Getter;

@Getter
public enum ExceptionEnum {
    LOGIN_REQUIRED("auth-001", "로그인하지 않은 유저입니다."),
    USER_NOT_EXIST("auth-002", "해당되는 아이디가 없습니다."),
    BAD_PASSWORD("auth-003", "잘못된 비밀번호입니다."),
    USER_ALREADY_EXIST("auth-004", "이미 존재하는 아이디입니다."),
    BAD_PROVIDER("auth-005", "잘못된 provider입니다."),
    NICKNAME_ALREADY_EXIST("auth-006", "변경할 닉네임은 이미 존재하는 닉네임입니다."),
    NOT_ALLOWED_ACCESS("auth-007", "접근 권한이 없습니다."),
    TOKEN_NOT_EXIST("auth-008", "토큰이 존재하지 않습니다."),
    TOKEN_NOT_VALID("auth-009", "토큰이 유효하지 않습니다."),
    UNSUPPORTED_OAUTH_PROVIDER("auth-010", "지원되지 않는 OAuth Provider 입니다."),
    TMP_USER_NOT_EXIST("auth-011", "임시 회원 id가 없습니다."),
    USER_AND_WRITER_NOT_EQUAL("auth-012", "글 작성자와 유저가 다릅니다."),

    EMAIL_SEND_FAIL("email-001", "인증 이메일 전송 실패하였습니다."),
    EMAIL_NOT_CERTIFIED("email-002", "해당 이메일은 인증되지 않은 이메일입니다."),
    EMAIL_ALREADY_EXIST("email-003", "이미 인증이 완료된 이메일 입니다."),
    EMAIL_ALREADY_REQUEST("email-004", "만료기간 내 해당 이메일로 이미 인증 요청되었습니다."),
    EMAIL_CHECK_UNKNOWN("email-005", "인증 요청된 이메일 주소가 아닙니다."),
    EMAIL_CHECK_DISCORD("email-006", "이메일 인증번호가 일치하지 않습니다."),
    EMAIL_ID_NOT_EXIST("email-007", "해당 이메일과 아이디로 가입된 회원이 존재하지 않습니다."),
    EMAIL_EXIST("email-008", "해당 이메일로 가입된 회원이 존재합니다."),
    EMAIL_NOT_EXIST("email-009", "해당 이메일로 가입된 회원이 존재하지 않습니다."),
    EMAIL_CHECK_TIME("eamil-010", "해당 이메일로 전송된 인증 기간이 만료되었습니다."),


    HEART_ALREADY_EXISTS("board-001", "해당 게시글의 좋아요를 이미 누르셨습니다."),
    HEART_NOT_EXISTS("board-002", "해당 게시글의 좋아요를 등록하지 않았습니다."),

    VALUE_NOT_FOUND("common-001", "값이 비어 있습니다."),
    CATEGORY_NOT_FOUND("common-002", "없는 카테고리이다."),
    PAGE_NOT_FOUND("common-003", "없는 페이지입니다."),
    REPLY_NOT_FOUND("common-004", "없는 댓글입니다."),
    ILLEGAL_ARGUMENT("common-005", "잘못된 형태의 요청입니다."),

    USER_PASSWORD_INCORRECT("user-001", "잘못된 비밀번호 입니다."),
    USER_PASSWORD_SAME("user-002", "변경할 비밀번호가 기존 비밀번호와 같습니다. 새로운 비밀번호를 입력해주세요."),
    REDIS_NOT_EXIST("redis-001", "해당 키의 값이 존재하지 않습니다."),
    FILE_OVER_SIZE("file-001", "파일 업로드 용량 초과입니다."),
    FILE_NOT_EXISTS("file-002", "파일이 필요합니다."),
    FILE_EXTENSION_NOT_SUPPORTED("file-003", "이미지 파일은 jpeg, png 확장자만 허용됩니다."),
    FILE_SAVE_FAIL("file-004", "S3서버 파일등록 실패"),
    FILE_REMOVE_FAIL("file-005", "S3서버 파일삭제 실패"),
    FILE_REMOVE_NOT_ALLOWED("file-006", "기본 이미지는 삭제가 불가능합니다."),
    HTTP_METHOD_NOT_ALLOWED("http-001", "잘못된 Http Method 요청입니다."),
    SERVER_ERROR("server-001", "서버 에러가 발생하였습니다.");

    private final String code;
    private final String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
