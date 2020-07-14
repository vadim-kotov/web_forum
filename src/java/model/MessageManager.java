package model;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import util.HibernateUtil;

import org.hibernate.criterion.*;

import java.util.List;

public class MessageManager
{
    public List<Integer> getMessageFromTopicIds(List<Integer> topicIds)
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

        List<Integer> messageIds = null;
        try
        {
            messageIds = session.createCriteria(Message.class)
                .add(Property.forName("topic.topicId").in(topicIds))
                .setProjection(Property.forName("messageId"))
                .list();
            
            session.getTransaction().commit();
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
        
        return messageIds;
    }
}