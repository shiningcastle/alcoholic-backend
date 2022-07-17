package someone.alcoholic.service.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.domain.category.BoardCategory;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.board.BoardDto;
import someone.alcoholic.dto.board.BoardInputDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.board.BoardRepository;
import someone.alcoholic.repository.heart.HeartRepository;
import someone.alcoholic.service.category.BoardCategoryService;
import someone.alcoholic.service.member.MemberService;
import someone.alcoholic.service.token.TokenService;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final BoardCategoryService boardCategoryService;
    private final MemberService memberService;
    private final TokenService tokenService;
    private final HeartRepository heartRepository;

    @Override
    public List<BoardDto> getBoards(HttpServletRequest request, String boardCategoryName, Pageable pageable) {
        log.info("{} 카테고리 게시물 조회 시작", boardCategoryName);
        BoardCategory boardCategory = boardCategoryService.getBoardCategory(boardCategoryName);
        List<Board> boards = boardRepository.findAllByBoardCategory(boardCategory, pageable).getContent();
        List<BoardDto> boardDtoList = new ArrayList<>();
        String memberId = tokenService.getMemberIdByAccessToken(request);
        Member member = memberService.findMemberById(memberId);
        for (Board board : boards) {
            boolean heartCheck = heartRepository.existsByMemberAndBoard(member, board);
            BoardDto boardDto = BoardDto.convertDTO(board, heartCheck);
            boardDtoList.add(boardDto);
        }
        log.info("{} 카테고리 게시물 조회 완료", boardCategoryName);
        return boardDtoList;
    }

    @Override
    public BoardDto getBoard(HttpServletRequest request, long boardSeq) {
        log.info("{} 게시글 조회 시작", boardSeq);
        String memberId = tokenService.getMemberIdByAccessToken(request);
        Member member = memberService.findMemberById(memberId);
        Board board = findBoardBySeq(boardSeq);
        boolean heartCheck = heartRepository.existsByMemberAndBoard(member, board);
        BoardDto boardDto = BoardDto.convertDTO(board, heartCheck);
        log.info("{} 게시글 조회 완료", boardSeq);
        return boardDto;
    }

    @Override
    public Board findBoardBySeq(long boardSeq) {
        log.info("{} 게시글 조회 시작", boardSeq);
        return boardRepository.findById(boardSeq)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.PAGE_NOT_FOUND));
    }

    @Override
    public BoardDto addBoard(String memberId, BoardInputDto boardInputDto) {
        log.info("{} 회원 게시글 등록 시작", memberId);
        Member member = memberService.findMemberById(memberId);
        BoardCategory boardCategory = boardCategoryService.getBoardCategory(boardInputDto.getCategory());
        Board board = Board.convertInputDtoToBoard(boardInputDto, member, boardCategory);
        Board savedBoard = boardRepository.save(board);
        log.info("{} 회원 {} 게시글 등록 완료", memberId, savedBoard.getSeq());
        return BoardDto.convertDTO(savedBoard, false);
    }

    @Override
    public BoardDto modifyBoard(String memberId, long boardSeq, BoardInputDto boardInputDto) {
        log.info("{} 회원 {} 게시글 수정 시작", memberId, boardSeq);
        Member member = memberService.findMemberById(memberId);
        Board board = findBoardBySeq(boardSeq);
        if (!member.equals(board.getMember())) {
            log.info("member {}의 board {} 수정 실패 - 게시글 작성자와 삭제 요청자 불일치", memberId, boardSeq);
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.USER_AND_WRITER_NOT_EQUAL);
        }
        BoardCategory boardCategory = boardCategoryService.getBoardCategory(boardInputDto.getCategory());
        board.setBoardCategory(boardCategory);
        board.setContent(boardInputDto.getContent());
        board.setTitle(boardInputDto.getTitle());
        board.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        Board modifiedBoard = boardRepository.save(board);
        log.info("{} 회원 {} 게시글 수정 완료", memberId, modifiedBoard.getSeq());
        return BoardDto.convertDTO(modifiedBoard, heartRepository.existsByMemberAndBoard(member, modifiedBoard));
    }

    @Override
    public void deleteBoard(long boardSeq, String memberId) {
        log.info("{} 회원 {} 게시글 삭제 시작", memberId, boardSeq);
        Member member = memberService.findMemberById(memberId);
        Board board = findBoardBySeq(boardSeq);
        if (!member.equals(board.getMember())) {
            log.info("member {}의 board {} 삭제 실패 - 게시글 작성자와 삭제 요청자 불일치", memberId, boardSeq);
            throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.USER_AND_WRITER_NOT_EQUAL);
        }
        boardRepository.delete(board);
        log.info("{} 회원 {} 게시글 삭제 완료", memberId, boardSeq);
    }
}
