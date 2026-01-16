package com.excelr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.excelr.entity.User;
import com.excelr.service.PostService;
import com.excelr.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
	
	
	 @Autowired
	    private UserService userService;
	 
	 @Autowired
	 private PostService postService;

	    @GetMapping("/register")
	    public String showRegisterPage(Model model) {
	        model.addAttribute("user", new User());
	        return "register";
	    }

	    
	    @PostMapping("/register")
	    public String registerUser(@ModelAttribute("user") User user, Model model) {
	        if (userService.isEmailAlreadyRegistered(user.getEmail())) {
	            model.addAttribute("error", "Email is already registered. Please login.");
	            return "register";  // Return back to registration page with error
	        }

	        userService.registerUser(user);
	        return "redirect:/login";
	    }


	    @GetMapping("/login")
	    public String index(Model model) {
	    	model.addAttribute("user", new User());
	        return "login";
	    }
	    @PostMapping("/login")
	    public String loginUser(@RequestParam String email, @RequestParam String pwd, HttpSession session, Model model) {
	        User user = userService.loginUser(email, pwd);
	        if (user != null) {
	            session.setAttribute("user", user);
	            return "redirect:/dashboard";
	        } else {
	            model.addAttribute("error", "Invalid Email or Password");
	            return "login";
	        }
	    }

	    @GetMapping("/logout")
	    public String logoutUser(HttpSession session) {
	        session.invalidate();
	        return "redirect:/login";
	    }
	}
