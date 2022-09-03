package someone.alcoholic.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import someone.alcoholic.filter.CookieAttributeFilter;

import java.util.Arrays;

@Configuration
public class CookieConfig implements WebMvcConfigurer {
    @Bean
    public FilterRegistrationBean getFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(new CookieAttributeFilter());
        registrationBean.setOrder(Integer.MIN_VALUE);
        return registrationBean;
    }
}
