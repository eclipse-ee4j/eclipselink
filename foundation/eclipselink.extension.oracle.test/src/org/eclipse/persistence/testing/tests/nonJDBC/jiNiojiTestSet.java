package org.eclipse.persistence.testing.tests.nonJDBC;

// javase imports
import java.io.FileInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.Vector;
import org.w3c.dom.Document;

// JUnit imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

// EclipseLink imports
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.plsql.OraclePLSQLTypes;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;

// Domain imports
import static org.eclipse.persistence.testing.tests.nonJDBC.NonJDBCTestHelper.buildTestProject;
import static org.eclipse.persistence.testing.tests.nonJDBC.NonJDBCTestHelper.buildWorkbenchXMLProject;
import static org.eclipse.persistence.testing.tests.nonJDBC.NonJDBCTestHelper.CONSTANT_PROJECT_BUILD_VERSION;
import static org.eclipse.persistence.testing.tests.nonJDBC.NonJDBCTestHelper.comparer;
import static org.eclipse.persistence.testing.tests.nonJDBC.NonJDBCTestHelper.TEST_DOT_PROPERTIES_KEY;
import static org.eclipse.persistence.testing.tests.nonJDBC.NonJDBCTestHelper.xmlParser;

 /*
  N == Non-JDBC type
  j == JDBC type
  i - IN parameter
  o - OUT parameter
  io - INOUT parameter
  */

public class jiNiojiTestSet {

    // testsuite fixture(s)
    static ObjectPersistenceWorkbenchXMLProject workbenchXMLProject;
    static Project project = null;

    @BeforeClass
    public static void setUpProjects() {
        try {
            Properties p = new Properties();
            String testPropertiesPath = System.getProperty(TEST_DOT_PROPERTIES_KEY);
            p.load(new FileInputStream(testPropertiesPath));
            project = buildTestProject(p);
            workbenchXMLProject = buildWorkbenchXMLProject();
        }
        catch (Exception e) {
            fail("error setting up Project's database properties " + e.getMessage());
        }
    }

    @Test
    public void writeToXml() {
        // PROCEDURE JINIOJI(X IN VARCHAR, Y IN OUT PLS_INTEGER, Z IN NUMBER)
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("jiNioji");
        call.addNamedArgument("X", JDBCTypes.VARCHAR_TYPE, 25);
        call.addNamedInOutputArgument("Y", OraclePLSQLTypes.PLSQLInteger);
        call.addNamedArgument("Z", JDBCTypes.NUMERIC_TYPE, 22, 2);
        DataReadQuery query = new DataReadQuery();
        query.addArgument("X", String.class);
        query.addArgument("Y", Integer.class);
        query.addArgument("Z", BigDecimal.class);
        query.setCall(call);
        project.getDescriptor(Empty.class).getQueryManager().addQuery("jiNioji", query);
        Project projectToXml = (Project)project.clone();
        // trim off login 'cause it changes under test - this way, a comparison
        // can be done to a control document
        projectToXml.setDatasourceLogin(null);
        XMLContext context = new XMLContext(workbenchXMLProject);
        XMLMarshaller marshaller = context.createMarshaller();
        Document doc = marshaller.objectToXML(projectToXml);
        Document controlDoc = xmlParser.parse(new StringReader(JINIOJI_PROJECT_XML));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String JINIOJI_PROJECT_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<eclipselink:object-persistence version=\"" + CONSTANT_PROJECT_BUILD_VERSION + "\"" + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + " xmlns:eclipselink=\"http://xmlns.oracle.com/ias/xsds/eclipselink\">" +
           "<eclipselink:name>nonJDBCTestProject</eclipselink:name>" +
           "<eclipselink:class-mapping-descriptors>" +
             "<eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:relational-class-mapping-descriptor\">" +
                "<eclipselink:class>org.eclipse.persistence.testing.tests.nonJDBC.Empty</eclipselink:class>" +
                "<eclipselink:alias>Empty</eclipselink:alias>" +
                "<eclipselink:primary-key>" +
                  "<eclipselink:field table=\"EMPTY\" name=\"ID\" xsi:type=\"eclipselink:column\"/>" +
                "</eclipselink:primary-key>" +
                 "<eclipselink:events xsi:type=\"eclipselink:event-policy\"/>" +
                 "<eclipselink:querying xsi:type=\"eclipselink:query-policy\">" +
                    "<eclipselink:queries>" +
                       "<eclipselink:query name=\"jiNioji\" xsi:type=\"eclipselink:data-read-query\">" +
                          "<eclipselink:arguments>" +
                             "<eclipselink:argument name=\"X\">" +
                                "<eclipselink:type>java.lang.String</eclipselink:type>" +
                             "</eclipselink:argument>" +
                             "<eclipselink:argument name=\"Y\">" +
                                "<eclipselink:type>java.lang.Integer</eclipselink:type>" +
                             "</eclipselink:argument>" +
                             "<eclipselink:argument name=\"Z\">" +
                                "<eclipselink:type>java.math.BigDecimal</eclipselink:type>" +
                             "</eclipselink:argument>" +
                          "</eclipselink:arguments>" +
                          "<eclipselink:maintain-cache>false</eclipselink:maintain-cache>" +
                          "<eclipselink:call xsi:type=\"eclipselink:plsql-stored-procedure-call\">" +
                             "<eclipselink:procedure-name>jiNioji</eclipselink:procedure-name>" +
                             "<eclipselink:arguments>" +
                                "<eclipselink:argument xsi:type=\"eclipselink:jdbc-type\" type-name=\"VARCHAR_TYPE\">" +
                                   "<eclipselink:name>X</eclipselink:name>" +
                                   "<eclipselink:index>0</eclipselink:index>" +
                                   "<eclipselink:length>25</eclipselink:length>" +
                                "</eclipselink:argument>" +
                                "<eclipselink:argument xsi:type=\"eclipselink:plsql-type\" type-name=\"PLSQLInteger\">" +
                                   "<eclipselink:name>Y</eclipselink:name>" +
                                   "<eclipselink:index>1</eclipselink:index>" +
                                   "<eclipselink:direction>INOUT</eclipselink:direction>" +
                                "</eclipselink:argument>" +
                                "<eclipselink:argument xsi:type=\"eclipselink:jdbc-type\" type-name=\"NUMERIC_TYPE\">" +
                                   "<eclipselink:name>Z</eclipselink:name>" +
                                   "<eclipselink:index>2</eclipselink:index>" +
                                   "<eclipselink:precision>22</eclipselink:precision>" +
                                   "<eclipselink:scale>2</eclipselink:scale>" +
                                "</eclipselink:argument>" +
                             "</eclipselink:arguments>" +
                          "</eclipselink:call>" +
                          "<eclipselink:container xsi:type=\"eclipselink:list-container-policy\">" +
                             "<eclipselink:collection-type>java.util.Vector</eclipselink:collection-type>" +
                          "</eclipselink:container>" +
                       "</eclipselink:query>" +
                    "</eclipselink:queries>" +
                 "</eclipselink:querying>" +
                 "<eclipselink:attribute-mappings>" +
                    "<eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
                       "<eclipselink:attribute-name>id</eclipselink:attribute-name>" +
                       "<eclipselink:field table=\"EMPTY\" name=\"ID\" xsi:type=\"eclipselink:column\"/>" +
                    "</eclipselink:attribute-mapping>" +
                 "</eclipselink:attribute-mappings>" +
                 "<eclipselink:descriptor-type>independent</eclipselink:descriptor-type>" +
                 "<eclipselink:instantiation/>" +
                 "<eclipselink:copying xsi:type=\"eclipselink:instantiation-copy-policy\"/>" +
                 "<eclipselink:tables>" +
                    "<eclipselink:table name=\"EMPTY\"/>" +
                 "</eclipselink:tables>" +
              "</eclipselink:class-mapping-descriptor>" +
           "</eclipselink:class-mapping-descriptors>" +
        "</eclipselink:object-persistence>";

    @Test
    public void readFromXml() {
        Project projectFromXML = XMLProjectReader.read(new StringReader(JINIOJI_PROJECT_XML),
            this.getClass().getClassLoader());
        projectFromXML.setDatasourceLogin(project.getDatasourceLogin());
        project = projectFromXML;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void runQuery() {
        Session s = project.createDatabaseSession();
        s.dontLogMessages();
        ((DatabaseSession)s).login();
        Object o = null;
        Vector queryArgs = new NonSynchronizedVector();
        queryArgs.add("snicker-doodle");
        queryArgs.add(Integer.valueOf(102));
        queryArgs.add(Integer.valueOf(103));
        boolean worked = false;
        String msg = null;
        try {
          o = s.executeQuery("jiNioji", Empty.class, queryArgs);
          worked = true;
        }
        catch (Exception e) {
          msg = e.getMessage();
        }
        assertTrue("invocation jiNioji failed: " + msg, worked);
        Vector results = (Vector)o;
        DatabaseRecord record = (DatabaseRecord)results.get(0);
        BigDecimal y = (BigDecimal)record.get("Y");
        assertTrue("wrong y value", y.intValue() == 149);
        ((DatabaseSession)s).logout();
    }
}
