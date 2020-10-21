package ru.webforum.model;

import ru.webforum.model.banlist.Banlist;

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;

public class User implements Cloneable
{
    private int userId;
    private String login;
    private String password;
    private Date registDate;
    private boolean rights;
    private String avatar;

    Set<Topic> topics = null;
    Set<Message> messages = null;

    SortedSet<Banlist> banlist;

    public int getUserId() { return this.userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getLogin() { return this.login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return this.password; }
    public void setPassword(String password) { this.password = password; }

    public Date getRegistDate() { return this.registDate; }
    public void setRegistDate(Date registDate) { this.registDate = registDate; }

    public boolean getRights() { return this.rights; }
    public void setRights(boolean rights) { this.rights = rights; }

    public String getAvatar() { return this.avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }


    public Set<Topic> getTopics() { return this.topics; }
    public void setTopics(Set<Topic> topics) { this.topics = topics; }

    public Set<Message> getMessages() { return this.messages; }
    public void setMessages(Set<Message> messages) { this.messages = messages; }


    public SortedSet<Banlist> getBanlist() { return this.banlist; }
    public void setBanlist(SortedSet<Banlist> banlist) { this.banlist = banlist; }

    public User clone() throws CloneNotSupportedException
    {
        User user = (User) super.clone();
        user.registDate = (Date) this.registDate.clone();
        return user;
    }
}