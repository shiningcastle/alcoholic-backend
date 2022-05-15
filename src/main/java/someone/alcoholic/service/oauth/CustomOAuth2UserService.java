package someone.alcoholic.service.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.enums.Role;
import someone.alcoholic.repository.member.MemberRepository;
import someone.alcoholic.domain.oauth.OAuth2Attribute;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService {

    private final MemberRepository memberRepository;
    private final OAuth2Attribute oAuth2Attribute;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService loadUser");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        log.info("oAuth2User : {}", oAuth2User);
        // 서비스 구분용 id
        String provider = userRequest.getClientRegistration().getRegistrationId();
        log.info("provider : {}", provider);
        // OAuth2 로그인 진행 시 PK가 되는 필드값을 이야기한다.
        String attributeKey = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        // 소셜 로그인 된 유저 정보
        OAuth2Attribute attribute =
                oAuth2Attribute.getAttribute(provider, attributeKey, oAuth2User.getAttributes());
        log.info("{}", attribute);
        // DB에 아이디 없으면 강제 회원가입, 있으면 nickname 업데이트
        Member member = saveOrUpdate(attribute);
        log.info("{}", member);
        // 토큰 만들어서 쿠키에 넣기

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(Role.USER.getRoleName())),
                oAuth2User.getAttributes(), attributeKey);
    }

    private Member saveOrUpdate(OAuth2Attribute attribute) {
        Member savedMember = null;
        try {
            Member member = memberRepository.findById(attribute.getAttributeKey())
                    .map(entity -> entity.update(attribute.getNickname())) // 닉네임만 업데이트
                    .orElse(attribute.toEntity());
            savedMember = memberRepository.save(member);
        } catch (Exception e) {
            log.error("saveOrUpdate Error : {}", e.getMessage());
        }
        return savedMember;
    }
}
