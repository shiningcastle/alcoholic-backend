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
import someone.alcoholic.controller.reply.ReplyController;
import someone.alcoholic.domain.board.Board;
import someone.alcoholic.domain.category.BoardCategory;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.domain.reply.Reply;
import someone.alcoholic.dto.reply.ReplyInputDto;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.enums.Role;
import someone.alcoholic.repository.board.BoardRepository;
import someone.alcoholic.repository.category.BoardCategoryRepository;
import someone.alcoholic.repository.member.MemberRepository;
import someone.alcoholic.repository.reply.ReplyRepository;
import someone.alcoholic.security.WithMockCustomUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ReplyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BoardCategoryRepository boardCategoryRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ReplyRepository replyRepository;

    @Test
    @WithMockCustomUser
    public void getReplies() throws Exception {
        Board board = setBoard();
        ResultActions result = mockMvc.perform(get("/api/board/" + board.getSeq() + "/replies")
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(handler().handlerType(ReplyController.class))
                .andExpect(handler().methodName("getReplies"));

    }

    @Test
    @WithMockCustomUser
    public void addReply() throws Exception {
        Board board = setBoard();

        ReplyInputDto replyInputDto = new ReplyInputDto(true, 0, "content");
        ResultActions result = mockMvc.perform(post("/api/board/" + board.getSeq() + "/reply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(replyInputDto)));

        result.andExpect(status().isOk())
                .andExpect(handler().handlerType(ReplyController.class))
                .andExpect(handler().methodName("addReply"));
    }

    @Test
    @WithMockCustomUser
    public void modifyReply() throws Exception {
        Reply reply = setBoardAndReply();

        ReplyInputDto replyInputDto = new ReplyInputDto(true, 0, "new content");
        ResultActions result = mockMvc.perform(put("/api/reply/" + reply.getSeq())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(replyInputDto)));

        result.andExpect(status().isOk())
                .andExpect(handler().handlerType(ReplyController.class))
                .andExpect(handler().methodName("modifyReply"));
    }

    @Test
    @WithMockCustomUser
    public void deleteReply() throws Exception {
        Reply reply = setBoardAndReply();

        ResultActions result = mockMvc.perform(delete("/api/reply/" + reply.getSeq())
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(handler().handlerType(ReplyController.class))
                .andExpect(handler().methodName("deleteReply"));
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

    private Reply setBoardAndReply() {
        Member member = setMember();
        BoardCategory boardCategory = setBoardCategory("category");

        Board board = new Board("title", "content");
        board.setMember(member);
        board.setBoardCategory(boardCategory);

        Reply reply = new Reply(true, 1, "good");
        reply.setBoard(board);
        reply.setMember(member);

        return replyRepository.save(reply);
    }
}
