package someone.alcoholic.dto.board;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import someone.alcoholic.domain.board_image.BoardImage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@NoArgsConstructor
public class BoardImageDto {

    private Long seq;
    private String url;

    @Builder
    public BoardImageDto(Long seq, String url) {
        this.seq = seq;
        this.url = url;
    }

    public static List<BoardImageDto> boardImagesToDto(List<BoardImage> boardImages, String s3PrefixUrl) {
        if (boardImages == null) {
            log.info("게시물 이미지 미존재");
            return null;
        }
        return boardImages.stream().map(i -> BoardImageDto.builder().seq(i.getSeq()).url(s3PrefixUrl + i.getFilePath()).build()).collect(Collectors.toList());
    }
}
