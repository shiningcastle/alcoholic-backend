package someone.alcoholic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import someone.alcoholic.util.ApiProvider;
import someone.alcoholic.util.ApiResult;

@RestControllerAdvice
public class ApiExceptionAdvice {

    @ExceptionHandler({CustomRuntimeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<?> exceptionHandler(CustomRuntimeException ex) {
        return ApiProvider.error(ex);
    }
}
