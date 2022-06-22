package someone.alcoholic.api;

import lombok.Getter;

@Getter
public class ApiResult<T> {
    private boolean success;
    private T data;
    private String message;

    public ApiResult(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
}
