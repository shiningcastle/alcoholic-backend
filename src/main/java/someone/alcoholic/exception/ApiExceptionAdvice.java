package someone.alcoholic.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.enums.ExceptionEnum;

import javax.mail.MessagingException;

@RestControllerAdvice
@Slf4j
public class ApiExceptionAdvice {

    @ExceptionHandler({CustomRuntimeException.class})
    public ResponseEntity<ApiResult<?>> customExceptionHandler(CustomRuntimeException e) {
        ExceptionEnum exception = e.getException();
        log.error("ERROR {} : {}" , exception.getCode(), exception.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>(new ApiResult<>(false, null, exception.getMessage()), e.getStatus());
    }

    @ExceptionHandler({MessagingException.class})
    public ResponseEntity<ApiResult<?>> messagingExceptionHandler(MessagingException e) {
        log.error("ERROR {} : {}", e.getClass(), e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>(new ApiResult<>(false, null, ExceptionEnum.EMAIL_SEND_FAIL.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResult<?>> exceptionHandler(Exception e) {
        log.error("ERROR {} : {}", e.getClass(), e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>(new ApiResult<>(false, null, ExceptionEnum.SERVER_ERROR.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
