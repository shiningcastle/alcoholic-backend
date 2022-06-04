package someone.alcoholic.dto.board;

import lombok.Getter;

@Getter
public class BoardInputDto {
    private String title;
    private String content;
    private String writer;
    private String category;
}
