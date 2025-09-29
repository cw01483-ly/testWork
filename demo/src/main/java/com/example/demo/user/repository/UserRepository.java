package com.example.demo.user.repository;
// DB 접근
import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // username 으로 회원을 찾을 때 사용 (로그인 등에 활용 가능)
    Optional<User> findByUsername(String username);
}
/*JpaRepository<T, ID> 는 제네릭(Generic) 인터페이스예요.
첫 번째 자리 <T> → 엔티티 타입 (여기서는 User)
두 번째 자리 <ID> → 기본 키 타입 (여기서는 Long)
즉, JpaRepository<User, Long> 은
➡️ “User 엔티티를 다루고, 그 PK 타입은 Long이다” 라는 의미.*/