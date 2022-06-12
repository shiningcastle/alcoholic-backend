package someone.alcoholic.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class MemberLoginDto {

    @NotBlank(message = "id - 빈칸 또는 공백을 허용하지 않습니다.")
    private String id;

    @NotBlank(message = "password - 빈칸 또는 공백을 허용하지 않습니다.")
    private String password;

    public MemberLoginDto(String id, String password) {
        this.id = id;
        this.password = password;
    }

}
