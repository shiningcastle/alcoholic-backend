package someone.alcoholic.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.enums.MessageEnum;

public class ApiProvider {

    public static ResponseEntity<ApiResult> success() {
        return new ResponseEntity<>(new ApiResult<>(true, null, null), HttpStatus.OK);
    }

    public static<T> ResponseEntity<ApiResult<T>> success(T data) {
        return new ResponseEntity<>(new ApiResult<>(true, data, null), HttpStatus.OK);
    }

    public static<T> ResponseEntity<ApiResult> success(MessageEnum message) {
        return new ResponseEntity<>(new ApiResult<>(true, null, message.getMessage()), HttpStatus.OK);
    }

    public static<T> ResponseEntity<ApiResult<T>> success(T data, MessageEnum message) {
        return new ResponseEntity<>(new ApiResult<>(true, data, message.getMessage()), HttpStatus.OK);
    }

    public static ResponseEntity<ApiResult> fail(HttpStatus status, ExceptionEnum message) {
        return new ResponseEntity<> (new ApiResult<>(false, null, message.getMessage()), status);
    }

    public static ResponseEntity<ApiResult> fail(HttpStatus status, String errorMessage) {
        return new ResponseEntity<> (new ApiResult<>(false, null, errorMessage), status);
    }
}
