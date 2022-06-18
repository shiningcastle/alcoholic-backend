package someone.alcoholic.api;

import lombok.Getter;

@Getter
public class ApiResult<T> {
    private boolean success;
    private T data;
    private T message;

    public ApiResult(boolean success, T data, T message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
}
