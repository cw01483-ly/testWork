package com.example.demo.user.domain;
//회원 정보를 담는 DB 테이블 모델

import com.example.demo.post.domain.Post;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor// 모든 필드를 매개변수로 받는 생성자 자동 생성.
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 12)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 20)
    private String nickname;

    //User가 작성한 글 목록 (1:N 관계) OneToMany = 하나가 여러개를 가진다 = 작성자1, 게시글 N
    @OneToMany(mappedBy="user",cascade=CascadeType.ALL,orphanRemoval = true)
    /*mappedBy="user" => 나는 PostList의 주인이 아니고, Post엔티티안의 user필드가 관계의 주인이야.
     => 외래키(주인)은 항상 ManyToOne(Post) 란다.
     => User 는 단순히 '내 글 목록만 모아두는 역할' 만 하고 실제 DB의 user_id 외래키는 Post테이블에 생김
     => User : 내가 직접 user_id 를 관리하지 않아. Post쪽(user 필드)이 나랑 연결해줄거야!*/
    /*cascade=CascadeType.ALL => ALL 은 모든 Type을 일괄포함한다
    =>(PERSIST : 저장 /MERGE : 수정 /REMOVE : 삭제 /REFRESH : 새로고침 /DETACH : 분리)
     => User에 어떤 동작을 하면 Post에도 같이 전파하라!
     => User를 save 하면 Post에도 같이 save, delete 하면 Post도 같이 delete
     => 부모(User)에게 시키면 자식(Post)도 따라간다!*/
    /*orphanRemoval = true =>User와 관계(Post)가 끊기면 DB에서도 지워라
     => 부모는 유지되어도 부모⊙자식관계가 끊어진다면 자식데이터 삭제
     ex) user.getPostList().remove(post);*/
    // User 입장에서 내가 쓴 글이 여러개(PostList) 있다.

    private List<Post> postList = new ArrayList<>();

}
