package ru.webforum.controller;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

@Controller
public class CustomDateEditor extends PropertyEditorSupport
{	
	@Override
	public void setAsText(String value) 
	{
		try
		{
			setValue(new SimpleDateFormat("yyyy-MM-dd").parse(value));
		}
		catch (ParseException e)
		{
			setValue(null);
		}
	}
}