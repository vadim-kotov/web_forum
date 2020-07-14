package test;

import model.MessageObject;
import model.Text;
import model.Image;
import model.Quote;

import static org.junit.Assert.*;

public class MessageObjectManagerTest
{
    static void compMessageObjects(MessageObject expected, MessageObject actual)
    {
        assertTrue(expected.getClass() == actual.getClass());

        assertEquals(expected.getMessageObjectId(), actual.getMessageObjectId());
        assertEquals(expected.getValue(), actual.getValue());
        if(expected.getClass() == Image.class)
        {
            assertEquals(((Image)expected).getWidth(), ((Image)actual).getWidth());
            assertEquals(((Image)expected).getHeight(), ((Image)actual).getHeight());
        }
    }
}