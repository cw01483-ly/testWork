package com.example.demo.post.repository;

import com.example.demo.post.domain.Post;
import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
   /*  JpaRepository 덕분에 기본적인 CRUD 메서드가 자동으로 제공됩니다.
   C : Create / R : Read / U : Update / D : Delete
   JpaRepository<Post, Long>를 상속받음 ,
   어떤 Entity를 관리할 것인가? : Post / Entity의 기본키(PK)의 타입은 ? : Long
   즉 이 인터페이스는 Post엔티티를 DB와 연결해 CRUD를 사용하는 기능을 가진다.
   save(), findAll(), findById(), deleteById(), count() 등등...
   */

    List<Post> findByUser(User user);
    /*Spring Data JPA 는 메서드 이름을 분석해서 자동으로 SQL로 만든다
    * >findByUser -> User라는 필드 기준으로 검색해라 라는 뜻.
    * > 즉, Post엔티티 안에있는 user필드(FK,작성자 정보값)를 보고
    * >> 이 유저와 관련된 모든 게시글을 가져오겠다.*/

    List<Post> findByTitleContainingOrContentContaining(String titleKeyword,
                                                   String contentKeyword);
    /*List<> : Post만 담는 여러개를 목록으로 돌려주는 반환타입.
    * findBy : JPA 메서드의 이름규칙 시작 키워드!
    * Containing : 부분 일치 검색하기 => SQL의 LIKE '%키워드%'
    * >> Title,Content는 Post엔티티안에 존재하는 프로퍼티(데이터)이름을 그대로 사용해야함!
    * >> TitleContaining Or(조건 묶기) ContentContaining
    * */

}
