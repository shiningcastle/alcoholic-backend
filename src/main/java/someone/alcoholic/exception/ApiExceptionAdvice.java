package someone.alcoholic.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.enums.ExceptionEnum;

import javax.mail.MessagingException;

@RestControllerAdvice
@Slf4j
public class ApiExceptionAdvice {

    @ExceptionHandler({CustomRuntimeException.class})
    public ResponseEntity<ApiResult> customExceptionHandler(CustomRuntimeException e) {
        ExceptionEnum exception = e.getException();
        log.error("ERROR {} : {}" , exception.getCode(), exception.getMessage());
        e.printStackTrace();
        return ApiProvider.fail(e.getStatus(), exception);
//        return new ResponseEntity<>(new ApiResult<>(false, null, exception.getMessage()), e.getStatus());
    }

    @ExceptionHandler({MessagingException.class})
    public ResponseEntity<ApiResult> messagingExceptionHandler(MessagingException e) {
        log.error("ERROR {} : {}", e.getClass(), e.getMessage());
        e.printStackTrace();
        return ApiProvider.fail(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionEnum.EMAIL_SEND_FAIL);
//        return new ResponseEntity<>(new ApiResult<>(false, null, ExceptionEnum.EMAIL_SEND_FAIL.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<ApiResult> illegalArgumentExceptionHandler(IllegalStateException e) {
        log.error("ERROR {} : {}", e.getClass(), e.getMessage());
        e.printStackTrace();
        return ApiProvider.fail(HttpStatus.BAD_REQUEST, ExceptionEnum.ILLEGAL_ARGUMENT);
//        return new ResponseEntity<>(new ApiResult<>(false, null, ExceptionEnum.ILLEGAL_ARGUMENT.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResult> exceptionHandler(Exception e) {
        log.error("ERROR {} : {}", e.getClass(), e.getMessage());
        e.printStackTrace();
        return ApiProvider.fail(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionEnum.SERVER_ERROR);
//        return new ResponseEntity<>(new ApiResult<>(false, null, ExceptionEnum.SERVER_ERROR.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResult> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder sb = new StringBuilder();
        for (FieldError fieldError: bindingResult.getFieldErrors()) {
            sb.append(fieldError.getDefaultMessage());
            sb.append(System.lineSeparator());
        }
        return ApiProvider.fail(HttpStatus.BAD_REQUEST, sb.toString());
    }

}
