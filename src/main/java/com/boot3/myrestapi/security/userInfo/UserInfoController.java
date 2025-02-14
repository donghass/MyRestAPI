package com.boot3.myrestapi.security.userInfo;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserInfoController {


    @GetMapping("/welcome")
    public String welcome() {
        return "환영합니다.";
    }
}