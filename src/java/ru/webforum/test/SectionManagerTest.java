package ru.webforum.test;

import ru.webforum.model.Section;
import ru.webforum.model.SectionManager;
import static ru.webforum.model.SectionManager.ForumSection;
import static ru.webforum.model.SectionManager.compSectionId;
import static ru.webforum.model.SectionManager.ForumTopic;
import static ru.webforum.model.SectionManager.compTopicId;
import ru.webforum.model.Topic;
import ru.webforum.model.TopicManager;
import ru.webforum.model.Message;
import ru.webforum.model.MessageManager;
import ru.webforum.model.User;
import ru.webforum.model.MessageObject;
import ru.webforum.model.Quote;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.hibernate.Session;
import ru.webforum.util.HibernateUtil;

import org.hibernate.criterion.*;
import org.hibernate.FetchMode;
import org.hibernate.Criteria;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Date;

import static java.lang.Math.random;

import static ru.webforum.test.MessageManagerTest.compMessages;
import static ru.webforum.test.TopicManagerTest.compTopics;
import static ru.webforum.test.UserManagerTest.compUsers;
import static ru.webforum.test.MessageObjectManagerTest.compMessageObjects;

public class SectionManagerTest extends TestCase
{
    SectionManager sectionManager = new SectionManager();
    TopicManager topicManager = new TopicManager();
    MessageManager messageManager = new MessageManager();

    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite()
    {
        return new TestSuite(SectionManagerTest.class);
    }

    static void compSections(Section expected, Section actual)
    {
        assertEquals(expected.getSectionId(), actual.getSectionId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    static void compForumSections(ForumSection expected, ForumSection actual)
    {
        compSections(expected.getSection(), actual.getSection());
        assertEquals(expected.getTopicNum(), actual.getTopicNum());
        assertEquals(expected.getMessageNum(), actual.getMessageNum());
        assertEquals(expected.getUserNum(), actual.getUserNum());

        Message expectedLastMessage = expected.getLastMessage();
        Message actualLastMessage = actual.getLastMessage();        
        if(expectedLastMessage == null)
        {
            assertNull(actualLastMessage);
        }
        else
        {
            compMessages(expectedLastMessage, actualLastMessage);
            compTopics(expectedLastMessage.getTopic(), actualLastMessage.getTopic());
            compUsers(expectedLastMessage.getAuthor(), actualLastMessage.getAuthor());
        }
    }

    static void compForumTopics(ForumTopic expected, ForumTopic actual)
    {
        Topic expectedTopic = expected.getTopic();
        Topic actualTopic = actual.getTopic();
        compTopics(expectedTopic, actualTopic);

        compUsers(expectedTopic.getCreator(), actualTopic.getCreator());

        assertEquals(expected.getMessageNum(), actual.getMessageNum());
        assertEquals(expected.getUserNum(), actual.getUserNum());

        Message expectedLastMessage = expected.getLastMessage();
        Message actualLastMessage = actual.getLastMessage();
        if(expectedLastMessage == null)
            assertNull(actualLastMessage);
        else
        {
            compMessages(expectedLastMessage, actualLastMessage);
            compUsers(expectedLastMessage.getAuthor(), actualLastMessage.getAuthor());
        }
    }

    public void testChangeNameDesc() throws CloneNotSupportedException
    {
        //System.out.println("1st Query");
        List<Section> sectionsList = sectionManager.getSections();
        if(sectionsList == null)
        {
            System.out.println("Table Section is empty");
            return;
        }
        int randomNum = (int) (random() * sectionsList.size());
        Section section = sectionsList.get(randomNum);
        String oldName = section.getName();
        String oldDescription = section.getDescription();

        boolean exFlag = false;
        try
        {
            //System.out.println("2nd Query");
            sectionManager.changeNameDesc(section.getSectionId(), null, null);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }
        assertTrue(exFlag);
        //System.out.println("3rd Query");
        Section sectionInDb = sectionManager.getSection(section.getSectionId());
        compSections(section, sectionInDb);

/*************************************************************************** */

        String newName = "new_name";

        section.setName(newName);
        //System.out.println("4th Query");
        sectionManager.changeNameDesc(section.getSectionId(), newName, null);
        //System.out.println("5th Query");
        sectionInDb = sectionManager.getSection(section.getSectionId());
        compSections(section, sectionInDb);

/*************************************************************************** */
        
        String newDescription = "new_description";

        section.setDescription(newDescription);
        //System.out.println("6th Query");
        sectionManager.changeNameDesc(section.getSectionId(), null, newDescription);
        //System.out.println("7th Query");
        sectionInDb = sectionManager.getSection(section.getSectionId());
        compSections(section, sectionInDb);

/*************************************************************************** */

        section.setName(oldName);
        section.setDescription(oldDescription);
        //System.out.println("8th Query");
        sectionManager.changeNameDesc(section.getSectionId(), oldName, oldDescription);
        //System.out.println("9th Query");
        sectionInDb = sectionManager.getSection(section.getSectionId());
        compSections(section, sectionInDb);

/*************************************************************************** */

        exFlag = false;
        //System.out.println("10th Query");
        int freeSectionId = sectionManager.getFreeId();
        try
        {
            //System.out.println("11th Query");
            sectionManager.changeNameDesc(freeSectionId, newName, newDescription);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }
        assertTrue(exFlag);
        //System.out.println("12th Query");
        sectionInDb = sectionManager.getSection(freeSectionId);
        assertNull(sectionInDb);
    }

    public void testCreateSection()
    {
        String name = "section_name", description = "section_description";
       
        //System.out.println("1st Query");
        List<Section> sectionsList = sectionManager.getSections();
        if(sectionsList == null)
        {
            System.out.println("Table Section is empty");
            return;
        }
        int randomNum = (int) (random() * sectionsList.size());
        Section upsection = sectionsList.get(randomNum);

        Section section = new Section();
        section.setName(name);
        section.setDescription(description);

        //System.out.println("2nd Query");
        sectionManager.createSection(section, new Integer(upsection.getSectionId()));

        //System.out.println("3rd Query");
        Section sectionInDb = sectionManager.getSection(section.getSectionId());
        assertNotNull(sectionInDb);
        compSections(section, sectionInDb);

        //System.out.println("4th Query");
        Section upsectionInDb = sectionManager.getUpsection(sectionInDb.getSectionId());
        assertNotNull(upsectionInDb);
        compSections(upsection, upsectionInDb);

        sectionManager.deleteSection(section.getSectionId());

/********************************************************** */

        section = new Section();
        section.setName(name);
        section.setDescription(description);
        
        //System.out.println("5th Query");
        sectionManager.createSection(section, null);

        //System.out.println("6th Query");
        sectionInDb = sectionManager.getSection(section.getSectionId());
        assertNotNull(sectionInDb);
        compSections(section, sectionInDb);

        //System.out.println("7th Query");
        upsectionInDb = sectionManager.getUpsection(sectionInDb.getSectionId());
        assertNull(upsectionInDb);

        sectionManager.deleteSection(section.getSectionId());

/*********************************************************** */

        section = new Section();
        section.setName(name);
        section.setDescription(description);

        boolean exFlag = false;
        //System.out.println("8th Query");
        int freeSectionId = sectionManager.getFreeId();
        try
        {
            //System.out.println("9th Query");
            sectionManager.createSection(section, new Integer(freeSectionId));
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }
        assertTrue(exFlag);
    }

    public void testGetHierarchySize()
    {
        boolean exFlag = false;
        try
        {
            sectionManager.getHierarchySize(null);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }

        assertTrue(exFlag);

/************************************************************ */

        List<Integer> sectionIds = new ArrayList<Integer>();
        exFlag = false;
        try
        {
            sectionManager.getHierarchySize(sectionIds);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }

        assertTrue(exFlag);

/************************************************************ */

        sectionIds = new ArrayList<Integer>();
        List<Section> sectionsList = sectionManager.getSections();
        if(sectionsList == null)
        {
            System.out.println("Table Section is empty");
            return;
        }
        for(Section s: sectionsList)
        {
            if((int)(random() * 2) == 1)
                sectionIds.add(s.getSectionId());
        }
        Set<Integer> subsectionIds = new HashSet<Integer>(sectionIds);

        SectionManager.HierarchySize actualHS = sectionManager.getHierarchySize(sectionIds);

        List<Integer> downsectionIds = null;
        while((downsectionIds = sectionManager.getDownsectionIds(sectionIds)).size() > 0)
        {
            subsectionIds.addAll(downsectionIds);
            sectionIds = downsectionIds;
        }
        Set<Integer> topicIds = new HashSet<Integer>(topicManager.getTopicFromSectionIds(new ArrayList<Integer>(subsectionIds)));
        Set<Integer> messageIds = new HashSet<Integer>(messageManager.getMessageFromTopicIds(new ArrayList<Integer>(topicIds)));
        SectionManager.HierarchySize expectedHS = new SectionManager.HierarchySize(subsectionIds.size(), topicIds.size(), messageIds.size());

        assertTrue(expectedHS.equals(actualHS));
    }

    public void testGetPath()
    {
        List<Section> sectionsList = sectionManager.getSections();
        if(sectionsList == null)
        {
            System.out.println("Table Section is empty");
            return;
        }
        int randomNum = (int) (random() * sectionsList.size());
        Section section = sectionsList.get(randomNum);

        List<Section> expectedPath = new ArrayList<Section>();

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        session.refresh(section);
        Section curSection = section;
        //!!!!!!!
        while(curSection != null)
        {
            expectedPath.add(curSection);
            curSection = curSection.getUpsection();
        }

        session.getTransaction().commit();

        Collections.reverse(expectedPath);

        List<Section> actualPath = sectionManager.getPath(section.getSectionId());

        assertNotNull(actualPath);
        assertEquals(expectedPath.size(), actualPath.size());
        for(int i = 0; i < expectedPath.size(); i++)
        {
            compSections(expectedPath.get(i), actualPath.get(i));
        }

/****************************************************************** */

        int freeSectionId = sectionManager.getFreeId();
        actualPath = sectionManager.getPath(freeSectionId);
        assertNull(actualPath);

/****************************************************************** */

        actualPath = sectionManager.getPath(null);
        assertEquals(actualPath.size(), 0);
    }

    private Set<Section> getSubsections(Section root)
    {
        Set<Section> result = new HashSet<Section>();

        Set<Section> downsections;
        if((downsections = root.getDownsections()).size() == 0)
        {
            return result;
        }

        result.addAll(downsections);
        for(Section d : downsections)
        {
            result.addAll(getSubsections(d));
        }
        return result;
    }

    public void testGetForumSectionList()
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Integer> sectionIdsList = session.createCriteria(Section.class)
            .setProjection(Property.forName("sectionId"))
            .list();

        if(sectionIdsList.size() == 0)
        {
            System.out.println("Table Section is empty");
            session.getTransaction().rollback();
            return;
        }
        Integer sectionId;
        Set<Section> downsections;
        int randomNum = (int) (random() * (sectionIdsList.size() + 1) - 1);
        if(randomNum < 0)
        {
            sectionId = null;
            downsections = new HashSet<Section>(session.createQuery("from Section where upsection.sectionId is null")
                .list());
        }
        else
        {
            sectionId = new Integer(sectionIdsList.get(randomNum));
            downsections = new HashSet<Section>(session.createQuery("from Section where upsection.sectionId = ?0")
                .setParameter(0, sectionId)
                .list());
        }

        List<ForumSection> expected = new ArrayList<ForumSection>();
        for(Section s : downsections)
        {
            ForumSection Item = new ForumSection();
            Item.setSection(s);

            Set<Section> subS = getSubsections(s);
            subS.add(s);

            Set<Topic> allTopics = new HashSet<Topic>();
            for(Section ss : subS)
            {
                List<Topic> topics = session.createCriteria(Topic.class)
                    .add(Property.forName("section.sectionId").eq(ss.getSectionId()))
                    .createAlias("messages", "m", Criteria.LEFT_JOIN)
                    .createAlias("m.author", "a", Criteria.LEFT_JOIN)
                    .list();

                ss.setTopics(new HashSet<Topic>(topics));
                allTopics.addAll(topics);
            }
            Item.setTopicNum(allTopics.size());

            Set<Message> allMessages = new HashSet<Message>();
            for(Topic t : allTopics)
            {
                allMessages.addAll(t.getMessages());
            }
            Item.setMessageNum(allMessages.size());

            Set<User> allUsers = new HashSet<User>();
            for(Message m : allMessages)
            {
                allUsers.add(m.getAuthor());
            }
            Item.setUserNum(allUsers.size());

            Message lastMessage;
            if(allMessages.size() == 0)
            {
                Item.setLastMessage(null);
            }
            else
            {
                lastMessage = Collections.max(allMessages, Comparator.comparing(Message::getDate));
                /*lastMessage.getTopic();
                lastMessage.getAuthor();*/
                Item.setLastMessage(lastMessage);
            }

            expected.add(Item);
        }
        session.getTransaction().commit();
        
        Collections.sort(expected, new compSectionId());

        List<ForumSection> actual = sectionManager.getForumSectionList(sectionId);
        Collections.sort(actual, new compSectionId());

        assertEquals(expected.size(), actual.size());
        for(int i = 0; i < expected.size(); i++)
        {
            compForumSections(expected.get(i), actual.get(i));
        }

/************************************************************ */

        sectionId = sectionManager.getFreeId();
        boolean exFlag = false;
        try
        {
            sectionManager.getForumSectionList(sectionId);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }
        assertTrue(exFlag);
    }

    public void testGetForumTopicList()
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Integer> sectionIdsList = session.createCriteria(Section.class)
            .setProjection(Property.forName("sectionId"))
            .list();

        if(sectionIdsList.size() == 0)
        {
            System.out.println("Table Section is empty");
            session.getTransaction().rollback();
            return;
        }
        int sectionId;
        List<Topic> topics;
        int randomNum = (int) (random() * sectionIdsList.size());
        //int randomNum = 2;
        sectionId = sectionIdsList.get(randomNum);

        topics = session.createCriteria(Topic.class)
            .add(Property.forName("section.sectionId").eq(sectionId))
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

        List<ForumTopic> actual = sectionManager.getForumTopicList(sectionId);
        Collections.sort(actual, new compTopicId());

        assertEquals(expected.size(), actual.size());
        for(int i = 0; i < expected.size(); i++)
        {
            compForumTopics(expected.get(i), actual.get(i));
        }

/******************************************************************** */

        sectionId = sectionManager.getFreeId();
        boolean exFlag = false;
        try
        {
            sectionManager.getForumTopicList(sectionId);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }
        assertTrue(exFlag);
    }

    public void testCreateTopic()
    {
        String header = "message_header";
        String name = "topic_name";

        Topic topic = new Topic();
        Message message = new Message();

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Section> sectionsList = session.createCriteria(Section.class)
            .list();
        int randomNum = (int) (random() * sectionsList.size());
        Section section = sectionsList.get(randomNum);
        topic.setSection(section);

        message.setUpmessage(null);
        message.setTopic(topic);

        List<User> usersList = session.createCriteria(User.class)
            .list();
        randomNum = (int) (random() * usersList.size());
        User creator = usersList.get(randomNum);
        topic.setCreator(creator);
        message.setAuthor(creator);

        topic.setName(name);
        Date date = new Date();
        topic.setDate(date);
        message.setDate(date);
        message.setHeader(header);

        List<MessageObject> messageObjects = session.createCriteria(MessageObject.class)
            .list();

        session.getTransaction().commit();
        for(MessageObject mo : messageObjects)
        {
            mo.setMessageObjectId(0);
            mo.setMessage(message);
        }

        message.setMessageObjects(messageObjects);

        Set<Message> messages = new HashSet<Message>();
        messages.add(message);
        topic.setMessages(messages);

        sectionManager.createTopic(topic);

        session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        Topic topicInDb = session.get(Topic.class, new Integer(topic.getTopicId()));
        assertNotNull(topicInDb);
        compTopics(topic, topicInDb);
        assertNotNull(topicInDb.getSection());
        assertEquals(topic.getSection().getSectionId(), topicInDb.getSection().getSectionId());
        assertNotNull(topicInDb.getCreator());
        assertEquals(creator.getUserId(), topicInDb.getCreator().getUserId());

        Set<Message> messagesInDb = topicInDb.getMessages();
        assertNotNull(messagesInDb);
        assertEquals(messagesInDb.size(), 1);
        Message messageInDb = messagesInDb.iterator().next();
        assertNotNull(messageInDb);

        compMessages(message, messageInDb);
        assertNotNull(messageInDb.getTopic());
        assertEquals(message.getTopic().getTopicId(), messageInDb.getTopic().getTopicId());
        assertNull(messageInDb.getUpmessage());
        assertNotNull(messageInDb.getAuthor());
        assertEquals(message.getAuthor().getUserId(), messageInDb.getAuthor().getUserId());

        List<MessageObject> messageObjectsInDb = messageInDb.getMessageObjects();
        assertEquals(messageObjects.size(), messageObjectsInDb.size());
        for(int i = 0; i < messageObjects.size(); i++)
        {
            MessageObject mo = messageObjects.get(i);
            MessageObject moInDb = messageObjectsInDb.get(i);
            compMessageObjects(mo, moInDb);

            if(mo.getClass() == Quote.class)
            {
                Quote quote = (Quote) mo;
                Quote quoteInDb = (Quote) moInDb;
                if(quote.getQuoteMessage() != null)
                {
                    assertNotNull(quoteInDb.getQuoteMessage());
                    assertEquals(quote.getQuoteMessage().getMessageId(), quoteInDb.getQuoteMessage().getMessageId());
                }
                else
                {
                    assertNull(quoteInDb.getQuoteMessage());
                }
                assertNotNull(quoteInDb.getQuoteAuthor());
                assertEquals(quote.getQuoteAuthor().getUserId(), quoteInDb.getQuoteAuthor().getUserId());
            }
        }
        session.delete(topicInDb);

        session.getTransaction().commit();
    }

    public void testDeleteTopics()
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Topic> allTopics = session.createCriteria(Topic.class)
            .createAlias("messages", "m", Criteria.LEFT_JOIN)
            .createAlias("m.messageObjects", "mo", Criteria.LEFT_JOIN)
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();
        
        session.getTransaction().commit();

        Set<Topic> topics = new HashSet<Topic>();

        for(Topic t : allTopics)
        {
            if(((int) (random() * 2)) == 1)
            {
                topics.add(t);
            }
        }
        
        List<Integer> topicIds = new ArrayList<Integer>();
        for(Topic t : topics)
        {
            topicIds.add(t.getTopicId());
        }

        sectionManager.deleteTopics(topicIds);

        session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        for(Topic t : topics)
        {
            Topic topInDb = session.get(Topic.class, new Integer(t.getTopicId()));
            assertNull(topInDb);

            List<Message> msInDb = session.createCriteria(Message.class)
                .add(Property.forName("topic.topicId").eq(t.getTopicId()))
                .list();
            assertEquals(msInDb.size(), 0);
            for(Message m : t.getMessages())
            {
                Message mInDb = session.get(Message.class, new Integer(m.getMessageId()));
                assertNull(mInDb);

                List<MessageObject> mosInDb = session.createCriteria(MessageObject.class)
                    .add(Property.forName("message.messageId").eq(m.getMessageId()))
                    .list();
                assertEquals(mosInDb.size(), 0);
                for(MessageObject mo : m.getMessageObjects())
                {
                    MessageObject moInDb = session.get(MessageObject.class, new Integer(mo.getMessageObjectId()));
                    assertNull(moInDb);
                }
            }
        }

        session.getTransaction().commit();
    }

    static public Set<Section> getSubsections(Set<Section> roots)
    {
        Set<Section> result = new HashSet<Section>();
        if(roots == null)
            return result;
        
        for(Section s : roots)
        {
            if(result.add(s))
            {
                result.addAll(getSubsections(s.getDownsections()));
            }
        }

        return result;
    }

    public void testDeleteSections()
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Section> allSections = session.createCriteria(Section.class)
            .createAlias("topics", "t", Criteria.LEFT_JOIN)
            .createAlias("t.messages", "m", Criteria.LEFT_JOIN)
            .createAlias("m.messageObjects", "mo", Criteria.LEFT_JOIN)
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();

        Set<Section> sections = new HashSet<Section>();
        
        for(Section s : allSections)
        {
            if(((int) (random() * 2)) == 1)
            {
                sections.add(s);
            }
        }
        
        Set<Section> secHierarchy = getSubsections(sections);

        session.getTransaction().commit();

        List<Integer> sectionIds = new ArrayList<Integer>();
        for(Section s : sections)
        {
            sectionIds.add(s.getSectionId());
        }
        
        sectionManager.deleteSections(sectionIds);

        session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        for(Section sec : secHierarchy)
        {
            List<Section> secsInDb = session.createCriteria(Section.class)
                .add(Property.forName("upsection.sectionId").eq(sec.getSectionId()))
                .list();
            assertEquals(secsInDb.size(), 0);
            Section secInDb = session.get(Section.class, new Integer(sec.getSectionId()));
            assertNull(secInDb);

            List<Topic> topsInDb = session.createCriteria(Topic.class)
                .add(Property.forName("section.sectionId").eq(sec.getSectionId()))
                .list();
            assertEquals(topsInDb.size(), 0);
            for(Topic t : sec.getTopics())
            {
                Topic topInDb = session.get(Topic.class, new Integer(t.getTopicId()));
                assertNull(topInDb);

                List<Message> messagesInDb = session.createCriteria(Message.class)
                    .add(Property.forName("topic.topicId").eq(t.getTopicId()))
                    .list();
                assertEquals(messagesInDb.size(), 0);
                for(Message mes : t.getMessages())
                {
                    Message mesInDb = session.get(Message.class, new Integer(mes.getMessageId()));
                    assertNull(mesInDb);

                    List<MessageObject> mosInDb = session.createCriteria(MessageObject.class)
                        .add(Property.forName("message.messageId").eq(mes.getMessageId()))
                        .list();
                    assertEquals(mosInDb.size(), 0);
                    for(MessageObject mo : mes.getMessageObjects())
                    {
                        MessageObject moInDb = session.get(MessageObject.class, new Integer(mo.getMessageObjectId()));
                        assertNull(moInDb);
                    }
                }
            }
        }

        session.getTransaction().commit();
    }

    public void testRelocateTopics()
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Section> allSections = session.createCriteria(Section.class)
            .list();
        int randomNum = (int) (random() * allSections.size());
        Section section = allSections.get(randomNum);

        List<Topic> allTopics = session.createCriteria(Topic.class)
            .setFetchMode("section", FetchMode.JOIN)
            .list();

        session.getTransaction().commit();

        List<Topic> topics = new ArrayList<Topic>();
        List<Integer> topicIds = new ArrayList<Integer>();
        for(Topic t : allTopics)
        {
            if(((int) (random() * 2)) == 1)
            {
                topics.add(t);
                topicIds.add(t.getTopicId());
            }
        }

        sectionManager.relocateTopics(section.getSectionId(), topicIds);

        session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        for(Topic t : topics)
        {
            Topic topicInDb = session.get(Topic.class, t.getTopicId());
            assertNotNull(topicInDb);
            compTopics(t, topicInDb);

            Section sectionInDb = topicInDb.getSection();
            assertNotNull(sectionInDb);
            compSections(section, sectionInDb);
        }

        session.clear();

        for(Topic t : topics)
        {
            session.update(t);
        }

        session.getTransaction().commit();
    }

    private List<Message> getMessageHierarchy(Message root)
    {
        List<Message> result = new ArrayList<Message>();
        if(root == null)
            return result;
        
        result.add(root);
        for(Message m : root.getDownmessages())
        {
            result.addAll(getMessageHierarchy(m));
        }

        return result;
    }

    public void testRelocateMessageThread()
    {
        
        String topicName = "topic_name";

        Topic newTopic = new Topic();
        newTopic.setName(topicName);

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Section> allSections = session.createCriteria(Section.class)
            .list();
        int randomNum = (int) (random() * allSections.size());
        Section section = allSections.get(randomNum);

        List<Message> allMessages = session.createCriteria(Message.class)
            .list();
        randomNum = (int) (random() * allMessages.size());
        Message message = allMessages.get(randomNum);

        User messageAuthor = session.get(User.class, new Integer(message.getAuthor().getUserId()));

        List<Message> messageThread = getMessageHierarchy(message);
        Collections.sort(messageThread, Comparator.comparing(Message::getMessageId));

        session.getTransaction().commit();

        sectionManager.relocateMessageThread(section.getSectionId(), newTopic, message.getMessageId());
        assertNotNull(newTopic.getTopicId());

        session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        Topic topicInDb = session.get(Topic.class, new Integer(newTopic.getTopicId()));
        assertNotNull(topicInDb);
        compTopics(newTopic, topicInDb);

        List<Message> messageThreadInDb = new ArrayList<Message>(topicInDb.getMessages());
        Collections.sort(messageThreadInDb, Comparator.comparing(Message::getMessageId));

        assertEquals(messageThread.size(), messageThreadInDb.size());
        for(int i = 0; i < messageThread.size(); i++)
        {
            compMessages(messageThread.get(i), messageThreadInDb.get(i));
        }

        compSections(section, topicInDb.getSection());

        compUsers(messageAuthor, topicInDb.getCreator());

        session.getTransaction().commit();
    }
}