package kakaotechbootcamp.communityservice.config;

import jakarta.servlet.Filter;
import kakaotechbootcamp.communityservice.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * NOTE: JwtAuthFilter 를 Spring Boot Application 의 필터체인에 등록하는 config 파일
 * 모든 HTTP 요청이 컨트롤러에 도달하기 전, 필터를 거친다
 */
@Configuration
@RequiredArgsConstructor
public class WebFilterConfig {
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public FilterRegistrationBean<Filter> jwtFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(jwtAuthFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }
}

