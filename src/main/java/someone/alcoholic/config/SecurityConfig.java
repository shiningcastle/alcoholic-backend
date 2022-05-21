package someone.alcoholic.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import someone.alcoholic.filter.TokenAuthenticationFilter;
import someone.alcoholic.security.CustomAuthenticationEntryPoint;
import someone.alcoholic.security.CustomUserDetailServeice;
import someone.alcoholic.service.oauth.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final CustomUserDetailServeice customUserDetailServeice;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;

    // encoder를 빈으로 등록.
    @Bean
    public PasswordEncoder bCryptPasswordEncoder() { return new BCryptPasswordEncoder(); }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailServeice)
                .passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                .authorizeRequests().antMatchers("/**").permitAll()
                .and().csrf().disable()
                .oauth2Login()
                .successHandler(oAuth2SuccessfulHandler)
//                .defaultSuccessUrl("/")
                .userInfoEndpoint()
                .userService(customOAuth2UserService); // oauth2 로그인에 성공하면, 유저 데이터를 가지고 우리가 생성한 // customOAuth2UserService에서 처리를 하겠다. 그리고 "/login-success"로 이동하라.
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
