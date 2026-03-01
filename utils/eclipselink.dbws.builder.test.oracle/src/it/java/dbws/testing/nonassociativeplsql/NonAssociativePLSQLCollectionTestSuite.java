/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     David McCann - December 29, 2011 - 2.4 - Initial implementation
package dbws.testing.nonassociativeplsql;

//javase imports
import java.io.StringReader;
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
 * Tests non-associative PL/SQL collections.
 *
 */
public class NonAssociativePLSQLCollectionTestSuite extends DBWSTestSuite {

    static final String CREATE_NONASSOC_PACKAGE =
            """
                    CREATE OR REPLACE PACKAGE NONASSOC_PACKAGE AS
                    TYPE NONASSO_ARRAY IS TABLE OF VARCHAR2(20);
                    TYPE ASSO_ARRAY IS TABLE OF VARCHAR2(20) INDEX BY BINARY_INTEGER;
                    PROCEDURE GETARRAYCOUNT(NAMES IN NONASSO_ARRAY, CNT OUT NUMERIC);
                    PROCEDURE GETITEMCOUNTFORINDEX(NAMES IN NONASSO_ARRAY, CNT IN OUT NUMERIC);
                    PROCEDURE CREATEARRAY(ITEM1 IN VARCHAR2, ITEM2 IN VARCHAR2, ITEM3 IN VARCHAR2, OUTARRAY OUT NONASSO_ARRAY);
                    PROCEDURE COPYARRAY(INARRAY IN NONASSO_ARRAY, OUTARRAY OUT NONASSO_ARRAY);
                    PROCEDURE COPYTOINDEXEDARRAY(INARRAY IN NONASSO_ARRAY, OUTARRAY OUT ASSO_ARRAY);
                    PROCEDURE COPYFROMINDEXEDARRAY(INARRAY IN ASSO_ARRAY, OUTARRAY OUT NONASSO_ARRAY);
                    PROCEDURE ADDNAMETOARRAY(NAMETOADD IN VARCHAR2, NAMES IN OUT NONASSO_ARRAY);
                    FUNCTION GETARRAYCOUNTFUNC(NAMES IN NONASSO_ARRAY) RETURN NUMERIC;
                    FUNCTION CREATEARRAYFUNC(ITEM1 IN VARCHAR2, ITEM2 IN VARCHAR2, ITEM3 IN VARCHAR2) RETURN NONASSO_ARRAY;
                    FUNCTION COPYARRAYFUNC(INARRAY IN NONASSO_ARRAY) RETURN NONASSO_ARRAY;
                    FUNCTION COPYTOINDEXEDARRAYFUNC(INARRAY IN NONASSO_ARRAY) RETURN ASSO_ARRAY;
                    FUNCTION COPYFROMINDEXEDARRAYFUNC(INARRAY IN ASSO_ARRAY) RETURN NONASSO_ARRAY;
                    END NONASSOC_PACKAGE;""";
    static final String CREATE_NONASSOC_PACKAGE_BODY =
            """
                    CREATE OR REPLACE PACKAGE BODY NONASSOC_PACKAGE AS
                    PROCEDURE GETARRAYCOUNT(NAMES IN NONASSO_ARRAY, CNT OUT NUMERIC) AS
                    BEGIN
                    CNT := NAMES.COUNT;
                    END GETARRAYCOUNT;
                    PROCEDURE GETITEMCOUNTFORINDEX(NAMES IN NONASSO_ARRAY, CNT IN OUT NUMERIC) AS
                    BEGIN
                    CNT := LENGTH(NAMES(CNT));
                    END GETITEMCOUNTFORINDEX;
                    PROCEDURE CREATEARRAY(ITEM1 IN VARCHAR2, ITEM2 IN VARCHAR2, ITEM3 IN VARCHAR2, OUTARRAY OUT NONASSO_ARRAY) AS
                    BEGIN
                    OUTARRAY := NONASSO_ARRAY(ITEM1, ITEM2, ITEM3);
                    END CREATEARRAY;
                    PROCEDURE COPYARRAY(INARRAY IN NONASSO_ARRAY, OUTARRAY OUT NONASSO_ARRAY) AS
                    BEGIN
                    OUTARRAY := NONASSO_ARRAY();
                    FOR I IN INARRAY.FIRST .. INARRAY.LAST
                    LOOP
                    OUTARRAY.EXTEND;
                    OUTARRAY(I) := INARRAY(I);
                    END LOOP;
                    END COPYARRAY;
                    PROCEDURE COPYTOINDEXEDARRAY(INARRAY IN NONASSO_ARRAY, OUTARRAY OUT ASSO_ARRAY) AS
                    BEGIN
                    FOR I IN INARRAY.FIRST .. INARRAY.LAST
                    LOOP
                    OUTARRAY(I) := INARRAY(I);
                    END LOOP;
                    END COPYTOINDEXEDARRAY;
                    PROCEDURE COPYFROMINDEXEDARRAY(INARRAY IN ASSO_ARRAY, OUTARRAY OUT NONASSO_ARRAY) AS
                    BEGIN
                    OUTARRAY := NONASSO_ARRAY();
                    FOR I IN INARRAY.FIRST .. INARRAY.LAST
                    LOOP
                    OUTARRAY.EXTEND;
                    OUTARRAY(I) := INARRAY(I);
                    END LOOP;
                    END COPYFROMINDEXEDARRAY;
                    PROCEDURE ADDNAMETOARRAY(NAMETOADD IN VARCHAR2, NAMES IN OUT NONASSO_ARRAY) AS
                    BEGIN
                    NAMES.EXTEND;
                    NAMES(NAMES.COUNT) := NAMETOADD;
                    END ADDNAMETOARRAY;
                    FUNCTION GETARRAYCOUNTFUNC(NAMES IN NONASSO_ARRAY) RETURN NUMERIC AS
                    CNT NUMERIC;
                    BEGIN
                    CNT := NAMES.COUNT;
                    RETURN CNT;
                    END GETARRAYCOUNTFUNC;
                    FUNCTION CREATEARRAYFUNC(ITEM1 IN VARCHAR2, ITEM2 IN VARCHAR2, ITEM3 IN VARCHAR2) RETURN NONASSO_ARRAY AS
                    OUTARRAY NONASSO_ARRAY;
                    BEGIN
                    OUTARRAY := NONASSO_ARRAY(ITEM1, ITEM2, ITEM3);
                    RETURN OUTARRAY;
                    END CREATEARRAYFUNC;
                    FUNCTION COPYARRAYFUNC(INARRAY IN NONASSO_ARRAY) RETURN NONASSO_ARRAY AS
                    OUTARRAY NONASSO_ARRAY;
                    BEGIN
                    OUTARRAY := NONASSO_ARRAY();
                    FOR I IN INARRAY.FIRST .. INARRAY.LAST
                    LOOP
                    OUTARRAY.EXTEND;
                    OUTARRAY(I) := INARRAY(I);
                    END LOOP;
                    RETURN OUTARRAY;
                    END COPYARRAYFUNC;
                    FUNCTION COPYTOINDEXEDARRAYFUNC(INARRAY IN NONASSO_ARRAY) RETURN ASSO_ARRAY AS
                    OUTARRAY ASSO_ARRAY;
                    BEGIN
                    FOR I IN INARRAY.FIRST .. INARRAY.LAST
                    LOOP
                    OUTARRAY(I) := INARRAY(I);
                    END LOOP;
                    RETURN OUTARRAY;
                    END COPYTOINDEXEDARRAYFUNC;
                    FUNCTION COPYFROMINDEXEDARRAYFUNC(INARRAY IN ASSO_ARRAY) RETURN NONASSO_ARRAY AS
                    OUTARRAY NONASSO_ARRAY;
                    BEGIN
                    OUTARRAY := NONASSO_ARRAY();
                    FOR I IN INARRAY.FIRST .. INARRAY.LAST
                    LOOP
                    OUTARRAY.EXTEND;
                    OUTARRAY(I) := INARRAY(I);
                    END LOOP;
                    RETURN OUTARRAY;
                    END COPYFROMINDEXEDARRAYFUNC;
                    END NONASSOC_PACKAGE;""";
    static final String CREATE_NONASSO_ARRAY_TYPE =
        "CREATE OR REPLACE TYPE NONASSOC_PACKAGE_NONASSO_ARRAY AS TABLE OF VARCHAR2(20)";
    static final String CREATE_ASSO_ARRAY_TYPE =
        "CREATE OR REPLACE TYPE NONASSOC_PACKAGE_ASSO_ARRAY AS TABLE OF VARCHAR2(20)";

    static final String DROP_PACKAGE_NONASSOC_PACKAGE =
        "DROP PACKAGE NONASSOC_PACKAGE";
    static final String DROP_NONASSO_ARRAY_TYPE =
        "DROP TYPE NONASSOC_PACKAGE_NONASSO_ARRAY";
    static final String DROP_ASSO_ARRAY_TYPE =
        "DROP TYPE NONASSOC_PACKAGE_ASSO_ARRAY";

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
            runDdl(conn, CREATE_NONASSOC_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_NONASSOC_PACKAGE_BODY, ddlDebug);
            runDdl(conn, CREATE_NONASSO_ARRAY_TYPE, ddlDebug);
            runDdl(conn, CREATE_ASSO_ARRAY_TYPE, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">NonAssociativePLSQLCollection</property>" +
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
                  "name=\"GetArrayCountTest\" " +
                  "catalogPattern=\"NONASSOC_PACKAGE\" " +
                  "procedurePattern=\"GETARRAYCOUNT\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"GetItemCountForIndexTest\" " +
                  "catalogPattern=\"NONASSOC_PACKAGE\" " +
                  "procedurePattern=\"GETITEMCOUNTFORINDEX\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CreateArrayTest\" " +
                  "catalogPattern=\"NONASSOC_PACKAGE\" " +
                  "procedurePattern=\"CREATEARRAY\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyArrayTest\" " +
                  "catalogPattern=\"NONASSOC_PACKAGE\" " +
                  "procedurePattern=\"COPYARRAY\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyToIndexedArrayTest\" " +
                  "catalogPattern=\"NONASSOC_PACKAGE\" " +
                  "procedurePattern=\"COPYTOINDEXEDARRAY\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyFromIndexedArrayTest\" " +
                  "catalogPattern=\"NONASSOC_PACKAGE\" " +
                  "procedurePattern=\"COPYFROMINDEXEDARRAY\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"AddNameToArrayTest\" " +
                  "catalogPattern=\"NONASSOC_PACKAGE\" " +
                  "procedurePattern=\"ADDNAMETOARRAY\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"GetArrayCountFuncTest\" " +
                  "catalogPattern=\"NONASSOC_PACKAGE\" " +
                  "procedurePattern=\"GETARRAYCOUNTFUNC\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CreateArrayFuncTest\" " +
                  "catalogPattern=\"NONASSOC_PACKAGE\" " +
                  "procedurePattern=\"CREATEARRAYFUNC\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyArrayFuncTest\" " +
                  "catalogPattern=\"NONASSOC_PACKAGE\" " +
                  "procedurePattern=\"COPYARRAYFUNC\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyToIndexedArrayFuncTest\" " +
                  "catalogPattern=\"NONASSOC_PACKAGE\" " +
                  "procedurePattern=\"COPYTOINDEXEDARRAYFUNC\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyFromIndexedArrayFuncTest\" " +
                  "catalogPattern=\"NONASSOC_PACKAGE\" " +
                  "procedurePattern=\"COPYFROMINDEXEDARRAYFUNC\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            runDdl(conn, DROP_PACKAGE_NONASSOC_PACKAGE, ddlDebug);
            runDdl(conn, DROP_NONASSO_ARRAY_TYPE, ddlDebug);
            runDdl(conn, DROP_ASSO_ARRAY_TYPE, ddlDebug);
        }
    }

    @Test
    public void getArrayCountTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputArray = unmarshaller.unmarshal(new StringReader(NON_ASSOC_ARRAY_XML));
        Invocation invocation = new Invocation("GetArrayCountTest");
        invocation.setParameter("NAMES", inputArray);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(COUNT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void getItemCountForIndexTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputArray = unmarshaller.unmarshal(new StringReader(NON_ASSOC_ARRAY_XML));
        Invocation invocation = new Invocation("GetItemCountForIndexTest");
        invocation.setParameter("NAMES", inputArray);
        invocation.setParameter("CNT", 1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(COUNT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String NON_ASSOC_ARRAY_XML =
        STANDALONE_XML_HEADER +
        "<nonassoc_package_nonasso_arrayType xmlns=\"urn:NonAssociativePLSQLCollection\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<item>Jim</item>" +
        "<item>Jack</item>" +
        "<item>John</item>" +
        "</nonassoc_package_nonasso_arrayType>";
    public static final String COUNT_XML =
        STANDALONE_XML_HEADER +
        "<value>3</value>";

    @Test
    public void createArrayTest() {
        Invocation invocation = new Invocation("CreateArrayTest");
        invocation.setParameter("ITEM1", "Some item");
        invocation.setParameter("ITEM2", "Another item");
        invocation.setParameter("ITEM3", "The last item");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NON_ASSOC_ITEMS_ARRAY_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String NON_ASSOC_ITEMS_ARRAY_XML =
        STANDALONE_XML_HEADER +
        "<nonassoc_package_nonasso_arrayType xmlns=\"urn:NonAssociativePLSQLCollection\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<item>Some item</item>" +
        "<item>Another item</item>" +
        "<item>The last item</item>" +
        "</nonassoc_package_nonasso_arrayType>";

    @Test
    public void copyArrayTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputArray = unmarshaller.unmarshal(new StringReader(NON_ASSOC_ARRAY_XML));
        Invocation invocation = new Invocation("CopyArrayTest");
        invocation.setParameter("INARRAY", inputArray);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NON_ASSOC_ARRAY_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void copyToIndexedArrayTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputArray = unmarshaller.unmarshal(new StringReader(NON_ASSOC_ARRAY_XML));
        Invocation invocation = new Invocation("CopyToIndexedArrayTest");
        invocation.setParameter("INARRAY", inputArray);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ASSOC_ARRAY_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void copyFromIndexedArrayTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputArray = unmarshaller.unmarshal(new StringReader(ASSOC_ARRAY_XML));
        Invocation invocation = new Invocation("CopyFromIndexedArrayTest");
        invocation.setParameter("INARRAY", inputArray);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NON_ASSOC_ARRAY_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ASSOC_ARRAY_XML =
        STANDALONE_XML_HEADER +
        "<nonassoc_package_asso_arrayType xmlns=\"urn:NonAssociativePLSQLCollection\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<item>Jim</item>" +
        "<item>Jack</item>" +
        "<item>John</item>" +
        "</nonassoc_package_asso_arrayType>";

    @Test
    public void addNameToArrayTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputArray = unmarshaller.unmarshal(new StringReader(NON_ASSOC_ARRAY_XML));
        Invocation invocation = new Invocation("AddNameToArrayTest");
        invocation.setParameter("NAMETOADD", "Fred");
        invocation.setParameter("NAMES", inputArray);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NON_ASSOC_ARRAY2_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String NON_ASSOC_ARRAY2_XML =
        STANDALONE_XML_HEADER +
        "<nonassoc_package_nonasso_arrayType xmlns=\"urn:NonAssociativePLSQLCollection\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "<item>Jim</item>" +
        "<item>Jack</item>" +
        "<item>John</item>" +
        "<item>Fred</item>" +
        "</nonassoc_package_nonasso_arrayType>";

    @Test
    public void getArrayCountFuncTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputArray = unmarshaller.unmarshal(new StringReader(NON_ASSOC_ARRAY_XML));
        Invocation invocation = new Invocation("GetArrayCountFuncTest");
        invocation.setParameter("NAMES", inputArray);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(COUNT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void createArrayFuncTest() {
        Invocation invocation = new Invocation("CreateArrayFuncTest");
        invocation.setParameter("ITEM1", "Some item");
        invocation.setParameter("ITEM2", "Another item");
        invocation.setParameter("ITEM3", "The last item");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NON_ASSOC_ITEMS_ARRAY_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void copyArrayFuncTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputArray = unmarshaller.unmarshal(new StringReader(NON_ASSOC_ITEMS_ARRAY_XML));
        Invocation invocation = new Invocation("CopyArrayFuncTest");
        invocation.setParameter("INARRAY", inputArray);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NON_ASSOC_ITEMS_ARRAY_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void copyToIndexedArrayFuncTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputArray = unmarshaller.unmarshal(new StringReader(NON_ASSOC_ARRAY_XML));
        Invocation invocation = new Invocation("CopyToIndexedArrayFuncTest");
        invocation.setParameter("INARRAY", inputArray);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ASSOC_ARRAY_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void copyFromIndexedArrayFuncTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputArray = unmarshaller.unmarshal(new StringReader(ASSOC_ARRAY_XML));
        Invocation invocation = new Invocation("CopyFromIndexedArrayFuncTest");
        invocation.setParameter("INARRAY", inputArray);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NON_ASSOC_ARRAY_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
}
