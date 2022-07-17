package someone.alcoholic.util;

import lombok.extern.slf4j.Slf4j;
import someone.alcoholic.enums.CookieExpiryTime;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
public class CookieUtil {
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        log.info("cookie 조회 시작, name={}", name);
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    log.info("cookie 조회 성공, name={}", name);
                    return Optional.of(cookie);
                }
            }
        }
        log.info("해당하는 이름의 쿠키가 없다");
        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, CookieExpiryTime expiryTime) {
        log.info("cookie 생성 시작, name={}, value={}, expiryTime={}", name, value, expiryTime);
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expiryTime.getValue());
        cookie.setPath("/");

        response.addCookie(cookie);
        log.info("cookie 생성 성공, name={}, value={}, expiryTime={}", name, value, expiryTime);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        log.info("cookie 삭제 시작, name={}", name);
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    log.info("cookie 삭제 성공, name={}", name);
                }
            }
        }
    }
}
