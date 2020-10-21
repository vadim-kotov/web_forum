package ru.webforum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.webforum.model.Section;
import ru.webforum.model.SectionManager;
import ru.webforum.model.SectionManager.ForumSection;
import ru.webforum.util.ControllerError;

@Controller
@RequestMapping("/forum")
public class ForumController 
{
	private final SectionManager sectionManager;
	
	@Autowired
	ForumController(SectionManager sectionManager)
	{
		this.sectionManager = sectionManager;
	}
	
	@GetMapping("/{sectionId}")
	public String forumPage(@PathVariable("sectionId") Integer id, Model model)
	{
		Section section = sectionManager.getSection(id);
		if(section == null)
		{
			ControllerError error = new ControllerError("Раздел не найден");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		
		List<ForumSection> sectionsList = sectionManager.getForumSectionList(id);
		
		model.addAttribute("subSects", sectionsList);
		model.addAttribute("section", section);
		
		return "forum/forum";
	}	
}