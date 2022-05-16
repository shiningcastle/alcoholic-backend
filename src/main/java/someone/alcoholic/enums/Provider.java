package someone.alcoholic.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    GOOGLE("sub", "name", "email"),
    KAKAO("id", "nickname", "email"),
    LOCAL("", "", "");

    private final String idKey;
    private final String nickNameKey;
    private final String emailKey;
}
