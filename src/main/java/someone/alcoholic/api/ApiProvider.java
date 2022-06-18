package someone.alcoholic.api;

import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.enums.MessageEnum;

public class ApiProvider {

    public static ApiResult<?> success() {
        return new ApiResult<>(true, null, null);
    }

    public static<T> ApiResult<T> success(T data) {
        return new ApiResult<>(true, data, null);
    }

    public static<T> ApiResult<?> success(T data, MessageEnum message) {
        return new ApiResult<>(true, data, message.getMessage());
    }

    public static ApiResult<?> fail() {
        return new ApiResult<>(false, null, null);
    }

    public static ApiResult<?> fail(ExceptionEnum message) {
        return new ApiResult<>(false, null, message.getMessage());
    }
}
