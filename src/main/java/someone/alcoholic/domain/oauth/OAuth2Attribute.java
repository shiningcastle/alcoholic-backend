package someone.alcoholic.domain.oauth;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.exception.oauth.OAuthProviderNotFoundException;

import java.util.Map;

@ToString
@Getter
@NoArgsConstructor
@Component
public class OAuth2Attribute {
    private String attributeKey;
    private String nickname;
    private String email;
    @Value("${member.default.image}")
    private String image;
    private Provider provider;

    @Builder
    public OAuth2Attribute(String attributeKey, String nickname, String email,
                           String image, Provider provider) {
        this.attributeKey = attributeKey;
        this.nickname = nickname;
        this.email = email;
        this.image = image;
        this.provider = provider;
    }

    public OAuth2Attribute getAttribute(String provider, String attributeKey,
                                        Map<String, Object> attributes) {
        // Provider 문자열 대문자 변환
        Provider oAuthProvider = Provider.valueOf(provider.toUpperCase());
        OAuth2Attribute attribute = null;
        switch (oAuthProvider) {
            case GOOGLE:
                attribute = ofGoogle(oAuthProvider, attributeKey,
                        attributes);
                break;
            case KAKAO:
                attribute = ofKakao(oAuthProvider, attributeKey,
                        attributes);
                break;
            default:
                throw new OAuthProviderNotFoundException("지원되지 않는 OAuth Provider 사용");
        }
        return attribute;
    }

    private OAuth2Attribute ofGoogle(Provider oAuthProvider, String attributeKey,
                                     Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .attributeKey((String) attributes.get(attributeKey))
                .nickname((String) attributes.get(oAuthProvider.getNickNameKey()))
                .email((String) attributes.get(oAuthProvider.getEmailKey()))
                .image(image)
                .provider(oAuthProvider)
                .build();
    }

    private OAuth2Attribute ofKakao(Provider oAuthProvider, String attributeKey,
                                    Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return OAuth2Attribute.builder()
                .attributeKey(String.valueOf(attributes.get(attributeKey)))
                .nickname((String) profile.get(oAuthProvider.getNickNameKey()))
                .email((String) kakaoAccount.get(oAuthProvider.getEmailKey()))
                .image(image)
                .provider(oAuthProvider)
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .id(attributeKey)
                .password(null)
                .nickname(nickname)
                .email(email)
                .image(image)
                .provider(provider)
                .build();
    }

}
