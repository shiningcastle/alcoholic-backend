package someone.alcoholic.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import someone.alcoholic.service.CustomOAuth2UserService;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 커스텀한 OAuth2UserService DI.
    private final CustomOAuth2UserService customOAuth2UserService;

    // encoder를 빈으로 등록.
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() { return new BCryptPasswordEncoder(); }

    // WebSecurity에 필터를 거는 게 훨씬 빠름. HttpSecrity에 필터를 걸면, 이미 스프링 시큐리티 내부에 들어온 상태기 때문에
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/members/**","/image/**"); // /image/** 있는 모든 파일들은 시큐리티 적용을 무시한다.
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()); // 정적인 리소스들에 대해서 시큐리티 적용 무시.
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest() // 모든 요청에 대해서 허용하라.
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/") // 로그아웃에 대해서 성공하면 "/"로 이동
                .and()
                .oauth2Login()
                //.defaultSuccessUrl("/login-success")
                .userInfoEndpoint()
                .userService(customOAuth2UserService); // oauth2 로그인에 성공하면, 유저 데이터를 가지고 우리가 생성한 // customOAuth2UserService에서 처리를 하겠다. 그리고 "/login-success"로 이동하라.
    }

}
