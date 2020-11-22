package ru.webforum.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.webforum.model.User;
import ru.webforum.model.UserManager;
import ru.webforum.util.ControllerError;

@Controller
@RequestMapping("/users")
public class UsersController 
{
	private final UserManager userManager;
	
	@Autowired
	UsersController(UserManager userManager)
	{
		this.userManager = userManager;
	}
	
	@GetMapping("/user_{userId}.do")
	public String userPage(@PathVariable("userId") Integer userId, Model model)
	{
		User user = userManager.getUser(userId);
		if(user == null)
		{
			ControllerError error = new ControllerError("Пользователь не найден");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		
		model.addAttribute("user", user);
		
		return "users/user";
	}
	
	@GetMapping("/account.do")
	public String accountPage(Model model, Principal principal)
	{
		User user = userManager.getUser(principal.getName());
		model.addAttribute("user", user);
		
		return "users/user";
	}
}
