package someone.alcoholic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import someone.alcoholic.domain.Member;
import someone.alcoholic.dto.MemberLoginDto;
import someone.alcoholic.dto.MemberSignupDto;
import someone.alcoholic.repository.MemberRepository;

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
