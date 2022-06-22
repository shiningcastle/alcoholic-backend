package someone.alcoholic.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import someone.alcoholic.api.ApiProvider;
import someone.alcoholic.api.ApiResult;
import someone.alcoholic.enums.ExceptionEnum;
import someone.alcoholic.exception.BadProviderException;
import someone.alcoholic.exception.CustomRuntimeException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //response에 넣기
        ResponseEntity<ApiResult> error;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        if(authException instanceof InsufficientAuthenticationException) {
            error = ApiProvider.fail(HttpStatus.UNAUTHORIZED, ExceptionEnum.NOT_ALLOWED_ACCESS);
        }else if(authException instanceof BadProviderException) {
            error = ApiProvider.fail(HttpStatus.UNAUTHORIZED, ExceptionEnum.BAD_PROVIDER);
        } else if (authException instanceof BadCredentialsException) {
            error = ApiProvider.fail(HttpStatus.UNAUTHORIZED, ExceptionEnum.BAD_PASSWORD);
        } else {
            error = ApiProvider.fail(HttpStatus.UNAUTHORIZED, ExceptionEnum.USER_NOT_EXIST);
        }
        ObjectMapper om = new ObjectMapper();
        response.getWriter().write(om.writeValueAsString(error));
    }
}
