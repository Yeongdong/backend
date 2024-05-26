package com.example.spinlog.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController { //TODO 배포 시 삭제

    //@GetMapping("/")
    public String homePage() {
        return "home";
    }

}
