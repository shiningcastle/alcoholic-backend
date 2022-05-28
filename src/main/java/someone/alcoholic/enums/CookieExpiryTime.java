package someone.alcoholic.enums;

import lombok.Getter;

@Getter
public enum CookieExpiryTime {
    ACCESS_COOKIE_MAX_AGE(3600),
    REFRESH_COOKIE_MAX_AGE(10800),
    NICKNAME_COOKIE_EXPIRY_TIME(1200);

    private int value;

    CookieExpiryTime(int value) {
        this.value = value;
    }
}
