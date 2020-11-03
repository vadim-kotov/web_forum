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
@RequestMapping("/users")
public class AuthenticationController 
{
	private final UserManager userManager;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	AuthenticationController(UserManager userManager, BCryptPasswordEncoder bCryptPasswordEncoder)
	{
		this.userManager = userManager;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@PostMapping("/registration")
	public String newUser(@ModelAttribute("user") User user)
	{
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userManager.createUser(user);
		
		return "redirect:/forum.do";
	}
}
