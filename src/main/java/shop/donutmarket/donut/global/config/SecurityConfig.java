package shop.donutmarket.donut.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shop.donutmarket.donut.global.jwt.JwtAuthorizationFilter;

@Slf4j
@Configuration
public class SecurityConfig {
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // JWT 커스텀 필터
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthorizationFilter(authenticationManager));
            super.configure(builder);
        }
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1. CSRF 해제
        http.csrf().disable(); // POSTMAN 접근을 위해

        // 2. 같은 도메인 iframe만 허용
        http.headers().frameOptions().sameOrigin();

        // 3. CORS 재설정
        http.cors().configurationSource(configurationSource());

        // 4. JSessionId 사용 거부
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 5. Form 로그인 해제
        http.formLogin().disable();

        // 6. httpBasic 정책 해제 (BasicAuthenticationFilter 해제)
        http.httpBasic().disable();

        // 7. XSS(lucy 필터 by naver)

        // 8. 커스텀 필터 적용(시큐리티 필터 교환)
        http.apply(new CustomSecurityFilterManager());

        // 9. 인증 실패 처리
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            // checkpoint -> 예외 핸들러 처리
            log.debug("디버그 : 인증 실패 : " + authException.getMessage());
            log.info("인포 : 인증 실패 : " + authException.getMessage());
            log.warn("워닝 : 인증 실패 : " + authException.getMessage());
            log.error("에러 : 인증 실패 : " + authException.getMessage());

            response.setContentType("text/plain; charset=utf-8");
            response.setStatus(403);
            response.getWriter().println("권한 실패");
        });

        // 10. 권한 실패 처리
        http.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
            // checkpoint -> 예외 핸들러 처리
            log.debug("디버그 : 권한 실패 : " + accessDeniedException.getMessage());
            log.info("인포 : 권한 실패 : " + accessDeniedException.getMessage());
            log.warn("워닝 : 권한 실패 : " + accessDeniedException.getMessage());
            log.error("에러 : 권한 실패 : " + accessDeniedException.getMessage());
        });

        // 11. 인증, 권한 필터 설정
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/admin/**", "/firebase", "/bootpay").hasRole("ROLE_ADMIN").anyRequest().permitAll());

        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE (Javascript 요청 허용)
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용 (프론트 앤드 IP만 허용 react)
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization"); // 옛날에는 디폴트 였다. 지금은 아닙니다.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
