package ru.webforum.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController 
{

	@GetMapping("/test_spring.do")
	public String test()
	{
		return "test_spring";
	}
}
