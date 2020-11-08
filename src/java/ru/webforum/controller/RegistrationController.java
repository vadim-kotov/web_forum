package ru.webforum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.webforum.model.User;
import ru.webforum.model.UserManager;

@Controller
@RequestMapping("/users/registration")
public class RegistrationController 
{
	private UserManager userManager;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	RegistrationController(UserManager userManager, BCryptPasswordEncoder bCryptPasswordEncoder)
	{
		this.userManager = userManager;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@GetMapping("")
	public String registrationPage(Model model)
	{
		model.addAttribute("user", new User());

		return "users/registration";
	}

	@PostMapping("")
	public String newUser(@ModelAttribute("user") User user)
	{
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userManager.createUser(user);
		
		return "redirect:/forum.do";
	}
}
