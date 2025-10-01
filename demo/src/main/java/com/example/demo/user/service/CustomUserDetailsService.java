package com.example.demo.user.service;

// DB에서 User를 조회해서 Security에 전달하는 서비스

import com.example.demo.user.domain.User; // User 엔티티
import com.example.demo.user.domain.CustomUserDetails; // 우리가 만든 UserDetails 구현체
import com.example.demo.user.repository.UserRepository; // DB 접근용 Repository
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // 스프링이 관리하는 Service 컴포넌트
@RequiredArgsConstructor // final 필드 생성자 자동 주입
public class CustomUserDetailsService implements UserDetailsService {
    // UserDetailsService 인터페이스 구현 → Security가 로그인 시 호출

    private final UserRepository userRepository;
    // DB 조회를 위해 UserRepository 주입

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 로그인 시 Security가 호출하는 메서드
        // username: 로그인 폼에서 입력한 아이디

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
        // DB에서 username으로 User를 찾음
        // 없으면 UsernameNotFoundException 발생 → 로그인 실패 처리됨

        return new CustomUserDetails(user);
        // 찾은 User를 CustomUserDetails로 감싸서 반환 → Security가 이 객체로 로그인 검증
    }
}
