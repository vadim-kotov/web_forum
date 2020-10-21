package ru.webforum.test;

public class SpringTestMessage
{
    public String message;

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void printMessage()
    {
        System.out.println(this.message);
    }
}