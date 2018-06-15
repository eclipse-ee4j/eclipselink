/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     David McCann - September 06, 2011 - 2.4 - Initial implementation
package dbws.testing.weakrefcursor;

//javase imports
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;

import org.w3c.dom.Document;

//java eXtension imports
import javax.wsdl.WSDLException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

    static final String ELEMENT = "element";
    static final String ELEMENT_NAME = "EMP_DEPTNO";
    static final String ELEMENT_TYPE = "xsd:decimal";

    static {
        username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
    }

    static final String WEAKLY_TYPED_REF_CURSOR_TABLE = "WTRC_TABLE";

    static final String CREATE_WEAKLY_TYPED_REF_CURSOR_EMP_TABLE =
        "CREATE TABLE " + username + ".EMP_" + WEAKLY_TYPED_REF_CURSOR_TABLE + " (" +
            "\nEMPNO NUMBER(4,0) NOT NULL ENABLE," +
            "\nENAME VARCHAR2(10 BYTE)," +
            "\nJOB   VARCHAR2(9 BYTE)," +
            "\nMGR   NUMBER(4,0)," +
            "\nHIREDATE DATE," +
            "\nSAL      NUMBER(7,2)," +
            "\nCOMM     NUMBER(7,2)," +
            "\nDEPTNO   NUMBER(2,0)," +
            "\nCONSTRAINT PK_EMP PRIMARY KEY (EMPNO) USING INDEX," +
            "\nCONSTRAINT FK_DEPTNO FOREIGN KEY (DEPTNO) REFERENCES " + username + ".DEPT_" + WEAKLY_TYPED_REF_CURSOR_TABLE + " (DEPTNO) ENABLE" +
        "\n)";

    static final String CREATE_WEAKLY_TYPED_REF_CURSOR_DEPT_TABLE =
        "CREATE TABLE " + username + ".DEPT_" + WEAKLY_TYPED_REF_CURSOR_TABLE + " (" +
            "\nDEPTNO NUMBER(2,0) NOT NULL ENABLE," +
            "\nDNAME  VARCHAR2(14 BYTE)," +
            "\nLOC    VARCHAR2(13 BYTE)," +
            "\nCONSTRAINT PK_DEPT PRIMARY KEY (DEPTNO) USING INDEX" +
        "\n)";

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
            "\nPROCEDURE GET_EMP(EMP_DEPTNO " + username + ".EMP_" + WEAKLY_TYPED_REF_CURSOR_TABLE +".deptno%TYPE, P_EMP_SET OUT " + WEAKLY_TYPED_REF_CURSOR + ");" +
            "\nFUNCTION GET_EMS_FUNC(P_EMS VARCHAR) RETURN " + WEAKLY_TYPED_REF_CURSOR + ";" +
        "\nEND " + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + ";";
    static final String CREATE_WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE_BODY =
        "CREATE OR REPLACE PACKAGE BODY " + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + " AS" +
            "\nPROCEDURE GET_EMS(P_EMS " + WEAKLY_TYPED_REF_CURSOR_TABLE+".NAME%TYPE, P_EMS_SET OUT " + WEAKLY_TYPED_REF_CURSOR + ") AS" +
            "\nBEGIN" +
                "\nOPEN P_EMS_SET FOR" +
                "\nSELECT ID, NAME, SINCE FROM " + WEAKLY_TYPED_REF_CURSOR_TABLE + " WHERE NAME LIKE P_EMS;" +
            "\nEND GET_EMS;" +
            "\nPROCEDURE GET_EMP(EMP_DEPTNO " + username + ".EMP_" + WEAKLY_TYPED_REF_CURSOR_TABLE +".deptno%TYPE, P_EMP_SET OUT " + WEAKLY_TYPED_REF_CURSOR + ") AS" +
            "\nBEGIN" +
                "\nOPEN P_EMP_SET FOR" +
                "\nSELECT * FROM " + username + ".EMP_" + WEAKLY_TYPED_REF_CURSOR_TABLE + " WHERE DEPTNO LIKE EMP_DEPTNO;" +
            "\nEND GET_EMP;" +
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
    static final String DROP_WEAKLY_TYPED_REF_CURSOR_DEPT_TABLE =
        "DROP TABLE " + username + ".DEPT_" + WEAKLY_TYPED_REF_CURSOR_TABLE;
    static final String DROP_WEAKLY_TYPED_REF_CURSOR_EMP_TABLE =
        "DROP TABLE " + username + ".EMP_" + WEAKLY_TYPED_REF_CURSOR_TABLE;

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
            runDdl(conn, CREATE_WEAKLY_TYPED_REF_CURSOR_DEPT_TABLE, ddlDebug);
            runDdl(conn, CREATE_WEAKLY_TYPED_REF_CURSOR_EMP_TABLE, ddlDebug);
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
                  "name=\"fkpkTest\" " +
                  "catalogPattern=\"" + WEAKLY_TYPED_REF_CURSOR_TEST_PACKAGE + "\" " +
                  "procedurePattern=\"GET_EMP\" " +
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
            runDdl(conn, DROP_WEAKLY_TYPED_REF_CURSOR_EMP_TABLE, ddlDebug);
            runDdl(conn, DROP_WEAKLY_TYPED_REF_CURSOR_DEPT_TABLE, ddlDebug);
        }
    }

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
              "<SINCE>2001-12-25T00:00:00-05:00</SINCE>" +
           "</simple-xml>" +
           "<simple-xml>" +
              "<ID>4</ID>" +
              "<NAME>mikey</NAME>" +
              "<SINCE>2010-01-01T00:00:00-05:00</SINCE>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    /**
     * Test %TYPE preceeded by schemaname.tablename.columnname,
     * i.e. SCHEMA1.EMP_TABLE.ID%TYPE
     *
     * Validation is done by testing the type set in the WSDL. We expect
     * xsd:decimal (xsd:base64Binary will be present if the %TYPE is not
     * processed properly.
     *
     */
    @Test
    public void testPercentTypeWSDLGen() {
        XMLInputFactory xif = XMLInputFactory.newFactory();
        StringReader xml = new StringReader(DBWS_WSDL_STREAM.toString());
        XMLStreamReader xsr = null;

        try {
            xsr = xif.createXMLStreamReader(xml);
        } catch (Exception x) {
            fail("Could not create a StringReader instance based on the DBWS_WSDL_STREAM: \n" + x.getMessage());
        }

        /*
         *    <xsd:complexType name="fkpkTestRequestType">
         *      <xsd:sequence>
         *        <xsd:element name="EMP_DEPTNO" type="xsd:decimal"/>
         *      </xsd:sequence>
         *    </xsd:complexType>"
         */

        // this assumes that the target element  [EMP_DEPTNO] has 2 attributes 'name'
        // and 'type', and that 'name' is at index 0 and 'type' is at index 1
        try {
            int idx = 0;
            xsr.nextTag();
            // loop until a break occurs (success), the stream reader runs out of events, or (finally)
            // if the counter reaches 10 (there are only 5 <xsd:element> occurrences)
            while (true && idx <= 10) {
                while(!xsr.getLocalName().equals(ELEMENT) || (xsr.getLocalName().equals(ELEMENT) && xsr.isEndElement())) {
                    xsr.nextTag();
                }
                if (xsr.getAttributeCount() > 0 && xsr.getAttributeValue(0).equals(ELEMENT_NAME)) {
                    break;
                } else {
                    xsr.nextTag();
                }
                idx++;
            }
            // at this point we've found the "EMP_DEPTNO" element, so check the type attribute
            assertTrue("Element '" + ELEMENT_NAME + "' should have type [" + ELEMENT_TYPE + "] but was ["+xsr.getAttributeValue(1)+"]", xsr.getAttributeValue(1).equals(ELEMENT_TYPE));
        } catch (Exception x) {
            fail("Did not locate [" + ELEMENT_NAME + "] element: \n" + x.getMessage());
        } finally {
            try {
                xsr.close();
            } catch (XMLStreamException e) {}
        }
    }
}
