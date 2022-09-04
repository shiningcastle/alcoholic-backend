package someone.alcoholic.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import someone.alcoholic.filter.TokenAuthenticationFilter;
import someone.alcoholic.security.CustomAccessDeniedHandler;
import someone.alcoholic.security.CustomAuthenticationEntryPoint;
import someone.alcoholic.security.CustomUserDetailServeice;
import someone.alcoholic.security.OAuth2SuccessfulHandler;
import someone.alcoholic.service.oauth.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final CustomUserDetailServeice customUserDetailServeice;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessfulHandler oAuth2SuccessfulHandler;

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
        http.cors().configurationSource(corsConfiguration())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
                .and()
                .authorizeRequests().antMatchers("/**").permitAll()
                .and().csrf().disable()
                .oauth2Login()
                .successHandler(oAuth2SuccessfulHandler)
                .defaultSuccessUrl("https://alcoholic.ml/")
                .userInfoEndpoint()
                .userService(customOAuth2UserService); // oauth2 로그인에 성공하면, 유저 데이터를 가지고 우리가 생성한 // customOAuth2UserService에서 처리를 하겠다. 그리고 "/login-success"로 이동하라.
        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CorsConfigurationSource corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
