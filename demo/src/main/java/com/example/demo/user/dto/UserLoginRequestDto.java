package com.example.demo.user.dto;
/*
 * 📌 로그인 요청 DTO
 * - 사용자가 로그인 폼에서 입력한 아이디/비밀번호를 담는 객체
 * - Controller → Service 로 전달될 때 사용
 */
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // 기본생성자를 자동으로 생성시킴
public class UserLoginRequestDto {
    private String username;
    private String password;
}
