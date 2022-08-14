package someone.alcoholic.service.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.domain.board_image.BoardImage;
import someone.alcoholic.domain.category.BoardCategory;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.board.BoardDto;
import someone.alcoholic.dto.board.BoardImageDto;
import someone.alcoholic.dto.board.BoardInputDto;
import someone.alcoholic.dto.board.BoardUpdateDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.board.BoardRepository;
import someone.alcoholic.repository.board_image.BoardImageRepository;
import someone.alcoholic.repository.heart.HeartRepository;
import someone.alcoholic.service.category.BoardCategoryService;
import someone.alcoholic.service.member.MemberService;
import someone.alcoholic.util.FileUtil;
import someone.alcoholic.util.S3Uploader;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    @Value("${image.board.directory}")
    private String imageDirectory;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final BoardCategoryService boardCategoryService;
    private final MemberService memberService;
    private final HeartRepository heartRepository;
    private final S3Uploader s3Uploader;

    public List<BoardDto> getBoards(Principal principal, long categorySeq, Pageable pageable) {
        log.info("{}번 카테고리 게시물 조회 시작", categorySeq);
        BoardCategory boardCategory = boardCategoryService.getBoardCategory(categorySeq);
        List<Board> boards = boardRepository.findAllByBoardCategory(boardCategory, pageable).getContent();
        List<BoardDto> boardDtoList = new ArrayList<>();
        Member member = (principal != null) ? memberService.findMemberById(principal.getName()) : null;
        for (Board board : boards) {
            boolean heartCheck = (member != null) ? heartRepository.existsByMemberAndBoard(member, board) : false;
            BoardDto boardDto = convertToBoardDto(board, heartCheck);
            boardDtoList.add(boardDto);
        }
        log.info("{}번 카테고리 게시물 조회 완료", categorySeq);
        return boardDtoList;
    }

    private BoardDto convertToBoardDto(Board board, boolean heartCheck) {
        return BoardDto.builder().seq(board.getSeq()).title(board.getTitle()).content(board.getContent()).createdDate(board.getCreatedDate())
                .updatedDate(board.getUpdatedDate()).writer(board.getMember().getNickname()).heartCount(board.getHearts().size())
                .heartCheck(heartCheck).images(BoardImageDto.boardImagesToDto(board.getBoardImages(), s3Uploader.s3PrefixUrl())).build();
    }

    public BoardDto getBoard(Principal principal, long boardSeq) {
        log.info("{}번 게시글 조회 시작", boardSeq);
        String loginId = memberService.getLoginUserId(principal);
        Member member = memberService.findMemberById(loginId);
        Board board = findBoardBySeq(boardSeq);
        boolean heartCheck = heartRepository.existsByMemberAndBoard(member, board);
        BoardDto boardDto = convertToBoardDto(board, heartCheck);
        log.info("{}번 게시글 조회 완료", boardSeq);
        return boardDto;
    }

    @Override
    public Board findBoardBySeq(long boardSeq) {
        log.info("{} 게시글 조회 시작", boardSeq);
        return boardRepository.findById(boardSeq)
                .orElseThrow(() -> new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.PAGE_NOT_FOUND));
    }

    @Transactional(rollbackFor = {Exception.class})
    public BoardDto addBoard(Principal principal, BoardInputDto boardInputDto, List<MultipartFile> fileList) {
        String loginId = memberService.getLoginUserId(principal);
        log.info("{} 회원 - 게시글 등록 시작", loginId);
        Board savedBoard = insertBoard(loginId, boardInputDto);
        checkAndUploadBoardFiles(loginId, savedBoard, fileList);
        log.info("{} 회원 {}번 게시글 등록 완료", loginId, savedBoard.getSeq());
        return convertToBoardDto(boardRepository.findById(savedBoard.getSeq()).get(), false);
    }

    private Board insertBoard(String loginId, BoardInputDto boardInputDto) {
        Member member = memberService.findMemberById(loginId);
        BoardCategory boardCategory = boardCategoryService.getBoardCategory(boardInputDto.getCategory());
        Long boardCategorySeq = boardCategory.getSeq();
        log.info("{} 회원 - {}번 카테고리 게시글 등록 시작", loginId, boardCategorySeq);
        Board board = Board.convertInputDtoToBoard(boardInputDto, member, boardCategory);
        Board savedBoard = boardRepository.save(board);
        log.info("{} 회원 - {}번 카테고리 {}번 Board 테이블 저장 성공", loginId, boardCategorySeq, savedBoard.getSeq());
        return savedBoard;
    }

    private void checkAndUploadBoardFiles(String loginId, Board board, List<MultipartFile> fileList) {
        Long boardSeq = board.getSeq();
        log.info("{} 회원 {}번 게시글 업로드 요청 파일 : {}", loginId, boardSeq, fileList);
        int listSize = 0;
        if (fileList != null) {
            listSize = fileList.size();
            log.info("{} 회원 - {}번 게시글 파일 {}개 저장요청", loginId, boardSeq, listSize);
            for (int i = 0; i < fileList.size(); i++) { // 모든 파일 누락 없는지 체크
                log.info("{} 회원 - {}번 게시글 저장요청 {}번째 파일 누락여부 체크", loginId, boardSeq, i);
                FileUtil.validateFile(fileList.get(i));
            }
            for (int i = 0; i < fileList.size(); i++) { // 모든 파일 저장
                MultipartFile file = fileList.get(i);
                String filePath = FileUtil.buildFilePath(file, imageDirectory, boardSeq); //저장 경로
                insertBoardImage(loginId, board, boardSeq, filePath);
                s3Uploader.uploadFile(file, filePath);
            }
        }
        log.info("{} 회원 {}번 게시글 업로드 파일 {}개 저장완료", loginId, boardSeq, listSize);
    }

    private void insertBoardImage(String loginId, Board board, Long boardSeq, String filePath) {
        log.info("{} 회원 - {}번 게시글 BoardImage 저장 시작", loginId, boardSeq);
        BoardImage boardImage = BoardImage.builder().board(board).filePath(filePath).registerDate(new Timestamp(System.currentTimeMillis())).build();
        BoardImage savedBoardImage = boardImageRepository.save(boardImage);
        log.info("{} 회원 - {}번 게시글 {}번 BoardImage 저장 완료", loginId, boardSeq, savedBoardImage.getSeq());
    }

    @Transactional(rollbackFor = {Exception.class})
    public BoardDto modifyBoard(Principal principal, long boardSeq, BoardUpdateDto boardUpdateDto, List<MultipartFile> fileList) {
        Board board = findBoardBySeq(boardSeq);
        String memberId = board.getMember().getId();
        log.info("{} 회원 {}번 게시글 수정 시작", memberId, boardSeq);
        memberService.checkAuthorizedUser(principal, memberId);
        //게시판 테이블 수정
        Board modifiedBoard = updateBoard(boardUpdateDto, board);
        // 삭제 원하는 이미지 있을 경우
        List<Long> deleteImageNumbers = boardUpdateDto.getDeleteImages();
        if (deleteImageNumbers != null && !deleteImageNumbers.isEmpty()) {
            log.info("{} 회원 {}번 게시글 삭제 요청 이미지 {}개", memberId, boardSeq, deleteImageNumbers.size());
            List<BoardImage> deleteBoardImages = boardImageRepository.findBySeqIn(deleteImageNumbers);
            log.info("{} 회원 {}번 게시글 조회한 삭제대상 이미지 {}개", memberId, boardSeq, deleteBoardImages.size());
            // S3 이미지 삭제
            for (BoardImage boardImage : deleteBoardImages) {
                Long seq = boardImage.getSeq();
                String filePath = boardImage.getFilePath();
                log.info("{} 회원 {}번 게시글 S3서버 파일 삭제시도 - 번호 : {}, 경로 : {}", memberId, boardSeq, seq, filePath);
                s3Uploader.deleteFile(filePath);
                modifiedBoard.getBoardImages().remove(boardImage);
                log.info("{} 회원 {}번 게시글 S3서버 파일 삭제완료 - 번호 : {}, 경로 : {}", memberId, boardSeq, seq, filePath);
            }
            boardImageRepository.deleteAllInBatch(deleteBoardImages);
            log.info("{} 회원 {}번 게시글 조회한 삭제대상 이미지 삭제완료", memberId, boardSeq);
        }
        // S3 업로드
        checkAndUploadBoardFiles(memberId, modifiedBoard, fileList);
        Member member = memberService.findMemberById(memberId);
        boolean heart = heartRepository.existsByMemberAndBoard(member, modifiedBoard);
        log.info("{} 회원 {}번 게시글 수정 완료", memberId, modifiedBoard.getSeq());
        return convertToBoardDto(boardRepository.findById(modifiedBoard.getSeq()).get(), heart);
    }

    private Board updateBoard(BoardUpdateDto boardUpdateDto, Board board) {
        BoardCategory boardCategory = boardCategoryService.getBoardCategory(boardUpdateDto.getCategory());
        board.setBoardCategory(boardCategory);
        board.setContent(boardUpdateDto.getContent());
        board.setTitle(boardUpdateDto.getTitle());
        board.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        Board modifiedBoard = boardRepository.save(board);
        return modifiedBoard;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteBoard(Principal principal, long boardSeq) {
        Board board = findBoardBySeq(boardSeq);
        String memberId = board.getMember().getId();
        memberService.checkAuthorizedUser(principal, memberId);
        log.info("{} 회원 {}번 게시글 삭제 시작", memberId, boardSeq);
        deleteAllBoardImages(board);
        board.setBoardImages(null);
        boardRepository.delete(board);
        log.info("{} 회원 {}번 게시글 삭제 완료", memberId, boardSeq);
    }

    // 게시글 모든 파일 삭제
    private void deleteAllBoardImages(Board board) {
        Long seq = board.getSeq();
        log.info("{}번 게시글 모든 파일 삭제 시작", seq);
        List<BoardImage> boardImages = board.getBoardImages();
        log.info("{}번 게시글 삭제 대상 파일 : {}개", seq, boardImages.size());
        boardImageRepository.deleteAllInBatch(boardImages);
        log.info("{}번 게시글 모든 파일 삭제 완료", seq);
    }
}
