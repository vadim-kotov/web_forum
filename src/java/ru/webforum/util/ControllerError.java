package ru.webforum.util;

public class ControllerError 
{
	private String errorMessage;
	
	public ControllerError(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public ControllerError()
	{
		this.errorMessage = "";
	}
	
	public String getErrorMessage() 
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) 
	{
		this.errorMessage = errorMessage;
	}
}
