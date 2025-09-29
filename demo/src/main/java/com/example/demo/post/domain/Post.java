package com.example.demo.post.domain;

import com.example.demo.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity //JPA Entity 선언( DB 테이블로 매핑시키기 )
@Getter  // lombok의 getter 메서드 자동 생성
@NoArgsConstructor // lombok의 기본 생성자 자동생성 메서드
public class Post {

    @Id                 //PK 지정
    @GeneratedValue(strategy= GenerationType.IDENTITY) // id(게시글 번호)의 수 자동증가
    private Long id;


    @Column(nullable = false, length = 100) //게시글 제목의 설정 , 공백x , 길이제한 100
    private String title; // String 타입의 title(글제목)

    @Column(nullable = false,columnDefinition = "VARCHAR(255)")
    /* columnDefinition= DB 열 생성시 지정할 타입
    columnDefinition = "VARCHAR(255)" 대신 length=255 라고 작성해도 자동매핑 해줌
     짧은 글이라면 VARCHAR가 속도,검색 측면에서 유리하지만 길고 큰 용량이라면
     TEXT 타입으로 지정해서 사용해야함
     TEXT : 64KB / MEDIUMTEXT : 16MB / LONGTEXT : 4GB*/
    private String content; // 글 내용을 content라 지정

    // 작성자 (User 엔티티와 연결, N:1관계)
    @ManyToOne(fetch = FetchType.LAZY) // Post입장에서 작성자는 User 1명이다.
    /*fetch = FetchType.LAZY => Post를 불러올 때 바로 불러오지말고 필요할 때 가져와라
    * fetch = FetchType.EAGER => Post를 불러올 때 바로 불러와라!*/
    @JoinColumn(name="user_id",nullable = false)
    // nullable = false 작성자가 없는 글은 만들 수 없다.
    /* JoinColumn = 외래키(FK)를 매핑할 때 사용하는 어노테이션
     이 필드는 다른 테이블과 연결된 컬럼이야!!!
     => Post 테이블 안에 user_id 라는 외래키컬럼을 만들겠어!
     => DB에 posts라는 테이블에 user_id 라는 컬럼이 생기고 해당 컬럼은 users 테이블의 id컬럼을
     참조한다 == user_id(FK)는 users라는 테이블의 id(PK)를 참조한다.
     */
    private User user;
    /*User 타입(String 처럼)의 user라는 객체를담을 수 있는 변수를 만든다.
    * 이 변수에는 작성자(User 객체)가 들어온다*/


    /*JPA에서 모든 필드는 DB컬럼으로 자동 매핑됨 @Column을 붙이는 이유는 조건설정을 위해서!*/
    private LocalDateTime createdAt; //작성일시  , 시간타입으로 자동매핑
    private LocalDateTime updatedAt; // 수정일시

    // 새로 글을 작성할 때 사용할 [생성자]메서드
    // 객체를 새로 만들 때 사용(호출) 하므로 반환형을 사용하지 않는다.
    public Post(String title, String content, User user){
        this.title=title;
        this.content=content;
        this.user = user; // 작성자를 User 객체로 연결
        this.createdAt=LocalDateTime.now();
        this.updatedAt=LocalDateTime.now();
    }

    //수정 할 때
    /* update는 이미 만들어진 객체를 수정하는 [일반] 메서드
     일반 메서드는 반드시 반환형을 명시해야함
     void : 아무것도 반환하지 않는다.
     int  : 정수 반환
     String : 문자 반환
     >> 여기선 update가 값을 돌려줄 필요가 없으므로 void를 사용한다.*/
    //단순히 동작을 고쳐둔다 라는 개념이므로 반환이 필요없음
    public void update(String title, String content){
        this.title=title;
        this.content=content;
        this.updatedAt=LocalDateTime.now();
    }
}
