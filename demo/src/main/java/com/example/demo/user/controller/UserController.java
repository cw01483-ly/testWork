package com.example.demo.user.controller;


import com.example.demo.user.domain.User;
import com.example.demo.user.dto.UserLoginRequestDto;
import com.example.demo.user.dto.UserSignupRequestDto;
import com.example.demo.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor //final이 붙은 필드만 자동으로 생성자에 넣어주는 기능
//Spring이 알아서 UserService 객체를 만들어서 userService에 넣어줌
@RequestMapping("/user") // /user 로 시작하는 요청을 처리한다 *ex: /user/signup, /user/login

public class UserController {
    private final UserService userService;
    //회원 관련 비지니스 로직을 처리하는 서비스
    //@RequiredArgsConstructor 덕분에 자동으로 주입된다

    //회원가입 페이지 요청시
    @GetMapping("/signup") // Get방식으로 /user/signup 요청이 오면 실행될 메서드
    public String signupPage(Model model){
        model.addAttribute("userSignupRequestDto", new UserSignupRequestDto());
        return "user/signup";  //templates/user/signup.html 파일을 반환한다
    }

    //회원가입 처리할 때, DB로 전송하니깐 PostMapping
    @PostMapping("/signup")
    // POST 방식으로 /user/signup 요청이 오면 실행됨 (폼 제출 시 == 데이터전송클릭(회원가입버튼 등))
    public String signup(@ModelAttribute UserSignupRequestDto dto, Model model){
        // @ModelAttribute → HTML 폼 입력값을 DTO(UserSignupRequestDto) 객체로 자동 바인딩
        // Model → 뷰로 데이터를 전달할 때 사용
        try{
            userService.signup(dto); // UserService에서 signup기능 꺼내오기
            return "redirect:/user/login"; // signup이 성공시 login페이지로 이동시키기
        }catch (IllegalArgumentException e){// 실패 시 에러 메시지를 모델에 담아 뷰로 전달
            model.addAttribute("error",e.getMessage());
            return "user/signup"; // 실패했으니 다시 signup페이지 보여주기
        }
    }

    /*로그인 페이지*/
    @GetMapping("/login") //Get방식으로 /user/login 요청이 오면 실행할 메서드
    public String loginPage(Model model){
        model.addAttribute("userLoginRequestDto",new UserLoginRequestDto());
        return ("user/login"); //templates/user/login 으로 반환
    }

    /*로그인 처리
    @PostMapping("/login")//Post방식으로 /user/login 요청이 오면 실행할 메서드
    public String login(@ModelAttribute UserLoginRequestDto dto,Model model, HttpSession session) {
        try {
            User user = userService.login(dto);//로그인 시도
            session.setAttribute("loginUser", user);//세센에 사용자 정보 저장
            return "/"; //로그인 성공시 index로 이동
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "user/login"; //로그인 실패시 로그인페이지로 보내기
        }

    }

    //로그아웃
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 전체 삭제
        return "/"; // index로 이동
    }
    PostMapping의 로그인과 로그아웃은 Security가 자동처리 함으로 컨트롤러에서 GetMapping만 유지해도 된다!*/
}
