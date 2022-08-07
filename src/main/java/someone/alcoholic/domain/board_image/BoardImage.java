package someone.alcoholic.domain.board_image;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.domain.category.BoardCategory;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(nullable = false, unique = true)
    private String fileName;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy.MM.dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @Column(nullable = false)
    private Timestamp registerDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_board_image_board_category"))
    private BoardCategory boardCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_board_image_board"))
    private Board board;

    @Builder
    public BoardImage(String fileName, BoardCategory boardCategory, Board board) {
        this.fileName = fileName;
        this.boardCategory = boardCategory;
        this.board = board;
    }
}
