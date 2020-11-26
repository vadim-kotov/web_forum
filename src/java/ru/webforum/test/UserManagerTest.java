package ru.webforum.test;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;

import ru.webforum.model.User;
import ru.webforum.model.UserManager;
import ru.webforum.model.Section;
import ru.webforum.model.Message;
import ru.webforum.model.MessageObject;
import ru.webforum.model.Text;
import ru.webforum.model.banlist.*;
import ru.webforum.model.Topic;
import static ru.webforum.test.BanlistManagerTest.compBanlists;
import static ru.webforum.test.TopicManagerTest.compTopics;
import static ru.webforum.test.SectionManagerTest.getSubsections;
import static ru.webforum.test.SectionManagerTest.compForumTopics;
import static ru.webforum.test.MessageManagerTest.compMessages;
import static ru.webforum.test.MessageObjectManagerTest.compMessageObjects;
import static ru.webforum.model.UserManager.Filter;
import static ru.webforum.model.UserManager.UserInfo;
import static ru.webforum.model.UserManager.compUserId;
import static ru.webforum.model.UserManager.getFullFilter;
import static ru.webforum.model.SectionManager.ForumTopic;
import static ru.webforum.model.SectionManager.compTopicId;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import ru.webforum.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;

import static java.lang.Math.random;

import static org.junit.Assert.*;

public class UserManagerTest extends TestCase
{
    UserManager userManager = new UserManager();

    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite()
    {
        return new TestSuite(UserManagerTest.class);
    }

    static void compUsers(User expected, User actual)
    {
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getLogin(), actual.getLogin());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getRegistDate(), actual.getRegistDate());
        assertEquals(expected.getRights(), actual.getRights());
        assertEquals(expected.getAvatar(), actual.getAvatar());
    }

    static void compUserInfo(UserInfo expected, UserInfo actual)
    {
        compUsers(expected.getUserInfo(), actual.getUserInfo());
        assertEquals(expected.getMessageNum(), actual.getMessageNum());
        assertEquals(expected.getTopicNum(), actual.getTopicNum());
        assertEquals(expected.getCreatedTopicNum(), actual.getCreatedTopicNum());
    }

    public void testCreateUser()
    {
        String newLogin = "user_login";
        String newPassword = "user_password";
        Date newRegistDate = null;
        boolean newRights = true;
        String newAvatar = "user_avatar";

        //System.out.println("1st Query");
        User user = userManager.getUser(newLogin);
        if(user != null)
        {
            System.out.println("User with login " + newLogin + " already exist in table User");
            return;
        }

        User newUser = new User();
        newUser.setLogin(newLogin);
        newUser.setPassword(newPassword);
        newUser.setRegistDate(newRegistDate);
        newUser.setRights(newRights);
        newUser.setAvatar(newAvatar);

        //System.out.println("2nd Query");
        userManager.createUser(newUser);

        User expectedUser = new User();

        assertNotNull(newUser.getUserId());

        expectedUser.setUserId(newUser.getUserId());

        expectedUser.setLogin(newLogin);
        expectedUser.setPassword(newPassword);

        assertNotNull(newUser.getRegistDate());

        expectedUser.setRegistDate(newUser.getRegistDate());

        expectedUser.setRights(false);
        expectedUser.setAvatar(null);

        //System.out.println("3rd Query");
        User userInDb = userManager.getUser(newLogin);
        compUsers(expectedUser, userInDb); 

        userManager.deleteUser(newUser.getUserId());

        /************************************************** */

        int userNum;
        //System.out.println("4th Query");
        List<User> usersList = userManager.getUsers();
        if(usersList == null)
        {
            userNum = 0;
        }
        else
        {
            userNum = usersList.size();
        }

        /************************************************** */

        boolean exFlag = false;
        try
        {
            //System.out.println("5th Query");
            userManager.createUser(null);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }
        assertTrue(exFlag);

        //System.out.println("6th Query");
        usersList = userManager.getUsers();
        assertEquals(userNum, usersList.size());

        /************************************************** */

        newUser = new User();
        newUser.setLogin(null);
        exFlag = false;

        try
        {
            //System.out.println("7th Query");
            userManager.createUser(newUser);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }
        assertTrue(exFlag);

        //System.out.println("8th Query");
        usersList = userManager.getUsers();
        assertEquals(userNum, usersList.size());

        /************************************************** */

        newUser = new User();
        exFlag = false;

        //System.out.println("9th Query");
        usersList = userManager.getUsers();
        if(usersList == null)
        {
            System.out.println("Table Usr is empty");
            return;
        }
        int randomNum = (int) (random() * usersList.size());

        user = usersList.get(randomNum);
        newUser.setLogin(user.getLogin());
        try
        {
            //System.out.println("10th Query");
            userManager.createUser(newUser);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }
        assertTrue(exFlag);

        //System.out.println("11th Query");
        usersList = userManager.getUsers();
        assertEquals(userNum, usersList.size());
    }

    public void testGetBanlist()
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<User> users = session.createCriteria(User.class)
            .list();

        session.getTransaction().commit();

        if(users.size() == 0)
        {
            System.out.println("Table User is empty");
            session.getTransaction().rollback();
            return;
        }
        int randomNum = (int) (random() * users.size());
        //int randomNum = 13;
        User user = users.get(randomNum);

        List<Banlist> actualBanlist = userManager.getBanlist(user.getUserId());

        session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        session.refresh(user);
        SortedSet<Banlist> expectedBanlist = user.getBanlist();

        assertEquals(expectedBanlist.size(), actualBanlist.size());

        int i = 0;
        for(Banlist exp : expectedBanlist)
        {
            Banlist act = actualBanlist.get(i);
            compBanlists(exp, act);
            compUsers(exp.getModer(), act.getModer());
            if(exp.getClass() == BanMessage.class)
            {
                assertTrue((act.getClass() == BanMessage.class));
                compTopics(((BanMessage)exp).getTopic(), ((BanMessage)act).getTopic());
            }
        }

        session.getTransaction().commit();

        /*********************************************** */

        int freeId = userManager.getFreeId();
        boolean exFlag = false;
        try
        {
            userManager.getBanlist(freeId);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }
        assertTrue(exFlag);
    }

    public void testBan()
    {
        long banPeriod = 600 * 1000;
        String banSource = "ban_message_source";

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        BanMessage banMessage = new BanMessage();

        List<User> usersList = session.createCriteria(User.class)
            .list();
        int randomNum = (int) (random() * usersList.size());
        banMessage.setUser(usersList.get(randomNum));

        List<Topic> topicsList = session.createCriteria(Topic.class)
            .list();
        randomNum = (int) (random() * topicsList.size());
        banMessage.setTopic(topicsList.get(randomNum));

        List<User> modersList = session.createCriteria(User.class)
            .add(Property.forName("rights").eq(true))
            .list();
        randomNum = (int) (random() * modersList.size());
        banMessage.setModer(modersList.get(randomNum));
        
        Date banDate = new Date();
        banMessage.setBanDate(banDate);
        banMessage.setUnbanDate(new Date(banDate.getTime() + banPeriod));
        banMessage.setSource(banSource);

        session.getTransaction().commit();

        userManager.ban(banMessage);

        session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        BanMessage banMessageInDb = (BanMessage) session.get(Banlist.class, new Integer(banMessage.getBanlistId()));

        compBanlists((Banlist)banMessage, (Banlist)banMessageInDb);
        assertNotNull(banMessageInDb.getUser());
        assertEquals(banMessage.getUser().getUserId(), banMessageInDb.getUser().getUserId());
        assertNotNull(banMessageInDb.getTopic());
        assertEquals(banMessage.getTopic().getTopicId(), banMessageInDb.getTopic().getTopicId());
        assertNotNull(banMessageInDb.getModer());
        assertEquals(banMessage.getModer().getUserId(), banMessageInDb.getModer().getUserId());

        session.getTransaction().commit();

        userManager.deleteBan(banMessageInDb.getBanlistId());        

    /*********************************** */

        banPeriod = 700 * 1000;
        banSource = "ban_topic_source";

        session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        BanTopic banTopic = new BanTopic();

        randomNum = (int) (random() * usersList.size());
        banTopic.setUser(usersList.get(randomNum));

        randomNum = (int) (random() * modersList.size());
        banTopic.setModer(modersList.get(randomNum));
        
        banDate = new Date();
        banTopic.setBanDate(banDate);
        banTopic.setUnbanDate(new Date(banDate.getTime() + banPeriod));
        banTopic.setSource(banSource);

        session.getTransaction().commit();

        userManager.ban(banTopic);

        session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        BanTopic banTopicInDb = (BanTopic) session.get(Banlist.class, new Integer(banTopic.getBanlistId()));

        compBanlists((Banlist)banTopic, (Banlist)banTopicInDb);
        assertNotNull(banTopicInDb.getUser());
        assertEquals(banTopic.getUser().getUserId(), banTopicInDb.getUser().getUserId());
        assertNotNull(banTopicInDb.getModer());
        assertEquals(banTopic.getModer().getUserId(), banTopicInDb.getModer().getUserId());

        session.getTransaction().commit();

        userManager.deleteBan(banTopicInDb.getBanlistId());
    }

    /*
    public void testGetUsersInfo() throws CloneNotSupportedException
    {
        Filter filter = new Filter();

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Section> allSections = session.createCriteria(Section.class)
            .list();

        List<Integer> sectionIds = new ArrayList<Integer>();
        for(Section s : allSections)
        {
            if(((int) (random() * 2)) == 1)
            {
                sectionIds.add(s.getSectionId());
            }
        }   
        filter.setSectionIds(sectionIds);

        Date minDate = (Date) session.createCriteria(Topic.class)
            .setProjection(Projections.min("date"))
            .uniqueResult();
        Date _minDate = (Date) session.createCriteria(Message.class)
            .setProjection(Projections.min("date"))
            .uniqueResult();
        if(_minDate.before(minDate))
        {
            minDate = _minDate;
        }

        Date maxDate = (Date) session.createCriteria(Topic.class)
            .setProjection(Projections.max("date"))
            .uniqueResult();
        Date _maxDate = (Date) session.createCriteria(Message.class)
            .setProjection(Projections.max("date"))
            .uniqueResult();
        if(_maxDate.after(maxDate))
        {
            maxDate = _maxDate;
        }

        Date startDate = null;
        if(((int) (random() * 2)) == 1)
        {
            startDate = minDate;
            filter.setStartDate(null);
        }
        else
        {
            long randomNum = (long) (random() * (maxDate.getTime() - minDate.getTime() + 1) + minDate.getTime());
            startDate = new Date(randomNum);
            filter.setStartDate(startDate);
        }
        if(((int) (random() * 2)) == 1)
        {
            filter.setEndDate(null);
        }
        else
        {
            long randomNum = (long) (random() * (maxDate.getTime() - startDate.getTime() + 1) + startDate.getTime());
            filter.setEndDate(new Date(randomNum));
        }

        Date minRegistDate = (Date) session.createCriteria(User.class)
            .setProjection(Projections.min("registDate"))
            .uniqueResult();
        Date maxRegistDate = (Date) session.createCriteria(User.class)
            .setProjection(Projections.max("registDate"))
            .uniqueResult();
        
        Date startRegistDate = null;
        if(((int) (random() * 2)) == 1)
        {
            startRegistDate = minRegistDate;
            filter.setStartRegistDate(null);
        }
        else
        {
            long randomNum = (long) (random() * (maxRegistDate.getTime() - minRegistDate.getTime() + 1) + minRegistDate.getTime());
            startRegistDate = new Date(randomNum);
            filter.setStartRegistDate(startRegistDate);
        }
        if(((int) (random() * 2)) == 1)
        {
            filter.setEndRegistDate(null);
        }
        else
        {
            long randomNum = (long) (random() * (maxRegistDate.getTime() - startRegistDate.getTime() + 1) + startRegistDate.getTime());
            filter.setEndRegistDate(new Date(randomNum));
        }

        List<Object[]> messageNumList = session.createCriteria(Message.class)
            .setProjection(Projections.projectionList()
                .add(Projections.groupProperty("author.userId"))
                .add(Projections.rowCount()))
            .list();
        
        long maxMessageNum = 0;
        for(Object[] l : messageNumList)
        {
            if((long)l[1] > maxMessageNum)
            {
                maxMessageNum = (long)l[1];
            }
        }

        long minMessageNum;
        if(((int) (random() * 2)) == 1)
        {
            minMessageNum = 0;
            filter.setMinMessageNum(null);
        }
        else
        {
            minMessageNum = (long) (random() * maxMessageNum);
            filter.setMinMessageNum(minMessageNum);
        }

        if(((int) (random() * 2)) == 1)
        {
            filter.setMaxMessageNum(null);
        }
        else
        {
            maxMessageNum = (long) (random() * (maxMessageNum - minMessageNum + 1) + minMessageNum);
            filter.setMaxMessageNum(maxMessageNum);
        }

        session.getTransaction().commit();

        List<UserInfo> actualUI = userManager.getUsersInfo(filter);

        Filter _filter = getFullFilter(filter);
        User usr;

        session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        session.enableFilter("registryDateBounds")
            .setParameter("minDate", _filter.getStartRegistDate())
            .setParameter("maxDate", _filter.getEndRegistDate());

        session.enableFilter("activityDateBounds")
            .setParameter("minDate", _filter.getStartDate())
            .setParameter("maxDate", _filter.getEndDate());

        List<Section> sectionsList = session.createCriteria(Section.class)
            .add(Property.forName("sectionId").in(sectionIds))
            .list();

        Set<Section> subsections = getSubsections(new HashSet<Section>(sectionsList));

        HashMap<Integer, UserInfo> mapUI = new HashMap<Integer, UserInfo>();

        List<Topic> createdTopics = new ArrayList<Topic>();
        List<Topic> topics = new ArrayList<Topic>();
        for(Section s : subsections)
        {
            createdTopics.addAll(session.createCriteria(Topic.class)
                .add(Property.forName("section.sectionId").eq(s.getSectionId()))
                .list());
            
            topics.addAll(s.getTopics());
        }

        Set<User> users = new HashSet<User>();
        for(Topic t : createdTopics)
        {
            usr = (User) session.createCriteria(User.class)
                .add(Property.forName("userId").eq(t.getCreator().getUserId()))
                .uniqueResult();
            if(usr != null)
            {
                if(mapUI.containsKey(usr.getUserId()))
                {
                    UserInfo userInfo = mapUI.get(usr.getUserId());
                    userInfo.setCreatedTopicNum(userInfo.getCreatedTopicNum() + 1);
                }
                else
                {
                    UserInfo userInfo = new UserInfo(usr, 0, 0, 1);
                    mapUI.put(usr.getUserId(), userInfo);
                }
            }
        }

        for(Topic t : topics)
        {
            Set<Integer> topicMemberIds = new HashSet<Integer>();

            List<Message> messages = session.createCriteria(Message.class)
                .add(Property.forName("topic.topicId").eq(t.getTopicId()))
                .list();
            for(Message m : messages)
            {
                usr = (User) session.createCriteria(User.class)
                    .add(Property.forName("userId").eq(m.getAuthor().getUserId()))
                    .uniqueResult();
                if(usr != null)
                {
                    if(mapUI.containsKey(usr.getUserId()))
                    {
                        UserInfo userInfo = mapUI.get(usr.getUserId());
                        userInfo.setMessageNum(userInfo.getMessageNum() + 1);
                    }
                    else
                    {
                        UserInfo userInfo = new UserInfo(usr, 1, 0, 0);
                        mapUI.put(usr.getUserId(), userInfo);
                    }

                    topicMemberIds.add(m.getAuthor().getUserId());
                }
            }

            for(Integer id : topicMemberIds)
            {
                UserInfo userInfo = mapUI.get(id);
                userInfo.setTopicNum(userInfo.getTopicNum() + 1);
            }
        }

        List<UserInfo> _expectedUI = new ArrayList<UserInfo>(mapUI.values());
        List<UserInfo> expectedUI = new ArrayList<UserInfo>();
        for(UserInfo ui : _expectedUI)
        {
            if(ui.getMessageNum() >= _filter.getMinMessageNum() && ui.getMessageNum() <= _filter.getMaxMessageNum())
            {
                expectedUI.add(ui);
            }
        }

        Collections.sort(actualUI, new compUserId());
        Collections.sort(expectedUI, new compUserId());

        assertEquals(expectedUI.size(), actualUI.size());
        for(int i = 0; i < expectedUI.size(); i++)
        {
            compUserInfo(expectedUI.get(i), actualUI.get(i));
        }

        session.getTransaction().commit();
*/
/********************************************************* */
/*
        Filter filter2 = filter.clone();
        filter2.setSectionIds(null);
        List<UserInfo> actualUI2 = userManager.getUsersInfo(filter2);

        List<UserInfo> expectedUI2 = new ArrayList<UserInfo>();
        Filter _filter2 = getFullFilter(filter2);

        session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        session.enableFilter("registryDateBounds")
            .setParameter("minDate", _filter2.getStartRegistDate())
            .setParameter("maxDate", _filter2.getEndRegistDate());

        session.enableFilter("activityDateBounds")
            .setParameter("minDate", _filter2.getStartDate())
            .setParameter("maxDate", _filter2.getEndDate());

        List<User> users2 = session.createCriteria(User.class)
            .list();

        for(User u : users2)
        {
            List<Topic> createdTopics2 = session.createCriteria(Topic.class)
                .add(Property.forName("creator.userId").eq(u.getUserId()))
                .list();
            List<Message> messages2 = session.createCriteria(Message.class)
                .add(Property.forName("author.userId").eq(u.getUserId()))
                .list();
            Set<Topic> topics2 = new HashSet<Topic>();
            for(Message m : messages2)
            {
                topics2.add(m.getTopic());
            }
            if(messages2.size() >= _filter2.getMinMessageNum() && messages2.size() <= _filter2.getMaxMessageNum())
            {
                expectedUI2.add(new UserInfo(u, messages2.size(), topics2.size(), createdTopics2.size()));
            }
        }
        Collections.sort(actualUI2, new compUserId());
        Collections.sort(expectedUI2, new compUserId());

        assertEquals(expectedUI2.size(), actualUI2.size());
        for(int i = 0; i < expectedUI2.size(); i++)
        {
            compUserInfo(expectedUI2.get(i), actualUI2.get(i));
        }

        session.getTransaction().commit();
    }
*/
    public void testGetUserInfo()
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<User> usersList = session.createCriteria(User.class)
            .list();
        if(usersList.size() == 0)
        {
            System.out.println("table User is empty");
            session.getTransaction().rollback();
            return;
        }
        int randomNum = (int) (random() * usersList.size());
        User user = usersList.get(randomNum);

        Set<Message> messages = user.getMessages();
        Set<Topic> topics = new HashSet<Topic>();
        for(Message m : messages)
        {
            topics.add(m.getTopic());
        }
        Set<Topic> createdTopics = user.getTopics();

        UserInfo expectedUI = new UserInfo(user, messages.size(), topics.size(), createdTopics.size());

        session.getTransaction().commit();

        UserInfo actualUI = userManager.getUserInfo(user.getUserId());

        compUserInfo(expectedUI, actualUI);
    }

    public void testGetCreatedTopicList()
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Integer> userIdsList = session.createCriteria(User.class)
            .setProjection(Property.forName("userId"))
            .list();
        if(userIdsList.size() == 0)
        {
            System.out.println("Table User is empty");
            session.getTransaction().rollback();
            return;
        }
        int userId;
        List<Topic> topics;
        int randomNum = (int) (random() * userIdsList.size());
        //int randomNum = 2;
        userId = userIdsList.get(randomNum);

        topics = session.createCriteria(Topic.class)
            .add(Property.forName("creator.userId").eq(userId))
            .createAlias("creator", "c", Criteria.LEFT_JOIN)
            .list();

        List<ForumTopic> expected = new ArrayList<ForumTopic>();
        for(Topic t : topics)
        {
            ForumTopic Item = new ForumTopic();
            Item.setTopic(t);

            List<Message> messages = new ArrayList<Message>();
            messages.addAll(t.getMessages());
            Item.setMessageNum(messages.size());

            Set<User> allUsers = new HashSet<User>();
            for(Message m : messages)
            {
                User author = m.getAuthor();
                author.getLogin();                                  //!!!!!!!! load proxy-object
                allUsers.add(author);
            }
            Item.setUserNum(allUsers.size());

            if(messages.size() == 0)
            {
                Item.setLastMessage(null);
            }
            else
            {
                Item.setLastMessage(Collections.max(messages, Comparator.comparing(Message::getDate)));
            }

            expected.add(Item);
        }
        session.getTransaction().commit();

        Collections.sort(expected, new compTopicId());

        List<ForumTopic> actual = userManager.getCreatedTopicList(userId);
        Collections.sort(actual, new compTopicId());

        assertEquals(expected.size(), actual.size());
        for(int i = 0; i < expected.size(); i++)
        {
            compForumTopics(expected.get(i), actual.get(i));
        }

/******************************************************************** */

        userId = userManager.getFreeId();
        boolean exFlag = false;
        try
        {
            userManager.getCreatedTopicList(userId);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }
        assertTrue(exFlag);
    }

    public void testGetTopicList()
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Integer> userIdsList = session.createCriteria(User.class)
            .setProjection(Property.forName("userId"))
            .list();
        if(userIdsList.size() == 0)
        {
            System.out.println("Table User is empty");
            session.getTransaction().rollback();
            return;
        }
        int userId;
        List<Topic> topics;
        int randomNum = (int) (random() * userIdsList.size());
        //int randomNum = 0;
        userId = userIdsList.get(randomNum);

        List<Integer> topicIds = session.createCriteria(Message.class)
            .add(Property.forName("author.userId").eq(userId))
            .setProjection(Property.forName("topic.topicId"))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();

        topics = session.createCriteria(Topic.class)
            .add(Property.forName("topicId").in(topicIds))
            .createAlias("creator", "c", Criteria.LEFT_JOIN)
            .list();

        List<ForumTopic> expected = new ArrayList<ForumTopic>();
        for(Topic t : topics)
        {
            ForumTopic Item = new ForumTopic();
            Item.setTopic(t);

            List<Message> messages = new ArrayList<Message>();
            messages.addAll(t.getMessages());
            Item.setMessageNum(messages.size());

            Set<User> allUsers = new HashSet<User>();
            for(Message m : messages)
            {
                User author = m.getAuthor();
                author.getLogin();                                  //!!!!!!!! load proxy-object
                allUsers.add(author);
            }
            Item.setUserNum(allUsers.size());

            if(messages.size() == 0)
            {
                Item.setLastMessage(null);
            }
            else
            {
                Item.setLastMessage(Collections.max(messages, Comparator.comparing(Message::getDate)));
            }

            expected.add(Item);
        }
        session.getTransaction().commit();

        Collections.sort(expected, new compTopicId());

        List<ForumTopic> actual = userManager.getTopicList(userId);
        Collections.sort(actual, new compTopicId());

        assertEquals(expected.size(), actual.size());
        for(int i = 0; i < expected.size(); i++)
        {
            compForumTopics(expected.get(i), actual.get(i));
        }

/******************************************************************** */

        userId = userManager.getFreeId();
        boolean exFlag = false;
        try
        {
            userManager.getTopicList(userId);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }
        assertTrue(exFlag);
    }

    public void testGetMessageList()
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<User> users = session.createCriteria(User.class)
            .list();
        if(users.size() == 0)
        {
            System.out.println("Table User is empty");
            session.getTransaction().rollback();
            return;
        }
        int randomNum = (int) (random() * users.size());
        User user = users.get(randomNum);

        List<Message> expectedMessages = new ArrayList<Message>(user.getMessages());
        for(Message m : expectedMessages)
        {
            List<MessageObject> mesObjs = m.getMessageObjects();
            for(MessageObject mo : mesObjs)
            {
                if(mo.getClass() == Text.class)
                {
                    List<MessageObject> expMesObjs = new ArrayList<MessageObject>();
                    expMesObjs.add(mo);
                    m.setMessageObjects(expMesObjs);

                    break;
                }
            }
        }

        session.getTransaction().commit();

        List<Message> actualMessages = userManager.getMessageList(user.getUserId());

        assertEquals(expectedMessages.size(), actualMessages.size());
        for(int i = 0; i < expectedMessages.size(); i++)
        {
            Message expMes = expectedMessages.get(i);
            Message actMes = actualMessages.get(i);
            compMessages(expMes, actMes);

            List<MessageObject> expMOs = expMes.getMessageObjects();
            List<MessageObject> actMOs = actMes.getMessageObjects();
            assertEquals(1, actMOs.size());

            compMessageObjects(expMOs.get(0), actMOs.get(0));
        }
    }
}