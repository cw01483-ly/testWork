package com.example.demo.comment.controller;

import com.example.demo.comment.domain.Comment;
import com.example.demo.comment.service.CommentService;
import com.example.demo.post.domain.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
@Controller
@RequiredArgsConstructor // 생성자 주입 자동 처리


public class CommentController {

    private final CommentService commentService; //댓글저장,조회 등 담당
    private final PostRepository postRepository; //postId로 게시글 조회
    private final UserRepository userRepository; //로그인한 사용자 조회

    //댓글 등록처리하기
    @PostMapping("/comments")
    public String createComment(@RequestParam Long postId,
                                @RequestParam String content,
                                Principal principal){
        //로그인한 사용자 usder네임 가져오기(principal은 로그인한 사용자 정보)
        String username = principal.getName();

        //가져온 username으로 DB에서 User엔티티 찾기
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new IllegalArgumentException("사용자가 존재하지 않습니다."));

        //댓글이 달릴 게시글(Post) 엔티티 찾기
        Post post =postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        //모든 검사와 조건을 통과하면 새로운 댓글 객체 생성 및 값 설정하기
        Comment comment= new Comment();
        comment.setContent(content); //댓글 내용
        comment.setUser(user); //댓글 작성자 (회원가입User)
        comment.setPost(post); //어떤 게시글에 달린 댓글인지

        commentService.createComment(comment);
        //저장이 완료되면 상세페이지로 돌려보내기
        return "redirect:/posts/"+postId;
    }


}
