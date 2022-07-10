package someone.alcoholic.dto.board;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class BoardInputDto {
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;
    @NotBlank(message = "본문은 필수 입력 값입니다.")
    private String content;
    @NotBlank(message = "카테고리는 필수 입력 값입니다.")
    private String category;

    public BoardInputDto(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}
