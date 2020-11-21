package ru.webforum.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import ru.webforum.model.Image;
import ru.webforum.model.Message;
import ru.webforum.model.MessageObject;
import ru.webforum.model.Section;
import ru.webforum.model.SectionManager;
import ru.webforum.model.Text;
import ru.webforum.model.Topic;
import ru.webforum.model.TopicManager;
import ru.webforum.model.TopicManager.TopicMessage;
import ru.webforum.model.User;
import ru.webforum.model.UserManager;
import ru.webforum.service.FileService;
import ru.webforum.util.ControllerError;

@Controller
@RequestMapping("/forum")
public class TopicController 
{
	private final SectionManager sectionManager;
	private final TopicManager topicManager;
	private final UserManager userManager;
	private final FileService fileService;
	
	@Autowired
	public TopicController(SectionManager sectionManager, TopicManager topicManager, UserManager userManager, FileService fileService) 
	{
		this.sectionManager = sectionManager;
		this.topicManager = topicManager;
		this.userManager = userManager;
		this.fileService = fileService;
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
		
		Topic topic = topicManager.getTopic(topicId);
		if(topic == null)
		{
			ControllerError error = new ControllerError("Тема не найдена");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		if(topic.getSection().getSectionId() != sectionId)
		{
			ControllerError error = new ControllerError("Страница не найдена");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		
		model.addAttribute("section", section);
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
	public String deleteMessage(
			@PathVariable("sectionId") Integer sectionId, 
			@PathVariable("topicId") Integer topicId, 
			@RequestParam("messageId") Integer messageId,
			@RequestParam(value="filename", required=false, defaultValue="") String filename,
			Model model) throws IOException
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
		
		if(!filename.isEmpty())
		{
			try
			{
				fileService.deleteFile(filename);
			}
			catch(IOException e)
			{
				ControllerError error = new ControllerError("Возникла ошибка при удалении сообщения");
				model.addAttribute("error", error);
				
				return "error/queryError";
			}
		}
		
		return "redirect:/forum/" + sectionId + "/" + "topic_" + topicId + ".do";
	}
	
	@GetMapping("/{sectionId}/topic_{topicId}/new_message.do")
	public String newMessagePage(@PathVariable("sectionId") Integer sectionId, @PathVariable("topicId") Integer topicId, Model model)
	{		
		Section section = sectionManager.getSection(sectionId);
		if(section == null)
		{
			ControllerError error = new ControllerError("Раздел не найден");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		Topic topic = topicManager.getTopic(topicId);
		if(topic == null)
		{
			ControllerError error = new ControllerError("Тема не найдена");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		if(topic.getSection().getSectionId() != sectionId)
		{
			ControllerError error = new ControllerError("Страница не найдена");
			model.addAttribute("error", error);
			
			return "error/queryError";
		}
		model.addAttribute("section", section);
		model.addAttribute("topic", topic);
		
		model.addAttribute("newMessage", new Message());
		return "forum/topic/newMessage";
	}
	
	@PostMapping("/{sectionId}/topic_{topicId}/new_message.do")
	public String newMessage(
			@PathVariable("sectionId") Integer sectionId,
			@PathVariable("topicId") Integer topicId, 
			@ModelAttribute("newMessage") Message newMessage, 
			@RequestParam("messageText") String messageText,
			@RequestParam("messageFile") MultipartFile messageFile, 
			Principal principal) throws IOException
	{
		User author = userManager.getUser(principal.getName());
		newMessage.setAuthor(author);
		
		Topic topic = topicManager.getTopic(topicId);
		newMessage.setTopic(topic);
		
		List<MessageObject> messageObjects = new ArrayList<MessageObject>();
		if(messageText != null && !messageText.isEmpty())
		{
			Text text = new Text();
			text.setValue(messageText);
			text.setMessage(newMessage);
			messageObjects.add(text);
		}
		
		if(messageFile != null && !messageFile.getOriginalFilename().isEmpty())
		{
			String resultFilename = fileService.uploadFile(messageFile);
			Image image = new Image();
			image.setValue(resultFilename);
			image.setMessage(newMessage);
			messageObjects.add(image);
		}
		
		newMessage.setMessageObjects(messageObjects);
		
		newMessage.setDate(new Date());
		
		topicManager.createMessage(newMessage);
		
		return "redirect:/forum/" + sectionId + "/topic_" + topicId + ".do";
	}
}
