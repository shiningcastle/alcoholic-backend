package someone.alcoholic.enums;

import lombok.Getter;

@Getter
public enum CookieExpiryTime {
    ACCESS_COOKIE_MAX_AGE(4200),        // 시간 만료보다 10분 더 김 
    REFRESH_COOKIE_MAX_AGE(10800),      // 토큰 만료와 시간 일치
    NICKNAME_COOKIE_EXPIRY_TIME(1200);

    private int value;

    CookieExpiryTime(int value) {
        this.value = value;
    }
}
