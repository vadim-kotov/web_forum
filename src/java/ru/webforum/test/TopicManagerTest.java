package ru.webforum.test;

import ru.webforum.model.Section;
import ru.webforum.model.Topic;
import ru.webforum.model.TopicManager;
import ru.webforum.model.Message;
import ru.webforum.model.MessageObject;
import ru.webforum.model.Text;
import ru.webforum.model.Image;
import ru.webforum.model.Quote;
import ru.webforum.model.User;
import static ru.webforum.model.TopicManager.TopicMessage;
import ru.webforum.model.Message;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import ru.webforum.util.HibernateUtil;

import org.hibernate.criterion.*;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.util.Date;

import static ru.webforum.test.MessageManagerTest.compMessages;
import static ru.webforum.test.UserManagerTest.compUsers;
import static ru.webforum.test.MessageObjectManagerTest.compMessageObjects;

import static java.lang.Math.random;

public class TopicManagerTest extends TestCase
{
    TopicManager topicManager = new TopicManager();

    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite()
    {
        return new TestSuite(TopicManagerTest.class);
    }

    static void compTopics(Topic expected, Topic actual)
    {
        assertEquals(expected.getTopicId(), actual.getTopicId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDate(), actual.getDate());
    }

    static void compTopicMessages(TopicMessage expected, TopicMessage actual)
    {
        assertEquals(expected.getLevel(), actual.getLevel());

        Message expMessage = expected.getMessage();
        Message actMessage = actual.getMessage();
        
        compMessages(expMessage, actMessage);

        User expAuthor = expMessage.getAuthor();
        User actAuthor = actMessage.getAuthor();
        compUsers(expAuthor, actAuthor);

        Message expUpmessage = expMessage.getUpmessage();
        Message actUpmessage = actMessage.getUpmessage();
        if(expUpmessage != null)
        {
            
            compMessages(expUpmessage, actUpmessage);
            compUsers(expUpmessage.getAuthor(), actUpmessage.getAuthor());
        }
        else
        {
            assertNull(actUpmessage);
        }

        List<MessageObject> expObjects = expMessage.getMessageObjects();
        List<MessageObject> actObjects = actMessage.getMessageObjects();
        if(expObjects != null)
        {
            assertEquals(expObjects.size(), actObjects.size());
            for(int i = 0; i < expObjects.size(); i++)
            {
                MessageObject expObject = expObjects.get(i);
                MessageObject actObject = actObjects.get(i);
                if(expObject != null)
                {
                    compMessageObjects(expObject, actObject);

                    if(expObject.getClass() == Quote.class)
                    {
                        assertTrue((actObject.getClass() == Quote.class));
                        Quote expQuote = (Quote)expObject;
                        Quote actQuote = (Quote)actObject;

                        if(expQuote.getQuoteMessage() != null)
                            compMessages(expQuote.getQuoteMessage(), actQuote.getQuoteMessage());
                        else
                            assertNull(actQuote.getMessage());

                        compUsers(expQuote.getQuoteAuthor(), actQuote.getQuoteAuthor());
                    }
                }
                else
                {
                    assertNull(actObject);
                }
            }
        }
        else
        {
            assertNull(actObjects);
        }
        
    }

    public void testChangeName()
    {
        //System.out.println("1st Query");
        List<Topic> topicsList = topicManager.getTopics();
        if(topicsList == null)
        {
            System.out.println("Table Section is empty");
            return;
        }
        int randomNum = (int) (random() * topicsList.size());
        Topic topic = topicsList.get(randomNum);
        String oldName = topic.getName();

        boolean exFlag = false;
        try
        {
            //System.out.println("2nd Query");
            topicManager.changeName(topic.getTopicId(), null);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }
        assertTrue(exFlag);
        //System.out.println("3rd Query");
        Topic topicInDb = topicManager.getTopic(topic.getTopicId());
        compTopics(topic, topicInDb);

/*************************************************************************** */

        String newName = "new_name";

        topic.setName(newName);
        //System.out.println("4th Query");
        topicManager.changeName(topic.getTopicId(), newName);
        //System.out.println("5th Query");
        topicInDb = topicManager.getTopic(topic.getTopicId());
        compTopics(topic, topicInDb);

        topicManager.changeName(topic.getTopicId(), oldName);

/*************************************************************************** */

        exFlag = false;
        //System.out.println("10th Query");
        int freeTopicId = topicManager.getFreeId();
        try
        {
            //System.out.println("11th Query");
            topicManager.changeName(freeTopicId, newName);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }
        assertTrue(exFlag);
        //System.out.println("12th Query");
        topicInDb = topicManager.getTopic(freeTopicId);
        assertNull(topicInDb);
    }

    public void testGetMessageNumber()
    {
        boolean exFlag = false;
        try
        {
            //System.out.println("1st Query");
            topicManager.getMessageNumber(null);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }

        assertTrue(exFlag);

/************************************************************ */

        List<Integer> topicIds = new ArrayList<Integer>();
        exFlag = false;
        try
        {
            //System.out.println("2nd Query");
            topicManager.getMessageNumber(topicIds);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }

        assertTrue(exFlag);

/************************************************************ */

        topicIds = new ArrayList<Integer>();
        //System.out.println("3rd Query");
        List<Topic> topicsList = topicManager.getTopics();
        if(topicsList == null)
        {
            System.out.println("Table Topic is empty");
            return;
        }
        int i = 0;
        List<Integer> topicsListNums = new ArrayList<Integer>();
        for(Topic t: topicsList)
        {
            if((int)(random() * 2) == 1)
            {
                topicsListNums.add(i);
                topicIds.add(t.getTopicId());
            }

            i++;
        }
        //System.out.println("4th Query");
        long actualMessageNumber = topicManager.getMessageNumber(topicIds);

        Set<Message> messagesSet = new HashSet<Message>();

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        for(Integer id : topicsListNums)
        {
            Topic curTopic = topicsList.get(id);
            session.refresh(curTopic);
            messagesSet.addAll(curTopic.getMessages());
        }

        session.getTransaction().commit();

        long expectedMessageNumber = messagesSet.size();

        assertEquals(expectedMessageNumber, actualMessageNumber);
    }

    public void testGetPath()
    {
        List<Topic> topicsList = topicManager.getTopics();
        if(topicsList == null)
        {
            System.out.println("Table Topic is empty");
            return;
        }
        int randomNum = (int) (random() * topicsList.size());
        Topic topic = topicsList.get(randomNum);

        List<Section> expectedPath = new ArrayList<Section>();

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        session.refresh(topic);
        Section curSection = topic.getSection();
        while(curSection != null)
        {
            expectedPath.add(curSection);
            curSection = curSection.getUpsection();
        }

        session.getTransaction().commit();

        Collections.reverse(expectedPath);

        List<Section> actualPath = topicManager.getPath(topic.getTopicId());

        assertNotNull(actualPath);
        assertEquals(expectedPath.size(), actualPath.size());
        for(int i = 0; i < expectedPath.size(); i++)
        {
            SectionManagerTest.compSections(expectedPath.get(i), actualPath.get(i));
        }

/****************************************************************** */

        int freeTopicId = topicManager.getFreeId();
        actualPath = topicManager.getPath(freeTopicId);
        assertNull(actualPath);

/****************************************************************** */

        actualPath = topicManager.getPath(null);
        assertEquals(actualPath.size(), 0);
    }

    private List<TopicMessage> getMessageHierarchy(Message root, int level)
    {
        List<TopicMessage> result = new ArrayList<TopicMessage>();
        if(root == null)
            return result;

        if(level == 1)
        {
            TopicMessage tm1 = new TopicMessage(level, root);
            result.add(tm1);
            level++;
        }

        List<Message> downmessages = new ArrayList<Message>(root.getDownmessages());
        Collections.sort(downmessages, Comparator.comparing(Message::getDate));
        if(downmessages.size() == 0)
        {
            return result;
        }

        for(Message d : downmessages)
        {
            TopicMessage tm = new TopicMessage(level, d);
            result.add(tm);
            result.addAll(getMessageHierarchy(d, level + 1));
        }
        return result;
    }

    public void testGetTopicMessageList()
    {
        List<Topic> topicsList = topicManager.getTopics();
        if(topicsList == null)
        {
            System.out.println("Table Topic is empty");
            return;
        }
        int randomNum = (int) (random() * topicsList.size());
        //int randomNum = 0;
        Topic topic = topicsList.get(randomNum);
    
        List<TopicMessage> actualList = topicManager.getTopicMessageList(topic.getTopicId());

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        Message rootMessage = (Message) session.createQuery("from Message where topic.topicId = ?0 and upmessage.messageId is null")
            .setParameter(0, topic.getTopicId())
            .uniqueResult();
        List<TopicMessage> expectedList = getMessageHierarchy(rootMessage, 1);

        assertEquals(expectedList.size(), actualList.size());

        for(int i = 0; i < expectedList.size(); i++)
        {
            compTopicMessages(expectedList.get(i), actualList.get(i));
        }

        session.getTransaction().commit();

        /*********************************************** */

        int freeTopicId = topicManager.getFreeId();
        boolean exFlag = false;
        try
        {
            topicManager.getTopicMessageList(freeTopicId);
        }
        catch(IllegalArgumentException e)
        {
            exFlag = true;
        }

        assertTrue(exFlag);
    }

    public void testCreateMessage()
    {
        String header = "message_header";

        Message message = new Message();

        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Message> messagesList = session.createCriteria(Message.class)
            .list();
        int randomNum = (int) (random() * messagesList.size());
        Message upmessage = messagesList.get(randomNum);
        message.setUpmessage(upmessage);

        message.setTopic(upmessage.getTopic());

        List<User> usersList = session.createCriteria(User.class)
            .list();
        randomNum = (int) (random() * usersList.size());
        User author = usersList.get(randomNum);
        message.setAuthor(author);

        message.setDate(new Date());
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

        topicManager.createMessage(message);

        session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        Message messageInDb = session.get(Message.class, new Integer(message.getMessageId()));
        assertNotNull(messageInDb);
        compMessages(message, messageInDb);
        assertNotNull(messageInDb.getTopic());
        assertEquals(message.getTopic().getTopicId(), messageInDb.getTopic().getTopicId());
        assertNotNull(messageInDb.getUpmessage());
        assertEquals(upmessage.getMessageId(), message.getUpmessage().getMessageId());
        assertNotNull(messageInDb.getAuthor());
        assertEquals(author.getUserId(), messageInDb.getAuthor().getUserId());

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
        session.delete(messageInDb);

        session.getTransaction().commit();
    }

    private Set<Message> getMessageHierarchy(Set<Message> roots)
    {
        Set<Message> result = new HashSet<Message>();
        if(roots == null)
            return result;
        
        for(Message r : roots)
        {
            if(result.add(r))
            {
                result.addAll(getMessageHierarchy(r.getDownmessages()));
            }
        }

        return result;
    }

    public void testDeleteMessages()
    {
        Session session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        List<Message> allMessages = session.createCriteria(Message.class)
            .setFetchMode("messageObjects", FetchMode.JOIN)
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();

        Set<Message> messages = new HashSet<Message>();
        
        for(Message m : allMessages)
        {
            if(((int) (random() * 2)) == 1)
            {
                messages.add(m);
            }
        }
        
        Set<Message> mesHierarchy = getMessageHierarchy(messages);

        session.getTransaction().commit();

        List<Integer> messageIds = new ArrayList<Integer>();
        for(Message m : messages)
        {
            messageIds.add(m.getMessageId());
        }
        
        topicManager.deleteMessages(messageIds);

        session = HibernateUtil.getSessionFactory()
            .getCurrentSession();
        session.beginTransaction();

        for(Message mes : mesHierarchy)
        {
            List<Message> messagesInDb = session.createCriteria(Message.class)
                .add(Property.forName("upmessage.messageId").eq(mes.getMessageId()))
                .list();
            assertEquals(messagesInDb.size(), 0);

            Message mesInDb = session.get(Message.class, new Integer(mes.getMessageId()));
            assertNull(mesInDb);
            for(MessageObject mo : mes.getMessageObjects())
            {
                MessageObject moInDb = session.get(MessageObject.class, new Integer(mo.getMessageObjectId()));
                assertNull(moInDb);
            }
            List<MessageObject> mosInDb = session.createCriteria(MessageObject.class)
                .add(Property.forName("message.messageId").eq(mes.getMessageId()))
                .list();
            assertEquals(mosInDb.size(), 0);
        }

        session.getTransaction().commit();
    }
}