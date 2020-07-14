package model.banlist;

import model.User;

import java.util.Date;

public class Banlist implements Comparable<Banlist>
{
    protected int banlistId;
    protected Date banDate;
    protected Date unbanDate;
    protected String source;

    protected User user;
    protected User moder;


    public int getBanlistId() { return this.banlistId; }
    public void setBanlistId(int banlistId) { this.banlistId = banlistId; }

    public Date getBanDate() { return this.banDate; }
    public void setBanDate(Date banDate) { this.banDate = banDate; }

    public Date getUnbanDate() { return this.unbanDate; }
    public void setUnbanDate(Date unbanDate) { this.unbanDate = unbanDate; }

    public String getSource() { return this.source; }
    public void setSource(String source) { this.source = source; }


    public User getUser() { return this.user; }
    public void setUser(User user) { this.user = user; }

    public User getModer() { return this.moder; }
    public void setModer(User moder) { this.moder = moder; }

    
    public int compareTo(Banlist banlist)
    {
        return banDate.compareTo(banlist.getBanDate());
    }
}