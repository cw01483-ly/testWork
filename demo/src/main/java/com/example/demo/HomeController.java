package com.example.demo;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")// 브라우저에서 http://localhost:8080/ 요청이 오면
    public String home(){
        return "index";
        // resources/templates/index.html 파일을 찾아서 보여줌
    }
}
