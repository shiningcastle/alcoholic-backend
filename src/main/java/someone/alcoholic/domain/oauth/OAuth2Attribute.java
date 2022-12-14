package someone.alcoholic.domain.oauth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import someone.alcoholic.domain.member.Member;
import someone.alcoholic.domain.member.TmpMember;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.enums.Provider;
import someone.alcoholic.exception.CustomRuntimeException;

import java.util.Map;

@ToString
@Getter
@NoArgsConstructor
@Component
public class OAuth2Attribute {
    private String attributeKey;
    private String nickname;
    private String email;
    private Provider provider;

    @Builder
    public OAuth2Attribute(String attributeKey, String nickname, String email,
                           Provider provider) {
        this.attributeKey = attributeKey;
        this.nickname = nickname;
        this.email = email;
        this.provider = provider;
    }


    public OAuth2Attribute getAttribute(String provider, Map<String, Object> attributes) {
        // Provider 문자열 대문자 변환
        Provider oAuthProvider = Provider.valueOf(provider.toUpperCase());
        OAuth2Attribute attribute = null;
        switch (oAuthProvider) {
            case GOOGLE:
                attribute = ofGoogle(oAuthProvider, attributes);
                break;
            case KAKAO:
                attribute = ofKakao(oAuthProvider, attributes);
                break;
            default:
                throw new CustomRuntimeException(HttpStatus.BAD_REQUEST, ExceptionEnum.UNSUPPORTED_OAUTH_PROVIDER);
        }
        return attribute;
    }

    private OAuth2Attribute ofGoogle(Provider oAuthProvider, Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .attributeKey((String) attributes.get(oAuthProvider.getIdKey()))
                .nickname((String) attributes.get(oAuthProvider.getNickNameKey()))
                .email((String) attributes.get(oAuthProvider.getEmailKey()))
                .provider(oAuthProvider)
                .build();
    }

    private OAuth2Attribute ofKakao(Provider oAuthProvider, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return OAuth2Attribute.builder()
                .attributeKey(String.valueOf(attributes.get(oAuthProvider.getIdKey())))
                .nickname((String) profile.get(oAuthProvider.getNickNameKey()))
                .email((String) kakaoAccount.get(oAuthProvider.getEmailKey()))
                .provider(oAuthProvider)
                .build();
    }

    public TmpMember toTmpMember() {
        return new TmpMember(attributeKey, email, provider);
    }

}
