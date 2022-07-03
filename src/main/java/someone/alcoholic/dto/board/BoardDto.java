package someone.alcoholic.dto.board;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private Long seq;
    private String title;
    private String content;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String writer;
}
