package ru.webforum.test;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import ru.webforum.util.HibernateUtil;

import ru.webforum.model.User;

public class HibernateTest
{
    public static void main(String args[]) throws Exception
    {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        User user;
        try
        {
            List userList = session.createQuery("from User").list(); 
            session.getTransaction().commit();
            for(int i = 0; i < userList.size(); i++)
            {
                user = (User) userList.get(i);
                System.out.println("" + (i + 1) + ": " + user.getLogin() + " " + user.getRegistDate());
            }
        }
        catch(HibernateException e)
        {
            session.getTransaction().rollback();
            throw e;
        }
    }
};