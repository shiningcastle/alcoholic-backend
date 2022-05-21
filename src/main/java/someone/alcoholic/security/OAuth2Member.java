package someone.alcoholic.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Map;
import java.util.Set;

@Getter
public class OAuth2Member extends DefaultOAuth2User {
    private String provider;

    public OAuth2Member(Map<String, Object> attributes, Set<GrantedAuthority> authorities,
                        String nameAttributeKey, String provider) {
        super(authorities, attributes, nameAttributeKey);
        this.provider = provider;
    }

}
