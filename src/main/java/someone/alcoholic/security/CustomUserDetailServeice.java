package someone.alcoholic.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.exception.BadProviderException;
import someone.alcoholic.repository.member.MemberRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailServeice implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username)
                .orElseThrow(() -> new InternalAuthenticationServiceException("UserService return null member, which means id is wrong"));
        if(!member.getProvider().equals(Provider.LOCAL)) {
            throw new BadProviderException("provider is not local");
        }
        return new User(member.getId(), member.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getRoleName())));
    }
}
