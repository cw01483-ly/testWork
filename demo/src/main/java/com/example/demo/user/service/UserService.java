package com.example.demo.user.service;
/*📌 UserService 단계에서 할 일
비즈니스 로직을 작성하는 계층
회원가입할 때
아이디 중복 확인
비밀번호 조건 확인
User 엔티티 생성 후 저장*/

import com.example.demo.user.domain.User;
import com.example.demo.user.dto.UserLoginRequestDto;
import com.example.demo.user.dto.UserSignupRequestDto;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor    // final 필드를 자동으로 생성자 주입

public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // BCryptPasswordEncoder 주입

    public User signup(UserSignupRequestDto dto){
        if(userRepository.findByUsername(dto.getUsername()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
    // 2. 비밀번호 암호화 하기
    String encodedPassword = passwordEncoder.encode(dto.getPassword());


    User user = User.builder()
            .username(dto.getUsername())
            .password(encodedPassword)// 암호화된 비밀번호 저장
            .nickname(dto.getNickname())
            .build();
    return userRepository.save(user);
    }

    /*로그인 하기*/
    public User login(UserLoginRequestDto dto){
        // 1. username으로 회원 조회하기(Optional 반환)
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        /*if(user == null){
            throw new IllegalArgumentException("존재하지 않는 아이디입니다.");
        } >> UserRepository 타입을 Optional로 변환하면서 제거한 부분*/
        // 2. 비밀번호 검사 (평문 vs 암호화)
        if(!passwordEncoder.matches(dto.getPassword(),user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 틀립니다.");
        }
        // 3. 로그인 성공하면 User엔티티로 반환
        return user;
    }
}
/*
📌 흐름 정리
Controller → UserSignupRequestDto 를 전달받음
Service → findByUsername() 으로 아이디 중복 확인
Service → 비밀번호를 passwordEncoder.encode() 로 암호화
Service → Builder로 User 객체 생성
Repository → save() 실행 → DB 저장*/
