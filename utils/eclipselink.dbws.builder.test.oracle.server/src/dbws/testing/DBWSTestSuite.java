/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David McCann - Aug.02, 2012 - 2.4.1 - Initial implementation
 ******************************************************************************/
package dbws.testing;

//javase imports
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//java eXtension imports
import javax.wsdl.WSDLException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.XRServiceAdapter;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

public class DBWSTestSuite {

    public static final String DATABASE_DRIVER_KEY = "db.driver";
    public static final String DEFAULT_DATABASE_DRIVER = "oracle.jdbc.OracleDriver";
    public static final String DATABASE_PLATFORM_KEY = "db.platform";
    public static final String DEFAULT_DATABASE_PLATFORM =
        "org.eclipse.persistence.platform.database.oracle.Oracle11Platform";
    public static final String DATABASE_USERNAME_KEY = "db.user";
    public static final String DATABASE_PASSWORD_KEY = "db.pwd";
    public static final String DATABASE_URL_KEY = "db.url";
    public static final String SERVER_PLATFORM_KEY = "server.platform";
    public static final String SERVER_DATASOURCE_KEY = "server.datasource";
    public static final String SERVER_HOST_KEY = "server.host";
    public static final String SERVER_PORT_KEY = "server.port";
    public static final String DEFAULT_DATABASE_USERNAME = "user";
    public static final String DEFAULT_DATABASE_PASSWORD = "password";
    public static final String DEFAULT_DATABASE_URL = "jdbc:oracle:thin:@localhost:1521:ORCL";
    public static final String DATABASE_DDL_CREATE_KEY = "db.ddl.create";
    public static final String DEFAULT_DATABASE_DDL_CREATE = "false";
    public static final String DATABASE_DDL_DROP_KEY = "db.ddl.drop";
    public static final String DEFAULT_DATABASE_DDL_DROP = "false";
    public static final String DATABASE_DDL_DEBUG_KEY = "db.ddl.debug";
    public static final String DEFAULT_DATABASE_DDL_DEBUG = "false";
    public static final String DEFAULT_SERVER_PLATFORM = "wls";
    public static final String DEFAULT_SERVER_DATASOURCE = "jdbc/DBWStestDS";
    public static final String DEFAULT_SERVER_HOST = "localhost";
    public static final String DEFAULT_SERVER_PORT = "7001";
    public static final String RELEASE_VERSION_KEY = "release.version";
    public static final String DEFAULT_RELEASE_VERSION= "2.5.0";
    public static String releaseVersion =
        System.getProperty(RELEASE_VERSION_KEY, DEFAULT_RELEASE_VERSION);
    public static final String REGULAR_XML_HEADER =
        "<?xml version = '1.0' encoding = 'UTF-8'?>";
    public static final String STANDALONE_XML_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    //shared JUnit fixtures
    protected static Connection conn;
    protected static String username = null;
    protected static String password = null;
    protected static String url = null;
    protected static String driver = null;
    protected static String platform = null;
    protected static String host = null;
    protected static String port = null;
    protected static String datasource = null;
    protected static String serverPlatform = null;
    protected static boolean ddlCreate = false;
    protected static boolean ddlDrop = false;
    protected static boolean ddlDebug = false;
    protected final static String stageDir = "stage";
    
    // JUnit test fixtures
    public static String DBWS_BUILDER_XML_USERNAME;
    public static String DBWS_BUILDER_XML_PASSWORD;
    public static String DBWS_BUILDER_XML_URL;
    public static String DBWS_BUILDER_XML_DRIVER;
    public static String DBWS_BUILDER_XML_PLATFORM;
    public static String DBWS_BUILDER_XML_MAIN;
    public static XMLComparer comparer = new XMLComparer();
    public static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    public static XMLParser xmlParser = xmlPlatform.newXMLParser();
    public static DBWSBuilder builder = null;
    public static XRServiceAdapter xrService = null;
    public static ByteArrayOutputStream DBWS_SERVICE_STREAM = null;
    public static ByteArrayOutputStream DBWS_SCHEMA_STREAM = null;
    public static ByteArrayOutputStream DBWS_SESSION_STREAM = null;
    public static ByteArrayOutputStream DBWS_OR_STREAM = null;
    public static ByteArrayOutputStream DBWS_OX_STREAM = null;
    public static ByteArrayOutputStream DBWS_WSDL_STREAM = null;

    protected static DBWSLogger dbwsLogger;

    static {
        username   = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        password   = System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
        url        = System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
        driver     = System.getProperty(DATABASE_DRIVER_KEY, DEFAULT_DATABASE_DRIVER);
        platform   = System.getProperty(DATABASE_PLATFORM_KEY, DEFAULT_DATABASE_PLATFORM);

        serverPlatform = System.getProperty(SERVER_PLATFORM_KEY, DEFAULT_SERVER_PLATFORM);
        host = System.getProperty(SERVER_HOST_KEY, DEFAULT_SERVER_HOST);
        port = System.getProperty(SERVER_PORT_KEY, DEFAULT_SERVER_PORT);
        datasource = System.getProperty(SERVER_DATASOURCE_KEY, DEFAULT_SERVER_DATASOURCE);
        
        ddlCreate = System.getProperty(DATABASE_DDL_CREATE_KEY, DEFAULT_DATABASE_DDL_CREATE).equals("true");
        ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP).equals("true");
        ddlDebug = System.getProperty(DATABASE_DDL_DEBUG_KEY, DEFAULT_DATABASE_DDL_DEBUG).equals("true");
    }
    
    public static void setUp(String stageDir) throws WSDLException {
    	setUp(stageDir, false);
    }
    public static void setUp(String stageDir, boolean useLogger) throws WSDLException {
        if (builder == null) {
            builder = new DBWSBuilder();
        }
    }

    public static void build(String projectName, String builderFile) throws WSDLException {
    	build(projectName, builderFile, false);
    }
    public static void build(String projectName, String builderFile, boolean useLogger) throws WSDLException {
        String[] builderArgs = new String[] {
                "-builderFile", 
                builderFile,
                "-stageDir",
                stageDir,
                "-packageAs",
                serverPlatform,
                projectName + ".war"
        };

        builder = new DBWSBuilder();
        builder.start(builderArgs);
    }

    /**
     * Creates the staging folder and writes the builder XML file to it.
     * The DB connection (if null) is created here as well. 
     */
    protected static void setupTest(String builderFile, String builderXml) {
        FileWriter builderFileWriter = null;
        try {
            new File(stageDir).mkdir();
            builderFileWriter = new FileWriter(builderFile);
            builderFileWriter.write(builderXml);
            builderFileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (builderFileWriter != null) {
                try {
                    builderFileWriter.close();
                    builderFileWriter.close();
                } catch (IOException e) {}
            }
        }
        if (conn == null) {
            try {
                conn = buildConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Build a web archive based on a given builder-xml file.
     */
    protected static void testBuild(String projectName, String builderFile) {
        try {
            DBWSTestSuite.build(projectName, builderFile);
            File warFile = new File(stageDir + "/" + projectName + ".war");
            assertTrue("Web archive [" + warFile.getName() + "] was not generated as expected.", warFile.exists());
        } catch (WSDLException e) {
            fail("Build phase failed: " + e.getMessage());
        }
    }
    
    /**
     * Helper method that removes empty text nodes from a Document.
     * This is typically called prior to comparing two documents
     * for equality.
     *
     */
    public static void removeEmptyTextNodes(Node node) {
        NodeList nodeList = node.getChildNodes();
        Node childNode;
        for (int x = nodeList.getLength() - 1; x >= 0; x--) {
            childNode = nodeList.item(x);
            if (childNode.getNodeType() == Node.TEXT_NODE) {
                if (childNode.getNodeValue().trim().equals("")) {
                    node.removeChild(childNode);
                }
            } else if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                removeEmptyTextNodes(childNode);
            }
        }
    }

    static Pattern pattern = Pattern.compile("[\\n\\x0B\\f\\r]");
    public static String removeLineTerminators(CharSequence inputStr) {
        String replaceStr = "";
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.replaceAll(replaceStr);
    }

    /**
     * Returns the given org.w3c.dom.Document as a String.
     *
     */
    public static String documentToString(Document doc) {
        DOMSource domSource = new DOMSource(doc);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.transform(domSource, result);
            return stringWriter.toString();
        } catch (Exception e) {
            // e.printStackTrace();
            return "<empty/>";
        }
    }

    public static Connection buildConnection() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static void runDdl(Connection conn, String ddl, boolean printStackTrace) {
        try {
            PreparedStatement pStmt = conn.prepareStatement(ddl);
            pStmt.execute();
        }
        catch (SQLException e) {
            if (printStackTrace) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Logger to test that a given message was logged correctly.
     *
     */
    public static class DBWSLogger extends Logger {
        List<String> messages;

        protected DBWSLogger(String name, String resourceBundleName) {
            super(name, resourceBundleName);
            messages = new ArrayList<String>();
        }

        public void log(Level level, String msg) {
            // System.out.println(level.getName() + ": " + msg);
            messages.add(level.getName() + ": " + msg);
        }

        public boolean hasMessages() {
            return messages != null && messages.size() > 0;
        }

        public boolean hasWarnings() {
            if (messages != null || messages.size() > 0) {
                for (String message : messages) {
                    if (message.startsWith("WARNING")) {
                        return true;
                    }
                }
            }
            return false;
        }

        public List<String> getWarnings() {
            List<String> warnings = null;
            if (messages != null || messages.size() > 0) {
                warnings = new ArrayList<String>();
                for (String message : messages) {
                    if (message.startsWith("WARNING")) {
                        warnings.add(message);
                    }
                }
            }
            return warnings;
        }

        public List<String> getMessages() {
            return messages;
        }
    }

    /**
     * Create a SOAP message based on a given String.
     */
    public static SOAPMessage createSOAPMessage(String message) {
        SOAPMessage soapMessage;
        try {
            MessageFactory factory = MessageFactory.newInstance();
            soapMessage = factory.createMessage();
            soapMessage.getSOAPPart().setContent((Source)new StreamSource(new StringReader(message)));
            soapMessage.saveChanges();
        } catch (Exception e) {
            e.printStackTrace();
            soapMessage = null;
        }
        return soapMessage;
    }
}