package someone.alcoholic.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResult<T> {
    @ApiModelProperty(name = "success", value = "성공여부", example = "true")
    private boolean success;

    @ApiModelProperty(name = "data", value = "데이터")
    private T data;

    @ApiModelProperty(name = "message", value = "메세지", example = "null")
    private String message;

    public ApiResult(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
}
