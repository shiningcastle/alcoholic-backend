package someone.alcoholic.service.token;

import someone.alcoholic.domain.member.Member;
import someone.alcoholic.domain.token.RefreshToken;
import someone.alcoholic.security.AuthToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public interface TokenService {
    void save(UUID uuid,  RefreshToken refreshToken);
    RefreshToken findById(UUID uuid);
    void delete(UUID uuid);
    AuthToken getAuthToken(HttpServletRequest request, String stringToken);
    void reissue(HttpServletResponse response, UUID uuid, RefreshToken savedRefreshToken, AuthToken refreshToken);
    String getMemberIdByAccessToken(HttpServletRequest request);
}
