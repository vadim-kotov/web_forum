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

}
