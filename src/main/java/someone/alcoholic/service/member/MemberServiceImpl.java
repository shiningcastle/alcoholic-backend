package someone.alcoholic.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.auth.MemberLoginDto;
import someone.alcoholic.dto.member.MemberSignupDto;
import someone.alcoholic.repository.member.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Member signup(MemberSignupDto signupDto) {
        return null;
    }

    @Override
    public Member login(MemberLoginDto loginDto) {
        return null;
    }
}
