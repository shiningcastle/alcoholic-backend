package someone.alcoholic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.controller.auth.AuthController;
import someone.alcoholic.controller.mail.MailController;
import someone.alcoholic.domain.member.TmpMember;
import someone.alcoholic.dto.auth.MemberLoginDto;
import someone.alcoholic.dto.member.MemberSignupDto;
import someone.alcoholic.dto.member.NicknameDto;
import someone.alcoholic.enums.MailType;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.repository.NicknameRepository;
import someone.alcoholic.repository.member.TmpMemberRepository;
import someone.alcoholic.repository.redis.RedisRepository;
import someone.alcoholic.security.AuthToken;
import someone.alcoholic.security.AuthTokenProvider;
import someone.alcoholic.security.WithMockCustomUser;
import someone.alcoholic.service.member.MemberService;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private NicknameRepository nicknameRepository;


    @Test
    public void emailCheck() throws Exception {
        String email = "email@naver.com";
        ResultActions result0 = mockMvc.perform(get("/api/mail/send/SIGNUP")
                .param("email", email)
                .contentType(MediaType.APPLICATION_JSON)
        );
        int number = (Integer) redisRepository.get(MailType.SIGNUP.getPrefix() + email);
        ResultActions result1 = mockMvc.perform(get("/api/mail/auth/SIGNUP")
                .param("email", email)
                .param("number", String.valueOf(number))
                .contentType(MediaType.APPLICATION_JSON)
        );

        result0.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MailController.class))
                .andExpect(handler().methodName("sendAuthEmail"));
        result1.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(MailController.class))
                .andExpect(handler().methodName("checkAuthEmail"));
    }

    @Test
    @DisplayName("mail ?????? ??? ???????????? (??????)")
    public void signup() throws Exception {
        emailCheck();
        MemberSignupDto signUpDto = new MemberSignupDto("tester123", "tester123!", "email@naver.com");
        String s = objectMapper.writeValueAsString(signUpDto);
        ResultActions result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(s));

        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AuthController.class))
                .andExpect(handler().methodName("signup"));
    }

    @Test
    @DisplayName("mail ?????? ?????? ????????? ???????????? (??????)")
    public void signupWithUnAuthorized() throws Exception {
        MemberSignupDto signUpDto = new MemberSignupDto("tester123", "tester123!", "email@naver.com");
        String s = objectMapper.writeValueAsString(signUpDto);
        ResultActions result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(s));

        result.andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(handler().handlerType(AuthController.class))
                .andExpect(handler().methodName("signup"));
    }

    @Test
    @WithMockCustomUser
    public void login() throws Exception {
        signup();

        MemberLoginDto memberLoginDto = new MemberLoginDto("tester123", "tester123!");
        String s = objectMapper.writeValueAsString(memberLoginDto);
        ResultActions result = mockMvc.perform(post("/api/auth/login")
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
        ResultActions result = mockMvc.perform(post("/api/auth/logout")
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

        NicknameDto nicknameDto = new NicknameDto("tester");
        String s = objectMapper.writeValueAsString(nicknameDto);

        ResultActions result = mockMvc.perform(post("/api/auth/oauth/signup")
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
