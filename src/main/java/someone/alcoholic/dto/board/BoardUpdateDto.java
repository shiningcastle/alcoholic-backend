package someone.alcoholic.dto.board;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BoardUpdateDto extends BoardInputDto {

    @ApiModelProperty(name = "images", value = "삭제할 게시물 이미지 번호들")
    private List<Long> deleteImages;

}
