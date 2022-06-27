package someone.alcoholic.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MailType {
    SIGNUP("[SU]", "회원가입"),
    ID("[ID]","아이디 찾기"),
    PASSWORD("[PW]","비밀번호 찾기");

    private final String prefix;
    private final String type;
}
