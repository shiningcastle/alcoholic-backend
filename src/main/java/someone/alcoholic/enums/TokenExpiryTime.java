package someone.alcoholic.enums;

import lombok.Getter;

@Getter
public enum TokenExpiryTime {
    ACCESS_TOKEN_MAX_AGE(3600000),
    REFRESH_TOKEN_MAX_AGE(10800000),
    NICKNAME_TOKEN_MAX_AGE(1200000),

    REDIS_REFRESH_TOKEN_MAX_AGE(3);

    private int value;
    TokenExpiryTime(int value) {
        this.value = value;
    }
}
