package com.example.demo.comment.repository;

import com.example.demo.comment.domain.Comment;
import com.example.demo.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
//JPA는 CRUD 메서드 제공!
public interface CommentRepository extends JpaRepository<Comment,Long> {
    //Comment엔티티 ,PK타입은 Long!

    List<Comment> findByPost(Post post); //게시글이 달린 모든 댓글 가져오기
}
