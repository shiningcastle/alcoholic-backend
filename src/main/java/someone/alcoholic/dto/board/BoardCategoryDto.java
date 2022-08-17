package someone.alcoholic.dto.board;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
public class BoardCategoryDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(nullable = false)
    private String name;

    public BoardCategoryDto(Long seq, String name) {
        this.seq = seq;
        this.name = name;
    }
}
