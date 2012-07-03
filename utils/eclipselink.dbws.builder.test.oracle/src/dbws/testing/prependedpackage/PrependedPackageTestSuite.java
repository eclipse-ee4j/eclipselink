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
 *     David McCann - June 12, 2012 - 2.4.1 - Initial implementation
 ******************************************************************************/
package dbws.testing.prependedpackage;

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
import org.eclipse.persistence.oxm.XMLUnmarshaller;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests parser handling of types, etc. that have the package name
 * prepended to the name, i.e. 'MYPKG.MY_CURSOR_TYPE'. 
 *
 */
public class PrependedPackageTestSuite extends DBWSTestSuite {
    static String username;
    static {
        username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
    }
    
    static final String CREATE_REF_CURSOR_PKG3 =
        "CREATE OR REPLACE PACKAGE REF_CURSOR_PKG3 AS" +
            "\nTYPE QTAB IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;" +
            "\nTYPE QRECORD IS RECORD (" +
                "\nQ1 NUMBER," +
                "\nQ2 QTAB" +
            "\n);" +
        "\nEND REF_CURSOR_PKG3;";
    
    static final String CREATE_REF_CURSOR_PKG2 =
        "CREATE OR REPLACE PACKAGE REF_CURSOR_PKG2 AS" +
          "\nTYPE typecursor IS REF CURSOR;" +
          "\nTYPE blahcursor IS REF CURSOR;" +
          "\nPROCEDURE getSomething(PARAM1 IN REF_CURSOR_PKG3.QTAB, PARAM2 OUT REF_CURSOR_PKG3.QTAB);" +
        "\nEND REF_CURSOR_PKG2;";
    static final String CREATE_REF_CURSOR_PKG2_BODY =
        "CREATE OR REPLACE PACKAGE BODY REF_CURSOR_PKG2 AS" +
            "\nPROCEDURE getSomething(PARAM1 IN REF_CURSOR_PKG3.QTAB, PARAM2 OUT REF_CURSOR_PKG3.QTAB) AS" +
            "\nBEGIN" + 
              "\nPARAM2 := PARAM1;" +
            "\nEND getSomething;" +
        "\nEND REF_CURSOR_PKG2;";

    static final String CREATE_REF_CURSOR_PKG =
        "create or replace PACKAGE  REF_CURSOR_PKG as" +
          "\nTYPE typecursor IS REF CURSOR  ;" +
          "\nPROCEDURE getEmpDataProc(PARAM1 OUT REF_CURSOR_PKG.typecursor);" +
          "\nFUNCTION getEmpData RETURN  REF_CURSOR_PKG.typecursor  ;" +
          "\nFUNCTION getEmpData2 RETURN REF_CURSOR_PKG2.typecursor;" +
          "\nFUNCTION getEmpData3(EMP_NUM NUMBER) RETURN REF_CURSOR_PKG2.blahcursor;" +
        "\nEND REF_CURSOR_PKG  ;";

    static final String CREATE_REF_CURSOR_BODY =
        "create or replace PACKAGE BODY REF_CURSOR_PKG AS" +
          "\nPROCEDURE getEmpDataProc(PARAM1 OUT REF_CURSOR_PKG.typecursor) AS" +
          "\nBEGIN" + 
            "\nOPEN PARAM1 FOR" +
            "\nSELECT  empno, ename, job, deptno FROM ref_cursor_emp  ;" +
          "\nEND getEmpDataProc;" +
          "\nFUNCTION getEmpData RETURN  REF_CURSOR_PKG.typecursor   AS" +
          "\nc_temp REF_CURSOR_PKG.typecursor  ;" +
          "\nBEGIN" +
            "\nOPEN c_temp FOR" +
            "\nSELECT  empno, ename, job, deptno FROM ref_cursor_emp  ;" +
            "\nRETURN c_temp  ;" +
          "\nEND getEmpData;" +
          "\nFUNCTION getEmpData2 RETURN REF_CURSOR_PKG2.typecursor AS" +
          "\nc_temp REF_CURSOR_PKG2.typecursor;" +
          "\nBEGIN" +
            "\nOPEN c_temp FOR" +
            "\nSELECT empno, ename, job, deptno FROM ref_cursor_emp;" +
            "\nRETURN c_temp;" +
          "\nEND getEmpData2;" +
          "\nFUNCTION getEmpData3(EMP_NUM NUMBER) RETURN REF_CURSOR_PKG2.blahcursor AS" +
          "\nc_temp REF_CURSOR_PKG2.blahcursor;" +
          "\nBEGIN" +
            "\nOPEN c_temp FOR" +
            "\nSELECT empno, ename, job, deptno FROM ref_cursor_emp WHERE empno = EMP_NUM;" +
            "\nRETURN c_temp;" +
          "\nEND getEmpData3;" +
        "\nEND REF_CURSOR_PKG;";
    
    static final String CREATE_EMP_TABLE =
        "create table ref_cursor_emp (" +
          "\nempno NUMERIC(4) NOT NULL," +
          "\nename VARCHAR(25)," +
          "\njob VARCHAR2(40)," +
          "\ndeptno NUMERIC(3)," +
          "\nPRIMARY KEY (empno)" +
        "\n)";

    static final String[] POPULATE_EMP_TABLE = new String[] {
        "INSERT INTO ref_cursor_emp (empno, ename, job, deptno) VALUES (100, 'jim', 'sales', 24)",
        "INSERT INTO ref_cursor_emp (empno, ename, job, deptno) VALUES (101, 'jack', 'delivery', 4)",
        "INSERT INTO ref_cursor_emp (empno, ename, job, deptno) VALUES (999, 'john', 'sales', 24)"
    };
    
    static final String DROP_REF_CURSOR_BODY =
        "DROP PACKAGE BODY REF_CURSOR_PKG";
    static final String DROP_REF_CURSOR_PKG =
        "DROP PACKAGE REF_CURSOR_PKG";
    static final String DROP_REF_CURSOR_PKG2_BODY =
        "DROP PACKAGE BODY REF_CURSOR_PKG2";
    static final String DROP_REF_CURSOR_PKG2 =
        "DROP PACKAGE REF_CURSOR_PKG2";
    static final String DROP_REF_CURSOR_PKG3 =
        "DROP PACKAGE REF_CURSOR_PKG3";
    static final String DROP_EMP_TABLE =
        "DROP TABLE ref_cursor_emp";

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
            runDdl(conn, CREATE_EMP_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_EMP_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_EMP_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {/*e.printStackTrace();*/}
            runDdl(conn, CREATE_REF_CURSOR_PKG3, ddlDebug);
            runDdl(conn, CREATE_REF_CURSOR_PKG2, ddlDebug);
            runDdl(conn, CREATE_REF_CURSOR_PKG2_BODY, ddlDebug);
            runDdl(conn, CREATE_REF_CURSOR_PKG, ddlDebug);
            runDdl(conn, CREATE_REF_CURSOR_BODY, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">PrependedPackage</property>" +
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
                  "name=\"TestGetEmpDataProc\" " +
                  "catalogPattern=\"REF_CURSOR_PKG\" " +
                  "procedurePattern=\"getEmpDataProc\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TestGetEmpData\" " +
                  "catalogPattern=\"REF_CURSOR_PKG\" " +
                  "procedurePattern=\"getEmpData\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TestGetEmpData2\" " +
                  "catalogPattern=\"REF_CURSOR_PKG\" " +
                  "procedurePattern=\"getEmpData2\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TestGetEmpData3\" " +
                  "catalogPattern=\"REF_CURSOR_PKG\" " +
                  "procedurePattern=\"getEmpData3\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TestPLSQLTypesFromAnotherPackage\" " +
                  "catalogPattern=\"REF_CURSOR_PKG2\" " +
                  "procedurePattern=\"getSomething\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");

          // execute shadow type ddl to generate JDBC equivalents of PL/SQL types 
          for (String ddl : builder.getTypeDDL()) {
              //System.out.println("create: " + ddl);
              runDdl(conn, ddl, ddlDebug);
          }
    }

    @AfterClass
    public static void tearDown() {
        // drop shadow type ddl 
        for (String ddl : builder.getTypeDropDDL()) {
            // may need to strip off trailing ';'
            try {
                int lastIdx = ddl.lastIndexOf(";");
                if (lastIdx == (ddl.length() - 1)) {
                    ddl = ddl.substring(0, ddl.length() - 1);
                }
            } catch (Exception xxx) {}
            //System.out.println("drop: " + ddl);
            runDdl(conn, ddl, ddlDebug);
        }
        if (ddlDrop) {
            runDdl(conn, DROP_REF_CURSOR_BODY, ddlDebug);
            runDdl(conn, DROP_REF_CURSOR_PKG, ddlDebug);
            runDdl(conn, DROP_REF_CURSOR_PKG2_BODY, ddlDebug);
            runDdl(conn, DROP_REF_CURSOR_PKG2, ddlDebug);
            runDdl(conn, DROP_REF_CURSOR_PKG3, ddlDebug);
        	runDdl(conn, DROP_EMP_TABLE, ddlDebug);
        }
    }
    
    @Test
    public void testCursorProc() {
        Invocation invocation = new Invocation("TestGetEmpDataProc");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(EMP_TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    
    @Test
    public void testCursorFunc() {
        Invocation invocation = new Invocation("TestGetEmpData");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(EMP_TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void testSameNameCursorFromOtherPkgFunc() {
        Invocation invocation = new Invocation("TestGetEmpData2");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(EMP_TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void testCursorFromOtherPkgFunc() {
        Invocation invocation = new Invocation("TestGetEmpData3");
        invocation.setParameter("EMP_NUM", 101);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(EMP_101_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void testPLSQLTypesFromAnotherPackage() {
        Invocation invocation = new Invocation("TestPLSQLTypesFromAnotherPackage");
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputRec = unmarshaller.unmarshal(new StringReader(Q_TABLE_XML));
        invocation.setParameter("PARAM1", inputRec);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(Q_TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String EMP_TABLE_XML =
        STANDALONE_XML_HEADER +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<EMPNO>100</EMPNO>" +
              "<ENAME>jim</ENAME>" +
              "<JOB>sales</JOB>" +
              "<DEPTNO>24</DEPTNO>" +
           "</simple-xml>" +
           "<simple-xml>" +
              "<EMPNO>101</EMPNO>" +
              "<ENAME>jack</ENAME>" +
              "<JOB>delivery</JOB>" +
              "<DEPTNO>4</DEPTNO>" +
           "</simple-xml>" +
           "<simple-xml>" +
              "<EMPNO>999</EMPNO>" +
              "<ENAME>john</ENAME>" +
              "<JOB>sales</JOB>" +
              "<DEPTNO>24</DEPTNO>" +
           "</simple-xml>" +
        "</simple-xml-format>";
    public static final String EMP_101_XML =
        STANDALONE_XML_HEADER +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<EMPNO>101</EMPNO>" +
              "<ENAME>jack</ENAME>" +
              "<JOB>delivery</JOB>" +
              "<DEPTNO>4</DEPTNO>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    public static final String Q_TABLE_XML =
        STANDALONE_XML_HEADER +
        "<REF_CURSOR_PKG3_QTAB xmlns=\"urn:PrependedPackage\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>101</item>" +
        "</REF_CURSOR_PKG3_QTAB>";
}
