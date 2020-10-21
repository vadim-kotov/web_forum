package ru.webforum.model;

import java.util.Date;
import java.util.Set;

public class Topic
{
    private int topicId;
    private String name;
    private Date date;

    private Section section = null;
    private User creator = null;
    private Set<Message> messages = null;

    public int getTopicId(){ return this.topicId; }
    public void setTopicId(int topicId){ this.topicId = topicId; }

    public String getName(){ return this.name; }
    public void setName(String name){ this.name = name; }

    public Date getDate(){ return this.date; }
    public void setDate(Date date){ this.date = date; }


    public Section getSection(){ return this.section; }
    public void setSection(Section section){ this.section = section; }

    public User getCreator(){ return this.creator; }
    public void setCreator(User creator){ this.creator = creator; }

    public Set<Message> getMessages(){ return this.messages; }
    public void setMessages(Set<Message> messages){ this.messages = messages; }
}