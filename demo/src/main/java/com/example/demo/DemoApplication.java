package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
    /*앞으로 추가할 목록
     1. 게시글 PK값이용이 아닌 DisplatNumber부여 후 사용
     2. 회원탈퇴기능, 관리자or사용자 설정 및 권한부여
     3. 게시글에 좋아요 누르는 기능?
     */
}
