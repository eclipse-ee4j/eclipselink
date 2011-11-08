/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 2008, created DBWS test package
 ******************************************************************************/
package dbws.testing.simplesp;

//javase imports
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;

import org.w3c.dom.Document;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.platform.database.MySQLPlatform;

//testing imports
import dbws.testing.DBWSTestSuite;

public class SimpleSPTestSuite extends DBWSTestSuite {

    static final String CREATE_SIMPLESP_TABLE =
        "CREATE TABLE IF NOT EXISTS simplesp (" +
            "\nEMPNO decimal(4,0)," +
            "\nENAME varchar(10)," +
            "\nJOB varchar(9)," +
            "\nMGR decimal(4,0)," +
            "\nHIREDATE date," +
            "\nSAL decimal(7,2)," +
            "\nCOMM decimal(7,2)," +
            "\nDEPTNO decimal(2)," +
            "\nPRIMARY KEY (EMPNO)" +
        "\n)";
    static String[] POPULATE_SIMPLESP_TABLE = new String[] {
        "INSERT INTO simplesp VALUES (7369,'SMITH','CLERK',7902,'1980-12--17',800,NULL,20)",
        "INSERT INTO simplesp VALUES (7499,'ALLEN','SALESMAN',7698,'1981-2-20',1600,300,30)",
        "INSERT INTO simplesp VALUES (7521,'WARD','SALESMAN',7698,'1981-2-22',1250,500,30)",
        "INSERT INTO simplesp VALUES (7566,'JONES','MANAGER',7839,'1981-4-2',2975,NULL,20)",
        "INSERT INTO simplesp VALUES (7654,'MARTIN','SALESMAN',7698,'1981-9-28',1250,1400,30)",
        "INSERT INTO simplesp VALUES (7698,'BLAKE','MANAGER',7839,'1981-5-1',2850,NULL,30)",
        "INSERT INTO simplesp VALUES (7782,'CLARK','MANAGER',7839,'1981-6-9',2450,NULL,10)",
        "INSERT INTO simplesp VALUES (7788,'SCOTT','ANALYST',7566,'1981-06-09',3000,NULL,20)",
        "INSERT INTO simplesp VALUES (7839,'KING','PRESIDENT',NULL,'1981-11-17',5000,NULL,10)",
        "INSERT INTO simplesp VALUES (7844,'TURNER','SALESMAN',7698,'1981-9-8',1500,0,30)",
        "INSERT INTO simplesp VALUES (7876,'ADAMS','CLERK',7788,'1987-05-23',1100,NULL,20)",
        "INSERT INTO simplesp VALUES (7900,'JAMES','CLERK',7698,'1981-12-03',950,NULL,30)",
        "INSERT INTO simplesp VALUES (7902,'FORD','ANALYST',7566,'1981-12-03',3000,NULL,20)",
        "INSERT INTO simplesp VALUES (7934,'MILLER','CLERK',7782,'1982-01-23',1300,NULL,10)"
    };
    static final String CREATE_NOARGSP_PROC =
        "CREATE PROCEDURE NoArgSP()" +
        "\nBEGIN" +
        "\nEND";
    static final String CREATE_VARCHARSP_PROC =
        "CREATE PROCEDURE VarcharSP(IN X VARCHAR(20))" +
        "\nBEGIN" +
        "\nEND";
    static final String CREATE_GETALL_PROC =
        "CREATE PROCEDURE GetAll()" +
        "\nBEGIN" +
            "\nSELECT * FROM simplesp;" +
        "\nEND";
    static final String CREATE_FINDBYJOB_PROC =
        "CREATE PROCEDURE FindByJob(IN J VARCHAR(29))" +
        "\nBEGIN" +
            "\nSELECT * FROM simplesp WHERE JOB LIKE J;" +
        "\nEND";
    static final String CREATE_INOUTARGSP_PROC =
        "CREATE PROCEDURE InOutArgsSP(IN T VARCHAR(20), OUT U VARCHAR(20), OUT V NUMERIC)" +
        "\nBEGIN" +
            "\nset U = CONCAT('barf-' , T);" +
            "\nset V = 55;" +
        "\nEND";
    static final String DROP_SIMPLESP_TABLE =
        "DROP TABLE simplesp";
    static final String DROP_NOARGSP_PROC =
        "DROP PROCEDURE NoArgSP";
    static final String DROP_VARCHARSP_PROC =
        "DROP PROCEDURE VarcharSP";
    static final String DROP_GETALL_PROC =
        "DROP PROCEDURE GetAll";
    static final String DROP_FINDBYJOB_PROC =
        "DROP PROCEDURE FindByJob";
    static final String DROP_INOUTARGSP_PROC =
        "DROP PROCEDURE InOutArgsSP";

    // JUnit test fixtures
    static String ddl = "false";

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
        ddl = System.getProperty(DATABASE_DDL_KEY, DEFAULT_DATABASE_DDL);
        if ("true".equalsIgnoreCase(ddl)) {
            try {
                createDbArtifact(conn, CREATE_SIMPLESP_TABLE);
                createDbArtifact(conn, CREATE_NOARGSP_PROC);
                createDbArtifact(conn, CREATE_VARCHARSP_PROC);
                createDbArtifact(conn, CREATE_GETALL_PROC);
                createDbArtifact(conn, CREATE_FINDBYJOB_PROC);
                createDbArtifact(conn, CREATE_INOUTARGSP_PROC);
            }
            catch (SQLException e) {
                //ignore
            }
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_SIMPLESP_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_SIMPLESP_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                //ignore
            }
        }
        DBWS_BUILDER_XML_USERNAME =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
          "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "<properties>" +
                "<property name=\"projectName\">simpleSP</property>" +
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
            "<procedure " +
                "name=\"VarcharTest\" " +
                "procedurePattern=\"VarcharSP\" " +
                "returnType=\"xsd:int\" " +
            "/>" +
            "<procedure " +
                "name=\"NoArgsTest\" " +
                "procedurePattern=\"NoArgSP\" " +
                "returnType=\"xsd:int\" " +
            "/>" +
            "<procedure " +
                "name=\"GetAllTest\" " +
                "procedurePattern=\"GetAll\" " +
                "isCollection=\"true\" " +
                "isSimpleXMLFormat=\"true\" " +
                "simpleXMLFormatTag=\"simplesp-rows\" " +
                "xmlTag=\"simplesp-row\" " +
            "/>" +
            "<procedure " +
                "name=\"FindByJobTest\" " +
                "procedurePattern=\"FindByJob\" " +
                "isCollection=\"true\" " +
                "isSimpleXMLFormat=\"true\" " +
                "simpleXMLFormatTag=\"simplesp-rows\" " +
                "xmlTag=\"simplesp-row\" " +
            "/>" +
            "<procedure " +
                "name=\"InOutArgsTest\" " +
                "procedurePattern=\"InOutArgsSP\" " +
                "isSimpleXMLFormat=\"true\" " +
            "/>" +
          "</dbws-builder>";
        builder = null;
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if ("true".equalsIgnoreCase(ddl)) {
            dropDbArtifact(conn, DROP_SIMPLESP_TABLE);
            dropDbArtifact(conn, DROP_NOARGSP_PROC);
            dropDbArtifact(conn, DROP_VARCHARSP_PROC);
            dropDbArtifact(conn, DROP_GETALL_PROC);
            dropDbArtifact(conn, DROP_FINDBYJOB_PROC);
            dropDbArtifact(conn, DROP_INOUTARGSP_PROC);
        }
    }

    @Test
    public void varcharTest() {
        Invocation invocation = new Invocation("VarcharTest");
        invocation.setParameter("X", "this is a test");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(xrService.getORSession()
            .getProject().getDatasourceLogin().getPlatform() instanceof MySQLPlatform ? VALUE_0_XML
            : VALUE_1_XML));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String VALUE_0_XML =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<value>0</value>";
    public static final String VALUE_1_XML =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<value>1</value>";

    @Test
    public void noargsTest() {
        Invocation invocation = new Invocation("NoArgsTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(xrService.getORSession()
            .getProject().getDatasourceLogin().getPlatform() instanceof MySQLPlatform ? VALUE_0_XML
            : VALUE_1_XML));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }

    @Test
    public void getAllTest() {
        Invocation invocation = new Invocation("GetAllTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ALL_SIMPLESP_ROWS_XML));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ALL_SIMPLESP_ROWS_XML =
      "<?xml version = '1.0' encoding = 'UTF-8'?>" +
      "<simplesp-rows xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"any\">" +
        "<simplesp-row>" +
          "<EMPNO>7369</EMPNO>" +
          "<ENAME>SMITH</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7902</MGR>" +
          "<HIREDATE>1980-12-17</HIREDATE>" +
          "<SAL>800.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7499</EMPNO>" +
          "<ENAME>ALLEN</ENAME>" +
          "<JOB>SALESMAN</JOB>" +
          "<MGR>7698</MGR>" +
          "<HIREDATE>1981-02-20</HIREDATE>" +
          "<SAL>1600.00</SAL>" +
          "<COMM>300.00</COMM>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7521</EMPNO>" +
          "<ENAME>WARD</ENAME>" +
          "<JOB>SALESMAN</JOB>" +
          "<MGR>7698</MGR>" +
          "<HIREDATE>1981-02-22</HIREDATE>" +
          "<SAL>1250.00</SAL>" +
          "<COMM>500.00</COMM>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7566</EMPNO>" +
          "<ENAME>JONES</ENAME>" +
          "<JOB>MANAGER</JOB>" +
          "<MGR>7839</MGR>" +
          "<HIREDATE>1981-04-02</HIREDATE>" +
          "<SAL>2975.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7654</EMPNO>" +
          "<ENAME>MARTIN</ENAME>" +
          "<JOB>SALESMAN</JOB>" +
          "<MGR>7698</MGR>" +
          "<HIREDATE>1981-09-28</HIREDATE>" +
          "<SAL>1250.00</SAL>" +
          "<COMM>1400.00</COMM>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7698</EMPNO>" +
          "<ENAME>BLAKE</ENAME>" +
          "<JOB>MANAGER</JOB>" +
          "<MGR>7839</MGR>" +
          "<HIREDATE>1981-05-01</HIREDATE>" +
          "<SAL>2850.00</SAL>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7782</EMPNO>" +
          "<ENAME>CLARK</ENAME>" +
          "<JOB>MANAGER</JOB>" +
          "<MGR>7839</MGR>" +
          "<HIREDATE>1981-06-09</HIREDATE>" +
          "<SAL>2450.00</SAL>" +
          "<DEPTNO>10</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7788</EMPNO>" +
          "<ENAME>SCOTT</ENAME>" +
          "<JOB>ANALYST</JOB>" +
          "<MGR>7566</MGR>" +
          "<HIREDATE>1981-06-09</HIREDATE>" +
          "<SAL>3000.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7839</EMPNO>" +
          "<ENAME>KING</ENAME>" +
          "<JOB>PRESIDENT</JOB>" +
          "<HIREDATE>1981-11-17</HIREDATE>" +
          "<SAL>5000.00</SAL>" +
          "<DEPTNO>10</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7844</EMPNO>" +
          "<ENAME>TURNER</ENAME>" +
          "<JOB>SALESMAN</JOB>" +
          "<MGR>7698</MGR>" +
          "<HIREDATE>1981-09-08</HIREDATE>" +
          "<SAL>1500.00</SAL>" +
          "<COMM>0.00</COMM>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7876</EMPNO>" +
          "<ENAME>ADAMS</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7788</MGR>" +
          "<HIREDATE>1987-05-23</HIREDATE>" +
          "<SAL>1100.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7900</EMPNO>" +
          "<ENAME>JAMES</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7698</MGR>" +
          "<HIREDATE>1981-12-03</HIREDATE>" +
          "<SAL>950.00</SAL>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7902</EMPNO>" +
          "<ENAME>FORD</ENAME>" +
          "<JOB>ANALYST</JOB>" +
          "<MGR>7566</MGR>" +
          "<HIREDATE>1981-12-03</HIREDATE>" +
          "<SAL>3000.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7934</EMPNO>" +
          "<ENAME>MILLER</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7782</MGR>" +
          "<HIREDATE>1982-01-23</HIREDATE>" +
          "<SAL>1300.00</SAL>" +
          "<DEPTNO>10</DEPTNO>" +
        "</simplesp-row>" +
      "</simplesp-rows>";

    @Test
    public void findByJobTest() {
        Invocation invocation = new Invocation("FindByJobTest");
        invocation.setParameter("J", "CL%");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ALL_SIMPLESP_CLERK_ROWS_XML));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ALL_SIMPLESP_CLERK_ROWS_XML =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<simplesp-rows xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"any\">" +
        "<simplesp-row>" +
          "<EMPNO>7369</EMPNO>" +
          "<ENAME>SMITH</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7902</MGR>" +
          "<HIREDATE>1980-12-17</HIREDATE>" +
          "<SAL>800.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7876</EMPNO>" +
          "<ENAME>ADAMS</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7788</MGR>" +
          "<HIREDATE>1987-05-23</HIREDATE>" +
          "<SAL>1100.00</SAL>" +
          "<DEPTNO>20</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7900</EMPNO>" +
          "<ENAME>JAMES</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7698</MGR>" +
          "<HIREDATE>1981-12-03</HIREDATE>" +
          "<SAL>950.00</SAL>" +
          "<DEPTNO>30</DEPTNO>" +
        "</simplesp-row>" +
        "<simplesp-row>" +
          "<EMPNO>7934</EMPNO>" +
          "<ENAME>MILLER</ENAME>" +
          "<JOB>CLERK</JOB>" +
          "<MGR>7782</MGR>" +
          "<HIREDATE>1982-01-23</HIREDATE>" +
          "<SAL>1300.00</SAL>" +
          "<DEPTNO>10</DEPTNO>" +
        "</simplesp-row>" +
      "</simplesp-rows>";

    @Test
    public void inOutArgsTest() {
        Invocation invocation = new Invocation("InOutArgsTest");
        invocation.setParameter("T", "yuck");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(IN_OUT_ARGS_XML));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String IN_OUT_ARGS_XML =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<simple-xml-format>" +
        "<simple-xml>" +
          "<U>barf-yuck</U>" +
          "<V>55</V>" +
        "</simple-xml>" +
      "</simple-xml-format>";
}