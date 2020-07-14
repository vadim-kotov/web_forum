package model;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.type.IntegerType;
import util.HibernateUtil;

import org.hibernate.FetchMode;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;

import java.util.Date;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class SectionManager
{
    static public class HierarchySize
    {
        private int sectionNum;
        private int topicNum;
        private int messageNum;

        public HierarchySize()
        {
            sectionNum = 0;
            topicNum = 0;
            messageNum = 0;
        }

        public HierarchySize(int sectionNum, int topicNum, int messageNum)
        {
            this.sectionNum = sectionNum;
            this.topicNum = topicNum;
            this.messageNum = messageNum;
        }

        public int getSectionNum() { return this.sectionNum; }
        public void setSectionNum(int sectionNum) { this.sectionNum = sectionNum; }
        public int getTopicNum() { return this.topicNum; }
        public void setTopicNum(int topicNum) { this.topicNum = topicNum; }
        public int getMessageNum() { return this.messageNum; }
        public void setMessageNum(int messageNum) { this.messageNum = messageNum; }

        public boolean equals(Object obj)
        {
            if(this == obj)
                return true;
            if(obj == null)
                return false;
            if(getClass() != obj.getClass())
                return false;
            
            HierarchySize other = (HierarchySize) obj;
            if(sectionNum != other.getSectionNum())
                return false;
            if(topicNum != other.getTopicNum())
                return false;
            if(messageNum != other.getMessageNum())
                return false;
            
            return true;            
        }
    }

    static public class ForumSection
    {
        private Section section;
        private int topicNum;
        private int messageNum;
        private int userNum;
        private Message lastMessage;

        public ForumSection()
        {
            section = null;
            topicNum = 0;
            messageNum = 0;
            userNum = 0;
            lastMessage = null;
        }

        public ForumSection(Object[] fields)
        {
            section = (Section)fields[0];
            topicNum = (Integer)fields[1];
            messageNum = (Integer)fields[2];
            userNum = (Integer)fields[3];
            lastMessage = (Message)fields[4];
        }

        public Section getSection() { return this.section; }
        public void setSection(Section section) { this.section = section; }

        public int getTopicNum() { return this.topicNum; }
        public void setTopicNum(int topicNum) { this.topicNum = topicNum; }

        public int getMessageNum() {return this.messageNum; }
        public void setMessageNum(int messageNum) { this.messageNum = messageNum; }

        public int getUserNum() { return this.userNum; }
        public void setUserNum(int userNum) { this.userNum = userNum; }

        public Message getLastMessage() { return this.lastMessage; }
        public void setLastMessage(Message lastMessage) { this.lastMessage = lastMessage; }
    }

    static public class compSectionId implements Comparator<ForumSection>
    {
        public int compare(ForumSection a, ForumSection b)
        {
            int aId = a.getSection().getSectionId();
            int bId = b.getSection().getSectionId();

            if(aId == bId)
                return 0;
            else if(aId > bId)
                return -1;
            else if(aId < bId)
                return 1;
                        
            return 0;
        }
    }

    static public class ForumTopic
    {
        private Topic topic;
        private int messageNum;
        private int userNum;
        private Message lastMessage;

        public ForumTopic()
        {
            topic = null;
            messageNum = 0;
            userNum = 0;
            lastMessage = null;
        }

        public ForumTopic(Object[] fields)
        {
            topic = (Topic)fields[0];
            messageNum = (Integer)fields[1];
            userNum = (Integer)fields[2];
            lastMessage = (Message)fields[3];
        }

        public Topic getTopic() { return this.topic; }
        public void setTopic(Topic topic) { this.topic = topic; }

        public int getMessageNum() { return this.messageNum; }
        public void setMessageNum(int messageNum) { this.messageNum = messageNum; }

        public int getUserNum() { return this.userNum; }
        public void setUserNum(int userNum) { this.userNum = userNum; }

        public Message getLastMessage() { return this.lastMessage; }
        public void setLastMessage(Message lastMessage) { this.lastMessage = lastMessage; }
    }

    static public class compTopicId implements Comparator<ForumTopic>
    {
        public int compare(ForumTopic a, ForumTopic b)
        {
            int aId = a.getTopic().getTopicId();
            int bId = b.getTopic().getTopicId();
            if(aId == bId)
                return 0;
            else if(aId > bId)
                return -1;
            else if(aId < bId)
                return 1;
                        
            return 0;
        }
    }

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
            maxId = (Integer) session.createCriteria(Section.class)
                .setProjection(Property.forName("sectionId").max())
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
        Returns row from Section table
        Returns null if row with sectionId doesn't exist
    */
    public Section getSection(int sectionId)
    {
    
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Section section = null;
        try
        {
            section = session.get(Section.class, new Integer(sectionId));
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        return section;
    }

    /*
        Returns all rows from table Section
        Returns null if table Section is empty
    */
    public List<Section> getSections()
    {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        List<Section> sectionsList = null;
        try
        {
            sectionsList = session.createQuery("from Section").list();
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        return sectionsList;
    }

    /*
        Integer upsectionId is id of parent Section.
        upsectionId might be null, then upsection_id column is set to NULL

        Throws OIllegalArgumentException if row with upsectionId doesn't exist
    */
    public void createSection(Section section, Integer upsectionId)
    {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        try
        {
            if(upsectionId != null)
            {
                Section upsection = session.get(Section.class, upsectionId);
                if(upsection == null)
                {
                    session.getTransaction().rollback();
                    throw new IllegalArgumentException("Row with id " + upsectionId + " in Table 'Section' doesn't exist");
                }
                else
                {
                    section.setUpsection(upsection);
                }
            }
            session.save(section);
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
    }

    /*
        Deletes row with sectionId from Section

        Throws ObjectNotFoundException if row with sectionId doesn't exist
    */
    public void deleteSection(int sectionId)
    {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        try
        {
            session.delete(session.load(Section.class, new Integer(sectionId)));
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
    }

    /* changes name or/and description if they are not both equal null,
        otherwise throws IllegalArgumentException.
    
        Also throws IllegalArgumentException if row with sectionId doesn't exist
    */
    public void changeNameDesc(int sectionId, String name, String description)
    {
        if(name == null && description == null)
            throw new IllegalArgumentException("Second and third arguments both equal 'null'");

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();
        try
        {
            Section section = session.get(Section.class, new Integer(sectionId));
            if(section == null)
            {
                session.getTransaction().rollback();
                throw new IllegalArgumentException("Row with id " + sectionId + " in Table 'Section' doesn't exist");
            }

            if(name != null)
                section.setName(name);
            if(description != null)
                section.setDescription(description);
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
        Returns parent Section to Section with sectionId
        Returns null if Section with sectionId doesn't have parent (root Section)

        Throws IllegalArgumentException if row with sectionId doesn't exist
    */
    public Section getUpsection(int sectionId)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        Section section = null, upsection = null;
        try
        {
            section = (Section) session.createCriteria(Section.class)
                .add(Restrictions.eq("sectionId", new Integer(sectionId)))
                .setFetchMode("upsection", FetchMode.EAGER)
                .uniqueResult();
            
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        if(section == null)
        {
            throw new IllegalArgumentException("Row with id " + sectionId + " in Table 'Section' doesn't exist");
        }

        return section.getUpsection();
    }

    /*
        Gives number of (sections + subsections), topics and messages contained in sections with sectionIds.
        Throws IllegalArgumentException if sectionIds is null or empty;
    */
    public HierarchySize getHierarchySize(List<Integer> sectionIds)
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

        HierarchySize hierarchySize;
        try
        {
            hierarchySize = (HierarchySize) session.createSQLQuery(
                "WITH temp(id) AS "
                + "(SELECT section_id "
	            + "FROM Section "
	            + "WHERE section_id IN ?0 "
	            + "UNION ALL "
                + "SELECT section_id "
	            + "FROM Section, temp "
	            + "WHERE upsection_id = id) "
	                + "SELECT COUNT(DISTINCT id) AS sectionNum, "
                        + "COUNT(DISTINCT Topic.topic_id) AS topicNum, "
                        + "COUNT(DISTINCT[Message].message_id) AS messageNum "
	                + "FROM temp LEFT JOIN Topic ON (Topic.section_id = temp.id) "
		                + "LEFT JOIN [Message] ON ([Message].topic_id = Topic.topic_id)")
                .setParameterList(0, sectionIds)
                .setResultTransformer(Transformers.aliasToBean(HierarchySize.class))
                .uniqueResult();

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
        catch(IllegalArgumentException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
        return hierarchySize;
    }

    /*
        Returns list of subsection ids
        Throws IllegalArgumentException if sectionIds is null or empty
    */
    public List<Integer> getDownsectionIds(List<Integer> sectionIds)    
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

        List<Integer> downsectionIds = null;
        try
        {
            downsectionIds = session.createCriteria(Section.class)
                .add(Property.forName("upsection.sectionId").in(sectionIds))
                .setProjection(Property.forName("sectionId"))
                .list();
            
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
        
        return downsectionIds;
    }

    /*
        Returns path to section with sectionId.
        First element of List is one of root sections, last is section with sectionId
        Returns null if section with sectiondId doesn't exist in database
        Returns empty List if section with sectionId is null
    */
    public List<Section> getPath(Integer sectionId)
    {
        if(sectionId == null)
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
                + "WHERE section_id = ?0 "
                + "UNION ALL "
                + "SELECT [level] + 1, Section.* "
                + "FROM temp JOIN Section ON (temp.upsection_id = Section.section_id)) "
                    + "SELECT section_id, [name], upsection_id, [description] FROM temp "
                    + "ORDER BY [level] DESC")
                .setParameter(0, sectionId)
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

    /*
        Returns information about direct subsections of section with id 'sectionId'
        This information uncludes topic number, message number, user number 
            and last message(with author and topic properties initialized)
        All this information gets from whole 'subhierarchy' of each subsection;

        If sectionId is null, method returns information about all root sections;
        If section with sectionId doesn't exist in database, method throws IllegalArgumentException;
    */
    public List<ForumSection> getForumSectionList(Integer sectionId)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Object[]> rows;
        try
        {
            Section section;
            if(sectionId != null)
            {
                section = session.get(Section.class, sectionId);
                if(section == null)
                {
                    session.getTransaction().rollback();
                    throw new IllegalArgumentException("Row with id " + sectionId + " in Table 'Section' doesn't exist");
                }
            }

            rows = session.createSQLQuery(
                "WITH temp(root_id, [name], upsection_id, [description], section_id) AS "
                + "(SELECT [Root].*, Section.section_id "
                + "FROM Section RIGHT JOIN "
                    + "(SELECT section_id, [name], upsection_id, [description] "
                    + "FROM Section "
                    + "WHERE ((?0 IS NULL AND upsection_id IS NULL) OR (?0 IS NOT NULL AND upsection_id = ?0))) "
                        + "AS [Root] "
                    + "ON (Section.upsection_id = [Root].section_id) "
                + "UNION ALL "
                + "SELECT temp.root_id, temp.[name], temp.upsection_id, temp.[description], Section.section_id "
                + "FROM Section, temp "
                + "WHERE Section.upsection_id = temp.section_id) "
                + "SELECT T1.root_id AS {section.sectionId}, T1.[name] AS {section.name}, T1.upsection_id AS {section.upsection}, T1.[description] AS {section.description}, "
                    + "T1.topicNum, T1.messageNum, T1.userNum, "
                    + "T2.message_id AS {lastMessage.messageId}, T2.message_topic_id AS {lastMessage.topic}, T2.upmessage_id AS {lastMessage.upmessage}, T2.author_id AS {lastMessage.author}, T2.message_date AS {lastMessage.date}, T2.header AS {lastMessage.header}, "
                    + "T2.topic_topic_id AS {topic.topicId}, T2.[name] AS {topic.name}, T2.section_id AS {topic.section}, T2.creator_id AS {topic.creator}, T2.topic_date AS {topic.date}, "
                    + "{[User].*} "

                + "FROM "

                    + "(SELECT temp.root_id, temp.[name], temp.upsection_id, temp.[description], COUNT(DISTINCT Topic.topic_id) AS topicNum, COUNT(DISTINCT message_id) AS messageNum, COUNT(DISTINCT author_id) AS userNum "
                    + "FROM temp LEFT JOIN Topic ON (root_id = Topic.section_id OR temp.section_id = Topic.section_id) "
                    + "LEFT JOIN [Message] ON (Topic.topic_id = [Message].topic_id) "
                    + "GROUP BY root_id, temp.[name], temp.upsection_id, temp.[description]) AS T1 LEFT JOIN "

                    + "(SELECT TOP 1 WITH TIES root_id, Topic.topic_id AS topic_topic_id, Topic.[name], Topic.section_id, Topic.creator_id, Topic.[date] AS topic_date, "
                        + "[Message].message_id, [Message].topic_id AS message_topic_id, [Message].upmessage_id, [Message].author_id, [Message].[date] AS message_date, [Message].header "
                    + "FROM temp LEFT JOIN Topic ON (root_id = Topic.section_id OR temp.section_id = Topic.section_id) "
                    + "LEFT JOIN [Message] ON (Topic.topic_id = [Message].topic_id) "
                    + "ORDER BY ROW_NUMBER() OVER(PARTITION BY root_id ORDER BY [Message].[date] DESC)) AS T2 "
                        + "ON (T1.root_id = T2.root_id) "
                    + "LEFT JOIN [User] ON (T2.author_id = [User].[user_id])")

                .setParameter(0, sectionId)
                .addEntity("section", Section.class)
                .addScalar("topicNum", IntegerType.INSTANCE)
                .addScalar("messageNum", IntegerType.INSTANCE)
                .addScalar("userNum", IntegerType.INSTANCE)
                .addEntity("lastMessage", Message.class)
                    .addJoin("topic", "lastMessage.topic")
                    .addJoin("[User]", "lastMessage.author")
                .list();

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }

        List<ForumSection> forumList = new ArrayList<ForumSection>();
        for(Object[] row : rows)
        {
            forumList.add(new ForumSection(row));
        }

        return forumList;
    }

    /*
        Returns information about each topic in section with sectionId
        This information includes creator of topic, number of messages, number of users in topic
            and last message(with author property initialized)

        If section with sectionId doesn't exist in database, method throws IllegalArgumentException
    */
    public List<ForumTopic> getForumTopicList(int sectionId)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Object[]> rows;
        try
        {
            Section section = session.get(Section.class, new Integer(sectionId));
            if(section == null)
            {
                session.getTransaction().rollback();
                throw new IllegalArgumentException("Row with id " + sectionId + " in Table 'Section' doesn't exist");
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
                    + "WHERE section_id = ?0 "
                    + "GROUP BY Topic.topic_id, [name], section_id, creator_id, Topic.[date], [user_id], [login], [password], regist_date, rights, avatar) AS T1 LEFT JOIN "

                    + "(SELECT TOP 1 WITH TIES Topic.topic_id AS id, [Message].*, [User].* "
                    + "FROM Topic LEFT JOIN [Message] ON (Topic.topic_id = [Message].topic_id) "
                        + "LEFT JOIN [User] ON ([Message].author_id = [User].[user_id]) "
                    + "WHERE section_id = ?0 "
                    + "ORDER BY ROW_NUMBER() OVER(PARTITION BY Topic.topic_id ORDER BY [Message].[date] DESC)) AS T2 "
                        + "ON (T1.topic_id = T2.id)")

                .setParameter(0, sectionId)
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

        List<ForumTopic> forumList = new ArrayList<ForumTopic>();
        for(Object[] row : rows)
        {
            forumList.add(new ForumTopic(row));
        }

        return forumList;
    }

    public void createTopic(Topic topic)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        try
        {
            session.save(topic);
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public void deleteTopics(List<Integer> topicIds)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        try
        {
            session.createQuery("delete Topic where topicId in ?0")
                .setParameterList(0, topicIds)
                .executeUpdate();

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public void deleteSections(List<Integer> sectionIds)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        try
        {
            session.createQuery("delete Section where sectionId in ?0")
                .setParameterList(0, sectionIds)
                .executeUpdate();

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public void relocateTopics(int sectionId, List<Integer> topicIds)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        try
        {
            session.createQuery("update Topic set section.sectionId = ?0 where topicId in ?1")
                .setParameter(0, sectionId)
                .setParameterList(1, topicIds)
                .executeUpdate();

            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public void relocateMessageThread(int sectionId, Topic newTopic, int messageId)
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        try
        {
            newTopic.setDate(new Date());

            Message message = session.load(Message.class, new Integer(messageId));
            newTopic.setCreator(message.getAuthor());

            newTopic.setSection(null);
            newTopic.setSection(session.load(Section.class, sectionId));

            session.save(newTopic);

            session.createQuery("update Message set topic.topicId = ?0 where messageId = ?1")
                .setParameter(0, newTopic.getTopicId())
                .setParameter(1, messageId)
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