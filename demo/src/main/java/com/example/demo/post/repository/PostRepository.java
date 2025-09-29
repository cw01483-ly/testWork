package com.example.demo.post.repository;

import com.example.demo.post.domain.Post;
import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
   /*  JpaRepository 덕분에 기본적인 CRUD 메서드가 자동으로 제공.
   C : Create / R : Read / U : Update / D : Delete
   JpaRepository<Post, Long>를 상속받음 ,
   어떤 Entity를 관리할 것인가? : Post / Entity의 기본키(PK)의 타입은 ? : Long
   즉 이 인터페이스는 Post엔티티를 DB와 연결해 CRUD를 사용하는 기능을 가진다.
   save(), findAll(), findById(), deleteById(), count() 등등...
   */

    List<Post> findByUser(User user); //엔티티기반
    /*Spring Data JPA 는 메서드 이름을 분석해서 자동으로 SQL로 만든다
    * >findByUser -> User라는 필드 기준으로 검색해라 라는 뜻.
    * > 즉, Post엔티티 안에있는 user필드(FK,작성자 정보값)를 보고
    * >> 이 유저와 관련된 모든 게시글을 가져오겠다.*/

    // User Id값으로 검색(검색창 입력값 기반)
    List<Post> findByUserId(Long userId);
    /*Post 엔티티 안에는 user라는 필드가 존재하는데 이 user는 User엔티티와
    * 연관관계(@ManyToOne)을 맺고있음
    * JPA는 user.id 라는 하위 필드를 자동으로 탐색하게된다.
    * ex) List<Post> posts = postRepository.findByUserId(1L);
    * user_id가 1인 유저가 작성한 모든 게시글을 리스트로 반환.
    즉, 작성자 ID로 게시글을 검색할 때 사용*/

    // 제목 + 내용 키워드 검색
    List<Post> findByTitleContainingOrContentContaining(String titleKeyword,
                                                   String contentKeyword);
        /*List<> : Post만 담는 여러개를 목록으로 돌려주는 반환타입.
    * findBy : JPA 메서드의 이름규칙 시작 키워드!
    * Containing : 부분 일치 검색하기 => SQL의 LIKE '%키워드%'
    * >> Title,Content는 Post엔티티안에 존재하는 프로퍼티(데이터)이름을 그대로 사용해야함!
    * >> TitleContaining Or(조건 묶기) ContentContaining
    * */

    //글 번호 검색
    List<Post> findAllById(Long id);
    /*여기서 Id는 Post 엔티티의 PK(기본키) 필드 id를 의미
    * ex)SELECT * FROM post WHERE id = ?;
    * 그렇다면 왜 findById가 아니라 findAllById 인가!?
    * JpaRepository는 이미 기본 메서드로 Optional<Post> findById(Long id)를 제공
    * >> 이름 충돌 회피 + 검색 결과 일관성 유지(List 반환) → findAllById 라고 작성
    * 단건 조회에 적합 (글 하나만 가져올 때).
    하지만 검색 기능은 항상 결과를 List로 반환하는 게 일관성이 있다.
    예: 제목 검색도 List<Post> 반환, 작성자 검색도 List<Post> 반환
    글번호 검색도 똑같이 List<Post>로 반환하면, 컨트롤러/서비스에서 공통 처리하기 편리.
    그래서 메서드 이름을 findAllById로 바꿔서 List<Post>를 반환하도록 만든 것*/
}
