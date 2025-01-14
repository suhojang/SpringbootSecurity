package com.jsh.security.example.controller;

import com.jsh.security.example.enums.role.UserRole;
import com.jsh.security.example.service.UserService;
import com.jsh.security.example.vo.UserVO;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/user")
@Slf4j
public class UserController {
    @Resource(name = "userService")
    private UserService userService;

    @NonNull
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/loginView")
    public String loginView(){
        return "user/login";
    }

    @GetMapping("/init")
    public String createAdmin(@ModelAttribute UserVO userVO){
        userVO.setUserEmail("user@naver.com");
        userVO.setUserPw(passwordEncoder.encode("test"));
        userVO.setRole(UserRole.USER);

        if (userService.createUser(userVO) == null){
            log.error("Create Admin Error");
        }

        userVO.setUserEmail("admin@naver.com");
        userVO.setUserPw(passwordEncoder.encode("test"));
        userVO.setRole(UserRole.ADMIN);
        if (userService.createUser(userVO) == null){
            log.error("Create Admin Error");
        }
        return "redirect:/index";
    }

    @PostMapping(value = "/login")
    public String login(HttpServletRequest request, RedirectAttributes redirectAttributes, @ModelAttribute UserVO userVO){
        String userPw = userVO.getUserPw();
        userVO = userService.findUserByUserEmail(userVO.getUserEmail());
        if(userVO == null || !passwordEncoder.matches(userPw, userVO.getUserPw())){
            redirectAttributes.addFlashAttribute("rsMsg", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "redirect:/user/loginView";
        }
        request.getSession().setAttribute("userVO", userVO);
        return "redirect:/index";
    }
}
