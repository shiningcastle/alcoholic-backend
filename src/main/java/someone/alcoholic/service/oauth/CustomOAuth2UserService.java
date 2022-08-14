package someone.alcoholic.service.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import someone.alcoholic.domain.oauth.OAuth2Attribute;
import someone.alcoholic.enums.Role;
import someone.alcoholic.repository.member.MemberRepository;
import someone.alcoholic.security.OAuth2Member;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

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
        return new OAuth2Member(oAuth2User.getAttributes(),
                Collections.singleton(new SimpleGrantedAuthority(Role.USER.getRoleName())),
                attributeKey, provider);
    }
}
