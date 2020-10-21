package ru.webforum.model.banlist;

import ru.webforum.model.Topic;

public class BanMessage extends Banlist
{
    private Topic topic;

    public Topic getTopic() { return this.topic; }
    public void setTopic(Topic topic) { this.topic = topic; }
}