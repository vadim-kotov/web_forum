package ru.webforum.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.webforum.model.Section;
import ru.webforum.model.SectionManager;
import ru.webforum.model.User;
import ru.webforum.model.UserManager;
import ru.webforum.model.UserManager.Filter;
import ru.webforum.model.UserManager.UserInfo;
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
	
	@GetMapping("/users_list.do")
	public String usersListPage(
			@RequestParam(required=false, defaultValue="false", value="filterEnabled") boolean filterEnabled, 
			@ModelAttribute("filter") Filter filter, 
			Model model) throws CloneNotSupportedException
	{
		Section section = SectionManager.getRootSection();
		model.addAttribute("section", section);
		/*
		if(!filter.equals(null))
		{
			System.out.println(filter.getStartDate());
			System.out.println(filter.getEndDate());
			System.out.println(filter.getMinMessageNum());
			System.out.println(filter.getMaxMessageNum());
			System.out.println(filter.getStartRegistDate());
			System.out.println(filter.getEndRegistDate());
		}
		*/
		List<UserInfo> usersList = null;
		if(!filterEnabled)
		{
			filter = new Filter();
		}
		filter = new Filter();
		
		usersList = userManager.getUsersInfo(filter);		
		model.addAttribute("usersList", usersList);
		
		model.addAttribute("filter", filter);
		
		return "users/usersList";
	}
	
	@GetMapping("/users_list/{sectionId}.do")
	public String usersListRootPage(@PathVariable("secitionId") Integer sectionId, Model model)
	{
		return "users/usersList";
	}
}
