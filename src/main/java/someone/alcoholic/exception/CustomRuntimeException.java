package someone.alcoholic.exception;

import lombok.Getter;
import someone.alcoholic.enums.ExceptionEnum;

@Getter
public class CustomRuntimeException extends RuntimeException {
    private ExceptionEnum exceptionEnum;

    public CustomRuntimeException(ExceptionEnum exceptionEnum) {
        this.exceptionEnum = exceptionEnum;
    }
}
