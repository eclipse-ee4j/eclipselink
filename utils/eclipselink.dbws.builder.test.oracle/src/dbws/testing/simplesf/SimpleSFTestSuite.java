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
 *     David McCann - August 31, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.simplesf;

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
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.oracle.OracleHelper;

//test imports
import dbws.testing.DBWSTestSuite;

public class SimpleSFTestSuite extends DBWSTestSuite {

    static final String CREATE_SIMPLESF_TABLE =
        "CREATE TABLE SIMPLESF2 (" +
            "\nEMPNO DECIMAL(4,0) NOT NULL," +
            "\nENAME VARCHAR(10)," +
            "\nJOB VARCHAR(9)," +
            "\nMGR DECIMAL(4,0)," +
            "\nHIREDATE DATE," +
            "\nSAL DECIMAL(7,2)," +
            "\nCOMM DECIMAL(7,2)," +
            "\nDEPTNO DECIMAL(2,0)," +
            "\nPRIMARY KEY (EMPNO)" +
        "\n)";
    static final String[] POPULATE_SIMPLESF_TABLE = new String[] {
        "INSERT INTO SIMPLESF2 VALUES (7369,'SMITH','CLERK',7902," +
            "TO_DATE('1980-12-17 00:00:00','YYYY-MM-DD HH24:MI:SS'),800,NULL,20)",
        "INSERT INTO SIMPLESF2 VALUES (7499,'ALLEN','SALESMAN',7698," +
            "TO_DATE('1981-2-20 00:00:00','YYYY-MM-DD HH24:MI:SS'),1600,300,30)",
        "INSERT INTO SIMPLESF2 VALUES (7521,'WARD','SALESMAN',7698," +
            "TO_DATE('1981-2-22 00:00:00','YYYY-MM-DD HH24:MI:SS'),1250,500,30)",
        "INSERT INTO SIMPLESF2 VALUES (7566,'JONES','MANAGER',7839," +
            "TO_DATE('1981-4-2 00:00:00','YYYY-MM-DD HH24:MI:SS'),2975,NULL,20)",
        "INSERT INTO SIMPLESF2 VALUES (7654,'MARTIN','SALESMAN',7698," +
            "TO_DATE('1981-9-28 00:00:00','YYYY-MM-DD HH24:MI:SS'),1250,1400,30)",
        "INSERT INTO SIMPLESF2 VALUES (7698,'BLAKE','MANAGER',7839," +
            "TO_DATE('1981-5-1 00:00:00','YYYY-MM-DD HH24:MI:SS'),2850,NULL,30)",
        "INSERT INTO SIMPLESF2 VALUES (7782,'CLARK','MANAGER',7839," +
            "TO_DATE('1981-6-9 00:00:00','YYYY-MM-DD HH24:MI:SS'),2450,NULL,10)",
        "INSERT INTO SIMPLESF2 VALUES (7788,'SCOTT','ANALYST',7566," +
            "TO_DATE('1981-06-09 00:00:00','YYYY-MM-DD HH24:MI:SS'),3000,NULL,20)",
        "INSERT INTO SIMPLESF2 VALUES (7839,'KING','PRESIDENT',NULL," +
            "TO_DATE('1981-11-17 00:00:00','YYYY-MM-DD HH24:MI:SS'),5000,NULL,10)",
        "INSERT INTO SIMPLESF2 VALUES (7844,'TURNER','SALESMAN',7698," +
            "TO_DATE('1981-9-8 00:00:00','YYYY-MM-DD HH24:MI:SS'),1500,0,30)",
        "INSERT INTO SIMPLESF2 VALUES (7876,'ADAMS','CLERK',7788," +
            "TO_DATE('1987-05-23 00:00:00','YYYY-MM-DD HH24:MI:SS'),1100,NULL,20)",
        "INSERT INTO SIMPLESF2 VALUES (7900,'JAMES','CLERK',7698," +
            "TO_DATE('1981-12-03 00:00:00','YYYY-MM-DD HH24:MI:SS'),950,NULL,30)",
        "INSERT INTO SIMPLESF2 VALUES (7902,'FORD','ANALYST',7566," +
            "TO_DATE('1981-12-03 00:00:00','YYYY-MM-DD HH24:MI:SS'),3000,NULL,20)",
        "INSERT INTO SIMPLESF2 VALUES (7934,'MILLER','CLERK',7782," +
            "TO_DATE('1982-01-23 00:00:00','YYYY-MM-DD HH24:MI:SS'),1300,NULL,10)"
    };
    static final String CREATE_FINDMAXSAL_FUNC =
        "CREATE OR REPLACE FUNCTION FINDMAXSAL RETURN DECIMAL AS" +
        "\nMAXSAL DECIMAL(7,2);" +
        "\nBEGIN" +
            "\nSELECT max(SAL) INTO MAXSAL FROM SIMPLESF2;" +
            "\nRETURN(MAXSAL);" +
        "\nEND FINDMAXSAL;";
    static final String CREATE_FINDMAXSALFORDEPT_FUNC =
        "CREATE OR REPLACE FUNCTION FINDMAXSALFORDEPT(DEPT IN DECIMAL) RETURN DECIMAL AS" +
        "\nMAXSAL DECIMAL(7,2);" +
        "\nBEGIN" +
            "\nSELECT max(SAL) INTO MAXSAL FROM SIMPLESF2 WHERE DEPTNO = DEPT;" +
            "\nRETURN(MAXSAL);" +
        "\nEND FINDMAXSALFORDEPT;";
    static final String DROP_SIMPLESF_TABLE =
        "DROP TABLE SIMPLESF2";
    static final String DROP_FINDMAXSAL_FUNC =
        "DROP FUNCTION FINDMAXSAL";
    static final String DROP_FINDMAXSALFORDEPT_FUNC =
        "DROP FUNCTION FINDMAXSALFORDEPT";

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
            runDdl(conn, CREATE_SIMPLESF_TABLE, ddlDebug);
            runDdl(conn, CREATE_FINDMAXSAL_FUNC, ddlDebug);
            runDdl(conn, CREATE_FINDMAXSALFORDEPT_FUNC, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_SIMPLESF_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_SIMPLESF_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">simpleSF</property>" +
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
                 "name=\"FindMaxSalTest\" " +
                 "procedurePattern=\"FINDMAXSAL\" " +
                 "isCollection=\"false\" " +
                 "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<procedure " +
                  "name=\"FindMaxSalForDeptTest\" " +
                  "procedurePattern=\"FINDMAXSALFORDEPT\" " +
                  "isCollection=\"false\" " +
                  "isSimpleXMLFormat=\"true\" " +
                  "simpleXMLFormatTag=\"max-sal-for-dept\" " +
              "/>" +
            "</dbws-builder>";
          builder = new DBWSBuilder();
          OracleHelper builderHelper = new OracleHelper(builder);
          builder.setBuilderHelper(builderHelper);
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_FINDMAXSAL_FUNC, ddlDebug);
            runDdl(conn, DROP_FINDMAXSALFORDEPT_FUNC, ddlDebug);
            runDdl(conn, DROP_SIMPLESF_TABLE, ddlDebug);
        }
    }

    @Test
    public void findMaxSalTest() {
        Invocation invocation = new Invocation("FindMaxSalTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(FIND_MAX_SAL_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String FIND_MAX_SAL_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
          "<simple-xml>" +
            "<result>5000</result>" +
          "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void findMaxSalForDeptTest() {
        Invocation invocation = new Invocation("FindMaxSalForDeptTest");
        invocation.setParameter("DEPT", 30);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(FIND_MAX_SAL_FOR_DEPT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String FIND_MAX_SAL_FOR_DEPT_XML =
        REGULAR_XML_HEADER +
        "<max-sal-for-dept xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"any\">" +
          "<simple-xml>" +
            "<result>2850</result>" +
          "</simple-xml>" +
        "</max-sal-for-dept>";
}