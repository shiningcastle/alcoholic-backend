package someone.alcoholic.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import someone.alcoholic.enums.ExceptionEnum;

@Getter
public class CustomRuntimeException extends RuntimeException {
    private final ExceptionEnum exception;
    private final HttpStatus status;

    public CustomRuntimeException (ExceptionEnum exception) {
        this.status = HttpStatus.BAD_REQUEST;
        this.exception = exception;
    }

    public CustomRuntimeException (HttpStatus status, ExceptionEnum exception) {
        this.status = status;
        this.exception = exception;
    }
}
