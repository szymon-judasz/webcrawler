package org.javauj.webcrawler;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SQLiteLoggerTest extends TestCase
{
	public SQLiteLoggerTest(String testName)
	{
		super(testName);
	}
	
    public static Test suite()
    {
        return new TestSuite( SQLiteLoggerTest.class );
    }
    
    public void testSQliteLogger()
    {
    	SQLiteLogger logger = new SQLiteLogger("test.db", true);
    	try {
			logger.logSite(new URL("http://www.oracle.com/"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	assertTrue(logger.getConnectionStatus());
    	logger.close();
    	assertFalse(logger.getConnectionStatus());
    }

}
