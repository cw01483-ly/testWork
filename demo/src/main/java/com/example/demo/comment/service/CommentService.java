package com.example.demo.comment.service;


import com.example.demo.comment.domain.Comment;
import com.example.demo.comment.repository.CommentRepository;
import com.example.demo.post.domain.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException; //댓글삭제시 검증

import java.util.List;


@Service
@RequiredArgsConstructor //final 필드 자동 생성자주입
public class CommentService { //서비스 클래스가 리포지터리를 사용하여 DB에 접근할건데, 댓글DB,PostDB, UserDB접근 해야함

    private final CommentRepository commentRepository; //댓글DB 접근
    private final PostRepository postRepository; //게시글 존재, 조회 확인하기
    private final UserRepository userRepository; //작성자 조회하기

    //댓글 달기
    @Transactional //이 메서드 안의DB작업들을 하나의 묶음으로 처리하겠다!하나라도 오류나면 엎어버림!
    public Comment createComment(Long postId, Long userId, String content){
        //대상 게시글을 조회하고 없다면 예외처리
        Post post = postRepository.findById(postId)
                // postId까지 보여주며 어느 값이 담겼는지 확인도 가능
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시물입니다. id="+postId));
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. id="+userId));
        //위의 조건 둘 다 있다면 댓글엔티티 생성
        Comment comment = new Comment(); //엔티티
        comment.setPost(post); //Comment엔티티에 @Setter로 인해 사용 가능
        comment.setUser(author);
        comment.setContent(content);
        //new Comment를 comment에 담아서 return값 전달
        return commentRepository.save(comment);//JPA리포지터리 상속받은 comment리포지터리의save기능을 사용해 저장
    }

    //조회하기
    @Transactional(readOnly = true)//트랜잭션은 원래 쓰기기능도 들어있어서 실수로라도 쓰는걸 방지하기 위해 읽기전용으로 선언
    public List<Comment> getCommentsByPost(Long postId){
        // postId로 해당 게시글의 모든 댓글을 검색
        // createdAt 기준으로 오름차순으로 정렬해서 반환!
        return commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId);
    }

    //댓글 작성하기
    @Transactional//하나 실패하면 다 실패!
    public Comment createComment(Comment comment){
        return commentRepository.save(comment);
    }

    //댓글 수정하기(내용만)
    @Transactional
    public void updateComment(Long id, String newContent){
        //댓글이 DB에 존재하는지 먼저 확인
        Comment comment=commentRepository.findById(id)
                .orElseThrow(() ->  new IllegalArgumentException("해당 댓글이 존재하지 않습니다. id=" + id));

        // Comment 엔티티 속 updateContent() 메서드 호출
        //이 메서드에서 content필드를 newContent로 변경후 updateAt 시간을 현재시간으로 갱신
        comment.updateContent(newContent);

        //void로 반환값이 필요없음! save()등 호출이 필요없음, JPA의 변경감지(Dirty Checking)기능으로 트랜잭션이 끝날때
        //자동으로 UPDATE 쿼리가 실행된다.
    }

    //댓글 삭제하기
    @Transactional
    public void deleteComment(Long commentId, Long userId){
        // 1차검증 > 댓글이 실제 DB에 존재하는지 먼저 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new IllegalArgumentException("해당 댓글이 존재하지 않습니다. id="+commentId));
        // 2차검증 > 댓글 작성자와 현재 로그인한 사용자가 같은지 비교
        if(!comment.getUser().getId().equals(userId)){
            /*객체.equals()는 null-safe 비교=> null이여도 안전하게 처리
             작성자 ID(authorId)와 현재 로그인한 사용자ID(currentUserId)가 다르면 실행!*/
            throw new AccessDeniedException("댓글 삭제 권한이 없습니다.");
            //예외를 던져서 컨트롤러에서 잡아내고 403 Forbidden처리 가능하다
        }
        //본인일 경우 삭제 진행
        commentRepository.delete(comment);//DB에서 해당 댓글 행을 DELETE실행
    }
}
