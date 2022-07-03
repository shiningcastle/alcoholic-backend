package someone.alcoholic.dto.board;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardInputDto {
    private String title;
    private String content;
    private String category;

    public BoardInputDto(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}
