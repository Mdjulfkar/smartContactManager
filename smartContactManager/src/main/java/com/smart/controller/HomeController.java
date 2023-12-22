package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.userRepository;
import com.smart.entities.User;
import com.smart.helper.message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private userRepository userRepository;

	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "home Page");
		return "home";
	}

	@RequestMapping("about")
	public String about(Model model) {
		model.addAttribute("title", "About page");
		return "about";
	}

	@RequestMapping("signup")
	public String signup(Model model) {
		model.addAttribute("title", "signup page");
		model.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, Errors errors, Model model,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, HttpSession session) {

		try {

			if (!agreement) {
				System.out.println("you have not checked terms and conditions!!");
				throw new Exception("you have not checked terms and conditions!!");

			}
			
			if(errors.hasErrors()) {
				model.addAttribute("user",user);
				System.out.println(errors);
				return "signup";
			}

			user.setEnabled(true);
			user.setImageURL("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			User result = userRepository.save(user);
			System.out.println(result);
			model.addAttribute("user", new User());

			session.setAttribute("message", new message("Successfully registered !!", "alert-success"));
			return "signup";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new message("Something went wrong !!" + e.getMessage(), "alert-warning"));

			return "signup";
		}

	}
	
	@RequestMapping("/login")
	public String custumsignin(Model model) {
		model.addAttribute("title","login");
		return "login1";
	}
	

}
