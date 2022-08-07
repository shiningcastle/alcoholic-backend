package someone.alcoholic.domain.board;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import someone.alcoholic.domain.board_image.BoardImage;
import someone.alcoholic.domain.heart.Heart;
import someone.alcoholic.domain.reply.Reply;
import someone.alcoholic.domain.category.BoardCategory;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.board.BoardInputDto;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy.MM.dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp createdDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy.MM.dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_board_member"))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_board_board_category"))
    private BoardCategory boardCategory;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BoardImage> boardImages = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Reply> replies = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Heart> hearts = new ArrayList<>();

    public Board(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static Board convertInputDtoToBoard(BoardInputDto boardInputDto, Member member, BoardCategory category) {
        Board board = new Board(boardInputDto.getTitle(), boardInputDto.getContent());
        board.setMember(member);
        board.setBoardCategory(category);
        return board;
    }
}
