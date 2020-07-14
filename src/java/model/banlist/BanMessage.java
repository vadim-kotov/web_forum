package model.banlist;

import model.Topic;

public class BanMessage extends Banlist
{
    private Topic topic;

    public Topic getTopic() { return this.topic; }
    public void setTopic(Topic topic) { this.topic = topic; }
}