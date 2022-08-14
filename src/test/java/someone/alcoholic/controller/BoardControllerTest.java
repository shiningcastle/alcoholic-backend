package someone.alcoholic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.controller.board.BoardController;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.domain.category.BoardCategory;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.board.BoardInputDto;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.enums.Role;
import someone.alcoholic.repository.board.BoardRepository;
import someone.alcoholic.repository.category.BoardCategoryRepository;
import someone.alcoholic.repository.member.MemberRepository;
import someone.alcoholic.security.WithMockCustomUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BoardCategoryRepository boardCategoryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardRepository boardRepository;

    @Test
    @WithMockCustomUser
    public void addBoard() throws Exception {
        setBoardCategory("category");
        setMember();

        BoardInputDto boardInputDto = new BoardInputDto("title", "content", 1L);
        ResultActions result = mockMvc.perform(post("/api/board")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardInputDto)));

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("addBoard"));
    }


    @Test
    @WithMockCustomUser
    public void modifyBoard() throws Exception {
        BoardInputDto boardInputDto = new BoardInputDto("new title", "new content", 1L);
        Board board = setBoard();

        ResultActions result = mockMvc.perform(put("/api/board/" + board.getSeq())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardInputDto)));

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("modifyBoard"));

    }

    @Test
    @WithMockCustomUser
    public void deleteBoard() throws Exception {
        Board board = setBoard();
        ResultActions result = mockMvc.perform(delete("/api/board/" + board.getSeq())
                .contentType(MediaType.APPLICATION_JSON));

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("deleteBoard"));
    }

    @Test
    @WithMockCustomUser
    public void getBoards() throws Exception {
        Board board = setBoard();
        ResultActions result = mockMvc.perform(get("/api/boards/" + "category")
                .contentType(MediaType.APPLICATION_JSON));

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("getBoards"));
    }

    @Test
    @WithMockCustomUser
    public void getBoard() throws Exception {
        Board board = setBoard();
        ResultActions result = mockMvc.perform(get("/api/board/" + board.getSeq())
                .contentType(MediaType.APPLICATION_JSON));

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(BoardController.class))
                .andExpect(handler().methodName("getBoard"));
    }

    private Member setMember() {
        return memberRepository.save(new Member("tester123", "tester123!", "nickname", "email",
                "", Provider.LOCAL, Role.USER));
    }

    private BoardCategory setBoardCategory(String category) {
        return boardCategoryRepository.save(new BoardCategory(category));
    }

    private Board setBoard() {
        Member member = setMember();
        BoardCategory boardCategory = setBoardCategory("category");

        Board board = new Board("title", "content");
        board.setMember(member);
        board.setBoardCategory(boardCategory);
        return boardRepository.save(board);
    }
}
