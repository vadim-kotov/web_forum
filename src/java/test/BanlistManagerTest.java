package test;

import model.banlist.Banlist;

import static org.junit.Assert.*;

public class BanlistManagerTest
{
    static public void compBanlists(Banlist expected, Banlist actual)
    {
        assertEquals(expected.getClass(), actual.getClass());
        assertEquals(expected.getBanlistId(), actual.getBanlistId());
        assertEquals(expected.getBanDate(), actual.getBanDate());
        assertEquals(expected.getUnbanDate(), actual.getUnbanDate());
        assertEquals(expected.getSource(), actual.getSource());
    }
}