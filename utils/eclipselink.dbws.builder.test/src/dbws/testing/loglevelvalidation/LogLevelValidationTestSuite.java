/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * David McCann - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.loglevelvalidation;

import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import javax.wsdl.WSDLException;

import org.eclipse.persistence.logging.SessionLog;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import dbws.testing.DBWSTestProviderHelper;
import dbws.testing.DBWSTestSuite;

/**
 * Test suite used to validate the DBWSBuilder's ability to detect an invalid session
 * log level and set the default (info).
 */
public class LogLevelValidationTestSuite extends DBWSTestSuite {
    final static String username = System.getProperty(DBWSTestProviderHelper.DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
    final static String password = System.getProperty(DBWSTestProviderHelper.DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
    final static String url = System.getProperty(DBWSTestProviderHelper.DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
    final static String driver = System.getProperty(DBWSTestProviderHelper.DATABASE_DRIVER_KEY, DBWSTestProviderHelper.DEFAULT_DATABASE_DRIVER);
    final static String platform = System.getProperty(DBWSTestProviderHelper.DATABASE_PLATFORM_KEY, DBWSTestProviderHelper.DEFAULT_DATABASE_PLATFORM);
    final static String version = System.getProperty("release.version", "2.4.0");
    final static String info_level = "info";
    final static String off_level = "off";
    final static String stageDir = "./";

    @BeforeClass
    public static void setUp() throws WSDLException {
        DBWS_BUILDER_XML_USERNAME =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
          "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "<properties>" +
                "<property name=\"projectName\">logLevelValidation</property>" +
                "<property name=\"logLevel\">finist</property>" +
                "<property name=\"username\">";
        DBWS_BUILDER_XML_PASSWORD =
                "</property><property name=\"password\">";
        DBWS_BUILDER_XML_URL =
                "</property><property name=\"url\">";
        DBWS_BUILDER_XML_DRIVER =
                "</property><property name=\"driver\">";
        DBWS_BUILDER_XML_PLATFORM =
                "</property><property name=\"platformClassname\">";
        DBWS_BUILDER_XML_MAIN =
                "</property>" +
            "</properties>" +
            "<table " +
              "schemaPattern=\"%\" " +
              "tableNamePattern=\"simpletable\" " +
            "/>" +
          "</dbws-builder>";
        
        setUp(stageDir);
    }

    static String SESSIONS_XML = 
    	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
    	"<sessions xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\""+version+"\">" +
    		"<session xsi:type=\"database-session\">" +
    			"<name>logLevelValidation-dbws-or-session</name>" +
    			"<logging xsi:type=\"eclipselink-log\" />" +
    			"<primary-project xsi:type=\"xml\">eclipselink-dbws-or.xml</primary-project>" +
    			"<login xsi:type=\"database-login\">" +
    				"<platform-class>org.eclipse.persistence.platform.database.MySQLPlatform</platform-class>" +
    				"<user-name>"+username+"</user-name>" +
    				"<password>"+password+"</password>" +
    				"<driver-class>"+driver+"</driver-class>" +
    				"<connection-url>"+url+"</connection-url>" +
    				"<byte-array-binding>false</byte-array-binding>" +
    				"<streams-for-binding>true</streams-for-binding>" +
    				"<optimize-data-conversion>false</optimize-data-conversion>" +
    				"<trim-strings>false</trim-strings>" +
    			"</login>" +
    		"</session>" +
    		"<session xsi:type=\"database-session\">" +
    			"<name>logLevelValidation-dbws-ox-session</name>" +
    			"<logging xsi:type=\"eclipselink-log\">" +
    			"<log-level>"+off_level+"</log-level>" +
    			"</logging>" +
    			"<primary-project xsi:type=\"xml\">eclipselink-dbws-ox.xml</primary-project>" +
    		"</session>" +
    	"</sessions>";
    
    @Test
    /**
     * Validate that the invalid session log level "finist" is set to the default
     * "info" by the builder.
     * 
     * Positive test.
     */
    public void testInvalidLogLevel() {
        Document testDoc = xmlParser.parse(new StringReader(DBWS_SESSION_STREAM.toString()));
        removeEmptyTextNodes(testDoc);
        Document controlDoc = xmlParser.parse(new StringReader(SESSIONS_XML));
        removeEmptyTextNodes(controlDoc);
        assertTrue("control document not same as instance document", comparer.isNodeEqual(controlDoc, testDoc));
    }
}
