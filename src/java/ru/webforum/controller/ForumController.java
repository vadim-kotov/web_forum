package ru.webforum.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import ru.webforum.model.Section;
import ru.webforum.model.SectionManager;
import ru.webforum.model.User;
import ru.webforum.model.SectionManager.ForumSection;
import ru.webforum.model.SectionManager.ForumTopic;
import ru.webforum.util.ControllerError;

@Controller
@RequestMapping("/forum")
@SessionAttributes("upsection")
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
		model.addAttribute("section", section);
		
		List<Section> path = sectionManager.getPath(id);
		model.addAttribute("path", path);
		
		Section upsection = (Section)new Object();
		/*if(path.size() == 1)
		{
			upsection = "";
		}*/
		if(path.size() > 1)
		{
			upsection = path.get(path.size() - 2);
		}
		model.addAttribute("upsection", upsection);
		
		List<ForumSection> sectionsList = sectionManager.getForumSectionList(id);
		model.addAttribute("subSects", sectionsList);
		
		List<ForumTopic> topicsList = sectionManager.getForumTopicList(id);
		model.addAttribute("subTopics", topicsList);
		
		return "forum/forum";
	}
	
	@GetMapping("")
	public String forumRootPage(Model model)
	{		
		Section section = new Section(null, "Форум", "");
		model.addAttribute("section", section);
		
		List<ForumSection> sectionsList = sectionManager.getForumSectionList(null);
		model.addAttribute("subSects", sectionsList);
		
		model.addAttribute("path", null);
		
		model.addAttribute("upsection", new Object());
		
		model.addAttribute("subTopics", null);
		
		return "forum/forum";
	}
	
	@PostMapping("/{sectionId}/delete_section.do")
	public String deleteSection(@PathVariable("sectionId") Integer sectionId, Model model)
	{
		if(sectionId == null)
		{
			ControllerError error = new ControllerError("Невозможно удалить несуществующий раздел");
			model.addAttribute("error", error);
				
			//return "error/queryError";
		}
		
		Section upsection = (Section)model.getAttribute("upsection");
		//System.out.println("UPSECTION ID - " + upsection.getSectionId());
		if(upsection == null)
		{
			return "redirect:/forum.do";
		}
		else
		{
			return "redirect:/forum/" + upsection.getSectionId() + ".do";
		}
		//sectionManager.deleteSection(sectionId);
	}
}