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
 *     David McCann - Aug.16, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.customsql;

//javase imports
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
//import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;

//testing imports
import dbws.testing.DBWSTestSuite;

public class CustomSQLTestSuite extends DBWSTestSuite {

    static final String CREATE_CUSTOM_TABLE =
        "CREATE TABLE CUSTOM (" +
            "\nEMPNO NUMERIC(4)," +
            "\nENAME VARCHAR(10)," +
            "\nJOB VARCHAR(9)," +
            "\nMGR NUMERIC(4)," +
            "\nHIREDATE DATE," +
            "\nSAL DECIMAL(7,2)," +
            "\nCOMM NUMERIC(7,2)," +
            "\nDEPTNO NUMERIC(2)," +
            "\nPRIMARY KEY (EMPNO)" +
        "\n)";
    static final String[] POPULATE_CUSTOM_TABLE = new String[] {
        "INSERT INTO CUSTOM VALUES (7369,'SMITH','CLERK',7902,'1980-12-17',800,NULL,20)",
        "INSERT INTO CUSTOM VALUES (7499,'ALLEN','SALESMAN',7698,'1981-2-20',1600,300,30)",
        "INSERT INTO CUSTOM VALUES (7521,'WARD','SALESMAN',7698,'1981-2-22',1250,500,30)",
        "INSERT INTO CUSTOM VALUES (7566,'JONES','MANAGER',7839,'1981-4-2',2975,NULL,20)",
        "INSERT INTO CUSTOM VALUES (7654,'MARTIN','SALESMAN',7698,'1981-9-28',1250,1400,30)",
        "INSERT INTO CUSTOM VALUES (7698,'BLAKE','MANAGER',7839,'1981-5-1',2850,NULL,30)",
        "INSERT INTO CUSTOM VALUES (7782,'CLARK','MANAGER',7839,'1981-6-9',2450,NULL,10)",
        "INSERT INTO CUSTOM VALUES (7788,'SCOTT','ANALYST',7566,'1981-06-09',3000.99,NULL,20)",
        "INSERT INTO CUSTOM VALUES (7839,'KING','PRESIDENT',NULL,'1981-11-17',5000.99,NULL,10)",
        "INSERT INTO CUSTOM VALUES (7844,'TURNER','SALESMAN',7698,'1981-9-8',1500,0,30)",
        "INSERT INTO CUSTOM VALUES (7876,'ADAMS','CLERK',7788,'1987-05-23',1100,NULL,20)",
        "INSERT INTO CUSTOM VALUES (7900,'JAMES','CLERK',7698,'1981-12-03',950,NULL,30)",
        "INSERT INTO CUSTOM VALUES (7902,'FORD','ANALYST',7566,'1981-12-03',3000,NULL,20)",
        "INSERT INTO CUSTOM VALUES (7934,'MILLER','CLERK',7782,'1982-01-23',1300,NULL,10)"
    };
    static final String DROP_CUSTOM_TABLE =
        "DROP TABLE CUSTOM";

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
            runDdl(conn, CREATE_CUSTOM_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_CUSTOM_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_CUSTOM_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                if (ddlDebug) {
                    e.printStackTrace();
                }
            }
        }
        DBWS_BUILDER_XML_USERNAME =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
          "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "<properties>" +
                "<property name=\"projectName\">customSQL</property>" +
                "<property name=\"logLevel\">off</property>" +
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
              "tableNamePattern=\"CUSTOM\" " +
              ">" +
              "<sql " +
                "name=\"countCustom\" " +
                "isCollection=\"false\" " +
                "simpleXMLFormatTag=\"custom-info\" " +
                "xmlTag=\"aggregate-info\" " +
                ">" +
                "<text><![CDATA[select count(*) from CUSTOM]]></text> " +
              "</sql>" +
              "<sql " +
                "name=\"customInfo\" " +
                "isCollection=\"false\" " +
                "simpleXMLFormatTag=\"custom-info\" " +
                "xmlTag=\"aggregate-info\" " +
                "> " +
                "<text><![CDATA[select count(*) as \"COUNT\", CAST(max(SAL) as NUMBER(7,2)) \"MAX-Salary\" from CUSTOM]]></text>" +
              "</sql>" +
            "</table>" +
          "</dbws-builder>";
        builder = null;
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_CUSTOM_TABLE, ddlDebug);
        }
    }

    @Test
    public void countCustom() {
        Invocation invocation = new Invocation("countCustom");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(COUNT_CUSTOM_CONTROL_DOC));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String COUNT_CUSTOM_CONTROL_DOC =
        REGULAR_XML_HEADER +
        "<custom-info xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"simple-xml-format\">" +
          "<aggregate-info>" +
            "<COUNT_x0028__x002A__x0029_>14</COUNT_x0028__x002A__x0029_>" +
          "</aggregate-info>" +
        "</custom-info>";

    @Test
    public void countMaxSalCustomSQLInfo() {
        Invocation invocation = new Invocation("customInfo");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(COUNT_MAXSAL_CONTROL_DOC));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String COUNT_MAXSAL_CONTROL_DOC =
        REGULAR_XML_HEADER +
        "<custom-info xsi:type=\"simple-xml-format\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<aggregate-info>" +
            "<COUNT>14</COUNT>" +
            "<MAX-Salary>5000.99</MAX-Salary>" +
          "</aggregate-info>" +
        "</custom-info>";

    @Test
    public void findByPrimaryKey() {
        Invocation invocation = new Invocation("findByPrimaryKey_customType");
        invocation.setParameter("empno", 7788);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(FINDBYPK_7788_CONTROL_DOC));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String FINDBYPK_7788_CONTROL_DOC =
        REGULAR_XML_HEADER +
        "<customType xmlns=\"urn:customSQL\">" +
          "<empno>7788</empno>" +
          "<ename>SCOTT</ename>" +
          "<job>ANALYST</job>" +
          "<mgr>7566</mgr>" +
          "<hiredate>1981-06-09</hiredate>" +
          "<sal>3000.99</sal>" +
          "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
          "<deptno>20</deptno>" +
        "</customType>";

    @SuppressWarnings("rawtypes")
    @Test
    public void findAll() {
        Invocation invocation = new Invocation("findAll_customType");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("collection");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(FIND_ALL_CONTROL_DOC));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String FIND_ALL_CONTROL_DOC =
        REGULAR_XML_HEADER +
        "<collection>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7369</empno>" +
                "<ename>SMITH</ename>" +
                "<job>CLERK</job>" +
                "<mgr>7902</mgr>" +
                "<hiredate>1980-12-17</hiredate>" +
                "<sal>800</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>20</deptno>" +
            "</customType>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7499</empno>" +
                "<ename>ALLEN</ename>" +
                "<job>SALESMAN</job>" +
                "<mgr>7698</mgr>" +
                "<hiredate>1981-02-20</hiredate>" +
                "<sal>1600</sal>" +
                "<comm>300</comm>" +
                "<deptno>30</deptno>" +
            "</customType>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7521</empno>" +
                "<ename>WARD</ename>" +
                "<job>SALESMAN</job>" +
                "<mgr>7698</mgr>" +
                "<hiredate>1981-02-22</hiredate>" +
                "<sal>1250</sal>" +
                "<comm>500</comm>" +
                "<deptno>30</deptno>" +
            "</customType>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7566</empno>" +
                "<ename>JONES</ename>" +
                "<job>MANAGER</job>" +
                "<mgr>7839</mgr>" +
                "<hiredate>1981-04-02</hiredate>" +
                "<sal>2975</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>20</deptno>" +
            "</customType>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7654</empno>" +
                "<ename>MARTIN</ename>" +
                "<job>SALESMAN</job>" +
                "<mgr>7698</mgr>" +
                "<hiredate>1981-09-28</hiredate>" +
                "<sal>1250</sal>" +
                "<comm>1400</comm>" +
                "<deptno>30</deptno>" +
            "</customType>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7698</empno>" +
                "<ename>BLAKE</ename>" +
                "<job>MANAGER</job>" +
                "<mgr>7839</mgr>" +
                "<hiredate>1981-05-01</hiredate>" +
                "<sal>2850</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>30</deptno>" +
            "</customType>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7782</empno>" +
                "<ename>CLARK</ename>" +
                "<job>MANAGER</job>" +
                "<mgr>7839</mgr>" +
                "<hiredate>1981-06-09</hiredate>" +
                "<sal>2450</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>10</deptno>" +
            "</customType>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7788</empno>" +
                "<ename>SCOTT</ename>" +
                "<job>ANALYST</job>" +
                "<mgr>7566</mgr>" +
                "<hiredate>1981-06-09</hiredate>" +
                "<sal>3000.99</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>20</deptno>" +
            "</customType>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7839</empno>" +
                "<ename>KING</ename>" +
                "<job>PRESIDENT</job>" +
                "<mgr xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<hiredate>1981-11-17</hiredate>" +
                "<sal>5000.99</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>10</deptno>" +
            "</customType>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7844</empno>" +
                "<ename>TURNER</ename>" +
                "<job>SALESMAN</job>" +
                "<mgr>7698</mgr>" +
                "<hiredate>1981-09-08</hiredate>" +
                "<sal>1500</sal>" +
                "<comm>0</comm>" +
                "<deptno>30</deptno>" +
            "</customType>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7876</empno>" +
                "<ename>ADAMS</ename>" +
                "<job>CLERK</job>" +
                "<mgr>7788</mgr>" +
                "<hiredate>1987-05-23</hiredate>" +
                "<sal>1100</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>20</deptno>" +
            "</customType>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7900</empno>" +
                "<ename>JAMES</ename>" +
                "<job>CLERK</job>" +
                "<mgr>7698</mgr>" +
                "<hiredate>1981-12-03</hiredate>" +
                "<sal>950</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>30</deptno>" +
            "</customType>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7902</empno>" +
                "<ename>FORD</ename>" +
                "<job>ANALYST</job>" +
                "<mgr>7566</mgr>" +
                "<hiredate>1981-12-03</hiredate>" +
                "<sal>3000</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>20</deptno>" +
            "</customType>" +
            "<customType xmlns=\"urn:customSQL\">" +
                "<empno>7934</empno>" +
                "<ename>MILLER</ename>" +
                "<job>CLERK</job>" +
                "<mgr>7782</mgr>" +
                "<hiredate>1982-01-23</hiredate>" +
                "<sal>1300</sal>" +
                "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                "<deptno>10</deptno>" +
            "</customType>" +
        "</collection>";
}