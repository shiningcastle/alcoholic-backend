package someone.alcoholic.domain.heart;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.domain.member.Member;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Heart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "member_seq"))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "board_seq"))
    private Board board;

    @Builder
    public Heart(Member member, Board board) {
        this.member = member;
        this.board = board;
    }

    public void setMember(Member member) {
        this.member = member;
        member.getHearts().add(this);
    }

    public void setBoard(Board board) {
        this.board = board;
        board.getHearts().add(this);
    }
}
