package someone.alcoholic.domain.category;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.dto.board.BoardCategoryDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "boardCategory", cascade = CascadeType.ALL)
    private List<Board> boards = new ArrayList<>();

    public BoardCategory(String name) {
        this.name = name;
    }

    public BoardCategory(Long seq, String name) {
        this.seq = seq;
        this.name = name;
    }

    public BoardCategoryDto convertBoardDto() {
        return new BoardCategoryDto(this.seq, this.name);
    }

    public static BoardCategory convertDtoToBoardCategory(BoardCategoryDto boardCategoryDto) {
        return new BoardCategory(boardCategoryDto.getSeq(), boardCategoryDto.getName());
    }

}
