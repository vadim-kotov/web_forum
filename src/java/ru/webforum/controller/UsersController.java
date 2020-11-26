package ru.webforum.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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
	private final SectionManager sectionManager;
	
	@Autowired
	UsersController(UserManager userManager, SectionManager sectionManager)
	{
		this.userManager = userManager;
		this.sectionManager = sectionManager;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) 
	{
		binder.registerCustomEditor(Date.class, new CustomDateEditor());
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
	public String usersListPage(@ModelAttribute("filter") Filter filter, Model model) throws CloneNotSupportedException
	{
		Section section = SectionManager.getRootSection();
		model.addAttribute("section", section);
		
		List<UserInfo> usersList = userManager.getUsersInfo(filter, null);		
		model.addAttribute("usersList", usersList);

		model.addAttribute("filter", filter);	
		
		return "users/usersList";
	}
	
	@GetMapping("/users_list/section_{sectionId}.do")
	public String usersListRootPage(
			@PathVariable("sectionId") Integer sectionId, 
			@ModelAttribute("filter") Filter filter,
			Model model) throws CloneNotSupportedException
	{
		Section section = sectionManager.getSection(sectionId);
		if(section == null)
		{
			ControllerError error = new ControllerError("Раздел не найден");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		model.addAttribute("section", section);
		
		List<Integer> sectionIdsList = new ArrayList<Integer>();
		sectionIdsList.add(sectionId);
		List<UserInfo> usersList = userManager.getUsersInfo(filter, sectionIdsList);		
		model.addAttribute("usersList", usersList);

		model.addAttribute("filter", filter);
		
		return "users/usersList";
	}
}
