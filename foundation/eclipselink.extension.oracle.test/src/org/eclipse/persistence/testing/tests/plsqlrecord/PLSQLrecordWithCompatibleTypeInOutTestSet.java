// Copyright (c) 2010 Oracle. All rights reserved.

package org.eclipse.persistence.testing.tests.plsqlrecord;

// javase imports
import java.io.FileInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Properties;
import org.w3c.dom.Document;

// JUnit imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

// EclipseLink imports
import org.eclipse.persistence.internal.helper.NonSynchronizedVector;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.platform.database.jdbc.JDBCTypes;
import org.eclipse.persistence.platform.database.oracle.Oracle8Platform;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLrecord;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;

// other imports
import static org.eclipse.persistence.testing.tests.plsqlrecord.PLSQLrecordTestHelper.CONSTANT_PROJECT_BUILD_VERSION;
import static org.eclipse.persistence.testing.tests.plsqlrecord.PLSQLrecordTestHelper.TEST_DOT_PROPERTIES_KEY;
import static org.eclipse.persistence.testing.tests.plsqlrecord.PLSQLrecordTestHelper.buildTestProject;
import static org.eclipse.persistence.testing.tests.plsqlrecord.PLSQLrecordTestHelper.buildWorkbenchXMLProject;
import static org.eclipse.persistence.testing.tests.plsqlrecord.PLSQLrecordTestHelper.comparer;
import static org.eclipse.persistence.testing.tests.plsqlrecord.PLSQLrecordTestHelper.xmlParser;

public class PLSQLrecordWithCompatibleTypeInOutTestSet  {

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

        PLSQLrecord r1 = new PLSQLrecord();
        r1.setTypeName("emp%ROWTYPE");
        r1.setCompatibleType("EMP_TYPE");
        r1.addField("EMPNO", JDBCTypes.NUMERIC_TYPE, 4, 0);
        r1.addField("ENAME", JDBCTypes.VARCHAR_TYPE, 10);
        r1.addField("JOB", JDBCTypes.VARCHAR_TYPE, 9);
        r1.addField("MGR", JDBCTypes.NUMERIC_TYPE, 4, 0);
        r1.addField("HIREDATE", JDBCTypes.DATE_TYPE);
        r1.addField("SAL", JDBCTypes.FLOAT_TYPE, 7, 2);
        r1.addField("COMM", JDBCTypes.NUMERIC_TYPE, 7, 2);
        r1.addField("DEPTNO", JDBCTypes.NUMERIC_TYPE, 2, 0);

        // PROCEDURE REC_TEST_INOUT(Z IN OUT EMP%ROWTYPE)
        PLSQLStoredProcedureCall call = new PLSQLStoredProcedureCall();
        call.setProcedureName("REC_TEST_INOUT");
        call.addNamedInOutputArgument("Z", r1);
        ReadObjectQuery query = new ReadObjectQuery(PLSQLEmployeeType.class);
        query.addArgument("Z", Struct.class);
        query.doNotCacheQueryResults();
        query.dontMaintainCache();
        query.setCall(call);

        project.getDescriptor(PLSQLEmployeeType.class).getQueryManager().
            addQuery("PLSQLrecordWithCompatibleTypeInOut", query);
        Project projectToXml = (Project)project.clone();
        // trim off login 'cause it changes under test - this way, a comparison
        // can be done to a control document
        projectToXml.setDatasourceLogin(null);
        XMLContext context = new XMLContext(workbenchXMLProject);
        XMLMarshaller marshaller = context.createMarshaller();
        Document doc = marshaller.objectToXML(projectToXml);
        Document controlDoc = xmlParser.parse(
            new StringReader(PLSQLRECORD_WITHCOMPATIBLETYPE_INOUT_PROJECT_XML));
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String PLSQLRECORD_WITHCOMPATIBLETYPE_INOUT_PROJECT_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<eclipselink:object-persistence version=\"" + CONSTANT_PROJECT_BUILD_VERSION + "\"" + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + " xmlns:eclipselink=\"http://xmlns.oracle.com/ias/xsds/eclipselink\">" +
          "<eclipselink:name>PLSQLrecordProject</eclipselink:name>" +
          "<eclipselink:class-mapping-descriptors>" +
            "<eclipselink:class-mapping-descriptor xsi:type=\"eclipselink:object-relational-class-mapping-descriptor\">" +
              "<eclipselink:class>org.eclipse.persistence.testing.tests.plsqlrecord.PLSQLEmployeeType</eclipselink:class>" +
              "<eclipselink:alias>PLSQLEmployeeType</eclipselink:alias>" +
              "<eclipselink:primary-key>" +
                 "<eclipselink:field name=\"EMPNO\" xsi:type=\"eclipselink:column\"/>" +
              "</eclipselink:primary-key>" +
              "<eclipselink:events xsi:type=\"eclipselink:event-policy\"/>" +
              "<eclipselink:querying xsi:type=\"eclipselink:query-policy\">" +
                 "<eclipselink:queries>" +
                   "<eclipselink:query name=\"PLSQLrecordWithCompatibleTypeInOut\" xsi:type=\"eclipselink:read-object-query\">" +
                      "<eclipselink:arguments>" +
                         "<eclipselink:argument name=\"Z\">" +
                            "<eclipselink:type>java.sql.Struct</eclipselink:type>" +
                         "</eclipselink:argument>" +
                      "</eclipselink:arguments>" +
                      "<eclipselink:maintain-cache>false</eclipselink:maintain-cache>" +
                      "<eclipselink:call xsi:type=\"eclipselink:plsql-stored-procedure-call\">" +
                         "<eclipselink:procedure-name>REC_TEST_INOUT</eclipselink:procedure-name>" +
                         "<eclipselink:arguments>" +
                             "<eclipselink:argument xsi:type=\"eclipselink:plsql-record\">" +
                               "<eclipselink:name>Z</eclipselink:name>" +
                               "<eclipselink:index>0</eclipselink:index>" +
                               "<eclipselink:direction>INOUT</eclipselink:direction>" +
                               "<eclipselink:record-name>Z</eclipselink:record-name>" +
                               "<eclipselink:type-name>emp%ROWTYPE</eclipselink:type-name>" +
                               "<eclipselink:compatible-type>EMP_TYPE</eclipselink:compatible-type>" +
                               "<eclipselink:fields>" +
                                  "<eclipselink:field xsi:type=\"eclipselink:jdbc-type\" type-name=\"NUMERIC_TYPE\">" +
                                     "<eclipselink:name>EMPNO</eclipselink:name>" +
                                     "<eclipselink:precision>4</eclipselink:precision>" +
                                     "<eclipselink:scale>0</eclipselink:scale>" +
                                  "</eclipselink:field>" +
                                  "<eclipselink:field xsi:type=\"eclipselink:jdbc-type\" type-name=\"VARCHAR_TYPE\">" +
                                     "<eclipselink:name>ENAME</eclipselink:name>" +
                                     "<eclipselink:length>10</eclipselink:length>" +
                                  "</eclipselink:field>" +
                                  "<eclipselink:field xsi:type=\"eclipselink:jdbc-type\" type-name=\"VARCHAR_TYPE\">" +
                                     "<eclipselink:name>JOB</eclipselink:name>" +
                                     "<eclipselink:length>9</eclipselink:length>" +
                                  "</eclipselink:field>" +
                                  "<eclipselink:field xsi:type=\"eclipselink:jdbc-type\" type-name=\"NUMERIC_TYPE\">" +
                                     "<eclipselink:name>MGR</eclipselink:name>" +
                                     "<eclipselink:precision>4</eclipselink:precision>" +
                                     "<eclipselink:scale>0</eclipselink:scale>" +
                                  "</eclipselink:field>" +
                                  "<eclipselink:field xsi:type=\"eclipselink:jdbc-type\" type-name=\"DATE_TYPE\">" +
                                     "<eclipselink:name>HIREDATE</eclipselink:name>" +
                                  "</eclipselink:field>" +
                                  "<eclipselink:field xsi:type=\"eclipselink:jdbc-type\" type-name=\"FLOAT_TYPE\">" +
                                     "<eclipselink:name>SAL</eclipselink:name>" +
                                     "<eclipselink:precision>7</eclipselink:precision>" +
                                     "<eclipselink:scale>2</eclipselink:scale>" +
                                  "</eclipselink:field>" +
                                  "<eclipselink:field xsi:type=\"eclipselink:jdbc-type\" type-name=\"NUMERIC_TYPE\">" +
                                     "<eclipselink:name>COMM</eclipselink:name>" +
                                     "<eclipselink:precision>7</eclipselink:precision>" +
                                     "<eclipselink:scale>2</eclipselink:scale>" +
                                  "</eclipselink:field>" +
                                  "<eclipselink:field xsi:type=\"eclipselink:jdbc-type\" type-name=\"NUMERIC_TYPE\">" +
                                     "<eclipselink:name>DEPTNO</eclipselink:name>" +
                                     "<eclipselink:precision>2</eclipselink:precision>" +
                                     "<eclipselink:scale>0</eclipselink:scale>" +
                                  "</eclipselink:field>" +
                               "</eclipselink:fields>" +
                            "</eclipselink:argument>" +
                         "</eclipselink:arguments>" +
                      "</eclipselink:call>" +
                      "<eclipselink:reference-class>org.eclipse.persistence.testing.tests.plsqlrecord.PLSQLEmployeeType</eclipselink:reference-class>" +
                   "</eclipselink:query>" +
                 "</eclipselink:queries>" +
              "</eclipselink:querying>" +
              "<eclipselink:attribute-mappings>" +
                 "<eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
                    "<eclipselink:attribute-name>employeeNumber</eclipselink:attribute-name>" +
                    "<eclipselink:field name=\"EMPNO\" xsi:type=\"eclipselink:column\"/>" +
                    "<eclipselink:attribute-classification>java.math.BigDecimal</eclipselink:attribute-classification>" +
                 "</eclipselink:attribute-mapping>" +
                 "<eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
                    "<eclipselink:attribute-name>name</eclipselink:attribute-name>" +
                    "<eclipselink:field name=\"ENAME\" xsi:type=\"eclipselink:column\"/>" +
                 "</eclipselink:attribute-mapping>" +
                 "<eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
                    "<eclipselink:attribute-name>job</eclipselink:attribute-name>" +
                    "<eclipselink:field name=\"JOB\" xsi:type=\"eclipselink:column\"/>" +
                 "</eclipselink:attribute-mapping>" +
                 "<eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
                    "<eclipselink:attribute-name>manager</eclipselink:attribute-name>" +
                    "<eclipselink:field name=\"MGR\" xsi:type=\"eclipselink:column\"/>" +
                    "<eclipselink:attribute-classification>java.math.BigDecimal</eclipselink:attribute-classification>" +
                 "</eclipselink:attribute-mapping>" +
                 "<eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
                    "<eclipselink:attribute-name>hireDate</eclipselink:attribute-name>" +
                    "<eclipselink:field name=\"HIREDATE\" xsi:type=\"eclipselink:column\"/>" +
                 "</eclipselink:attribute-mapping>" +
                 "<eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
                    "<eclipselink:attribute-name>salary</eclipselink:attribute-name>" +
                    "<eclipselink:field name=\"SAL\" xsi:type=\"eclipselink:column\"/>" +
                    "<eclipselink:attribute-classification>java.lang.Float</eclipselink:attribute-classification>" +
                 "</eclipselink:attribute-mapping>" +
                 "<eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
                    "<eclipselink:attribute-name>commission</eclipselink:attribute-name>" +
                    "<eclipselink:field name=\"COMM\" xsi:type=\"eclipselink:column\"/>" +
                    "<eclipselink:attribute-classification>java.lang.Float</eclipselink:attribute-classification>" +
                 "</eclipselink:attribute-mapping>" +
                 "<eclipselink:attribute-mapping xsi:type=\"eclipselink:direct-mapping\">" +
                    "<eclipselink:attribute-name>department</eclipselink:attribute-name>" +
                    "<eclipselink:field name=\"DEPTNO\" xsi:type=\"eclipselink:column\"/>" +
                    "<eclipselink:attribute-classification>java.math.BigDecimal</eclipselink:attribute-classification>" +
                 "</eclipselink:attribute-mapping>" +
              "</eclipselink:attribute-mappings>" +
              "<eclipselink:descriptor-type>independent</eclipselink:descriptor-type>" +
              "<eclipselink:instantiation/>" +
              "<eclipselink:copying xsi:type=\"eclipselink:instantiation-copy-policy\"/>" +
              "<eclipselink:tables>" +
                 "<eclipselink:table name=\"EMP\"/>" +
              "</eclipselink:tables>" +
              "<eclipselink:structure>EMP_TYPE</eclipselink:structure>" +
              "<eclipselink:field-order>" +
                 "<eclipselink:field name=\"EMPNO\" xsi:type=\"eclipselink:column\"/>" +
                 "<eclipselink:field name=\"ENAME\" xsi:type=\"eclipselink:column\"/>" +
                 "<eclipselink:field name=\"JOB\" xsi:type=\"eclipselink:column\"/>" +
                 "<eclipselink:field name=\"MGR\" xsi:type=\"eclipselink:column\"/>" +
                 "<eclipselink:field name=\"HIREDATE\" xsi:type=\"eclipselink:column\"/>" +
                 "<eclipselink:field name=\"SAL\" xsi:type=\"eclipselink:column\"/>" +
                 "<eclipselink:field name=\"COMM\" xsi:type=\"eclipselink:column\"/>" +
                 "<eclipselink:field name=\"DEPTNO\" xsi:type=\"eclipselink:column\"/>" +
              "</eclipselink:field-order>" +
            "</eclipselink:class-mapping-descriptor>" +
          "</eclipselink:class-mapping-descriptors>" +
        "</eclipselink:object-persistence>";

    @Test
    public void readFromXml() {
        Project projectFromXML = XMLProjectReader.read(new StringReader(PLSQLRECORD_WITHCOMPATIBLETYPE_INOUT_PROJECT_XML),
            this.getClass().getClassLoader());
        projectFromXML.setDatasourceLogin(project.getDatasourceLogin());
        project = projectFromXML;
    }

    @Test
    public void runQuery() {
        Session s = project.createDatabaseSession();
        s.dontLogMessages();
        ((DatabaseSession)s).login();
        Object[] attributes = {
            new BigDecimal(10),
            "MikeNorman",
            "Developer",
            null,
            new Date(System.currentTimeMillis()),
            new BigDecimal(3000),
            new BigDecimal(100),
            null,
        };
        Struct struct = null;
        try {
            struct = ((Oracle8Platform)s.getPlatform()).createStruct(
                "EMP_TYPE", attributes, ((DatabaseSessionImpl)s).getAccessor().getConnection());
        }
        catch (SQLException e1) {
            e1.printStackTrace();
        }
        NonSynchronizedVector queryArgs = new NonSynchronizedVector();
        queryArgs.add(struct);
        boolean worked = false;
        String msg = null;
        PLSQLEmployeeType result = null;
        try {
            result = (PLSQLEmployeeType)s.executeQuery("PLSQLrecordWithCompatibleTypeInOut",
                PLSQLEmployeeType.class, queryArgs);
            worked = true;
        }
        catch (Exception e) {
            msg = e.getMessage();
        }
        assertTrue("invocation rec_test_inout failed: " + msg, worked);
        assertNotNull("result is supposed to be not-null", result);
        assertTrue("incorrect EMPNO" , result.employeeNumber.equals(new BigDecimal(1234)));
        assertTrue("incorrect ENAME" , result.name.equals("GOOFY"));
        assertTrue("incorrect JOB" , result.job.equals("ACTOR"));
        assertNull("MGR is supposed to be null",  result.manager);
        assertTrue("incorrect SAL" , result.salary.equals(new Float(3500)));
        assertTrue("incorrect DEPTNO" , result.department.equals(new BigDecimal(20)));
        ((DatabaseSession)s).logout();
    }
}
