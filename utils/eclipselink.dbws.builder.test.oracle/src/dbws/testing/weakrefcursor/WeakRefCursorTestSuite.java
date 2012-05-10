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
 *     David McCann - September 06, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.weakrefcursor;

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

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests PL/SQL procedures with simple arguments.
 *
 */
public class WeakRefCursorTestSuite extends DBWSTestSuite {

    static final String WEAKLY_TYPED_REF_CURSOR_TABLE = "WTRC_TABLE";

    static final String CREATE_WEAKLY_TYPED_REF_CURSOR_TABLE =
        "CREATE TABLE " + WEAKLY_TYPED_REF_CURSOR_TABLE + " (" +
            "\nID NUMBER NOT NULL," +
            "\nNAME VARCHAR(25)," +
            "\nSINCE DATE," +
            "\nPRIMARY KEY (ID)" +
        "\n)";
    static final String[] POPULATE_WEAKLY_TYPED_REF_CURSOR_TABLE = new String[] {
        "INSERT INTO " + WEAKLY_TYPED_REF_CURSOR_TABLE + " (ID, NAME, SINCE) VALUES (1, 'mike', " +
            "TO_DATE('2001-12-25 00:00:00','YYYY-MM-DD HH24:MI:SS'))",
        "INSERT INTO " + WEAKLY_TYPED_REF_CURSOR_TABLE + " (ID, NAME, SINCE) VALUES (2, 'blaise', " +
            "TO_DATE('2002-02-12 00:00:00','YYYY-MM-DD HH24:MI:SS'))",
        "INSERT INTO " + WEAKLY_TYPED_REF_CURSOR_TABLE + " (ID, NAME, SINCE) VALUES (3, 'rick', " +
            "TO_DATE('2001-10-30 00:00:00','YYYY-MM-DD HH24:MI:SS'))",
        "INSERT INTO " + WEAKLY_TYPED_REF_CURSOR_TABLE + " (ID, NAME, SINCE) VALUES (4, 'mikey', " +
            "TO_DATE('2010-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'))"
    };
    static final String WEAKLY_TYPED_REF_CURSOR = "WEAKLY_TYPED_REF_CURSOR";
    static final String WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE = WEAKLY_TYPED_REF_CURSOR + "_TEST";
    static final String CREATE_WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE =
        "CREATE OR REPLACE PACKAGE " + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + " AS" +
            "\nTYPE " + WEAKLY_TYPED_REF_CURSOR + " IS REF CURSOR;" +
            "\nPROCEDURE GET_EMS(P_EMS " + WEAKLY_TYPED_REF_CURSOR_TABLE+".NAME%TYPE, P_EMS_SET OUT " + WEAKLY_TYPED_REF_CURSOR + ");" +
            "\nFUNCTION GET_EMS_FUNC(P_EMS VARCHAR) RETURN " + WEAKLY_TYPED_REF_CURSOR + ";" + 
        "\nEND " + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + ";";
    static final String CREATE_WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE_BODY =
        "CREATE OR REPLACE PACKAGE BODY " + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + " AS" +
            "\nPROCEDURE GET_EMS(P_EMS " + WEAKLY_TYPED_REF_CURSOR_TABLE+".NAME%TYPE, P_EMS_SET OUT " + WEAKLY_TYPED_REF_CURSOR + ") AS" +
            "\nBEGIN" +
                "\nOPEN P_EMS_SET FOR" +
                "\nSELECT ID, NAME, SINCE FROM " + WEAKLY_TYPED_REF_CURSOR_TABLE + " WHERE NAME LIKE P_EMS;" +
            "\nEND GET_EMS;" +
            "\nFUNCTION GET_EMS_FUNC(P_EMS VARCHAR) RETURN " + WEAKLY_TYPED_REF_CURSOR + " IS" +
            "\nP_EMS_SET " + WEAKLY_TYPED_REF_CURSOR + ";"+
            "\nBEGIN" +
                "\nOPEN P_EMS_SET FOR" +
                "\nSELECT ID, NAME, SINCE FROM " + WEAKLY_TYPED_REF_CURSOR_TABLE + " WHERE NAME LIKE P_EMS;" +
                "\nRETURN P_EMS_SET;" +
            "\nEND GET_EMS_FUNC;" +
        "\nEND " + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + ";";

    static final String DROP_WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE =
        "DROP PACKAGE " + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE;
    static final String DROP_WEAKLY_TYPED_REF_CURSOR_TABLE =
        "DROP TABLE " + WEAKLY_TYPED_REF_CURSOR_TABLE;

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
            runDdl(conn, CREATE_WEAKLY_TYPED_REF_CURSOR_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_WEAKLY_TYPED_REF_CURSOR_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_WEAKLY_TYPED_REF_CURSOR_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
            runDdl(conn, CREATE_WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE_BODY, ddlDebug);
        }
        username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">weakRefCursor</property>" +
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
                  "name=\"weakRefCursorTest\" " +
                  "catalogPattern=\"" + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + "\" " +
                  "procedurePattern=\"GET_EMS\" " +
                  "isCollection=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"weakRefCursorTest2\" " +
                  "catalogPattern=\"" + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + "\" " +
                  "procedurePattern=\"GET_EMS_FUNC\" " +
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
            runDdl(conn, DROP_WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE, ddlDebug);
            runDdl(conn, DROP_WEAKLY_TYPED_REF_CURSOR_TABLE, ddlDebug);
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void weakRefCursorTest() {
        Invocation invocation = new Invocation("weakRefCursorTest");
        invocation.setParameter("P_EMS", "mike%");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(EMPLOYEES_IN_DEPT10_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @SuppressWarnings("rawtypes")
    @Test
    public void weakRefCursorTest2() {
        Invocation invocation = new Invocation("weakRefCursorTest2");
        invocation.setParameter("P_EMS", "mike%");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(EMPLOYEES_IN_DEPT10_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String EMPLOYEES_IN_DEPT10_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<ID>1</ID>" +
              "<NAME>mike</NAME>" +
              "<SINCE>2001-12-25T00:00:00.0</SINCE>" +
           "</simple-xml>" +
           "<simple-xml>" +
              "<ID>4</ID>" +
              "<NAME>mikey</NAME>" +
              "<SINCE>2010-01-01T00:00:00.0</SINCE>" +
           "</simple-xml>" +
        "</simple-xml-format>";

}