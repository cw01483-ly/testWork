package com.example.demo.post.controller;


import com.example.demo.post.domain.Post;
import com.example.demo.post.service.PostService;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository; // User조회하기 위한 Repository
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // 로그인한 사용자 정보를 가져오는 객체
import java.util.List;


@Controller
// 이 클래스는 웹 요청을 처리하는 컨트롤러임을 명시 웹 요청(HTTP 요청)을 받아서 처리하고, 뷰(html)를 반환하는 역할
// >> 컨트롤러 클래스 안에서 메서드가 String을 반환하면 스프링은 그 값을 뷰(html파일의 이름)로 인식
@RequiredArgsConstructor
/*👉 final이 붙은 필드(postService)를 자동으로 생성자 주입해줌
(Lombok이 "public PostController(PostService postService) { this.postService = postService; }"
이런 코드를 자동 생성해줌)
 **/
@RequestMapping("/posts") //이 컨트롤러의 기본 주소는 /posts이다. >> "/posts"로 시작하는 요청들을 모두 이 컨트롤러에서 처리하게 됨
public class PostController {

    private final PostService postService;
    //DB 작업은 Service가 담당하므로 Service를 호출해서 사용하겠다!
    private final UserRepository userRepository; //DB에서 User를 찾기위해 필요함

    //게시글 목록 조회
    @GetMapping //Get 방식 요청의 "/posts"를 처리한다
    public String list(Model model){
        List<Post> posts = postService.findAllPosts(); // 전체 글 조회하기
        model.addAttribute("posts",posts); // 뷰에 데이터 전달
        return "post/list";                           // templates/post/list.html 렌더링
    }

    //게시글 작성 페이지 열기
    @GetMapping("/new")  // 👉 GET 방식 요청의"/posts/new" 처리 (글쓰기 폼 열기)
    public String createForm(){
        return "post/create";   // templates/post/create.html 반환
    }

    //  게시글 작성 처리
    @PostMapping     // 👉 POST 방식 요청 "/posts" 처리 (글 저장) , 글쓰기 폼 제출시 실행
    public String create(@RequestParam String title, //글 제목을 form에서 받아옴
                         @RequestParam String content, // 글 내용을 form에서 받아옴
                         Principal principal){ // 현재 로그인한 사용자 정보(Principal)를 받아오기
        //1. 현재 로그인한 사용자의 username 가져오기
        String username = principal.getName();
        //principal.getName() >> 로그인 할 때 입력한 username 값

        //2. DB에서 User 엔티티 조회(username으로 찾기)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        postService.createPost(title,content,user);//저장기능
        return "redirect:/posts";
    }

    // 게시글 조회하는 메서드
    @GetMapping("/{id}") // /posts/{id} 요청을 처리 (게시글 id)
    public String detail(@PathVariable Long id, Model model){
        //@PathVariable = URL 경로의 {id} 값을 id 변수에 담아줌
        // Model : 조회한 게시글 데이터를 뷰(detail.html)에 전달하는 객체
        Post post = postService.findPostById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        model.addAttribute("post",post);
        return "post/detail";
    }

    // 검색기능 (제목+내용, 작성자ID, 게시글ID)
    @GetMapping("/search") // Get방식으로 /posts/search 요청이 오면 실행
    @ResponseBody //JSON 응답을 위해서 필요하다(없으면 뷰 이름으로 인식)
    // 해당 메서드의 반환타입은 List<Post> 라서 뷰이름이 아닌 객체라 혼돈이 생기기에 반환객체를 이름으로 해석하지말고
    // JSON형식으로 변환해서 HTTP응답 본문(body)에 넣어달라는 요청
    public List<Post> searchPosts(
            // 반환타입: List<Post> → Post 객체 여러 개를 JSON 배열로 돌려줌(스프링이 자동으로 직렬화).
            @RequestParam("type") String type,     // 검색 기준 (titleContent, id, userId)
            //이 부분은 스프링에게 **“쿼리스트링에 있는 type 파라미터 값을,
            //자바 메서드의 type 변수에 넣어줘”**
            @RequestParam("keyword") String keyword // 검색어
    ){
        if(type.equals("titleContent")){// 검색 기준이 titleContent"(제목+내용 통합검색) 라면?
            return postService.searchPostsByKeyword(keyword);
            //포스트서비스를 호출 >> 제목 또는 내용에 키워드가 포함되는 게시글들을 찾아
            //List<Post>로 반환하겠다!,
            // 리포지터리의 findByTitleContainingOrContentContaining 메서드를 사용
            //결과가 없으면 빈 리스트[]를 반환하므로 오류걱정 X
        }else if(type.equals("userId")){
            //작성자 ID로 검색할 경우
            Long userId = Long.valueOf(keyword);
            return postService.findPostsByUserId(userId);
        }else if(type.equals("postId")){
            //글번호(ID)로 검색할 경우
            Long postId = Long.valueOf(keyword);
            return postService.findPostsByPostId(postId);
        }
        throw new IllegalArgumentException("지원하지 않는 검색 타입입니다. 입력값: "+type);
        //type 까지 알려주면서 나중에 디버깅할때 편하게 설정
    }


}
/*ReQuestParam 이란?
    사용자가 [폼 입력]이나 [URL 쿼리스트링] 으로 보낸 값을 [메서드의 파라미터(매개변수)]로 받아주는 어노테이션
    >>> 사용자가 입력한걸 변수에 담아줘 !!!
    내부에 선언하는 이유 : 상단에 배치하면 모든 메서드에 같은 규칙이 적용되어버림
 */
/*list() → 게시글 목록 페이지 (posts/list.html)
createForm() → 게시글 작성 페이지 (post/create.html)
create() → 작성된 데이터 DB 저장 후 목록으로 이동*/
/*쿼리스트링 :브라우저 주소창에서 ? 뒤에 붙는 추가 정보(파라미터) */