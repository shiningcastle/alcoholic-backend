package someone.alcoholic.dto.board;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import someone.alcoholic.domain.board.Board;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
public class BoardDto {

    private Long seq;
    private String title;
    private String content;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private String writer;
    private int heartCount;
    private boolean heartCheck;

    @Builder
    public BoardDto(Long seq, String title, String content, Timestamp createdDate, Timestamp updatedDate, String writer, int heartCount, boolean heartCheck) {
        this.seq = seq;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.writer = writer;
        this.heartCount = heartCount;
        this.heartCheck = heartCheck;
    }

    public static BoardDto convertDTO(Board board, boolean heartCheck) {
        return BoardDto.builder().seq(board.getSeq()).title(board.getTitle()).content(board.getContent()).createdDate(board.getCreatedDate())
                .updatedDate(board.getUpdatedDate()).writer(board.getMember().getNickname()).heartCount(board.getHearts().size())
                .heartCheck(heartCheck).build();
    }
}
