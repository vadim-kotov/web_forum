package ru.webforum.controller;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.webforum.model.Section;
import ru.webforum.model.SectionManager;
import ru.webforum.model.Topic;
import ru.webforum.model.TopicManager;
import ru.webforum.model.TopicManager.TopicMessage;
import ru.webforum.util.ControllerError;

@Controller
@RequestMapping("/forum")
public class TopicController 
{
	private final SectionManager sectionManager;
	private final TopicManager topicManager;
	
	@Autowired
	public TopicController(SectionManager sectionManager, TopicManager topicManager) 
	{
		this.sectionManager = sectionManager;
		this.topicManager = topicManager;
	}
	
	@GetMapping("/{sectionId}/topic_{topicId}.do")
	public String topicPage(@PathVariable("sectionId") Integer sectionId, @PathVariable("topicId") Integer topicId, Model model)
	{
		Section section = sectionManager.getSection(sectionId);
		if(section == null)
		{
			ControllerError error = new ControllerError("Раздел не найден");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		model.addAttribute("section", section);
		
		Topic topic = topicManager.getTopic(topicId);
		if(topic == null)
		{
			ControllerError error = new ControllerError("Раздел не найден");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		model.addAttribute("topic", topic);
		
		List<Section> path = topicManager.getPath(topicId);
		model.addAttribute("path", path);
		
		List<TopicMessage> topicMessages = topicManager.getTopicMessageList(topicId);
		model.addAttribute("topicMessages", topicMessages);
		
		return "forum/topic/topic";
	}

	@PostMapping("/{sectionId}/topic_{topicId}/delete_topic.do")
	public String deleteTopic(@PathVariable("sectionId") Integer sectionId, @PathVariable("topicId") Integer topicId, Model model)
	{
		if(topicId == null)
		{
			ControllerError error = new ControllerError("Невозможно удалить несуществующую тему");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		
		try
		{
			sectionManager.deleteTopic(topicId);
		}
		catch(ObjectNotFoundException e)
		{
			ControllerError error = new ControllerError("Невозможно удалить несуществующую тему");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		
		Section section = sectionManager.getSection(sectionId);
		return "redirect:/forum/" + section.getSectionId() + ".do";
	}
	
	@PostMapping("/{sectionId}/topic_{topicId}/delete_message.do")
	public String deleteMessage(@PathVariable("sectionId") Integer sectionId, @PathVariable("topicId") Integer topicId, @RequestParam("messageId") Integer messageId, Model model)
	{
		try
		{
			topicManager.deleteMessage(messageId);
		}
		catch(ObjectNotFoundException e)
		{
			ControllerError error = new ControllerError("Невозможно удалить несуществующее сообщение");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		
		return "redirect:/forum/" + sectionId + "/" + "topic_" + topicId + ".do";
	}
}
