package test;

import model.Message;
import model.MessageManager;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import util.HibernateUtil;

import static org.junit.Assert.*;

public class MessageManagerTest extends TestCase
{
    MessageManager messageManager = new MessageManager();

    public static void main(String args[])
    {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite()
    {
        return new TestSuite(SectionManagerTest.class);
    }

    static void compMessages(Message expected, Message actual)
    {
        assertEquals(expected.getMessageId(), actual.getMessageId());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getHeader(), actual.getHeader());
    }
}