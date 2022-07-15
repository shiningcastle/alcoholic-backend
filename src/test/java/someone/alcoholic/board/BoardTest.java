package someone.alcoholic.board;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.enums.Role;
import someone.alcoholic.repository.member.MemberRepository;
import someone.alcoholic.security.WithMockCustomUser;
import someone.alcoholic.service.board.BoardService;
import someone.alcoholic.service.member.MemberService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BoardTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @InjectMocks
    private BoardService boardService;


    @Test
    @WithMockCustomUser
    public void insertBoard() throws Exception {
//        Member member = Member.builder().id("test1234").password("000000").email("test@naver.com").image("default.jpg")
//                .nickname("테스트계정").provider(Provider.LOCAL).role(Role.USER).build();


    }
}
