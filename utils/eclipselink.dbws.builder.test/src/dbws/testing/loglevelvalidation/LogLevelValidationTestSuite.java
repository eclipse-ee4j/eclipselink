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

//javase imports
import java.io.StringReader;
import java.lang.reflect.Field;
import org.w3c.dom.Document;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.sessions.factories.XMLSessionConfigProject_11_1_1;
import org.eclipse.persistence.internal.sessions.factories.model.SessionConfigs;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.converters.XMLConverterAdapter;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.tools.dbws.BaseDBWSBuilderHelper;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.jdbc.JDBCHelper;

//testing imports
import dbws.testing.DBWSTestSuite;

/**
 * Test suite used to validate the DBWSBuilder's ability to detect an invalid session
 * log level and set the default (info).
 */
public class LogLevelValidationTestSuite extends DBWSTestSuite {

    static final String CREATE_LOGLEVEL_TABLE =
        "CREATE TABLE IF NOT EXISTS loglevel (" +
            "\nID NUMERIC NOT NULL," +
            "\nNAME VARCHAR(25)," +
            "\nSINCE DATE," +
            "\nPRIMARY KEY (ID)" +
        "\n)";
    static final String DROP_LOGLEVEL_TABLE =
        "DROP TABLE loglevel";

    // JUnit test fixtures
    final static String username =
        System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
    final static String password =
        System.getProperty(DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
    final static String url =
        System.getProperty(DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
    final static String VERSION = "SOME_VERSION";
    final static String info_level = "info";
    final static String off_level = "off";
    final static String stageDir = "./";

    static boolean ddlCreate = false;
    static boolean ddlDrop = false;
    static boolean ddlDebug = false;

    @BeforeClass
    public static void setUp() throws WSDLException {
        if (conn == null) {
            try {
                conn = buildConnection();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        String ddlCreateProp = System.getProperty(DATABASE_DDL_CREATE_KEY, DEFAULT_DATABASE_DDL_CREATE);
        if ("true".equalsIgnoreCase(ddlCreateProp)) {
            ddlCreate = true;
        }
        String ddlDropProp = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDropProp)) {
            ddlDrop = true;
        }
        String ddlDebugProp = System.getProperty(DATABASE_DDL_DEBUG_KEY, DEFAULT_DATABASE_DDL_DEBUG);
        if ("true".equalsIgnoreCase(ddlDebugProp)) {
            ddlDebug = true;
        }
        if (ddlCreate) {
            runDdl(conn, CREATE_LOGLEVEL_TABLE, ddlDebug);
        }
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
              "tableNamePattern=\"loglevel\" " +
            "/>" +
          "</dbws-builder>";
        builder = new DBWSBuilder();
        JDBCHelper builderHelper = new JDBCHelper(builder);
        builder.setBuilderHelper(builderHelper);
        Field sessConfigProj_field = null;
        try {
            sessConfigProj_field =
                BaseDBWSBuilderHelper.class.getDeclaredField("sessionConfigProject");
            sessConfigProj_field.setAccessible(true);
        }
        catch (Exception e) {
            // should never happen
            e.printStackTrace();
        }
        XMLSessionConfigProject_11_1_1 sessionConfigProject = null;
        try {
            sessionConfigProject = (XMLSessionConfigProject_11_1_1)sessConfigProj_field.get(builderHelper);
        }
        catch (Exception e) {
            // should never happen
            e.printStackTrace();
        }
        XMLDirectMapping versionMapping =
            (XMLDirectMapping)sessionConfigProject.getDescriptor(SessionConfigs.class).
                getMappings().firstElement();
        versionMapping.setConverter(new XMLConverterAdapter() {
            public Object convertObjectValueToDataValue(Object objectValue, Session session,
                XMLMarshaller marshaller) {
                return VERSION;
            }
            public Object convertDataValueToObjectValue(Object dataValue, Session session,
                XMLUnmarshaller unmarshaller) {
                return dataValue;
            }
            public boolean isMutable() {
                return false;
            }
            public void initialize(DatabaseMapping mapping, Session session) {
            }
        });
        DBWSTestSuite.setUp(stageDir);
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_LOGLEVEL_TABLE, ddlDebug);
        }
    }

    static String SESSIONS_XML =
    	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    	"<sessions xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\""+VERSION+"\">" +
    		"<session xsi:type=\"database-session\">" +
    			"<name>logLevelValidation-dbws-or-session</name>" +
    			"<logging xsi:type=\"eclipselink-log\" />" +
    			"<primary-project xsi:type=\"xml\">eclipselink-dbws-or.xml</primary-project>" +
    			"<login xsi:type=\"database-login\">" +
    				"<platform-class>org.eclipse.persistence.platform.database.MySQLPlatform</platform-class>" +
    				"<user-name>"+username+"</user-name>" +
    				"<password>"+password+"</password>" +
    				"<driver-class>"+DATABASE_DRIVER+"</driver-class>" +
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
     * Validate that the invalid session log level "finest" is set to the default
     * "info" by the builder.
     *
     * Positive test.
     */
    public void testInvalidLogLevel() {
        Document testDoc = xmlParser.parse(new StringReader(DBWS_SESSION_STREAM.toString()));
        removeEmptyTextNodes(testDoc);
        Document controlDoc = xmlParser.parse(new StringReader(SESSIONS_XML));
        removeEmptyTextNodes(controlDoc);
        assertTrue("Control document not same as instance document.\n Expected:\n" +
            documentToString(controlDoc) + "\nActual:\n" + documentToString(testDoc),
            comparer.isNodeEqual(controlDoc, testDoc));
    }
}
