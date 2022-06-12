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
import someone.alcoholic.controller.auth.AuthController;
import someone.alcoholic.domain.member.TmpMember;
import someone.alcoholic.dto.auth.MemberLoginDto;
import someone.alcoholic.dto.member.MemberSignupDto;
import someone.alcoholic.dto.member.OAuthSignupDto;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.repository.member.TmpMemberRepository;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.security.AuthTokenProvider;
import someone.alcoholic.security.WithMockCustomUser;
import someone.alcoholic.service.member.MemberService;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private AuthTokenProvider authTokenProvider;
    @Autowired
    private TmpMemberRepository tmpMemberRepository;


    @Test
    public void signup() throws Exception {
        MemberSignupDto signUpDto = new MemberSignupDto("tester", "pw", "nickname", "email");
        String s = objectMapper.writeValueAsString(signUpDto);
        ResultActions result = mockMvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(s));

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AuthController.class))
                .andExpect(handler().methodName("signup"));
    }

    @Test
    @WithMockCustomUser
    public void login() throws Exception {
        signup();

        MemberLoginDto memberLoginDto = new MemberLoginDto("tester", "pw");
        String s = objectMapper.writeValueAsString(memberLoginDto);
        ResultActions result = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(s));

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AuthController.class))
                .andExpect(handler().methodName("login"));
    }

    @Test
    @WithMockCustomUser
    public void logout() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/logout")
                .contentType(MediaType.APPLICATION_JSON));

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AuthController.class))
                .andExpect(handler().methodName("logout"));
    }


    @Test
    public void oAuthSignup() throws Exception {
        String memberId = "tester";

        AuthToken nicknameToken = authTokenProvider.createNicknameToken(memberId);
        TmpMember tmpMember = new TmpMember(memberId, "email", Provider.GOOGLE);
        tmpMemberRepository.save(tmpMember);

        OAuthSignupDto oAuthSignupDto = new OAuthSignupDto("tester");
        String s = objectMapper.writeValueAsString(oAuthSignupDto);

        ResultActions result = mockMvc.perform(post("/api/oauth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(s)
                .cookie(new Cookie(AuthToken.NICKNAME_TOKEN, nicknameToken.getToken()))
        );

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AuthController.class))
                .andExpect(handler().methodName("oAuthSignup"));
    }
}
