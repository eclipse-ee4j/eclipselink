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
 *     David McCann - Sept. 07, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.simpleplsqlsf;

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

public class SimplePLSQLSFTestSuite extends DBWSTestSuite {

    static final String CREATE_SIMPLESF_TABLE =
        "CREATE TABLE SIMPLESF (" +
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
        "INSERT INTO SIMPLESF VALUES (7369,'SMITH','CLERK',7902," +
            "TO_DATE('1980-12-17 00:00:00','YYYY-MM-DD HH24:MI:SS'),800,NULL,20)",
        "INSERT INTO SIMPLESF VALUES (7499,'ALLEN','SALESMAN',7698," +
            "TO_DATE('1981-2-20 00:00:00','YYYY-MM-DD HH24:MI:SS'),1600,300,30)",
        "INSERT INTO SIMPLESF VALUES (7521,'WARD','SALESMAN',7698," +
            "TO_DATE('1981-2-22 00:00:00','YYYY-MM-DD HH24:MI:SS'),1250,500,30)",
        "INSERT INTO SIMPLESF VALUES (7566,'JONES','MANAGER',7839," +
            "TO_DATE('1981-4-2 00:00:00','YYYY-MM-DD HH24:MI:SS'),2975,NULL,20)",
        "INSERT INTO SIMPLESF VALUES (7654,'MARTIN','SALESMAN',7698," +
            "TO_DATE('1981-9-28 00:00:00','YYYY-MM-DD HH24:MI:SS'),1250,1400,30)",
        "INSERT INTO SIMPLESF VALUES (7698,'BLAKE','MANAGER',7839," +
            "TO_DATE('1981-5-1 00:00:00','YYYY-MM-DD HH24:MI:SS'),2850,NULL,30)",
        "INSERT INTO SIMPLESF VALUES (7782,'CLARK','MANAGER',7839," +
            "TO_DATE('1981-6-9 00:00:00','YYYY-MM-DD HH24:MI:SS'),2450,NULL,10)",
        "INSERT INTO SIMPLESF VALUES (7788,'SCOTT','ANALYST',7566," +
            "TO_DATE('1981-06-09 00:00:00','YYYY-MM-DD HH24:MI:SS'),3000,NULL,20)",
        "INSERT INTO SIMPLESF VALUES (7839,'KING','PRESIDENT',NULL," +
            "TO_DATE('1981-11-17 00:00:00','YYYY-MM-DD HH24:MI:SS'),5000,NULL,10)",
        "INSERT INTO SIMPLESF VALUES (7844,'TURNER','SALESMAN',7698," +
            "TO_DATE('1981-9-8 00:00:00','YYYY-MM-DD HH24:MI:SS'),1500,0,30)",
        "INSERT INTO SIMPLESF VALUES (7876,'ADAMS','CLERK',7788," +
            "TO_DATE('1987-05-23 00:00:00','YYYY-MM-DD HH24:MI:SS'),1100,NULL,20)",
        "INSERT INTO SIMPLESF VALUES (7900,'JAMES','CLERK',7698," +
            "TO_DATE('1981-12-03 00:00:00','YYYY-MM-DD HH24:MI:SS'),950,NULL,30)",
        "INSERT INTO SIMPLESF VALUES (7902,'FORD','ANALYST',7566," +
            "TO_DATE('1981-12-03 00:00:00','YYYY-MM-DD HH24:MI:SS'),3000,NULL,20)",
        "INSERT INTO SIMPLESF VALUES (7934,'MILLER','CLERK',7782," +
            "TO_DATE('1982-01-23 00:00:00','YYYY-MM-DD HH24:MI:SS'),1300,NULL,10)"
    };
    static final String CREATE_SIMPLEPACKAGE2_PACKAGE =
        "CREATE OR REPLACE PACKAGE SIMPLEPACKAGE2 AS" +
            "\nFUNCTION FINDPLSQLMAXSAL RETURN DECIMAL;" +
            "\nFUNCTION FINDPLSQLMAXSALFORDEPT(DEPT IN DECIMAL) RETURN DECIMAL;" +
        "\nEND SIMPLEPACKAGE2;";
    static final String CREATE_SIMPLEPACKAGE2_BODY =
        "CREATE OR REPLACE PACKAGE BODY SIMPLEPACKAGE2 AS" +
            "\nFUNCTION FINDPLSQLMAXSAL RETURN DECIMAL AS" +
            "\nMAXSAL DECIMAL(7,2);" +
            "\nBEGIN" +
                "\nSELECT max(SAL) INTO MAXSAL FROM SIMPLESF;" +
                "\nRETURN(MAXSAL);" +
            "\nEND FINDPLSQLMAXSAL;" +
            "\nFUNCTION FINDPLSQLMAXSALFORDEPT(DEPT IN DECIMAL) RETURN DECIMAL AS" +
            "\nMAXSAL DECIMAL(7,2);" +
            "\nBEGIN" +
                "\nSELECT max(SAL) INTO MAXSAL FROM SIMPLESF WHERE DEPTNO = DEPT;" +
                "\nRETURN(MAXSAL);" +
            "\nEND FINDPLSQLMAXSALFORDEPT;" +
        "\nEND SIMPLEPACKAGE2;";
    static final String DROP_SIMPLESF_TABLE =
        "DROP TABLE SIMPLESF";
    static final String DROP_SIMPLEPACKAGE2_PACKAGE =
        "DROP PACKAGE SIMPLEPACKAGE2";

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
                createDbArtifact(conn, CREATE_SIMPLESF_TABLE);
                createDbArtifact(conn, CREATE_SIMPLEPACKAGE2_PACKAGE);
                createDbArtifact(conn, CREATE_SIMPLEPACKAGE2_BODY);
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
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
              "<plsql-procedure " +
                 "name=\"FindMaxSalTest\" " +
                 "catalogPattern=\"SIMPLEPACKAGE2\" " +
                 "procedurePattern=\"FINDPLSQLMAXSAL\" " +
                 "isCollection=\"false\" " +
                 "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"FindMaxSalForDeptTest\" " +
                  "catalogPattern=\"SIMPLEPACKAGE2\" " +
                  "procedurePattern=\"FINDPLSQLMAXSALFOR%\" " +
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
        if ("true".equalsIgnoreCase(ddl)) {
            dropDbArtifact(conn, DROP_SIMPLEPACKAGE2_PACKAGE);
            dropDbArtifact(conn, DROP_SIMPLESF_TABLE);
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
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
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
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<max-sal-for-dept xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"any\">" +
          "<simple-xml>" +
            "<result>2850</result>" +
          "</simple-xml>" +
        "</max-sal-for-dept>";
}