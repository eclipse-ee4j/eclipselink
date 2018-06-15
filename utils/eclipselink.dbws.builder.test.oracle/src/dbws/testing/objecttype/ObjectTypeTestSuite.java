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
//     David McCann - October 24, 2011 - 2.4 - Initial implementation
package dbws.testing.objecttype;

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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Project;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests Oracle Object types.
 *
 */
public class ObjectTypeTestSuite extends DBWSTestSuite {
    static final String EMP_TYPE_ALIAS = "Dbws_emp_type";
    static final String EMP_TYPE_CLASSNAME = "objecttypetests.Dbws_emp_type";
    static final String PHONE_TYPE_ALIAS = "Dbws_phone_type";
    static final String PHONE_TYPE_CLASSNAME = "objecttypetests.Dbws_phone_type";

    //==============================================================================
    static final String CREATE_MYTYPE =
        "CREATE OR REPLACE TYPE MYTYPE_1 AS OBJECT (" +
            "\nid INTEGER," +
            "\nname VARCHAR2(30)" +
        "\n)";

    static final String CREATE_MYTYPE_ARRAY =
        "CREATE OR REPLACE TYPE MYTYPE_1_ARRAY AS VARRAY(10) OF MYTYPE_1;";

    static final String CREATE_MYTYPE_PKG =
        "CREATE OR REPLACE PACKAGE USERTYPEINOUT AS" +
            "\nPROCEDURE TEST_IN_AND_OUT(P1 IN MYTYPE_1, P2 OUT MYTYPE_1);" +
            "\nPROCEDURE TEST_OUT(P1 OUT MYTYPE_1);" +
            "\nPROCEDURE TEST_IN(P1 IN MYTYPE_1);" +
            "\nPROCEDURE TEST_INOUT(P1 IN OUT MYTYPE_1);" +
            "\nFUNCTION ECHO_MYTYPE_1_ARRAY(P1 MYTYPE_1_ARRAY) RETURN MYTYPE_1_ARRAY;" +
            "\nPROCEDURE TEST_MULTI_IN_AND_OUT(P1 IN MYTYPE_1, P2 IN VARCHAR2, P3 IN INTEGER, P4 OUT MYTYPE_1);" +
        "\nEND USERTYPEINOUT;";

    static final String CREATE_MYTYPE_PKG_BODY =
        "CREATE OR REPLACE PACKAGE BODY USERTYPEINOUT AS" +
            "\nPROCEDURE TEST_IN_AND_OUT(P1 IN MYTYPE_1, P2 OUT MYTYPE_1) AS" +
            "\nBEGIN" +
                "\nP2 := P1;" +
            "\nEND TEST_IN_AND_OUT; " +
            "\nPROCEDURE TEST_OUT(P1 OUT MYTYPE_1) AS" +
            "\nBEGIN" +
                "\nP1 := MYTYPE_1(66, 'Steve French');" +
            "\nEND TEST_OUT; " +
            "\nPROCEDURE TEST_IN(P1 IN MYTYPE_1) AS" +
            "\nBEGIN" +
                "\nNULL;" +
            "\nEND TEST_IN; " +
            "\nPROCEDURE TEST_INOUT(P1 IN OUT MYTYPE_1) AS" +
            "\nBEGIN" +
                "\nNULL;" +
            "\nEND TEST_INOUT; " +
            "\nFUNCTION ECHO_MYTYPE_1_ARRAY(P1 MYTYPE_1_ARRAY) RETURN MYTYPE_1_ARRAY AS" +
            "\nBEGIN" +
                "\nRETURN P1;" +
            "\nEND ECHO_MYTYPE_1_ARRAY;" +
            "\nPROCEDURE TEST_MULTI_IN_AND_OUT(P1 IN MYTYPE_1, P2 IN VARCHAR2, P3 IN INTEGER, P4 OUT MYTYPE_1) AS" +
            "\nBEGIN" +
                "\nP4 := MYTYPE_1(P1.id + P3, concat(P1.name, P2));" +
            "\nEND TEST_MULTI_IN_AND_OUT; " +
        "\nEND USERTYPEINOUT;";
    //==============================================================================

    static final String CREATE_PHONE_TYPE =
        "CREATE OR REPLACE TYPE DBWS_PHONE_TYPE AS OBJECT (" +
            "\nHOME VARCHAR2(20)," +
            "\nCELL VARCHAR2(20)" +
        "\n)";
    static final String CREATE_EMP_TYPE =
        "CREATE OR REPLACE TYPE DBWS_EMP_TYPE AS OBJECT (" +
            "\nID NUMBER," +
            "\nNAME VARCHAR2(20)," +
            "\nPHONE DBWS_PHONE_TYPE" +
        "\n)";
    static final String CREATE_EMP_TYPE_TABLE =
        "CREATE TABLE DBWS_EMP_TYPE_TABLE OF DBWS_EMP_TYPE";
    static final String[] POPULATE_EMP_TYPE_TABLE = new String[] {
        "INSERT INTO DBWS_EMP_TYPE_TABLE VALUES (" +
            "DBWS_EMP_TYPE(66, 'BUBBLES', DBWS_PHONE_TYPE('(613) 234-4567', '(613) 858-3434')))",
        "INSERT INTO DBWS_EMP_TYPE_TABLE VALUES (" +
            "DBWS_EMP_TYPE(69, 'RICKY', DBWS_PHONE_TYPE('(613) 344-1232', '(613) 823-2323')))",
        "INSERT INTO DBWS_EMP_TYPE_TABLE VALUES (" +
            "DBWS_EMP_TYPE(99, 'JULIAN', DBWS_PHONE_TYPE('(613) 424-0987', '(613) 555-8888')))"
        };
    static final String CREATE_GET_EMP_TYPE_BY_ID_PROC =
        "CREATE OR REPLACE PROCEDURE GET_EMP_TYPE_BY_ID(EID IN NUMBER, ETYPE OUT DBWS_EMP_TYPE) AS" +
        "\nBEGIN" +
            "\nSELECT VALUE(E) INTO ETYPE FROM DBWS_EMP_TYPE_TABLE E WHERE E.ID = EID;" +
        "\nEND GET_EMP_TYPE_BY_ID;";
    static final String CREATE_GET_EMP_TYPE_BY_ID_2_FUNC =
        "CREATE OR REPLACE FUNCTION GET_EMP_TYPE_BY_ID_2(EID IN NUMBER) RETURN DBWS_EMP_TYPE AS" +
        "\nETYPE DBWS_EMP_TYPE;" +
        "\nBEGIN" +
            "\nSELECT VALUE(E) INTO ETYPE FROM DBWS_EMP_TYPE_TABLE E WHERE E.ID = EID;" +
            "\nRETURN ETYPE;" +
        "\nEND GET_EMP_TYPE_BY_ID_2;";
    static final String CREATE_ADD_EMP_TYPE_PROC =
        "CREATE OR REPLACE PROCEDURE ADD_EMP_TYPE(ETYPE IN DBWS_EMP_TYPE, RESULT OUT DBWS_EMP_TYPE) AS" +
        "\nBEGIN" +
            "\nDELETE FROM DBWS_EMP_TYPE_TABLE WHERE ID = ETYPE.ID;" +
            "\nINSERT INTO DBWS_EMP_TYPE_TABLE VALUES (ETYPE);" +
            "\nSELECT VALUE(E) INTO RESULT FROM DBWS_EMP_TYPE_TABLE E WHERE E.ID = ETYPE.ID;" +
            "\nDELETE FROM DBWS_EMP_TYPE_TABLE WHERE ID = ETYPE.ID;" +
        "\nEND ADD_EMP_TYPE;";
    static final String CREATE_ADD_EMP_TYPE2_FUNC =
        "CREATE OR REPLACE FUNCTION ADD_EMP_TYPE2(ETYPE IN DBWS_EMP_TYPE) RETURN DBWS_EMP_TYPE AS" +
        "\nRESULT DBWS_EMP_TYPE;" +
        "\nBEGIN" +
            "\nADD_EMP_TYPE(ETYPE, RESULT);" +
            "\nRETURN RESULT;" +
        "\nEND ADD_EMP_TYPE2;";
    static final String DROP_PHONE_TYPE =
        "DROP TYPE DBWS_PHONE_TYPE FORCE";
    static final String DROP_EMP_TYPE =
        "DROP TYPE DBWS_EMP_TYPE FORCE";
    static final String DROP_EMP_TYPE_TABLE =
        "DROP TABLE DBWS_EMP_TYPE_TABLE FORCE";
    static final String DROP_GET_EMP_TYPE_BY_ID_PROC =
        "DROP PROCEDURE GET_EMP_TYPE_BY_ID";
    static final String DROP_GET_EMP_TYPE_BY_ID_2_FUNC =
        "DROP FUNCTION GET_EMP_TYPE_BY_ID_2";
    static final String DROP_ADD_EMP_TYPE_PROC =
        "DROP PROCEDURE ADD_EMP_TYPE";
    static final String DROP_ADD_EMP_TYPE2_FUNC =
        "DROP FUNCTION ADD_EMP_TYPE2";
    static final String DROP_TYPE_MYTYPE_1 =
        "DROP TYPE MYTYPE_1";
    static final String DROP_TYPE_MYTYPE_1_ARRAY =
        "DROP TYPE MYTYPE_1_ARRAY";
    static final String DROP_PACKAGE_USER_TYPE_INOUT =
        "DROP PACKAGE USERTYPEINOUT";
    static final String DROP_PACKAGE_BODY_USER_TYPE_INOUT =
        "DROP PACKAGE BODY USERTYPEINOUT";

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
        //because of behaviour of DBWS_EMP_TYPE_TABLE w.r.t. identity, force create/drop
        //no matter what the external properties say
        ddlCreate = true;
        ddlDrop = true;
        String ddlDebugProp = System.getProperty(DATABASE_DDL_DEBUG_KEY, DEFAULT_DATABASE_DDL_DEBUG);
        if ("true".equalsIgnoreCase(ddlDebugProp)) {
            ddlDebug = true;
        }
        if (ddlCreate) {
            runDdl(conn, CREATE_PHONE_TYPE, ddlDebug);
            runDdl(conn, CREATE_EMP_TYPE, ddlDebug);
            runDdl(conn, CREATE_EMP_TYPE_TABLE, ddlDebug);
            runDdl(conn, CREATE_GET_EMP_TYPE_BY_ID_PROC, ddlDebug);
            runDdl(conn, CREATE_GET_EMP_TYPE_BY_ID_2_FUNC, ddlDebug);
            runDdl(conn, CREATE_ADD_EMP_TYPE_PROC, ddlDebug);
            runDdl(conn, CREATE_ADD_EMP_TYPE2_FUNC, ddlDebug);
            runDdl(conn, CREATE_MYTYPE, ddlDebug);
            runDdl(conn, CREATE_MYTYPE_ARRAY, ddlDebug);
            runDdl(conn, CREATE_MYTYPE_PKG, ddlDebug);
            runDdl(conn, CREATE_MYTYPE_PKG_BODY, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_EMP_TYPE_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_EMP_TYPE_TABLE[i]);
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
                  "<property name=\"projectName\">ObjectTypeTests</property>" +
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
                  "name=\"GetEmpTypeByIdTest\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GET_EMP_TYPE_BY_ID\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"dbws_emp_typeType\" " +
              "/>" +
              "<procedure " +
                  "name=\"GetEmpTypeByIdTest2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GET_EMP_TYPE_BY_ID_2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"dbws_emp_typeType\" " +
              "/>" +
              "<procedure " +
                  "name=\"AddEmpTypeTest\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"ADD_EMP_TYPE\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"dbws_emp_typeType\" " +
              "/>" +
              "<procedure " +
                  "name=\"AddEmpTypeTest2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"ADD_EMP_TYPE2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"dbws_emp_typeType\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TestIn\" " +
                  "catalogPattern=\"USERTYPEINOUT\" " +
                  "procedurePattern=\"TEST_IN\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TestOut\" " +
                  "catalogPattern=\"USERTYPEINOUT\" " +
                  "procedurePattern=\"TEST_OUT\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TestInAndOut\" " +
                  "catalogPattern=\"USERTYPEINOUT\" " +
                  "procedurePattern=\"TEST_IN_AND_OUT\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TestInOut\" " +
                  "catalogPattern=\"USERTYPEINOUT\" " +
                  "procedurePattern=\"TEST_INOUT\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TestEchoArray\" " +
                  "catalogPattern=\"USERTYPEINOUT\" " +
                  "procedurePattern=\"ECHO_MYTYPE_1_ARRAY\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TestMultiInAndOut\" " +
                  "catalogPattern=\"USERTYPEINOUT\" " +
                  "procedurePattern=\"TEST_MULTI_IN_AND_OUT\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_EMP_TYPE_TABLE, ddlDebug);
            runDdl(conn, DROP_ADD_EMP_TYPE2_FUNC, ddlDebug);
            runDdl(conn, DROP_ADD_EMP_TYPE_PROC, ddlDebug);
            runDdl(conn, DROP_GET_EMP_TYPE_BY_ID_2_FUNC, ddlDebug);
            runDdl(conn, DROP_GET_EMP_TYPE_BY_ID_PROC, ddlDebug);
            runDdl(conn, DROP_EMP_TYPE, ddlDebug);
            runDdl(conn, DROP_PHONE_TYPE, ddlDebug);
            runDdl(conn, DROP_PACKAGE_BODY_USER_TYPE_INOUT, ddlDebug);
            runDdl(conn, DROP_PACKAGE_USER_TYPE_INOUT, ddlDebug);
            runDdl(conn, DROP_TYPE_MYTYPE_1_ARRAY, ddlDebug);
            runDdl(conn, DROP_TYPE_MYTYPE_1, ddlDebug);
        }
    }

    @Test
    public void getEmpTypeByIdTest() {
        Invocation invocation = new Invocation("GetEmpTypeByIdTest");
        invocation.setParameter("EID", 66);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String RESULT_XML =
        REGULAR_XML_HEADER +
        "<dbws_emp_typeType xmlns=\"urn:ObjectTypeTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<id>66</id>" +
            "<name>BUBBLES</name>" +
            "<phone>" +
                "<home>(613) 234-4567</home>" +
                "<cell>(613) 858-3434</cell>" +
            "</phone>" +
        "</dbws_emp_typeType>";

    @Test
    public void getEmpTypeByIdTest2() {
        Invocation invocation = new Invocation("GetEmpTypeByIdTest2");
        invocation.setParameter("EID", 69);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT2_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String RESULT2_XML =
        REGULAR_XML_HEADER +
        "<dbws_emp_typeType xmlns=\"urn:ObjectTypeTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<id>69</id>" +
            "<name>RICKY</name>" +
            "<phone>" +
                "<home>(613) 344-1232</home>" +
                "<cell>(613) 823-2323</cell>" +
            "</phone>" +
        "</dbws_emp_typeType>";

    @Test
    public void addEmpTypeTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object eType = unmarshaller.unmarshal(new StringReader(ETYPE_XML));
        Invocation invocation = new Invocation("AddEmpTypeTest");
        invocation.setParameter("ETYPE", eType);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ETYPE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void addEmpTypeTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object eType = unmarshaller.unmarshal(new StringReader(ETYPE_XML));
        Invocation invocation = new Invocation("AddEmpTypeTest2");
        invocation.setParameter("ETYPE", eType);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ETYPE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String ETYPE_XML =
        REGULAR_XML_HEADER +
        "<dbws_emp_typeType xmlns=\"urn:ObjectTypeTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<id>9</id>" +
            "<name>LAHEY</name>" +
            "<phone>" +
                "<home>(902) 987-0011</home>" +
                "<cell>(902) 789-1100</cell>" +
            "</phone>" +
        "</dbws_emp_typeType>";

    @Test
    public void validateJavaClassName() {
        Project orProject = builder.getOrProject();
        ClassDescriptor empORDesc = orProject.getDescriptorForAlias(EMP_TYPE_ALIAS);
        assertNotNull("No OR descriptor found for alias [" + EMP_TYPE_ALIAS + "]", empORDesc);
        assertEquals("Expected class name [" + EMP_TYPE_CLASSNAME + "] but was [" + empORDesc.getJavaClassName() + "]", empORDesc.getJavaClassName(), EMP_TYPE_CLASSNAME);
        ClassDescriptor phoneORDesc = orProject.getDescriptorForAlias(PHONE_TYPE_ALIAS);
        assertNotNull("No OR descriptor found for alias [" + PHONE_TYPE_ALIAS + "]", phoneORDesc);
        assertEquals("Expected class name [" + PHONE_TYPE_CLASSNAME + "] but was [" + phoneORDesc.getJavaClassName() + "]", phoneORDesc.getJavaClassName(), PHONE_TYPE_CLASSNAME);

        Project oxProject = builder.getOxProject();
        ClassDescriptor empOXDesc = oxProject.getDescriptorForAlias(EMP_TYPE_ALIAS);
        assertNotNull("No OX descriptor found for alias [" + EMP_TYPE_ALIAS + "]", empOXDesc);
        assertEquals("Expected class name [" + EMP_TYPE_CLASSNAME + "] but was [" + empOXDesc.getJavaClassName() + "]", empOXDesc.getJavaClassName(), EMP_TYPE_CLASSNAME);
        ClassDescriptor phoneOXDesc = oxProject.getDescriptorForAlias(PHONE_TYPE_ALIAS);
        assertNotNull("No OX descriptor found for alias [" + PHONE_TYPE_ALIAS + "]", phoneOXDesc);
        assertEquals("Expected class name [" + PHONE_TYPE_CLASSNAME + "] but was [" + phoneOXDesc.getJavaClassName() + "]", phoneOXDesc.getJavaClassName(), PHONE_TYPE_CLASSNAME);
    }

    @Test
    public void inTypeTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object pType = unmarshaller.unmarshal(new StringReader(MYTYPE_XML));
        Invocation invocation = new Invocation("TestIn");
        invocation.setParameter("P1", pType);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RETURN_VALUE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String RETURN_VALUE_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<value>1</value>";

    @Test
    public void outTypeTest() {
        Invocation invocation = new Invocation("TestOut");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(MYTYPE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void inAndOutTypeTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object pType = unmarshaller.unmarshal(new StringReader(MYTYPE_XML));
        Invocation invocation = new Invocation("TestInAndOut");
        invocation.setParameter("P1", pType);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(MYTYPE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void inOutTypeTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object pType = unmarshaller.unmarshal(new StringReader(MYTYPE_XML));
        Invocation invocation = new Invocation("TestInOut");
        invocation.setParameter("P1", pType);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(MYTYPE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String MYTYPE_XML =
        REGULAR_XML_HEADER +
        "<mytype_1Type xmlns=\"urn:ObjectTypeTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<id>66</id>" +
            "<name>Steve French</name>" +
        "</mytype_1Type>";

    @Test
    public void arrayEchoTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object pType = unmarshaller.unmarshal(new StringReader(MYTYPE_ARRAY_XML));
        Invocation invocation = new Invocation("TestEchoArray");
        invocation.setParameter("P1", pType);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(MYTYPE_ARRAY_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String MYTYPE_ARRAY_XML =
        REGULAR_XML_HEADER +
        "<mytype_1_arrayType xmlns=\"urn:ObjectTypeTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<item>" +
                "<id>66</id>" +
                "<name>Steve French</name>" +
            "</item>" +
        "</mytype_1_arrayType>";

    @Test
    public void multiInAndOutTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object pType = unmarshaller.unmarshal(new StringReader(MYTYPE_XML));
        Invocation invocation = new Invocation("TestMultiInAndOut");
        invocation.setParameter("P1", pType);
        invocation.setParameter("P2", " the Cougar!");
        invocation.setParameter("P3", 3);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(MYTYPE2_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String MYTYPE2_XML =
        REGULAR_XML_HEADER +
        "<mytype_1Type xmlns=\"urn:ObjectTypeTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<id>69</id>" +
            "<name>Steve French the Cougar!</name>" +
        "</mytype_1Type>";
}
