package com.example.spinlog.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController { //TODO 배포 시 삭제

    //@GetMapping("/profile")
    public String profilePage() {
        return "profile";
    }
}
