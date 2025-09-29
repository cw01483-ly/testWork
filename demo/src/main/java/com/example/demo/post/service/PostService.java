package com.example.demo.post.service;

import com.example.demo.post.domain.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.user.domain.User;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;  // 여러개의 결과값을 담을 때 사용 >> List<Post>
import java.util.Optional; // 값이 있거나 없을 수도 있을 수도 있을 때 사용 >> Optional<Post> : 찾는게 없어도 ㄱㅊㄱㅊ

@Service //서비스 계층임을 표시! (스프링이 관리한다)
@RequiredArgsConstructor // final 필드를 자동으로 생성자 주입
public class PostService {

    private final PostRepository postRepository; // DB 접근용 Repository
    //서비스가 DB 일을 하려면 Repository 도구가 필요하니, 한 번 주입받아(생성자 주입) 평생 안전하게 쓰겠다!!
    // PostRepository 인터페이스를 사용해 postRepository를 생성

    // 게시글 작성하기
    public Post createPost(String title, String content, User user){
        /*이미 Post.Entity에 nullable 조건이 들어가있지만
        이렇게 조건을 걸어두면 DB까지 보내고 검사하는게 아닌 바로 검사함으로써 개발자의 의도를
        사용자에게 전달할 수 있음!!! >> 사용자 친화적 피드백
        데이터의 안정성만 생각한다면 굳이 필요는 없음!**/
        if ( title == null || title.isBlank()){
            throw new IllegalArgumentException("제목은 비워둘 수 없습니다.");
        }
        if ( content == null || content.isBlank()){
            throw new IllegalArgumentException("내용은 비워둘 수 없습니다.");
        }
        if ( user == null){
            throw new IllegalArgumentException("작성자가 존재하지 않습니다.");
        }
        Post post = new Post(title,content,user);
        /*Post Entity의 Post생성자 사용 > new Post를 post에 저장*/
        return postRepository.save(post); // DB에 저장, (post)는 전송 방식이 아닌 Post post변수
    }

    // 전체 게시글 조회 기능
    public List<Post> findAllPosts(){
        /*List<Post> → Post 객체들을 여러 개 담을 수 있는 상자
        즉, “게시글(Post)들을 모아둔 목록”을 의미
        → DB에 있는 모든 Post 글들을 불러와서 List로 반환*/
        return postRepository.findAll();
    }

    // 특정 ID로 게시글 하나만 조회하는 기능 >> 게시글 번호로 찾기
    public Optional<Post> findPostById(Long id){
        /*postRepository의 findById(id)를 사용해서
        id로 Post를 찾아 Optional에 담아서 반환한다.
        찾은게 있다면 Optional<Post>, 없으면 Optional.empty()로 반환!
         */
        return Optional.ofNullable(postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음")));
    }

    // 게시글 수정하기
    public Optional<Post> updatePost(Long id, String title, String content){
        /*Optional<Post>👉게시글(Post 객체)이 들어 있을 수도 있고, 없을 수도 있는 상자 */
        return postRepository.findById(id) //id로 Post찾기 → Optional<Post> 못찾았으면 → Optional<Empty>
                .map( post -> {// Optional 안에 Post가 있으면 실행 .map은 if문을 짧게 줄여쓴 Optional이 가진 메서드
                    post.update(title, content);// Post 엔티티의 update 메서드 호출
                    return postRepository.save(post);// 수정된 Post를 반환
                });
    }
    /*👉 "id로 DB에서 Post를 찾고,
    찾았다면 그 Post를 post라는 이름으로 꺼내서 제목과 내용을 고친 뒤,
     수정된 Post를 다시 Optional에 담아 반환한다."*/

    // 게시글 삭제하기
    public void deletePost(Long id){ //삭제는 성공or실패 만 중요하기에 돌려줄 값이 없으므로 void 사용!
        // 반환할 값이 없으므로 Optional도 사용안함
        //Long id >> 게시글 pk값
        Post post = postRepository.findById(id)
         /*JPA가 DB에서 id에 해당하는 Post를 찾아 Optional<Post>로 돌려준다
        postRepository가 이미 Optional를 사용할 수 있기에 Optional선언안해도 사용 가능*/
                .orElseThrow(() -> new IllegalArgumentException(
                        "게시글번호 " + id + "번에 해당하는 게시글이 존재하지 않습니다."
                ));
                //.orElseThrow :Optional이 비어있을 경우(해당 게시글의id 가 없는경우 실행)
                //바로 던져서 예외처리 할거임
        postRepository.delete(post);
        // JPA가 실제 SQL: DELETE FROM posts WHERE id=? 실행
        // DB에서 해당 게시글 삭제
    }

    // 작성자로 게시글 찾기(Post의FK값, User의 PK값)
    public List<Post> findPostsByUser(User user){
        /*List<Post> : 반환타입. 'Post객체들의 목록을 의미한다.(제네릭 < > 을 사용함.'
        >> 제네릭이란 쉽게 말해 파일 단계에서 타입을 강제하는 것! > 이 List는 Post 객체만 담을 수 있어!!
        >> 잘못된 타입저장이나 캐스팅하는 번거로움을 없애준다        *
        * (User user) : 매개변수 목록,User 타입의 user 하나를 입력으로 받음*/
        return postRepository.findByUser(user);
        /* postRepository.findByUser(user)의 호출 결과가 (List<Post>)를 그대로 반환
        * JPA 규칙상 결과가 없으면 null값이 빈 리스트[]에 담겨져 나오기때문에 이 메서드에서는 Optional 안써도 괜찮음!*/
    }

    //페이지 나누기(Pageable 사용)
    public Page<Post> findAllWithPaging(Pageable pageable){
       /*Page : JPA에서 제공하는 '클래스' List와 달리
       글목록+부가정보(실제 글 목록,전체 글 개수, 전체 페이지수,페이지 번호...)등을 함께 담아줌
       * >> Page<Post> 는 게시글 목록을 페이지 다누이로 담는 상자!*/
        //Pageable pageable = “책갈피 + 읽는 규칙 (몇 장부터 몇 개, 어떤 순서)”
        //findAllWithPaging(pageable) = “책갈피 규칙대로 필요한 부분만 펼쳐서 가져와”
        PageRequest.of(0,10, Sort.by("id").descending());
        //페이지 설정하기 (0번째 페이지(첫번째 페이지),한페이지에 10개, id기준 내림차순 정렬하기)
        // 0번째 페이지는 html에서+1 하여 사용자에게 1페이지 부터 보이게 하기! 프로그램은 0부터 시작
        return postRepository.findAll(pageable);
    }

    // 제목+내용 키워드로 검색하기
    public List<Post> searchPostsByKeyword(String keyword){
        return postRepository.findByTitleContainingOrContentContaining(keyword,keyword);
        /*
      - keyword : 사용자가 검색창에 입력한 문자열
      - postRepository가 SQL 실행 시
        SELECT * FROM posts
        WHERE title LIKE '%keyword%'
           OR content LIKE '%keyword%'
      - 결과 : 제목이나 내용에 keyword가 들어간 모든 게시글 반환
    */
    }
}
