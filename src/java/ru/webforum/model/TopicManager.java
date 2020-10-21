package ru.webforum.model;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import ru.webforum.util.HibernateUtil;

import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;
import org.hibernate.Criteria;

import java.util.List;
import java.util.ArrayList;

public class TopicManager
{
    static public class TopicMessage
    {
        private int level;
        private Message message;

        public TopicMessage(int level, Message message)
        {
            this.level = level;
            this.message = message;
        }

        public int getLevel() { return this.level; }
        public void setLevel(int level) { this.level = level; }

        public Message getMessage() { return this.message; }
        public void setMessage(Message message) { this.message = message; }
    }

    /*
        Returns maximum id number in table Topic
        Returns null if table Topic is empty
    */
    public Integer getMaxId()
    {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Integer maxId;
        try
        {
            maxId = (Integer) session.createCriteria(Topic.class)
                .setProjection(Property.forName("topicId").max())
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
    
    /*
        Returns list of topic ids
        Throws IllegalArgumentException if sectionIds is null or empty
    */
    public List<Integer> getTopicFromSectionIds(List<Integer> sectionIds)
    {
        if(sectionIds == null)
        {
            throw new IllegalArgumentException("List of section ids is null");
        }
        if(sectionIds.size() == 0)
        {
            throw new IllegalArgumentException("List of section ids is empty");
        }

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Integer> topicIds = null;
        try
        {
            topicIds = session.createCriteria(Topic.class)
                .add(Property.forName("section.sectionId").in(sectionIds))
                .setProjection(Property.forName("topicId"))
                .list();
            
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
        
        return topicIds;
    }

    /* changes name of topic if name is not null, 
        otherwise throws IllegalArgumentException.
    
        Also throws IllegalArgumentException if row with topicId doesn't exist
    */
    public void changeName(int topicId, String name)
    {
        if(name == null)
            throw new IllegalArgumentException("Second argument equal 'null'");

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();
        try
        {
            Topic topic = session.get(Topic.class, new Integer(topicId));
            if(topic == null)
            {
                session.getTransaction().rollback();
                throw new IllegalArgumentException("Row with id " + topicId + " in Table 'Topic' doesn't exist");
            }
            topic.setName(name);
            session.getTransaction().commit();

            return;
        }
        catch (HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
    }

    /*
        Returns all rows from table Topic
        Returns null if table Topic is empty
    */
    public List<Topic> getTopics()
    {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        List<Topic> topicsList = null;
        try
        {
            topicsList = session.createQuery("from Topic").list();
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        return topicsList;
    }

    /*
        Returns row from Topic table
        Returns null if row with topicId doesn't exist
    */
    public Topic getTopic(int topicId)
    {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Topic topic = null;
        try
        {
            topic = session.get(Topic.class, new Integer(topicId));
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        return topic;
    }

    /*
        Returns number of messages in topics
        Throws IllegalArgumentException if topicIds is empty or null
    */
    public Long getMessageNumber(List<Integer> topicIds)
    {
        if(topicIds == null)
        {
            throw new IllegalArgumentException("List of topic ids is null");
        }
        if(topicIds.size() == 0)
        {
            throw new IllegalArgumentException("List of topic ids is empty");
        }

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        Long messageNumber;
        try
        {
            messageNumber = (Long) session.createCriteria(Message.class)
                .add(Property.forName("topic.topicId").in(topicIds))
                .setProjection(Projections.rowCount())
                .uniqueResult();

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        return messageNumber;
    }

    /*
        Returns path to topic with topicId.
        First element of List is one of root sections, last section is direct section of topic
        Returns null if topic with topicId doesn't exist in database
        Returns empty List if topicId is null
    */
    public List<Section> getPath(Integer topicId)
    {
        if(topicId == null)
            return new ArrayList<Section>();

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Section> path = null;
        try
        {
            path = session.createSQLQuery(
                "WITH temp([level], section_id, [name], upsection_id, [description]) AS "
                + "(SELECT 1, Section.* "
                + "FROM Section "
                + "WHERE section_id = "
                    + "(SELECT section_id FROM Topic WHERE topic_id = ?0) "
                + "UNION ALL "
                + "SELECT [level] + 1, Section.* "
                + "FROM temp JOIN Section ON (temp.upsection_id = Section.section_id)) "
                    + "SELECT section_id, [name], upsection_id, [description] FROM temp "
                    + "ORDER BY [level] DESC")
                .setParameter(0, topicId)
                .addEntity(Section.class)
                .list();
            
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        if(path.size() == 0)
            return null;
        return path;
    }

    public List<TopicMessage> getTopicMessageList(int topicId)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();       

        Topic topic = null;
        List<Object[]> rows;
        try
        {
            topic = session.get(Topic.class, new Integer(topicId));
            if(topic == null)
            {
                session.getTransaction().rollback();
                throw new IllegalArgumentException("Row with id " + topicId + " in Table 'Topic' doesn't exist");
            }
            
            rows = session.createNativeQuery(
                "WITH temp([level], message_id, topic_id, upmessage_id, author_id, [date], header, [path]) AS "
                + "(SELECT 1 AS [level], [Message].*, cast('1' AS varchar(MAX)) "
                + "FROM [Message] "
                + "WHERE topic_id = ?0 AND upmessage_id IS NULL "
                + "UNION ALL "
                + "SELECT [level] + 1, [Message].*, "
	                + "[path] + '.' + cast(ROW_NUMBER() OVER(ORDER BY [Message].[date] ASC) AS varchar(MAX)) "
                + "FROM temp JOIN [Message] ON (temp.message_id = [Message].upmessage_id)) "

                    + "SELECT [level], {message.*}, "
                        + "{author.*}, "
                        + "{upmessage.*}, {upauthor.*}, "
                        + "{messageObjects.*}, "
                        + "{quote.*}, {quote_author.*} "
                    + "FROM temp message "
                       + "LEFT JOIN [User] author ON (message.author_id = author.[user_id])"
                       + "LEFT JOIN [Message] upmessage ON (message.upmessage_id = upmessage.message_id) "
                       + "LEFT JOIN [User] upauthor ON (upmessage.author_id = upauthor.[user_id]) "
                        + "LEFT JOIN [Object] messageObjects ON (message.message_id = messageObjects.message_id) "
                       + "LEFT JOIN [Message] quote ON (messageObjects.quote_id = quote.message_id) "
                       + "LEFT JOIN [User] quote_author ON (messageObjects.quote_author_id = quote_author.[user_id]) "
                    + "ORDER BY [path]")
                .setParameter(0, topicId)
                .addScalar("level", IntegerType.INSTANCE)
                .addEntity("message", Message.class)
                    .addJoin("author", "message.author")
                    .addJoin("upmessage", "message.upmessage")
                    .addJoin("upauthor", "upmessage.author")
                    .addJoin("messageObjects", "message.messageObjects")
                    .addJoin("quote", "messageObjects.quoteMessage")
                    .addJoin("quote_author", "messageObjects.quoteAuthor")
                .list();

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        List<TopicMessage> topicList = new ArrayList<TopicMessage>();
        int curId = 0;
        for(Object[] r : rows)
        {
            Message mes = (Message)r[1];
            if(mes.getMessageId() != curId)
            {
                TopicMessage curTM = new TopicMessage((Integer)r[0], (Message)r[1]);
                topicList.add(curTM);

                curId = (mes.getMessageId());
            }
        }

        return topicList;
    }

    public void createMessage(Message message)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        try
        {
            session.save(message);
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public void deleteMessages(List<Integer> messageIds)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        try
        {
            session.createQuery("delete Message where messageId in ?0")
                .setParameterList(0, messageIds)
                .executeUpdate();

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
    }
}