package com.example.demo.post.controller;

import com.example.demo.post.domain.Post;
import com.example.demo.post.service.PostService;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository; // User조회하기 위한 Repository
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable; // ✅ 올바른 Pageable import
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

    // (뷰 렌더링 방식: 결과를 post/list.html에 뿌려줌)
    // 검색기능 (제목+내용, 작성자ID, 게시글ID)
    @GetMapping("/search")
    public String searchPosts(@RequestParam("type") String type,      // 검색 기준: titleContent | userId | postId
                              @RequestParam("keyword") String keyword, // 검색어(문자열)
                              Model model) {                           // 뷰에 데이터를 전달할 상자
        // 1) 입력 정리: null 방지 + 앞뒤 공백 제거
        final String kw = (keyword == null) ? "" : keyword.trim();
        // 2) 결과를 담을 리스트 준비
        List<Post> results;
        try {
            // 3) 검색 기준(type)에 따라 분기
            switch (type) {
                case "titleContent":
                    // 제목+내용 통합 검색: LIKE '%keyword%'
                    results = postService.searchPostsByKeyword(kw);
                    break;
                case "userId":
                    // 작성자 ID 검색: 숫자만 허용 → 숫자 아니면 NumberFormatException 발생
                    Long userId = Long.parseLong(kw);
                    results = postService.findPostsByUserId(userId);
                    break;
                case "postId":
                    // 글번호(ID) 검색: 숫자만 허용
                    Long postId = Long.parseLong(kw);
                    results = postService.findPostsByPostId(postId);
                    break;
                default:
                    // 지원하지 않는 타입이면 개발자 실수 가능성이 높으므로 즉시 예외
                    throw new IllegalArgumentException("지원하지 않는 검색 타입: " + type);
            }
        } catch (NumberFormatException nfe) {
            // 4) 숫자 타입(userId/postId)에 숫자가 아닌 검색어가 들어온 경우
            results = java.util.Collections.emptyList();  // 결과는 빈 리스트로
            model.addAttribute("message", "숫자만 입력해 주세요."); // 뷰에서 안내 문구로 표시 가능
        }
        // 5) 검색 결과 및 폼 상태를 모델에 담아 동일 템플릿으로 전달
        model.addAttribute("posts", results);  // 목록 테이블이 이 리스트를 그대로 사용
        model.addAttribute("selectedType", type); // 검색 셀렉트박스 선택 유지 용도(옵션)
        model.addAttribute("keyword", kw);        // 검색어 입력칸 값 유지 용도(옵션)
        // 6) 목록 화면 재사용
        return "post/list";
    }

    //게시글 삭제하기 (삭제완료되면 목록으로 이동)
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id){
        postService.deletePost(id); // 서비스호출 , DB에서 삭제
        return "redirect:/posts"; //삭제 후 목록페이지로 리다이렉트
    }

    // 📋 게시글 목록 조회 (페이징 기능 포함)
    @GetMapping  // 👉 GET 방식 "/posts" 요청 처리
    public String list(@RequestParam(defaultValue = "0") int page,   // 🔹 현재 페이지 번호 (기본값=0, 즉 첫 페이지)
                       @RequestParam(defaultValue = "10") int size,  // 🔹 한 페이지에 보여줄 글 수 (기본값=10)
                       Model model) {                                // 🔹 뷰(HTML)에 데이터 전달하기 위한 객체

        // ✅ 1. Pageable 객체 생성
        // PageRequest.of(페이지번호, 글 수, 정렬방식)
        // Sort.by("id").descending() → 글 번호(id) 기준 내림차순 (최신 글이 위로)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // ✅ 2. 서비스 호출 → DB에서 페이징된 게시글 목록 가져오기
        // postService.findAllWithPaging(pageable) → Page<Post> 객체 반환
        Page<Post> postPage = postService.findAllWithPaging(pageable);

        // ✅ 3. 뷰에 데이터 전달 (model 사용)
        model.addAttribute("postPage", postPage);           // 전체 Page<Post> 객체 전달 (총 페이지 수, 현재 페이지 등 부가정보 포함)
        model.addAttribute("posts", postPage.getContent()); // 실제 게시글 리스트(List<Post>)만 추출해서 전달
        model.addAttribute("currentPage", page);            // 현재 페이지 번호를 따로 전달
        model.addAttribute("totalPages", postPage.getTotalPages()); // 전체 페이지 개수 전달

        // ✅ 4. 반환
        // "post/list" → templates/post/list.html 뷰 파일을 찾아서 렌더링
        return "post/list";
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