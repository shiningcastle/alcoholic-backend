package someone.alcoholic.exception;

import com.nimbusds.oauth2.sdk.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
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
    }

    @ExceptionHandler({MessagingException.class})
    public ResponseEntity<ApiResult> messagingExceptionHandler(MessagingException e) {
        log.error("ERROR {} : {}", e.getClass(), e.getMessage());
        e.printStackTrace();
        return ApiProvider.fail(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionEnum.EMAIL_SEND_FAIL);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ApiResult> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("ERROR {} : {}", e.getClass(), e.getMessage());
        e.printStackTrace();
        return ApiProvider.fail(HttpStatus.BAD_REQUEST, ExceptionEnum.ILLEGAL_ARGUMENT);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ApiResult> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e) {
        log.error("ERROR {} : {}", e.getClass(), e.getMessage());
        e.printStackTrace();
        return ApiProvider.fail(HttpStatus.BAD_REQUEST, ExceptionEnum.FILE_OVER_SIZE);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ApiResult> AuthenticationExceptionHandler(AuthenticationException e) {
        log.error("ERROR {} : {}", e.getClass(), e.getMessage());
        e.printStackTrace();
        ResponseEntity<ApiResult> error;
        if(e instanceof InsufficientAuthenticationException) {
            error = ApiProvider.fail(HttpStatus.UNAUTHORIZED, ExceptionEnum.NOT_ALLOWED_ACCESS);
        }else if(e instanceof BadProviderException) {
            error = ApiProvider.fail(HttpStatus.UNAUTHORIZED, ExceptionEnum.BAD_PROVIDER);
        } else if (e instanceof BadCredentialsException) {
            error = ApiProvider.fail(HttpStatus.UNAUTHORIZED, ExceptionEnum.BAD_PASSWORD);
        } else {
            error = ApiProvider.fail(HttpStatus.UNAUTHORIZED, ExceptionEnum.USER_NOT_EXIST);
        }
        return error;
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResult> exceptionHandler(Exception e) {
        log.error("ERROR {} : {}", e.getClass(), e.getMessage());
        e.printStackTrace();
        return ApiProvider.fail(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionEnum.SERVER_ERROR);
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
