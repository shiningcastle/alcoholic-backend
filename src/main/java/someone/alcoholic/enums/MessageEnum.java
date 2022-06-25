package someone.alcoholic.enums;

import lombok.Getter;

@Getter
public enum MessageEnum {
    EMAIL_SEND_SUCCESS("이메일 전송 성공"),
    EMAIL_AUTH_SUCCESS("이메일 인증 성공"),
    EMAIL_CHECK_SUCCESS("인증된 이메일"),
    MEMBER_ID_SUCCESS("회원 아이디 찾기 성공"),
    MEMBER_PASSWORD_SUCCESS("회원 비밀번호 변경 성공");

    private final String message;

    MessageEnum(String message) {
        this.message = message;
    }
}
