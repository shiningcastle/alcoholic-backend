package someone.alcoholic.dto.board;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardDto {

    @ApiModelProperty(name = "seq", value = "글번호", example = "132")
    private Long seq;

    @ApiModelProperty(name = "title", value = "글제목", example = "이태원 술집 TOP 5")
    private String title;

    @ApiModelProperty(name = "content", value = "글내용", example = "여기 동생이랑 가봤는데 리얼 찐 맛집!")
    private String content;

    @ApiModelProperty(name = "createdDate", value = "생성일", example = "2022.07.01T14:54:22")
    @JsonFormat(pattern = "yyyy.MM.dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp createdDate;

    @ApiModelProperty(name = "updatedDate", value = "수정일", example = "2022.07.02T14:54:22")
    @JsonFormat(pattern = "yyyy.MM.dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp updatedDate;

    @ApiModelProperty(name = "writer", value = "작성자(닉네임)", example = "1000 병의 소주를 마신 아저씨")
    private String writer;

    @ApiModelProperty(name = "heartCount", value = "좋아요 개수", example = "15")
    private int heartCount;

    @ApiModelProperty(name = "heartCheck", value = "유저 좋아요 여부", example = "true")
    private boolean heartCheck;

    @ApiModelProperty(name = "images", value = "게시물 이미지 리스트")
    private List<BoardImageDto> images;

    @Builder
    public BoardDto(Long seq, String title, String content, Timestamp createdDate, Timestamp updatedDate, String writer, int heartCount, boolean heartCheck, List<BoardImageDto> images) {
        this.seq = seq;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.writer = writer;
        this.heartCount = heartCount;
        this.heartCheck = heartCheck;
        this.images = images;
    }

}
