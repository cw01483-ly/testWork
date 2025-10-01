package com.example.demo.config;
// 비밀번호 암호화 하기 위한 작업

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//필터체인으로 보안규칙 구성
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

//비밀번호 해시화를 위한 BCrypt구현, 패스엔코더 인터페이스
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration //스프링설정 클래스임을 표시하는것(컴포넌트 스캔 대상)

public class SecurityConfig {

    @Bean//빈등록 : 회원가입시 비밀번호 해시,로그인 시 검증에 사용
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();//BCrypt는 강도가 적절하고 보편적으로 사용된다고 한다.
    }

    @Bean//보안 규칙 담는 필터체인
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                //페이지 접근권한 설정
                //authorizeHttpRequests 블록에서 요청 경로별 접근 권한을 정의
                //permitAll() 대상은 로그인 없이 접근 가능, 그외에는 인증 필요
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/user/signup", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated())

        //로그인 설정
        .formLogin(form -> form //formLogin : 스프링 시큐리티 내장 로그인처리 사용하기
                .loginPage("/user/login")         // 로그인 페이지 URL
                        .defaultSuccessUrl("/",true)
                        //.defaultSuccessUrl("/",true) : true => 사용자가 어디서 왔던 무조건 "/"로 리다이렉트
                        .permitAll()
        )
                
        //로그아웃 설정
        .logout(logout -> logout
                .logoutUrl("/user/logout")
                .logoutSuccessUrl("/")
                .permitAll()
        );
        return http.build(); //http.build호출로 최종 시큐리티필터체인을 생성하여 빈으로 노출
    }


}
