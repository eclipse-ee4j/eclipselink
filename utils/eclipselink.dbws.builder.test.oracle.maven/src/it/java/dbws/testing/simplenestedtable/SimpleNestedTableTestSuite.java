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
//     David McCann - June 28, 2012 - 2.4.1 - Initial implementation
package dbws.testing.simplenestedtable;

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
import org.eclipse.persistence.tools.dbws.oracle.OracleHelper;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests nested tables with columns containing scalar values.
 *
 */
public class SimpleNestedTableTestSuite extends DBWSTestSuite {

    static final String CREATE_NUMBER_TABLE =
        "CREATE OR REPLACE TYPE NUMTAB IS TABLE OF NUMBER";

    static final String CREATE_NUMBER_WRAPPER =
        "CREATE OR REPLACE TYPE WRAPPER_NUMTAB AS OBJECT (" +
            "\nNUMBTABLIST NUMTAB" +
        "\n);";

    static final String CREATE_TEST_NUMBER_PACKAGE =
        "CREATE OR REPLACE PACKAGE TEST_NUMBER_LIST_PKG AS" +
            "\nFUNCTION TEST_NUM RETURN WRAPPER_NUMTAB;" +
        "\nEND TEST_NUMBER_LIST_PKG;";

    static final String CREATE_TEST_NUMBER_PACKAGE_BODY =
        "CREATE OR REPLACE PACKAGE BODY TEST_NUMBER_LIST_PKG AS" +
            "\nFUNCTION TEST_NUM RETURN WRAPPER_NUMTAB IS" +
            "\nL_WRAPPER_NUMTAB WRAPPER_NUMTAB;" +
            "\nL_NUMTAB NUMTAB;" +
            "\nBEGIN" +
                "\nL_NUMTAB := NUMTAB();" +
                "\nL_NUMTAB.EXTEND(1);" +
                "\nL_NUMTAB(1) := 115;" +
                "\nL_WRAPPER_NUMTAB := WRAPPER_NUMTAB(L_NUMTAB);" +
                "\nRETURN L_WRAPPER_NUMTAB;" +
            "\nEND;" +
        "\nEND TEST_NUMBER_LIST_PKG;";

    static final String DROP_TEST_NUMBER_BODY =
        "DROP PACKAGE BODY TEST_NUMBER_LIST_PKG";
    static final String DROP_TEST_NUMBER_PACKAGE =
        "DROP PACKAGE TEST_NUMBER_LIST_PKG";
    static final String DROP_NUMBER_WRAPPER_TYPE =
        "DROP TYPE WRAPPER_NUMTAB";
    static final String DROP_NUMTAB_TYPE =
        "DROP TYPE NUMTAB";

    static final String CREATE_USERS_TABLE =
        "CREATE TABLE USERS_TABLE(" +
            "\nU_ID VARCHAR2(8)," +
            "\nU_NAME VARCHAR2(10)," +
            "\nPRIMARY KEY (U_ID)" +
        "\n)";
    static final String[] POPULATE_TABLE = new String[] {
        "INSERT INTO USERS_TABLE (U_ID, U_NAME) VALUES ('abc123', 'Ricky')",
        "INSERT INTO USERS_TABLE (U_ID, U_NAME) VALUES ('xxx666', 'Julian')",
        "INSERT INTO USERS_TABLE (U_ID, U_NAME) VALUES ('qwerty', 'Bubbles')",
        "INSERT INTO USERS_TABLE (U_ID, U_NAME) VALUES ('foobar', 'Conky')",
        "INSERT INTO USERS_TABLE (U_ID, U_NAME) VALUES ('321123', 'Lahey')",
        "INSERT INTO USERS_TABLE (U_ID, U_NAME) VALUES ('barfoo', 'Randy')"
    };

    static final String CREATE_USERS_PKG =
        "CREATE OR REPLACE PACKAGE USERS_PKG AS" +
            "\nFUNCTION GET_USERS_NAME_test(in_users_id_list IN USERS_ID_LIST_TYPE) RETURN VARCHAR2;" +
        "\nEND USERS_PKG;";
    static final String CREATE_USERS_BODY =
        "CREATE OR REPLACE PACKAGE BODY USERS_PKG AS" +
            "\nFUNCTION GET_USERS_NAME_TEST(in_users_id_list IN  USERS_ID_LIST_TYPE) RETURN VARCHAR2 AS" +
              "\nout_usersname VARCHAR2(255);" +
              "\nUSER_NAME VARCHAR2(10);" +
              "\nNUM NUMBER;" +
            "\nBEGIN" +
                "\nNUM := in_users_id_list.COUNT;" +
                "\nFOR I IN 1..NUM LOOP" +
                    "\nSELECT U_NAME INTO USER_NAME FROM USERS_TABLE WHERE U_ID LIKE in_users_id_list(I);" +
                    "\nout_usersname := CONCAT(out_usersname, USER_NAME);" +
                    "\nIF I < NUM THEN" +
                        "\nout_usersname := CONCAT(out_usersname, ', ');" +
                    "\nEND IF;" +
                "\nEND LOOP;" +
                "\nRETURN out_usersname;" +
             "\nEND;" +
         "\nEND USERS_PKG;";
    static final String CREATE_USERS_ID_LIST_TYPE =
        "CREATE OR REPLACE TYPE USERS_ID_LIST_TYPE AS TABLE OF varchar2(8);";

    static final String DROP_USERS_BODY =
        "DROP PACKAGE BODY USERS_PKG";
    static final String DROP_USERS_PKG =
        "DROP PACKAGE USERS_PKG";
    static final String DROP_USERS_ID_LIST_TYPE =
        "DROP TYPE USERS_ID_LIST_TYPE";
    static final String DROP_USERS_TABLE =
        "DROP TABLE USERS_TABLE";

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
            runDdl(conn, CREATE_NUMBER_TABLE, ddlDebug);
            runDdl(conn, CREATE_NUMBER_WRAPPER, ddlDebug);
            runDdl(conn, CREATE_TEST_NUMBER_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_TEST_NUMBER_PACKAGE_BODY, ddlDebug);
            runDdl(conn, CREATE_USERS_TABLE, ddlDebug);
            runDdl(conn, CREATE_USERS_ID_LIST_TYPE, ddlDebug);
            runDdl(conn, CREATE_USERS_PKG, ddlDebug);
            runDdl(conn, CREATE_USERS_BODY, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_TABLE[i]);
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
                  "<property name=\"projectName\">SimpleNestedTableTest</property>" +
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
                 "name=\"GetUsersTest\" " +
                 "catalogPattern=\"USERS_PKG\" " +
                 "procedurePattern=\"GET_USERS_NAME_TEST\" " +
              "/>" +
              "<plsql-procedure " +
                 "name=\"NumberTableTest\" " +
                 "catalogPattern=\"TEST_NUMBER_LIST_PKG\" " +
                 "procedurePattern=\"TEST_NUM\" " +
                 //"returnType=\"wrapper_numtabType\" " +
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
            runDdl(conn, DROP_USERS_BODY, ddlDebug);
            runDdl(conn, DROP_USERS_PKG, ddlDebug);
            runDdl(conn, DROP_USERS_ID_LIST_TYPE, ddlDebug);
            runDdl(conn, DROP_USERS_TABLE, ddlDebug);

            runDdl(conn, DROP_TEST_NUMBER_BODY, ddlDebug);
            runDdl(conn, DROP_TEST_NUMBER_PACKAGE, ddlDebug);
            runDdl(conn, DROP_NUMBER_WRAPPER_TYPE, ddlDebug);
            runDdl(conn, DROP_NUMTAB_TYPE, ddlDebug);
        }
    }

    @Test
    public void testGetUsers() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputList = unmarshaller.unmarshal(new StringReader(INPUT_ID_LIST_XML));
        Invocation invocation = new Invocation("GetUsersTest");
        invocation.setParameter("in_users_id_list", inputList);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUT_USERS_LIST_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String INPUT_ID_LIST_XML =
        STANDALONE_XML_HEADER +
        "<users_id_list_typeType xmlns=\"urn:SimpleNestedTableTest\">" +
          "<item>qwerty</item>" +
          "<item>321123</item>" +
          "<item>xxx666</item>" +
        "</users_id_list_typeType>";
    public static final String OUTPUT_USERS_LIST_XML =
        STANDALONE_XML_HEADER +
        "<value>Bubbles, Lahey, Julian</value>";

    @Test
    public void testNumberTable() {
        Invocation invocation = new Invocation("NumberTableTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NUMBERS_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String NUMBERS_XML =
        STANDALONE_XML_HEADER +
        "<wrapper_numtabType xmlns=\"urn:SimpleNestedTableTest\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<numbtablist>115</numbtablist>" +
        "</wrapper_numtabType>";
}
