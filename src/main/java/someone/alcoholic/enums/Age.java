package someone.alcoholic.enums;

import lombok.Getter;

@Getter
public enum Age {
    ACCESS_TOKEN_MAX_AGE(3600000),
    REFRESH_TOKEN_MAX_AGE(10800000),
    NICKNAME_TOKEN_MAX_AGE(1200000),

    ACCESS_COOKIE_MAX_AGE(3600),
    REFRESH_COOKIE_MAX_AGE(10800),
    NICKNAME_COOKIE_MAX_AGE(1200);

    private int time;

    Age(int time) {
        this.time = time;
    }
}
