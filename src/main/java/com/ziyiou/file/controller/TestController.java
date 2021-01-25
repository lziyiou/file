package com.ziyiou.file.controller;

import com.ziyiou.file.pojo.User;
import com.ziyiou.file.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @Autowired
    UserService userService;

    @GetMapping("/test")
    public String getAllFiles(Model model) {
        // 只有管理员可以访问
        Subject subject = SecurityUtils.getSubject();
        if (!subject.hasRole("root")) {
            model.addAttribute("msg", "无权限");
            return "login";
        }

        // 可管理的用户目录，可上传的用户
        model.addAttribute("users", userService.getAllUsers());

        // 当前用户
        model.addAttribute("current", (User) subject.getPrincipal());

        return "test";
    }

}