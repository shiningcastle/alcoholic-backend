package someone.alcoholic.dto.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class BoardDto {
    private String title;
    private String content;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String writer;
}
