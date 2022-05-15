package someone.alcoholic.api;

import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;

public class ApiProvider {
    public static<T> ApiResult<T> success() {
        return new ApiResult<>(true, null, null);
    }

    public static<T> ApiResult<T> success(T message) {
        return new ApiResult<>(true, message, null);
    }

    public static<T> ApiResult<T> error(String errorCode, String errorMessage) {
        return new ApiResult<>(false, null,
                new ApiError(errorCode, errorMessage));
    }

    public static<T> ApiResult<T> error(CustomRuntimeException exception) {
        ExceptionEnum exceptionEnum = exception.getExceptionEnum();
        return error(exceptionEnum.getCode(), exceptionEnum.getMessage());
    }
}
