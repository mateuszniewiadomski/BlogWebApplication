package com.example.blog.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("loginwebcontroller")
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
