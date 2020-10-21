package ru.webforum.model;

import ru.webforum.model.banlist.*;
import static ru.webforum.model.SectionManager.ForumTopic;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Comparator;
import java.util.Collections;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import ru.webforum.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;

public class UserManager
{
    /*
        Returns maximum id number in table Section
        Returns null if table Section is empty
    */
    public Integer getMaxId()
    {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Integer maxId;
        try
        {
            maxId = (Integer) session.createCriteria(User.class)
                .setProjection(Property.forName("userId").max())
                .uniqueResult();

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        return maxId;
    }

    public int getFreeId()
    {
        Integer _freeId = getMaxId();
        if(_freeId == null)
        {
            return 1;
        }
        else
        {
            return _freeId.intValue() + 1;
        }
    }

    public User getUser(String login)
    {
    
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        User user = null;
        try
        {
            user = (User) session.createQuery("from User where login = ?0")
                .setString(0, login).uniqueResult();
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        return user;
    }

    public User getUser(int userId)
    {
    
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        User user = null;
        try
        {
            user = session.get(User.class, new Integer(userId));
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        return user;
    }

    public List getUsers()
    {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        List usersList = null;
        try
        {
            usersList = session.createQuery("from User").list();
            session.getTransaction().commit();
        }

        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        return usersList;
    }

    public void createUser(User user)
    {
        if(user == null)
        {
            throw new IllegalArgumentException("user is null");
        }

        if(user.getLogin() == null)
        {
            throw new IllegalArgumentException("Login is null");
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        try
        {
            User userInDb = (User) session.createQuery("from User where login = ?0")
                .setString(0, user.getLogin()).uniqueResult();
        

            if(userInDb != null)
            {
                session.getTransaction().rollback();
                throw new IllegalArgumentException("User with login " + user.getLogin() + " already exist in table User");
            }

            user.setRegistDate(new Date());
            user.setRights(false);
            user.setAvatar(null);
    
            session.save(user);
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public void deleteUser(int userId)
    {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        try
        {
            session.delete(session.load(User.class, new Integer(userId)));
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
    }

/*
    Gets all bans given to user
    Returns List of Banlists with topic and moder properties fetched

    Throws IllegalArgumentException if user with userId doesn't exist in database
*/
    public List<Banlist> getBanlist(int userId)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Banlist> banlist;
        try
        {
            User user = session.get(User.class, new Integer(userId));
            if(user == null)
            {
                session.getTransaction().rollback();
                throw new IllegalArgumentException("Row with id " + userId + " doesn't exist in table User");
            }

            banlist = session.createCriteria(Banlist.class)
                .add(Property.forName("user.userId").eq(userId))
                .createAlias("topic", "t", Criteria.LEFT_JOIN)
                .createAlias("moder", "m", Criteria.LEFT_JOIN)
                .list();

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        Collections.sort(banlist, Comparator.comparing(Banlist::getBanDate));
        return banlist;
    }

    public <T extends Banlist> void ban(T banlist)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        try
        { 
            session.save(banlist);
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }        
    }

    public void deleteBan(int banlistId)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        try
        {
            session.delete(session.load(Banlist.class, new Integer(banlistId)));
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
    }

    static public class Filter implements Cloneable
    {
        private List<Integer> sectionIds = null;
        private Date startDate = null, endDate = null;
        private Long minMessageNum = null, maxMessageNum = null;
        private Date startRegistDate = null, endRegistDate = null;

        public List<Integer> getSectionIds() { return this.sectionIds; }
        public void setSectionIds(List<Integer> sectionIds) { this.sectionIds = sectionIds; }
        public Date getStartDate() { return this.startDate; }
        public void setStartDate(Date startDate) { this.startDate = startDate; }
        public Date getEndDate() { return this.endDate; }
        public void setEndDate(Date endDate) { this.endDate = endDate; }
        public Long getMinMessageNum() { return this.minMessageNum; }
        public void setMinMessageNum(Long minMessageNum) { this.minMessageNum = minMessageNum; }
        public Long getMaxMessageNum() { return this.maxMessageNum; }
        public void setMaxMessageNum(Long maxMessageNum) { this.maxMessageNum = maxMessageNum; }
        public Date getStartRegistDate() { return this.startRegistDate; }
        public void setStartRegistDate(Date startRegistDate) { this.startRegistDate = startRegistDate; }
        public Date getEndRegistDate() { return this.endRegistDate; }
        public void setEndRegistDate(Date endRegistDate) { this.endRegistDate = endRegistDate; }

        public Filter clone() throws CloneNotSupportedException
        {
            return (Filter) super.clone();
        }
    }

    static public class UserInfo
    {
        private User userInfo;
        private long messageNum;
        private long topicNum;
        private long createdTopicNum;

        public UserInfo()
        {
            userInfo = null;
            messageNum = 0;
            topicNum = 0;
            createdTopicNum = 0;
        }

        public UserInfo(User user, int mn, int tn, int ctn)
        {
            userInfo = user;
            messageNum = mn;
            topicNum = tn;
            createdTopicNum = ctn;
        }

        public User getUserInfo() { return this.userInfo; }
        public void setUserInfo(User userInfo) { this.userInfo = userInfo; }
        public long getMessageNum() { return this.messageNum; }
        public void setMessageNum(long messageNum) { this.messageNum = messageNum; }
        public long getTopicNum() { return this.topicNum; }
        public void setTopicNum(long topicNum) { this.topicNum = topicNum; }
        public long getCreatedTopicNum() { return this.createdTopicNum; }
        public void setCreatedTopicNum(long createdTopicNum) { this.createdTopicNum = createdTopicNum; }
    }

    static public class compUserId implements Comparator<UserInfo>
    {
        public int compare(UserInfo a, UserInfo b)
        {
            int aId = a.getUserInfo().getUserId();
            int bId = b.getUserInfo().getUserId();

            if(aId == bId)
                return 0;
            else if(aId > bId)
                return 1;
            else if(aId < bId)
                return -1;
                        
            return 0;
        }
    }

    static public Filter getFullFilter(Filter filter) throws CloneNotSupportedException
    {
        final long maxDate = 100000000000000l;

        Filter _filter = filter.clone();

        if(_filter.getStartDate() == null)
        {
            _filter.setStartDate(new Date(0));
        }
        if(_filter.getEndDate() == null)
        {
            _filter.setEndDate(new Date(maxDate));
        }
        if(_filter.getMinMessageNum() == null)
        {
            _filter.setMinMessageNum(0L);
        }
        if(_filter.getMaxMessageNum() == null)
        {
            _filter.setMaxMessageNum(Long.MAX_VALUE);
        }
        if(_filter.getStartRegistDate() == null)
        {
            _filter.setStartRegistDate(new Date(0));
        }
        if(_filter.getEndRegistDate() == null)
        {
            _filter.setEndRegistDate(new Date(maxDate));
        }

        return _filter;
    }

    public List<UserInfo> getUsersInfo(Filter filter) throws CloneNotSupportedException
    {
        List<UserInfo> userInfoList;

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        Filter _filter = getFullFilter(filter);

        try
        {
            if(_filter.getSectionIds() != null)
            {
                userInfoList = session.createSQLQuery(
                    "WITH temp(section_id) AS " +
                    "(SELECT section_id " +
                    "FROM Section " +
                    "WHERE section_id IN :sectionIds " +
                    "UNION ALL " +
                    "SELECT Section.section_id " +
                    "FROM temp JOIN Section ON (Section.upsection_id = temp.section_id)) " +
                    "SELECT {userInfo.*}, " +
                        "CASE WHEN messageNum IS NULL THEN 0 ELSE messageNum END AS messageNum, " +
                        "CASE WHEN topicNum IS NULL THEN 0 ELSE topicNum END AS topicNum, " +
                        "CASE WHEN createdTopicNum IS NULL THEN 0 ELSE createdTopicNum END AS createdTopicNum " +
                    "FROM " +
                        "(SELECT [user_id], COUNT(DISTINCT message_id) AS messageNum, COUNT(DISTINCT Topic.topic_id) AS topicNum " +
                        "FROM [User] JOIN [Message] ON ([User].[user_id] = [Message].author_id) " +
                            "JOIN Topic ON ([Message].topic_id = Topic.topic_id) " +
                            "JOIN temp ON (Topic.section_id = temp.section_id) " +
                        "WHERE [User].regist_date BETWEEN :startRegistDate AND :endRegistDate " +
                            "AND [Message].[date] BETWEEN :startDate AND :endDate " +
                        "GROUP BY [user_id]) AS T1 " +
                            "FULL JOIN " +
                        "(SELECT [user_id], COUNT(DISTINCT topic_id) AS createdTopicNum " +
                        "FROM [User] JOIN Topic ON ([User].[user_id] = Topic.creator_id) " +
                            "JOIN temp ON (Topic.section_id = temp.section_id) " +
                        "WHERE [User].regist_date BETWEEN :startRegistDate AND :endRegistDate " +
                            "AND Topic.[date] BETWEEN :startDate AND :endDate " +
                        "GROUP BY [user_id]) AS T2 ON (T1.[user_id] = T2.[user_id]) " +
                            "JOIN " +
                        "[User] userInfo ON (T1.[user_id] IS NOT NULL AND T1.[user_id] = userInfo.[user_id] " +
                            "OR T2.[user_id] IS NOT NULL AND T2.[user_id] = userInfo.[user_id]) " +
                    "WHERE (messageNum BETWEEN :minMessageNum AND :maxMessageNum) OR (messageNum IS NULL AND :minMessageNum = 0)")
                        .setParameterList("sectionIds", _filter.getSectionIds())
                        .setParameter("startRegistDate", _filter.getStartRegistDate())
                        .setParameter("endRegistDate", _filter.getEndRegistDate())
                        .setParameter("startDate", _filter.getStartDate())
                        .setParameter("endDate", _filter.getEndDate())
                        .setParameter("minMessageNum", _filter.getMinMessageNum())
                        .setParameter("maxMessageNum", _filter.getMaxMessageNum())
                        .addEntity("userInfo", User.class)
                        .addScalar("messageNum", IntegerType.INSTANCE)
                        .addScalar("topicNum", IntegerType.INSTANCE)
                        .addScalar("createdTopicNum", IntegerType.INSTANCE)
                        .setResultTransformer(Transformers.aliasToBean(UserInfo.class))
                        .list();
            }
            else
            {
                userInfoList = session.createSQLQuery(
                    "SELECT {userInfo.*}, CASE WHEN messageNum IS NULL THEN 0 ELSE messageNum END AS messageNum, " +
                        "CASE WHEN topicNum IS NULL THEN 0 ELSE topicNum END AS topicNum, " +
                        "CASE WHEN createdTopicNum IS NULL THEN 0 ELSE createdTopicNum END AS createdTopicNum " +
                    "FROM " +
                        "[User] AS userInfo LEFT JOIN " +
                        "(SELECT [user_id], COUNT(DISTINCT message_id) AS messageNum, COUNT(DISTINCT topic_id) AS topicNum " +
                        "FROM [User] JOIN [Message] ON ([User].[user_id] = [Message].author_id) " +
                        "WHERE [User].regist_date BETWEEN :startRegistDate AND :endRegistDate " +
                            "AND [Message].[date] BETWEEN :startDate AND :endDate " +
                        "GROUP BY [user_id]) AS T1 ON (userInfo.[user_id] = T1.[user_id]) " +
                        "LEFT JOIN " +
                        "(SELECT [user_id], COUNT(DISTINCT topic_id) AS createdTopicNum " +
                        "FROM [User] JOIN Topic ON ([User].[user_id] = Topic.creator_id) " +
                        "WHERE [User].regist_date BETWEEN :startRegistDate AND :endRegistDate " +
                            "AND Topic.[date] BETWEEN :startDate AND :endDate " +
                        "GROUP BY [user_id]) AS T2 ON (userInfo.[user_id] = T2.[user_id]) " +
                    "WHERE (regist_date BETWEEN :startRegistDate AND :endRegistDate) AND " +
                    "((messageNum BETWEEN :minMessageNum AND :maxMessageNum) OR (messageNum IS NULL AND :minMessageNum = 0))")
                        .setParameter("startRegistDate", _filter.getStartRegistDate())
                        .setParameter("endRegistDate", _filter.getEndRegistDate())
                        .setParameter("startDate", _filter.getStartDate())
                        .setParameter("endDate", _filter.getEndDate())
                        .setParameter("minMessageNum", _filter.getMinMessageNum())
                        .setParameter("maxMessageNum", _filter.getMaxMessageNum())
                        .addEntity("userInfo", User.class)
                        .addScalar("messageNum", IntegerType.INSTANCE)
                        .addScalar("topicNum", IntegerType.INSTANCE)
                        .addScalar("createdTopicNum", IntegerType.INSTANCE)
                        .setResultTransformer(Transformers.aliasToBean(UserInfo.class))
                        .list();
            }

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        return userInfoList;
    }

    public UserInfo getUserInfo(int userId)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        UserInfo userInfo;
        try
        {
            userInfo = (UserInfo) session.createQuery(
                "select user as userInfo, count(distinct messages.messageId) as messageNum, " +
                    "count(distinct messages.topic.topicId) as topicNum, " +
                    "count(distinct topics.topicId) as createdTopicNum " +
                "from User user left join user.messages messages left join user.topics topics " +
                "where user.userId = ?0 " +
                "group by user.userId, user.login, user.password, user.registDate, user.rights, user.avatar")
                .setParameter(0, userId)
                .setResultTransformer(Transformers.aliasToBean(UserInfo.class))
                .uniqueResult();

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        return userInfo;
    }

    /*
        Returns information about each topic in created by user with userId
        This information includes creator of topic, number of messages, number of users in topic
            and last message(with author property initialized)

        If user with userId doesn't exist in database, method throws IllegalArgumentException
    */
    public List<ForumTopic> getCreatedTopicList(int userId)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Object[]> rows;
        try
        {
            User user = session.get(User.class, new Integer(userId));
            if(user == null)
            {
                session.getTransaction().rollback();
                throw new IllegalArgumentException("Row with id " + userId + " in Table 'User' doesn't exist");
            }

            rows = session.createSQLQuery(
                "SELECT T1.topic_id AS {topic.topicId}, T1.[name] AS {topic.name}, T1.section_id AS {topic.section}, T1.creator_id AS {topic.creator}, T1.[date] AS {topic.date}, "
                    + "T1.[user_id] AS {creator.userId}, T1.[login] AS {creator.login}, T1.[password] AS {creator.password}, T1.regist_date AS {creator.registDate}, T1.rights AS {creator.rights}, T1.avatar AS {creator.avatar}, "
                    + "T1.messageNum, T1.userNum, "
                    + "T2.message_id AS {lastMessage.messageId}, T2.topic_id AS {lastMessage.topic}, T2.upmessage_id AS {lastMessage.upmessage}, T2.author_id AS {lastMessage.author}, T2.[date] AS {lastMessage.date}, T2.header AS {lastMessage.header}, "
                    + "T2.[user_id] AS {author.userId}, T2.[login] AS {author.login}, T2.[password] AS {author.password}, T2.regist_date AS {author.registDate}, T2.rights AS {author.rights}, T2.avatar AS {author.avatar} "
                + "FROM "
                    + "(SELECT Topic.*, [User].*, COUNT(DISTINCT [Message].message_id) AS messageNum, COUNT(DISTINCT [Message].author_id) AS userNum "
                    + "FROM Topic LEFT JOIN [User] ON (Topic.creator_id = [User].[user_id]) "
                        + "LEFT JOIN [Message] ON (Topic.topic_id = [Message].topic_id) "
                    + "WHERE creator_id = ?0 "
                    + "GROUP BY Topic.topic_id, [name], section_id, creator_id, Topic.[date], [user_id], [login], [password], regist_date, rights, avatar) AS T1 LEFT JOIN "

                    + "(SELECT TOP 1 WITH TIES Topic.topic_id AS id, [Message].*, [User].* "
                    + "FROM Topic LEFT JOIN [Message] ON (Topic.topic_id = [Message].topic_id) "
                        + "LEFT JOIN [User] ON ([Message].author_id = [User].[user_id]) "
                    + "WHERE creator_id = ?0 "
                    + "ORDER BY ROW_NUMBER() OVER(PARTITION BY Topic.topic_id ORDER BY [Message].[date] DESC)) AS T2 "
                        + "ON (T1.topic_id = T2.id)")

                .setParameter(0, userId)
                .addEntity("topic", Topic.class)
                .addScalar("messageNum", IntegerType.INSTANCE)
                .addScalar("userNum", IntegerType.INSTANCE)
                .addEntity("lastMessage", Message.class)
                    .addJoin("creator", "topic.creator")
                    .addJoin("author", "lastMessage.author")
                .list();

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        List<ForumTopic> createdTopics = new ArrayList<ForumTopic>();
        for(Object[] row : rows)
        {
            createdTopics.add(new ForumTopic(row));
        }

        return createdTopics;
    }

    public List<ForumTopic> getTopicList(int userId)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Object[]> rows;
        try
        {
            User user = session.get(User.class, new Integer(userId));
            if(user == null)
            {
                session.getTransaction().rollback();
                throw new IllegalArgumentException("Row with id " + userId + " in Table 'User' doesn't exist");
            }

            rows = session.createSQLQuery(
                "SELECT T1.topic_id AS {topic.topicId}, T1.[name] AS {topic.name}, T1.section_id AS {topic.section}, T1.creator_id AS {topic.creator}, T1.[date] AS {topic.date}, "
                    + "T1.[user_id] AS {creator.userId}, T1.[login] AS {creator.login}, T1.[password] AS {creator.password}, T1.regist_date AS {creator.registDate}, T1.rights AS {creator.rights}, T1.avatar AS {creator.avatar}, "
                    + "T1.messageNum, T1.userNum, "
                    + "T2.message_id AS {lastMessage.messageId}, T2.topic_id AS {lastMessage.topic}, T2.upmessage_id AS {lastMessage.upmessage}, T2.author_id AS {lastMessage.author}, T2.[date] AS {lastMessage.date}, T2.header AS {lastMessage.header}, "
                    + "T2.[user_id] AS {author.userId}, T2.[login] AS {author.login}, T2.[password] AS {author.password}, T2.regist_date AS {author.registDate}, T2.rights AS {author.rights}, T2.avatar AS {author.avatar} " +
                "FROM " +
                    "(SELECT Topic.*, [User].*, COUNT(DISTINCT [Message].message_id) AS messageNum, COUNT(DISTINCT [Message].author_id) AS userNum " +
                    "FROM Topic LEFT JOIN [User] ON (Topic.creator_id = [User].[user_id]) " +
                        "LEFT JOIN [Message] ON (Topic.topic_id = [Message].topic_id) " +
                    "WHERE Topic.topic_id IN " +
                        "(SELECT topic_id FROM [Message] WHERE author_id = ?0) " +
                    "GROUP BY Topic.topic_id, [name], section_id, creator_id, Topic.[date], [user_id], [login], [password], regist_date, rights, avatar) AS T1 LEFT JOIN " +

                    "(SELECT TOP 1 WITH TIES Topic.topic_id AS id, [Message].*, [User].* " +
                    "FROM Topic LEFT JOIN [Message] ON (Topic.topic_id = [Message].topic_id) " +
                        "LEFT JOIN [User] ON ([Message].author_id = [User].[user_id]) " +
                    "WHERE Topic.topic_id IN " +
                        "(SELECT topic_id FROM [Message] WHERE author_id = ?0) " +
                    "ORDER BY ROW_NUMBER() OVER(PARTITION BY Topic.topic_id ORDER BY [Message].[date] DESC)) AS T2 " +
                        "ON (T1.topic_id = T2.id)")

                .setParameter(0, userId)
                .addEntity("topic", Topic.class)
                .addScalar("messageNum", IntegerType.INSTANCE)
                .addScalar("userNum", IntegerType.INSTANCE)
                .addEntity("lastMessage", Message.class)
                    .addJoin("creator", "topic.creator")
                    .addJoin("author", "lastMessage.author")
                .list();

                session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        List<ForumTopic> topics = new ArrayList<ForumTopic>();
        for(Object[] row : rows)
        {
            topics.add(new ForumTopic(row));
        }

        return topics;
    }

    public List<Message> getMessageList(int userId)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Message> messages = new ArrayList<Message>();
        List<Object[]> ms;
        try
        {
            ms = session.createSQLQuery(
                "SELECT TOP 1 WITH TIES {message.*}, {text.*} " +
                "FROM [Message] JOIN [Object] text ON ([Message].message_id = text.message_id AND text.[type_id] = 1) " +
                "WHERE [Message].author_id = ?0 " +
                "ORDER BY ROW_NUMBER() OVER(PARTITION BY [Message].message_id ORDER BY object_num ASC)"
            )
            .setParameter(0, userId)
            .addEntity("message", Message.class)
            .addEntity("text", Text.class)
            .list();

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        for(Object[] m : ms)
        {
            List<MessageObject> mesObj = new ArrayList<MessageObject>();
            mesObj.add((MessageObject)m[1]);
            ((Message)m[0]).setMessageObjects(mesObj);
            messages.add((Message)m[0]);
        }

        return messages;
    }
}