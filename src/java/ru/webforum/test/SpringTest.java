package ru.webforum.test;

import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringTest
{
    public static void main(String args[]) throws Exception
    {
        FileSystemXmlApplicationContext factory = 
            new FileSystemXmlApplicationContext("src/conf/springtest-applicationcontext.xml");

        SpringTestMessage stm = (SpringTestMessage) factory
                .getBean("springtestmessage");
    }
}