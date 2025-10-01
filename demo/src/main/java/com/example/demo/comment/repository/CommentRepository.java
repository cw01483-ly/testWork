package com.example.demo.comment.repository;

import com.example.demo.comment.domain.Comment;
import com.example.demo.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
//JPA는 CRUD 메서드 제공!
public interface CommentRepository extends JpaRepository<Comment,Long> {
    //Comment엔티티 ,PK타입은 Long!

    //특정 게시글(게시글상세페이지)의 댓글 전부 불러오기
    List<Comment> findAllByPostIdOrderByCreatedAtAsc(Long postId);

}
