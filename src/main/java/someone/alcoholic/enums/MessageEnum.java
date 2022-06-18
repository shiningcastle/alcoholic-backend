package someone.alcoholic.enums;

import lombok.Getter;

@Getter
public enum MessageEnum {
    EMAIL_SEND_SUCCESS("email 전송 성공");

    private final String message;

    MessageEnum(String message) {
        this.message = message;
    }
}
