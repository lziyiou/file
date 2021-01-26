package com.ziyiou.file.controller;

import com.ziyiou.file.mapper.UserMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home(Integer userId) {
        Subject subject = SecurityUtils.getSubject();
        if (subject.hasRole("root")) {
            return "redirect:/";
        }
        return "redirect:/files/" + userId;
    }

    @PostMapping("/login")
    public String login(String username, String password, Model model) {
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try {

            // 记住我
            token.setRememberMe(true);
            subject.login(token);

            if (subject.hasRole("root")) {
                return "redirect:/";
            } else {
                Integer userId = userMapper.getIdByUsername(username);
                return "redirect:/files/" + userId;
            }

        } catch (UnknownAccountException e) {
            model.addAttribute("msg", "用户名错误");
            return "/login";
        } catch (IncorrectCredentialsException e) {
            model.addAttribute("msg", "密码错误");
            return "/login";
        } catch (Exception e) {
            model.addAttribute("msg", "未知错误");
            e.printStackTrace();
            return "/login";
        }

    }

    @GetMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            subject.logout();
        }
        return "redirect:/login";
    }

    @GetMapping("/error")
    public String error() {
        return "/error";
    }
}