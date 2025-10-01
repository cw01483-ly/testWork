package com.example.demo.comment.domain;


import com.example.demo.post.domain.Post;
import com.example.demo.user.domain.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity //엔티티선언
@Getter
@Setter
@NoArgsConstructor  //기본 생성자 자동 생성

public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  //댓글 기본 키 자동증가

    @Column(nullable = false,length = 100)
    private String content; //댓글내용 100자제한, 공백거부

    @ManyToOne(fetch = FetchType.LAZY) // N:1 관계(게시글1개에 여러개의댓글)
    @JoinColumn(name="post_id",nullable = false) //@JoinColumn: 외래키 지정 어노테이션
    private Post post; //댓글이 속한 게시글(FK : post_id)


    @ManyToOne(fetch = FetchType.LAZY) // N:1 관계(하나의 사용자 여러개의댓글)
    @JoinColumn(name="user_id",nullable = false)
    private User user; //댓글 작성자 (FK : user_id)

    private LocalDateTime createdAt = LocalDateTime.now();
    //댓글 생성시점 자동 저장

    private LocalDateTime updatedAt;
    //댓글 수정할 때 마다 갱신

    //댓글 수정 메서드
    public void updateContent(String newContent){
        this.content=newContent;
        this.updatedAt=LocalDateTime.now();
    }
}
