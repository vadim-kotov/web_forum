package ru.webforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.webforum.model.User;
import ru.webforum.model.UserManager;

@Controller
@RequestMapping("/users")
public class AuthenticationController 
{
	private final UserManager userManager;
	
	@Autowired
	AuthenticationController(UserManager userManager)
	{
		this.userManager = userManager;
	}
	
	@GetMapping("/registration")
	public void registrationForm(Model model)
	{
		model.addAttribute("user", new User());
	}
	
	@PostMapping("/registration")
	public String newUser(@ModelAttribute("user") User user)
	{
		System.out.println(user);
		userManager.createUser(user);
		
		return "forum/forum";
	}
}
