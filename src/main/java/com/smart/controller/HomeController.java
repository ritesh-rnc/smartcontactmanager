package com.smart.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repository.UserRepository;

@Controller
public class HomeController {

	@Autowired
	private UserRepository repo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@RequestMapping("/home")
	public String home(Model model) {
		model.addAttribute("title", "Home-Smart Contact manager");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About-Smart Contact manager");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Register-Smart Contact manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	// handler for register user
	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@ModelAttribute("user") User user,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,HttpSession session) 
	{
		try {
			
			//if not agreed the checkbox
			if (!agreement) {
				System.out.println("you have not agreed term n condition");
				throw new Exception("you have not agreed term n condition");
			}
			
			
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agreement :" + agreement);
			System.out.println("User :" + user);
			
			this.repo.save(user);
			
			model.addAttribute("user", new User());
			session.setAttribute("msg",new Message("sucessFully Registered !!" ,"alert-success"));
			
			return "signup";
			
		} catch(Exception e)
		{
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("msg",new Message("something went wrong !!" +e.getMessage(),"alert-danger"));
			return "signup";
		}
		
		
	}

	//handler for custom login
		@RequestMapping("/signin")
		public String customLogn(Model model)
		{
			model.addAttribute("title","Login Page");
			return "login";
		}
		
	
	
}
