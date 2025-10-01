package com.example.demo.user.domain;

// User 엔티티를 Spring Security에서 인식할 수 있게 감싸주는 클래스

import org.springframework.security.core.GrantedAuthority; // 권한 객체 타입
import org.springframework.security.core.userdetails.UserDetails; // Security 표준 User 인터페이스

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    // UserDetails 인터페이스를 구현 → Security가 로그인 검증에 사용

    private final User user;
    // 실제 DB의 User 엔티티를 필드로 가지고 있음

    public CustomUserDetails(User user) {
        this.user = user; // 생성자에서 User 엔티티를 받아서 저장
    }

    @Override
    public String getUsername() {
        // Security가 로그인 시 아이디로 사용할 값 반환
        return user.getUsername(); // User 엔티티의 username 필드
    }

    @Override
    public String getPassword() {
        // Security가 로그인 시 비밀번호로 사용할 값 반환
        return user.getPassword(); // 암호화된 비밀번호
    }

    //nickname 꺼내오는 메서드 추가
    public String getNickname() {
        return user.getNickname();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자 권한(ROLE)을 반환 → 지금은 권한 기능을 안 쓰므로 빈 리스트 반환
        return Collections.emptyList();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부 (true = 만료 안 됨)
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠김 여부 (true = 잠기지 않음)
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 비밀번호 만료 여부 (true = 만료 안 됨)
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정 활성화 여부 (true = 사용 가능)
        return true;
    }
}
