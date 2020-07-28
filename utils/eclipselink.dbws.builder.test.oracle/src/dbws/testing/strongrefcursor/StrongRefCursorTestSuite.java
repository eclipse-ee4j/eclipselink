/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     David McCann - September 06, 2011 - 2.4 - Initial implementation
package dbws.testing.strongrefcursor;

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
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests PL/SQL functions/procedures with a strongly-typed
 * ref cursor OUT argument.
 *
 */
public class StrongRefCursorTestSuite extends DBWSTestSuite {

    static final String STRONGLY_TYPED_REF_CURSOR_TABLE = "STRC_TABLE";
    static final String CREATE_STRONGLY_TYPED_REF_CURSOR_TABLE =
        "CREATE TABLE " + STRONGLY_TYPED_REF_CURSOR_TABLE + " (" +
            "\nID NUMBER NOT NULL," +
            "\nNAME VARCHAR(25)," +
            "\nSINCE DATE," +
            "\nPRIMARY KEY (ID)" +
        "\n)";
    static final String[] POPULATE_STRONGLY_TYPED_REF_CURSOR_TABLE = new String[] {
        "INSERT INTO " + STRONGLY_TYPED_REF_CURSOR_TABLE + " (ID, NAME, SINCE) VALUES (1, 'mike', " +
            "TO_DATE('2001-12-25 00:00:00','YYYY-MM-DD HH24:MI:SS'))",
        "INSERT INTO " + STRONGLY_TYPED_REF_CURSOR_TABLE + " (ID, NAME, SINCE) VALUES (2, 'blaise', " +
            "TO_DATE('2002-02-12 00:00:00','YYYY-MM-DD HH24:MI:SS'))",
        "INSERT INTO " + STRONGLY_TYPED_REF_CURSOR_TABLE + " (ID, NAME, SINCE) VALUES (3, 'rick', " +
            "TO_DATE('2001-10-30 00:00:00','YYYY-MM-DD HH24:MI:SS'))",
        "INSERT INTO " + STRONGLY_TYPED_REF_CURSOR_TABLE + " (ID, NAME, SINCE) VALUES (4, 'mikey', " +
            "TO_DATE('2010-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'))",
        "INSERT INTO " + STRONGLY_TYPED_REF_CURSOR_TABLE + " (ID, NAME, SINCE) VALUES (5, 'richard', " +
            "TO_DATE('2012-01-03 00:00:00','YYYY-MM-DD HH24:MI:SS'))",
        "INSERT INTO " + STRONGLY_TYPED_REF_CURSOR_TABLE + " (ID, NAME, SINCE) VALUES (6, 'rilley', " +
            "TO_DATE('2012-02-03 00:00:00','YYYY-MM-DD HH24:MI:SS'))"
    };
    static final String STRONGLY_TYPED_REF_CURSOR = "STR_CURSOR";
    static final String STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE = STRONGLY_TYPED_REF_CURSOR + "_TEST";
    static final String CREATE_TAB1_SHADOW_TYPE = "CREATE OR REPLACE TYPE " + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + "_STRC_TAB1 AS TABLE OF VARCHAR2(111)";
    static final String CREATE_STRC_CURSOR_SHADOW_TYPE =
        "CREATE OR REPLACE TYPE STR_CURSOR_TEST_STR_CURSOR AS OBJECT(" +
            "\n ID NUMBER," +
            "\n NAME VARCHAR(25)," +
            "\n SINCE DATE" +
        "\n)";

    static final String CREATE_STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE =
        "CREATE OR REPLACE PACKAGE " + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + " AS" +
            "\nTYPE STRC_TAB1 IS TABLE OF VARCHAR2(111) INDEX BY BINARY_INTEGER;" +
            "\nTYPE " + STRONGLY_TYPED_REF_CURSOR + " IS REF CURSOR RETURN " + STRONGLY_TYPED_REF_CURSOR_TABLE + "%ROWTYPE;" +
            "\nTYPE EMPREC IS RECORD("+
                "\n ID NUMBER," +
                "\n NAME VARCHAR(25)," +
                "\n SINCE DATE" +
            "\n);" +
            "\nTYPE EMPREC_CURSOR IS REF CURSOR RETURN EMPREC;" +
            "\nPROCEDURE GET_EMS(P_EMS " + STRONGLY_TYPED_REF_CURSOR_TABLE+".NAME%TYPE, P_EMS_SET OUT " + STRONGLY_TYPED_REF_CURSOR + ");" +
            "\nPROCEDURE GET_EMS2(MYTAB1 IN STRC_TAB1, P_EMS_SET OUT " + STRONGLY_TYPED_REF_CURSOR + ");" +
            "\nPROCEDURE GET_EMPREC(ENAME IN VARCHAR, EMPREC_SET OUT EMPREC_CURSOR);" +
            "\nPROCEDURE GET_EMPREC2(MYTAB1 IN STRC_TAB1, EMPREC_SET OUT EMPREC_CURSOR);" +
            "\nFUNCTION GET_EMS_FUNC(P_EMS " + STRONGLY_TYPED_REF_CURSOR_TABLE+".NAME%TYPE) RETURN " + STRONGLY_TYPED_REF_CURSOR + ";" +
            "\nFUNCTION GET_EMS2_FUNC(MYTAB1 IN STRC_TAB1) RETURN " + STRONGLY_TYPED_REF_CURSOR + ";" +
            "\nFUNCTION GET_EMPREC_FUNC(ENAME IN VARCHAR) RETURN EMPREC_CURSOR;" +
            "\nFUNCTION GET_EMPREC2_FUNC(MYTAB1 IN STRC_TAB1) RETURN EMPREC_CURSOR;" +
        "\nEND " + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + ";";

    static final String CREATE_STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE_BODY =
        "CREATE OR REPLACE PACKAGE BODY " + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + " AS" +
            "\nPROCEDURE GET_EMS(P_EMS " + STRONGLY_TYPED_REF_CURSOR_TABLE+".NAME%TYPE, P_EMS_SET OUT " + STRONGLY_TYPED_REF_CURSOR + ") AS" +
            "\nBEGIN" +
              "\n OPEN P_EMS_SET FOR" +
              "\n SELECT ID, NAME, SINCE FROM " + STRONGLY_TYPED_REF_CURSOR_TABLE + " WHERE NAME LIKE P_EMS;" +
            "\nEND GET_EMS;" +
            "\nPROCEDURE GET_EMS2(MYTAB1 IN STRC_TAB1, P_EMS_SET OUT " + STRONGLY_TYPED_REF_CURSOR + ") AS" +
            "\nBEGIN" +
              "\n OPEN P_EMS_SET FOR" +
              "\n SELECT ID, NAME, SINCE FROM " + STRONGLY_TYPED_REF_CURSOR_TABLE + " WHERE NAME LIKE MYTAB1(1);" +
            "\nEND GET_EMS2;" +
            "\nPROCEDURE GET_EMPREC(ENAME IN VARCHAR, EMPREC_SET OUT EMPREC_CURSOR) AS" +
            "\nBEGIN" +
                "\nOPEN EMPREC_SET FOR" +
                "\nSELECT * FROM " + STRONGLY_TYPED_REF_CURSOR_TABLE + " WHERE NAME LIKE ENAME;" +
            "\nEND GET_EMPREC;" +
            "\nPROCEDURE GET_EMPREC2(MYTAB1 IN STRC_TAB1, EMPREC_SET OUT EMPREC_CURSOR) AS" +
            "\nBEGIN" +
                "\nOPEN EMPREC_SET FOR" +
                "\nSELECT * FROM " + STRONGLY_TYPED_REF_CURSOR_TABLE + " WHERE NAME LIKE MYTAB1(1);" +
            "\nEND GET_EMPREC2;" +
            "\nFUNCTION GET_EMS_FUNC(P_EMS " + STRONGLY_TYPED_REF_CURSOR_TABLE+".NAME%TYPE) RETURN " + STRONGLY_TYPED_REF_CURSOR + " IS" +
            "\nP_EMS_SET " + STRONGLY_TYPED_REF_CURSOR + ";" +
            "\nBEGIN" +
                "\n OPEN P_EMS_SET FOR" +
                "\n SELECT ID, NAME, SINCE FROM " + STRONGLY_TYPED_REF_CURSOR_TABLE + " WHERE NAME LIKE P_EMS;" +
                "\n RETURN P_EMS_SET;" +
            "\nEND GET_EMS_FUNC;" +
            "\nFUNCTION GET_EMS2_FUNC(MYTAB1 IN STRC_TAB1) RETURN " + STRONGLY_TYPED_REF_CURSOR + " IS" +
            "\nP_EMS_SET " + STRONGLY_TYPED_REF_CURSOR + ";" +
            "\nBEGIN" +
                "\n OPEN P_EMS_SET FOR" +
                "\n SELECT ID, NAME, SINCE FROM " + STRONGLY_TYPED_REF_CURSOR_TABLE + " WHERE NAME LIKE MYTAB1(1);" +
                "\n RETURN P_EMS_SET;" +
            "\nEND GET_EMS2_FUNC;" +
            "\nFUNCTION GET_EMPREC_FUNC(ENAME IN VARCHAR) RETURN EMPREC_CURSOR IS" +
            "\nEMPREC_SET EMPREC_CURSOR;" +
            "\nBEGIN" +
                "\nOPEN EMPREC_SET FOR" +
                "\nSELECT * FROM " + STRONGLY_TYPED_REF_CURSOR_TABLE + " WHERE NAME LIKE ENAME;" +
                "\nRETURN EMPREC_SET;" +
            "\nEND GET_EMPREC_FUNC;" +
            "\nFUNCTION GET_EMPREC2_FUNC(MYTAB1 IN STRC_TAB1) RETURN EMPREC_CURSOR IS" +
            "\nEMPREC_SET EMPREC_CURSOR;" +
            "\nBEGIN" +
                "\nOPEN EMPREC_SET FOR" +
                "\nSELECT * FROM " + STRONGLY_TYPED_REF_CURSOR_TABLE + " WHERE NAME LIKE MYTAB1(1);" +
                "\nRETURN EMPREC_SET;" +
            "\nEND GET_EMPREC2_FUNC;" +
        "\nEND " + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + ";";

    static final String DROP_STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE = "DROP PACKAGE " + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE;
    static final String DROP_STRONGLY_TYPED_REF_CURSOR_TABLE = "DROP TABLE " + STRONGLY_TYPED_REF_CURSOR_TABLE;
    static final String DROP_TAB1_SHADOW_TYPE = "DROP TYPE " + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + "_STRC_TAB1";
    static final String DROP_STRC_CURSOR_SHADOW_TYPE  = "DROP TYPE STR_CURSOR_TEST_STR_CURSOR";

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
            runDdl(conn, CREATE_STRONGLY_TYPED_REF_CURSOR_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_STRONGLY_TYPED_REF_CURSOR_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_STRONGLY_TYPED_REF_CURSOR_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
            runDdl(conn, CREATE_TAB1_SHADOW_TYPE, ddlDebug);
            runDdl(conn, CREATE_STRC_CURSOR_SHADOW_TYPE, ddlDebug);
            runDdl(conn, CREATE_STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE_BODY, ddlDebug);
        }
        username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">strongRefCursor</property>" +
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
                  "name=\"strongRefCursorTest\" " +
                  "catalogPattern=\"" + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + "\" " +
                  "procedurePattern=\"GET_EMS\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"strongRefCursorTest2\" " +
                  "catalogPattern=\"" + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + "\" " +
                  "procedurePattern=\"GET_EMS2\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"emprecRefCursorTest\" " +
                  "catalogPattern=\"" + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + "\" " +
                  "procedurePattern=\"GET_EMPREC\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"emprecRefCursorTest2\" " +
                  "catalogPattern=\"" + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + "\" " +
                  "procedurePattern=\"GET_EMPREC2\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"strongRefCursorTestFunc\" " +
                  "catalogPattern=\"" + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + "\" " +
                  "procedurePattern=\"GET_EMS_FUNC\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"strongRefCursorTest2Func\" " +
                  "catalogPattern=\"" + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + "\" " +
                  "procedurePattern=\"GET_EMS2_FUNC\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"emprecRefCursorTestFunc\" " +
                  "catalogPattern=\"" + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + "\" " +
                  "procedurePattern=\"GET_EMPREC_FUNC\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"emprecRefCursorTest2Func\" " +
                  "catalogPattern=\"" + STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE + "\" " +
                  "procedurePattern=\"GET_EMPREC2_FUNC\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +

            "</dbws-builder>";
          builder = new DBWSBuilder();
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_STRONGLY_TYPED_REF_CURSOR_TEST_PACKAGE, ddlDebug);
            runDdl(conn, DROP_STRONGLY_TYPED_REF_CURSOR_TABLE, ddlDebug);
            runDdl(conn, DROP_TAB1_SHADOW_TYPE, ddlDebug);
        }
    }

    @Test
    public void strongRefCursorTest() {
        Invocation invocation = new Invocation("strongRefCursorTest");
        invocation.setParameter("P_EMS", "mike%");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(MIKE_NAMES_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) +  "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void strongRefCursorTestFunc() {
        Invocation invocation = new Invocation("strongRefCursorTestFunc");
        invocation.setParameter("P_EMS", "mike%");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(MIKE_NAMES_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) +  "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String MIKE_NAMES_XML =
        REGULAR_XML_HEADER +
        "<STR_CURSOR xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"simple-xml-format\">" +
           "<STRC_TABLE>" +
              "<ID>1</ID>" +
              "<NAME>mike</NAME>" +
              "<SINCE>2001-12-25T00:00:00-05:00</SINCE>" +
           "</STRC_TABLE>" +
           "<STRC_TABLE>" +
              "<ID>4</ID>" +
              "<NAME>mikey</NAME>" +
              "<SINCE>2010-01-01T00:00:00-05:00</SINCE>" +
           "</STRC_TABLE>" +
        "</STR_CURSOR>";

    @Test
    public void strongRefCursorTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Invocation invocation = new Invocation("strongRefCursorTest2");
        invocation.setParameter("MYTAB1", inputTab);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(MIKE_NAMES_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) +  "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void strongRefCursorTest2Func() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Invocation invocation = new Invocation("strongRefCursorTest2Func");
        invocation.setParameter("MYTAB1", inputTab);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(MIKE_NAMES_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) +  "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String TABLE_XML =
        STANDALONE_XML_HEADER +
        "<str_cursor_test_strc_tab1Type xmlns=\"urn:strongRefCursor\">" +
            "<item>mike%</item>" +
        "</str_cursor_test_strc_tab1Type>";

    @Test
    public void emprecRefCursorTest() {
        Invocation invocation = new Invocation("emprecRefCursorTest");
        invocation.setParameter("ENAME", "ri%");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NAMES_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) +  "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void emprecRefCursorTestFunc() {
        Invocation invocation = new Invocation("emprecRefCursorTestFunc");
        invocation.setParameter("ENAME", "ri%");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NAMES_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) +  "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void emprecRefCursorTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab = unmarshaller.unmarshal(new StringReader(TABLE2_XML));
        Invocation invocation = new Invocation("emprecRefCursorTest2");
        invocation.setParameter("MYTAB1", inputTab);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NAMES_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) +  "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void emprecRefCursorTest2Func() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab = unmarshaller.unmarshal(new StringReader(TABLE2_XML));
        Invocation invocation = new Invocation("emprecRefCursorTest2Func");
        invocation.setParameter("MYTAB1", inputTab);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NAMES_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) +  "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String TABLE2_XML =
        STANDALONE_XML_HEADER +
        "<str_cursor_test_strc_tab1Type xmlns=\"urn:strongRefCursor\">" +
            "<item>ri%</item>" +
        "</str_cursor_test_strc_tab1Type>";

    public static final String NAMES_XML =
        REGULAR_XML_HEADER +
        "<EMPREC_CURSOR xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"simple-xml-format\">" +
           "<EMPREC>" +
              "<ID>3</ID>" +
              "<NAME>rick</NAME>" +
              "<SINCE>2001-10-30T00:00:00-05:00</SINCE>" +
           "</EMPREC>" +
           "<EMPREC>" +
              "<ID>5</ID>" +
              "<NAME>richard</NAME>" +
              "<SINCE>2012-01-03T00:00:00-05:00</SINCE>" +
           "</EMPREC>" +
           "<EMPREC>" +
               "<ID>6</ID>" +
               "<NAME>rilley</NAME>" +
               "<SINCE>2012-02-03T00:00:00-05:00</SINCE>" +
           "</EMPREC>" +
        "</EMPREC_CURSOR>";
}
