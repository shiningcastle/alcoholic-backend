package someone.alcoholic.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.dto.member.MemberSignupDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.member.MemberRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member signup(MemberSignupDto signupDto) {
        memberRepository.findById(signupDto.getEmail())
                .ifPresent((mem) -> { throw new CustomRuntimeException(ExceptionEnum.USER_ALREADY_EXIST);});
        memberRepository.findByNickname(signupDto.getNickname())
                .ifPresent((mem) -> { throw new CustomRuntimeException(ExceptionEnum.NICKNAME_ALREADY_EXIST);});

        return memberRepository.save(Member.createLocalMember(
                signupDto.getId(), passwordEncoder.encode(signupDto.getPassword()),
                signupDto.getNickname(), signupDto.getEmail()));
    }
}
