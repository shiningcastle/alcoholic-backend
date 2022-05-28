package someone.alcoholic.enums;

import lombok.Getter;

@Getter
public enum ExpiryTime {
    ACCESS_TOKEN_EXPIRY_TIME(3600000),
    REFRESH_TOKEN_EXPIRY_TIME(10800000),
    REFRESH_TOKEN_EXPIRY_HOUR(3),
    NICKNAME_TOKEN_EXPIRY_TIME(1200000),

    ACCESS_COOKIE_EXPIRY_TIME(3600),
    REFRESH_COOKIE_EXPIRY_TIME(10800),
    NICKNAME_COOKIE_EXPIRY_TIME(1200);

    private int time;

    ExpiryTime(int time) {
        this.time = time;
    }
}
