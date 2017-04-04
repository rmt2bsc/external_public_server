package org.rmt2.config;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.util.RMT2File;
import com.util.RMT2String2;

/**
 * Tests the availability and functionality of application configuration.
 * 
 * @author Roy Terrell
 *
 */
public class AppServerConfigurationTest {
    protected static String APP_CONFIG_FILENAME;

    @Before
    public void setUp() throws Exception {
        // /Users/royterrell/work/External_WebServices_Server
        String curDir = RMT2File.getCurrentDirectory();
        APP_CONFIG_FILENAME = curDir + "/src/test/resources/config/TestAppServerConfig.xml";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConfigExistence() {
        Assert.assertNotNull(APP_CONFIG_FILENAME);
        String xmlDoc = null;
        try {
            xmlDoc = RMT2File.getTextFileContents(APP_CONFIG_FILENAME);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Error loading application configuration file, " + APP_CONFIG_FILENAME);
        }
        Assert.assertNotNull(xmlDoc);
        Assert.assertTrue(RMT2String2.isNotEmpty(xmlDoc));
        System.out.print(xmlDoc);
    }

}
