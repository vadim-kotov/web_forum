package ru.webforum.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import ru.webforum.model.Section;
import ru.webforum.model.SectionManager;
import ru.webforum.model.User;
import ru.webforum.model.UserManager;
import ru.webforum.model.SectionManager.ForumSection;
import ru.webforum.model.SectionManager.ForumTopic;
import ru.webforum.model.Topic;
import ru.webforum.util.ControllerError;

@Controller
@RequestMapping("/forum")
@SessionAttributes("upsection")
public class ForumController 
{
	private final SectionManager sectionManager;
	private final UserManager userManager;
	
	@Autowired
	ForumController(SectionManager sectionManager, UserManager userManager)
	{
		this.sectionManager = sectionManager;
		this.userManager = userManager;
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
		
		Section upsection;
		if(path.size() > 1)
		{
			upsection = path.get(path.size() - 2);
		}
		else
		{
			upsection = SectionManager.getRootSection();
		}
		model.addAttribute("upsection", upsection);
		
		List<ForumSection> sectionsList = sectionManager.getForumSectionList(id);
		for(ForumSection fs : sectionsList)
		{
			if(fs.getLastMessage() != null)
			{
				Topic lastMesTopic = fs.getLastMessage().getTopic(); 
				lastMesTopic.setSection(sectionManager.getTopicsSection(lastMesTopic.getTopicId()));
			}
		}
		model.addAttribute("subSects", sectionsList);
		
		List<ForumTopic> topicsList = sectionManager.getForumTopicList(id);
		model.addAttribute("subTopics", topicsList);
		
		return "forum/forum";
	}
	
	@GetMapping("")
	public String forumRootPage(Model model)
	{		
		Section section = SectionManager.getRootSection();
		model.addAttribute("section", section);
		
		List<ForumSection> sectionsList = sectionManager.getForumSectionList(null);
		for(ForumSection fs : sectionsList)
		{
			if(fs.getLastMessage() != null)
			{
				Topic lastMesTopic = fs.getLastMessage().getTopic(); 
				lastMesTopic.setSection(sectionManager.getTopicsSection(lastMesTopic.getTopicId()));
			}
		}
		model.addAttribute("subSects", sectionsList);
		
		model.addAttribute("path", null);
		
		model.addAttribute("upsection", SectionManager.getRootSection());
		
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
			
			return "error/queryError";
		}
		
		try
		{
			sectionManager.deleteSection(sectionId);
		}
		catch(ObjectNotFoundException e)
		{
			ControllerError error = new ControllerError("Невозможно удалить несуществующий раздел");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		
		Section upsection = (Section)model.getAttribute("upsection");
		if(upsection.getSectionId() == null)
		{
			return "redirect:/forum.do";
		}
		else
		{
			return "redirect:/forum/" + upsection.getSectionId() + ".do";
		}
	}
	
	@GetMapping("/new_section.do")
	public String newRootSectionPage(Model model)
	{
		model.addAttribute("section", SectionManager.getRootSection());
		
		model.addAttribute("newSection", new Section());
		
		return "forum/newSection";
	}
	
	@GetMapping("/{sectionId}/new_section.do")
	public String newSectionPage(@PathVariable("sectionId") Integer sectionId, Model model)
	{
		Section section = sectionManager.getSection(sectionId);
		if(section == null)
		{
			ControllerError error = new ControllerError("Невозможно создать раздел в несуществующем разделе");
			model.addAttribute("error", error);
				
			return "error/queryError";
		}
		model.addAttribute("section", section);
		
		model.addAttribute("newSection", new Section());
		
		return "forum/newSection";
	}
	
	@PostMapping("/new_section.do")
	public String newRootSection(@ModelAttribute("newSection") Section newSection)
	{
		sectionManager.createSection(newSection, null);

		return "redirect:/forum.do";
	}
	
	@PostMapping("/{sectionId}/new_section.do")
	public String newSection(@PathVariable("sectionId") Integer sectionId, @ModelAttribute("newSection") Section newSection)
	{
		sectionManager.createSection(newSection, sectionId);

		return "redirect:/forum/" + sectionId + ".do";
	}

	@GetMapping("/{sectionId}/new_topic.do")
	public String newTopicPage(@PathVariable("sectionId") Integer sectionId, Model model)
	{
		Section section = sectionManager.getSection(sectionId);
		if(section == null)
		{
			ControllerError error = new ControllerError("Невозможно создать тему в несуществующем разделе");
			model.addAttribute("error", error);
				
			return "error/queryError";
		}
		model.addAttribute("section", section);
		
		model.addAttribute("newTopic", new Topic());
		
		return "forum/newTopic";
	}

	@PostMapping("/{sectionId}/new_topic.do")
	public String newTopic(@PathVariable("sectionId") Integer sectionId, @ModelAttribute("newTopic") Topic newTopic, Principal principal)
	{
		User creator = userManager.getUser(principal.getName());
		newTopic.setCreator(creator);
		Section section = sectionManager.getSection(sectionId);
		newTopic.setSection(section);
		sectionManager.createTopic(newTopic);

		return "redirect:/forum/" + sectionId + ".do";
	}
}