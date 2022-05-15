package someone.alcoholic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import someone.alcoholic.domain.Member;
import someone.alcoholic.dto.MemberLoginDto;
import someone.alcoholic.dto.MemberSignupDto;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.CustomRuntimeException;
import someone.alcoholic.repository.MemberRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    @Autowired
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Member signup(MemberSignupDto signupDto) {
        memberRepository.findByEmail(signupDto.getEmail())
                .ifPresent((mem) -> { throw new CustomRuntimeException(ExceptionEnum.USER_ALREADY_EXIST);});
        memberRepository.findByEmail(signupDto.getNickname())
                .ifPresent((mem) -> { throw new CustomRuntimeException(ExceptionEnum.NICKNAME_ALREADY_EXIST);});

        return memberRepository.save(Member.createLocalMember(
                signupDto.getEmail(), passwordEncoder.encode(signupDto.getPassword()), signupDto.getNickname()));
    }

}
