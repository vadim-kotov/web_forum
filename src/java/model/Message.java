package model;

import java.util.Date;
import java.util.Set;
import java.util.List;
public class Message
{
    private int messageId;
    private Date date;
    private String header;

    private Message upmessage = null;
    private Set<Message> downmessages = null;
    private Topic topic = null;
    private User author = null;

    private List<MessageObject> messageObjects;

    public int getMessageId(){ return this.messageId; }
    public void setMessageId(int messageId) { this.messageId = messageId; }

    public Date getDate(){ return this.date; }
    public void setDate(Date date) { this.date = date; }
    
    public String getHeader(){ return this.header; }
    public void setHeader(String header) { this.header = header; }


    public Message getUpmessage() { return this.upmessage; }
    public void setUpmessage(Message upmessage) { this.upmessage = upmessage; }

    public Set<Message> getDownmessages() { return this.downmessages; }
    public void setDownmessages(Set<Message> downmessages) { this.downmessages = downmessages; }

    public Topic getTopic() { return this.topic; }
    public void setTopic(Topic topic) { this.topic = topic; }

    public User getAuthor() { return this.author; }
    public void setAuthor(User author) { this.author = author; }


    public List<MessageObject> getMessageObjects() { return this.messageObjects; }
    public void setMessageObjects(List<MessageObject> messageObjects) { this.messageObjects = messageObjects; }
}