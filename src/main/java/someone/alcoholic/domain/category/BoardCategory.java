package someone.alcoholic.domain.category;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import someone.alcoholic.domain.board.Board;

import javax.persistence.*;
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
    private List<Board> boards;

    public BoardCategory(String name) {
        this.name = name;
    }
}
