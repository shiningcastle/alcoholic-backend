package someone.alcoholic.enums;

import lombok.Getter;

@Getter
public enum MessageEnum {
    EMAIL_SEND_SUCCESS("email 전송 성공"),
    EMAIL_AUTH_SUCCESS("email 인증 성공"),
    MEMBER_ID_SUCCESS("회원 아이디 찾기 성공");

    private final String message;

    MessageEnum(String message) {
        this.message = message;
    }
}
