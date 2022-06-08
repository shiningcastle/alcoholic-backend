package someone.alcoholic.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
        ApiResult<?> error;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        if(authException instanceof InsufficientAuthenticationException) {
            error = ApiProvider.error(new CustomRuntimeException(ExceptionEnum.NOT_ALLOWED_ACCESS));
        }else if(authException instanceof BadProviderException) {
            error = ApiProvider.error(new CustomRuntimeException(ExceptionEnum.BAD_PROVIDER));
        } else if (authException instanceof BadCredentialsException) {
            error = ApiProvider.error(new CustomRuntimeException(ExceptionEnum.BAD_PASSWORD));
        } else {
            error = ApiProvider.error(new CustomRuntimeException(ExceptionEnum.USER_NOT_EXIST));
        }
        ObjectMapper om = new ObjectMapper();
        response.getWriter().write(om.writeValueAsString(error));
    }
}
