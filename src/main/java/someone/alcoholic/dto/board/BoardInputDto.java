package someone.alcoholic.dto.board;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class BoardInputDto {

    @ApiModelProperty(name = "title", value = "글제목", example = "이태원 술집 TOP 5")
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;
    @ApiModelProperty(name = "content", value = "글내용", example = "여기 동생이랑 가봤는데 리얼 찐 맛집!")
    private String content;
    @ApiModelProperty(name = "category", value = "카테고리 명", example = "주류 할인 정보")
    private String category;

    public BoardInputDto(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}
