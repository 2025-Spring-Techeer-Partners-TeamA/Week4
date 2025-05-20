package daiseek.sbb;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration // 환경설정 파일
@EnableWebSecurity // 모든 요청 URL이 스프링 시큐리티 제어를 받도록 명시
// 내부적으로 SecurityFilterChain 클래스가 동작해 모든 요청 URL에 이 클래스가 필터로 적용
// 따라서 URL별로 특별한 설정을 할 수 있게 됨

// 스프링 시큐리티 세부 설정은 @Bean을 통해 SecurityFilterChain 빈을 생성하여 설정할 수 있음
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 인증되지 않은 모든 페이지의 요청을 허락함
        // 즉, 로그인하지 않아도 모든 페이지에 접근 가능
        http
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
                .headers((headers) -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
                        )))
                // 로그인 로직
                .formLogin((formLogin) -> formLogin
                        // 로그인 완료시 "/"로 리다이렉트
                        .loginPage("/user/login")
                        .defaultSuccessUrl("/"))
                // 로그아웃 로직
                .logout((logout)
                        ->logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                        .logoutSuccessUrl("/") // 로그아웃 성공시 /로 리다이렉트
                        .invalidateHttpSession(true)); // 로그아웃시 생성된 사용자 세션도 삭제

        return http.build();
    }

    // BCrypt 객체를 빈으로 생성
    // 모든 클래스가 공통된 객체를 참조하여 사용

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // 스프링 시큐리티의 인증을 처리하는 빈
    // 사용자 인증시 UserSecurityService, PasswordEncoder를 내부적으로 사용하여 인증과 권한 프로세스를 처리
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}

// Note. h2의 403 코드
// 이 설정을 적용하면, 로컬 서버에서 403 코드가 뜬다.
// 즉, 접근이 금지되었다는 것인데 이것은 h2 데이터베이스 서버가 접근을 차단한것이다.
// 이유는 스프링 시큐리티의 CSRF 방어 기능 때문이다.
// CSRF : 웹 보안 공격 중 하나, 조작된 정보로 웹 사이트가 실행되게 속이는 공격 기술
// 스프링 시큐리티는 이를 방지하기 위해 CSRF 토큰을 세션에서 발행하고,
// 웹 페이지에서는 토큰을 포함하여 폼을 전송한다. 스프링 시큐리티는 받은 토큰이 유효한지 판단한다.

// Note. 세부 질문으로 들어가면, CSRF 토큰이 뜬다.
//<form action="/answer/create/307" method="post" class="my-3"><input type="hidden" name="_csrf" value="WraDPUvPFg83JP6s_VW-Ivx5NaxCY5CVYhoa4Z8yO-sAr3YhaoGyDy3_dzkaHcnPyniKG80bGJV3BqC4Vil41aoGC99izEEY"/>
// 이 원리에 의해 의도적으로 조작된 토큰이 주입될경우, 접근을 차단한다.