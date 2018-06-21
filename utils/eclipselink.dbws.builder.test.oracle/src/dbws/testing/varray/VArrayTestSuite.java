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
//     David McCann - October 3, 2011 - 2.4 - Initial implementation
package dbws.testing.varray;

//javase imports
import java.io.StringReader;
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
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests VARRAY types.
 *
 */
public class VArrayTestSuite extends DBWSTestSuite {
    static final String VCARRAY_ALIAS = "Vcarray";
    static final String VCARRAY_CLASSNAME = "varraytests.Vcarray_CollectionWrapper";

    static final String CREATE_VCARRAY_VARRAY =
        "CREATE OR REPLACE TYPE VCARRAY AS VARRAY(4) OF VARCHAR2(20)";
    static final String CREATE_GETVCARRAY_PROC =
        "CREATE OR REPLACE PROCEDURE GETVCARRAY(T IN VARCHAR, U OUT VCARRAY) AS" +
        "\nBEGIN" +
            "\nU := VCARRAY();" +
            "\nU.EXTEND;" +
            "\nU(1) := CONCAT('entry1-', T);" +
            "\nU.EXTEND;" +
            "\nU(2) := CONCAT('entry2-', T);" +
        "\nEND GETVCARRAY;";
    static final String CREATE_GETVCARRAY_PROC2 =
         "CREATE OR REPLACE PROCEDURE GETVCARRAY_PROC2(T IN VARCHAR, U IN OUT VCARRAY) AS" +
         "\nBEGIN" +
             "\nU(1) := CONCAT('entry1-', T);" +
             "\nU(2) := CONCAT('entry2-', T);" +
         "\nEND GETVCARRAY_PROC2;";
    static final String CREATE_GETVCARRAY2_FUNC =
        "CREATE OR REPLACE FUNCTION GETVCARRAY2(T IN VARCHAR) RETURN VCARRAY AS" +
        "\nL_DATA VCARRAY := VCARRAY();" +
        "\nBEGIN" +
            "\nL_DATA.EXTEND;" +
            "\nL_DATA(1) := CONCAT('entry1-', T);" +
            "\nL_DATA.EXTEND;" +
            "\nL_DATA(2) := CONCAT('entry2-', T);" +
            "\nRETURN L_DATA;" +
        "\nEND GETVCARRAY2;";
    static final String CREATE_COPYVCARRAY_PROC =
        "CREATE OR REPLACE PROCEDURE COPYVCARRAY(V IN VCARRAY, U OUT VCARRAY) AS" +
        "\nBEGIN" +
            "\nU := V;" +
            "\nU.EXTEND;" +
            "\nU(3) := 'copy';" +
        "\nEND COPYVCARRAY;";
    static final String CREATE_COPYVCARRAY2_FUNC =
        "CREATE OR REPLACE FUNCTION COPYVCARRAY2(V IN VCARRAY) RETURN VCARRAY AS" +
        "\nL_DATA VCARRAY := V;" +
        "\nBEGIN" +
            "\nL_DATA.EXTEND;" +
            "\nL_DATA(3) := 'copy';" +
            "\nRETURN L_DATA;" +
        "\nEND COPYVCARRAY2;";
    static final String CREATE_GETVALUEFROMVCARRAY_PROC =
        "CREATE OR REPLACE PROCEDURE GETVALUEFROMVCARRAY(V IN VCARRAY, I IN INTEGER, U OUT VARCHAR2, O OUT VARCHAR2) AS" +
        "\nBEGIN" +
            "\nU := V(I);" +
            "\nO := CONCAT('copy of ', V(I));" +
        "\nEND GETVALUEFROMVCARRAY;";
    static final String CREATE_GETVALUEFROMVCARRAY2_FUNC =
        "CREATE OR REPLACE FUNCTION GETVALUEFROMVCARRAY2(V IN VCARRAY, I IN INTEGER) RETURN VARCHAR2 AS" +
        "\nBEGIN" +
            "\nRETURN V(I);" +
        "\nEND GETVALUEFROMVCARRAY2;";
    static final String DROP_VCARRAY_VARRAY =
        "DROP TYPE VCARRAY";
    static final String DROP_GETVCARRAY_PROC =
        "DROP PROCEDURE GETVCARRAY";
    static final String DROP_GETVCARRAY_PROC2 =
        "DROP PROCEDURE GETVCARRAY_PROC2";
    static final String DROP_GETVCARRAY2_FUNC =
        "DROP FUNCTION GETVCARRAY2";
    static final String DROP_COPYVCARRAY_PROC =
        "DROP PROCEDURE COPYVCARRAY";
    static final String DROP_COPYVCARRAY2_FUNC =
        "DROP FUNCTION COPYVCARRAY2";
    static final String DROP_GETVALUEFROMVCARRAY_PROC =
        "DROP PROCEDURE GETVALUEFROMVCARRAY";
    static final String DROP_GETVALUEFROMVCARRAY2_FUNC =
        "DROP FUNCTION GETVALUEFROMVCARRAY2";

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
            runDdl(conn, CREATE_VCARRAY_VARRAY, ddlDebug);
            runDdl(conn, CREATE_GETVCARRAY_PROC, ddlDebug);
            runDdl(conn, CREATE_GETVCARRAY_PROC2, ddlDebug);
            runDdl(conn, CREATE_GETVCARRAY2_FUNC, ddlDebug);
            runDdl(conn, CREATE_COPYVCARRAY_PROC, ddlDebug);
            runDdl(conn, CREATE_COPYVCARRAY2_FUNC, ddlDebug);
            runDdl(conn, CREATE_GETVALUEFROMVCARRAY_PROC, ddlDebug);
            runDdl(conn, CREATE_GETVALUEFROMVCARRAY2_FUNC, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">VArrayTests</property>" +
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
                  "name=\"GetVCArrayTest\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GETVCARRAY\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"vcarrayType\" " +
              "/>" +
              "<procedure " +
                  "name=\"GetVCArrayProcTest2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GETVCARRAY_PROC2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"vcarrayType\" " +
              "/>" +
              "<procedure " +
                  "name=\"GetVCArrayTest2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GETVCARRAY2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"vcarrayType\" " +
              "/>" +
              "<procedure " +
                  "name=\"CopyVCArrayTest\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"COPYVCARRAY\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"vcarrayType\" " +
              "/>" +
              "<procedure " +
                  "name=\"CopyVCArrayTest2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"COPYVCARRAY2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"vcarrayType\" " +
              "/>" +
              "<procedure " +
                  "name=\"GetValueFromVCArrayTest\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GETVALUEFROMVCARRAY\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
                  "isCollection=\"true\" " +
                  "returnType=\"xsd:string\" " +
              "/>" +
              "<procedure " +
                  "name=\"GetValueFromVCArrayTest2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GETVALUEFROMVCARRAY2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "isSimpleXMLFormat=\"true\" " +
               "/>" +
            "</dbws-builder>";
          builder = new DBWSBuilder();
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            runDdl(conn, DROP_GETVCARRAY_PROC, ddlDebug);
            runDdl(conn, DROP_GETVCARRAY_PROC2, ddlDebug);
            runDdl(conn, DROP_GETVCARRAY2_FUNC, ddlDebug);
            runDdl(conn, DROP_COPYVCARRAY_PROC, ddlDebug);
            runDdl(conn, DROP_COPYVCARRAY2_FUNC, ddlDebug);
            runDdl(conn, DROP_GETVALUEFROMVCARRAY_PROC, ddlDebug);
            runDdl(conn, DROP_GETVALUEFROMVCARRAY2_FUNC, ddlDebug);
            runDdl(conn, DROP_VCARRAY_VARRAY, ddlDebug);
        }
    }

    @Test
    public void getVCArrayTest() {
        Invocation invocation = new Invocation("GetVCArrayTest");
        invocation.setParameter("T", "xxx");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(VARRAY_RESULT));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void getVCArrayTest2() {
        Invocation invocation = new Invocation("GetVCArrayTest2");
        invocation.setParameter("T", "xxx");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(VARRAY_RESULT));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    static String VARRAY_RESULT =
        REGULAR_XML_HEADER +
        "<vcarrayType xmlns=\"urn:VArrayTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<item>entry1-xxx</item>" +
            "<item>entry2-xxx</item>" +
        "</vcarrayType>";

    @Test
    public void copyVCArrayTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object input = unmarshaller.unmarshal(new StringReader(INPUT_XML));
        Invocation invocation = new Invocation("CopyVCArrayTest");
        invocation.setParameter("V", input);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void copyVCArrayTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object input = unmarshaller.unmarshal(new StringReader(INPUT_XML));
        Invocation invocation = new Invocation("CopyVCArrayTest2");
        invocation.setParameter("V", input);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void getVCArrayProcTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object input = unmarshaller.unmarshal(new StringReader(INPUT_XML));
        Invocation invocation = new Invocation("GetVCArrayProcTest2");
        invocation.setParameter("T", "xxx");
        invocation.setParameter("U", input);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(VARRAY_RESULT));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    static String INPUT_XML =
        REGULAR_XML_HEADER +
        "<vcarrayType xmlns=\"urn:VArrayTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<item>one</item>" +
            "<item>two</item>" +
        "</vcarrayType>";

    static String RESULT_XML =
        REGULAR_XML_HEADER +
        "<vcarrayType xmlns=\"urn:VArrayTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<item>one</item>" +
            "<item>two</item>" +
            "<item>copy</item>" +
        "</vcarrayType>";

    @Test
    public void getValueFromVCArrayTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object input = unmarshaller.unmarshal(new StringReader(INPUT2_XML));
        Invocation invocation = new Invocation("GetValueFromVCArrayTest");
        invocation.setParameter("V", input);
        invocation.setParameter("I", 2);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT2_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void getValueFromVCArrayTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object input = unmarshaller.unmarshal(new StringReader(INPUT2_XML));
        Invocation invocation = new Invocation("GetValueFromVCArrayTest2");
        invocation.setParameter("V", input);
        invocation.setParameter("I", 3);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT3_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    static String INPUT2_XML =
        REGULAR_XML_HEADER +
        "<vcarrayType xmlns=\"urn:VArrayTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<item>1-foo</item>" +
            "<item>2-bar</item>" +
            "<item>3-foobar</item>" +
            "<item>4-blah</item>" +
        "</vcarrayType>";

    static String RESULT2_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
            "<simple-xml>" +
                "<U>2-bar</U>" +
                "<O>copy of 2-bar</O>" +
            "</simple-xml>" +
        "</simple-xml-format>";

    static String RESULT3_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
            "<simple-xml>" +
                "<result>3-foobar</result>" +
            "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void validateJavaClassName() {
        Project orProject = builder.getOrProject();
        ClassDescriptor vcarrayORDesc = orProject.getDescriptorForAlias(VCARRAY_ALIAS);
        assertNotNull("No OR descriptor found for alias [" + VCARRAY_ALIAS + "]", vcarrayORDesc);
        assertEquals("Expected class name [" + VCARRAY_CLASSNAME + "] but was [" + vcarrayORDesc.getJavaClassName() + "]", vcarrayORDesc.getJavaClassName(), VCARRAY_CLASSNAME);

        Project oxProject = builder.getOxProject();
        ClassDescriptor vcarrayOXDesc = oxProject.getDescriptorForAlias(VCARRAY_ALIAS);
        assertNotNull("No OX descriptor found for alias [" + VCARRAY_ALIAS + "]", vcarrayOXDesc);
        assertEquals("Expected class name [" + VCARRAY_CLASSNAME + "] but was [" + vcarrayOXDesc.getJavaClassName() + "]", vcarrayOXDesc.getJavaClassName(), VCARRAY_CLASSNAME);
    }
}
