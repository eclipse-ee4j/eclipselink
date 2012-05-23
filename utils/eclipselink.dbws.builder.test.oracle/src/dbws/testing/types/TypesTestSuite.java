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
 *     David McCann - Novebmer 10, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.types;

//javase imports
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests various types.
 *
 */
public class TypesTestSuite extends DBWSTestSuite {

    static final String CREATE_PACKAGE_TEST_TYPES =
    	"CREATE OR REPLACE PACKAGE TEST_TYPES AS" +
          "\nFUNCTION ECHO_INTEGER (PINTEGER IN INTEGER) RETURN INTEGER;" +
          "\nFUNCTION ECHO_SMALLINT(PSMALLINT IN SMALLINT) RETURN SMALLINT;" +
          "\nFUNCTION ECHO_NUMERIC (PNUMERIC IN NUMERIC) RETURN NUMERIC;" +
          "\nFUNCTION ECHO_DEC (PDEC IN DEC) RETURN DEC;" +
          "\nFUNCTION ECHO_DECIMAL (PDECIMAL IN DECIMAL) RETURN DECIMAL;" +
          "\nFUNCTION ECHO_NUMBER (PNUMBER IN NUMBER) RETURN NUMBER;" +
          "\nFUNCTION ECHO_VARCHAR(PVARCHAR IN VARCHAR) RETURN VARCHAR;" +
          "\nFUNCTION ECHO_VARCHAR2 (PINPUTVARCHAR IN VARCHAR2) RETURN VARCHAR2;" +
          "\nFUNCTION ECHO_CHAR (PINPUTCHAR IN CHAR) RETURN CHAR;" +
          "\nFUNCTION ECHO_REAL (PREAL IN REAL) RETURN REAL;" +
          "\nFUNCTION ECHO_FLOAT (PINPUTFLOAT IN FLOAT) RETURN FLOAT;" +
          "\nFUNCTION ECHO_DOUBLE (PDOUBLE IN DOUBLE PRECISION) RETURN DOUBLE PRECISION;" +
          "\nFUNCTION ECHO_DATE (PINPUTDATE IN DATE) RETURN DATE;" +
          "\nFUNCTION ECHO_TIMESTAMP (PINPUTTS IN TIMESTAMP) RETURN TIMESTAMP;" +
          "\nFUNCTION ECHO_CLOB (PINPUTCLOB IN CLOB) RETURN CLOB;" +
          "\nFUNCTION ECHO_BLOB (PINPUTBLOB IN BLOB) RETURN BLOB;" +
          "\nFUNCTION ECHO_LONG (PLONG IN LONG) RETURN LONG;" +
          "\nFUNCTION ECHO_LONG_RAW (PLONGRAW IN LONG RAW) RETURN LONG RAW;" +
          "\nFUNCTION ECHO_RAW(PRAW IN RAW) RETURN RAW;" +
        "\nEND;" ;

    static final String CREATE_PACKAGE_BODY_TEST_TYPES =
        "CREATE OR REPLACE PACKAGE BODY TEST_TYPES AS" +
          "\nFUNCTION ECHO_INTEGER (PINTEGER IN INTEGER) RETURN INTEGER IS" +
          "\nBEGIN" +
            "\nRETURN PINTEGER;" +
          "\nEND ECHO_INTEGER;" +
          "\nFUNCTION ECHO_SMALLINT(PSMALLINT IN SMALLINT) RETURN SMALLINT IS" +
          "\nBEGIN" +
            "\nRETURN PSMALLINT;" +
          "\nEND ECHO_SMALLINT;" +
          "\nFUNCTION ECHO_NUMERIC (PNUMERIC IN NUMERIC) RETURN NUMERIC IS" +
          "\nBEGIN" +
            "\nRETURN PNUMERIC;" +
          "\nEND ECHO_NUMERIC;" +
          "\nFUNCTION ECHO_DEC (PDEC IN DEC) RETURN DEC IS" +
          "\nBEGIN" +
            "\nRETURN PDEC;" +
          "\nEND ECHO_DEC;" +
          "\nFUNCTION ECHO_DECIMAL (PDECIMAL IN DECIMAL) RETURN DECIMAL IS" +
          "\nBEGIN" +
            "\nRETURN PDECIMAL;" +
          "\nEND ECHO_DECIMAL;" +
          "\nFUNCTION ECHO_NUMBER (PNUMBER IN NUMBER) RETURN NUMBER IS" +
          "\nBEGIN" +
            "\nRETURN PNUMBER;" +
          "\nEND ECHO_NUMBER;" +
          "\nFUNCTION ECHO_VARCHAR(PVARCHAR IN VARCHAR) RETURN VARCHAR IS" +
          "\nBEGIN" +
            "\nRETURN PVARCHAR;" +
          "\nEND ECHO_VARCHAR;" +
          "\nFUNCTION ECHO_VARCHAR2 (PINPUTVARCHAR IN VARCHAR2) RETURN VARCHAR2 IS" +
          "\nBEGIN" +
            "\nRETURN PINPUTVARCHAR;" +
          "\nEND ECHO_VARCHAR2;" +
          "\nFUNCTION ECHO_CHAR (PINPUTCHAR IN CHAR) RETURN CHAR IS" +
          "\nBEGIN" +
            "\nRETURN PINPUTCHAR;" +
          "\nEND ECHO_CHAR;" +
          "\nFUNCTION ECHO_REAL (PREAL IN REAL) RETURN REAL IS" +
          "\nBEGIN" +
              "\nRETURN PREAL;" +
          "\nEND ECHO_REAL;" +
          "\nFUNCTION ECHO_FLOAT (PINPUTFLOAT IN FLOAT) RETURN FLOAT IS" +
          "\nBEGIN" +
            "\nRETURN PINPUTFLOAT;" +
          "\nEND ECHO_FLOAT;" +
          "\nFUNCTION ECHO_DOUBLE (PDOUBLE IN DOUBLE PRECISION) RETURN DOUBLE PRECISION IS" +
          "\nBEGIN" +
            "\nRETURN PDOUBLE;" +
          "\nEND ECHO_DOUBLE;" +
          "\nFUNCTION ECHO_DATE (PINPUTDATE IN DATE) RETURN DATE IS" +
          "\nBEGIN" +
            "\nRETURN PINPUTDATE;" +
          "\nEND ECHO_DATE;" +
          "\nFUNCTION ECHO_TIMESTAMP (PINPUTTS IN TIMESTAMP) RETURN TIMESTAMP IS" +
          "\nBEGIN" +
            "\nRETURN PINPUTTS;" +
          "\nEND ECHO_TIMESTAMP;" +
          "\nFUNCTION ECHO_CLOB (PINPUTCLOB IN CLOB) RETURN CLOB IS" +
          "\nBEGIN" +
            "\nRETURN PINPUTCLOB;" +
          "\nEND ECHO_CLOB;" +
          "\nFUNCTION ECHO_BLOB (PINPUTBLOB IN BLOB) RETURN BLOB IS" +
          "\nBEGIN" +
            "\nRETURN PINPUTBLOB;" +
          "\nEND ECHO_BLOB;" +
          "\nFUNCTION ECHO_LONG (PLONG IN LONG) RETURN LONG IS" +
          "\nBEGIN" +
            "\nRETURN PLONG;" +
          "\nEND ECHO_LONG;" +
          "\nFUNCTION ECHO_LONG_RAW (PLONGRAW IN LONG RAW) RETURN LONG RAW IS" +
          "\nBEGIN" +
            "\nRETURN PLONGRAW;" +
          "\nEND ECHO_LONG_RAW;" +
          "\nFUNCTION ECHO_RAW(PRAW IN RAW) RETURN RAW IS" +
          "\nBEGIN" +
            "\nRETURN PRAW;" +
          "\nEND ECHO_RAW;" +
        "\nEND;" ;

    static final String DROP_PACKAGE_TEST_TYPES =
        "DROP PACKAGE TEST_TYPES";

    static final String DROP_PACKAGE_BODY_TEST_TYPES =
        "DROP PACKAGE BODY TEST_TYPES";

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
            runDdl(conn, CREATE_PACKAGE_TEST_TYPES, ddlDebug);
            runDdl(conn, CREATE_PACKAGE_BODY_TEST_TYPES, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">Types</property>" +
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
                  "name=\"echoInteger\" " +
                  "catalogPattern=\"TEST_TYPES\" " +
                  "procedurePattern=\"ECHO_INTEGER\" " +
                  "isSimpleXMLFormat=\"true\" " +
               "/>" +
               "<procedure " +
                  "name=\"echoSmallint\" " +
                  "catalogPattern=\"TEST_TYPES\" " +
                  "procedurePattern=\"ECHO_SMALLINT\" " +
                  "isSimpleXMLFormat=\"true\" " +
               "/>" +
               "<procedure " +
                  "name=\"echoNumeric\" " +
                  "catalogPattern=\"TEST_TYPES\" " +
                  "procedurePattern=\"ECHO_NUMERIC\" " +
                  "isSimpleXMLFormat=\"true\" " +
               "/>" +
	           "<procedure " +
	              "name=\"echoDec\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_DEC\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoDecimal\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_DECIMAL\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoNumber\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_NUMBER\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoVarchar\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_VARCHAR\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoVarchar2\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_VARCHAR2\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoChar\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_CHAR\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoReal\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_REAL\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoFloat\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_FLOAT\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoDouble\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_DOUBLE\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoDate\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_DATE\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoTimestamp\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_TIMESTAMP\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoClob\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_CLOB\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoBlob\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_BLOB\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoLong\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_LONG\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoLongRaw\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_LONG_RAW\" " +
	              "isSimpleXMLFormat=\"true\" " +
	           "/>" +
	           "<procedure " +
	              "name=\"echoRaw\" " +
	              "catalogPattern=\"TEST_TYPES\" " +
	              "procedurePattern=\"ECHO_RAW\" " +
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
            runDdl(conn, DROP_PACKAGE_BODY_TEST_TYPES, ddlDebug);
            runDdl(conn, DROP_PACKAGE_TEST_TYPES, ddlDebug);
        }
    }

    @Test
    public void echoInteger() {
        Invocation invocation = new Invocation("echoInteger");
        invocation.setParameter("PINTEGER", Integer.valueOf(128));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_INTEGER_RESULT));
        assertTrue("Control document not same as instance document.\nExpected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ECHO_INTEGER_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>128</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoSmallint() {
        Invocation invocation = new Invocation("echoSmallint");
        invocation.setParameter("PSMALLINT", Integer.valueOf(7));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_SMALLINT_RESULT));
        assertTrue("Control document not same as instance document.\nExpected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ECHO_SMALLINT_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>7</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoNumeric() {
        Invocation invocation = new Invocation("echoNumeric");
        invocation.setParameter("PNUMERIC", new BigDecimal("123.45"));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_NUMERIC_RESULT));
        assertTrue("Control document not same as instance document.\nExpected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ECHO_NUMERIC_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>123.45</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoDec() {
        Invocation invocation = new Invocation("echoDec");
        invocation.setParameter("PDEC", new BigDecimal("543.21"));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_DEC_RESULT));
        assertTrue("Control document not same as instance document.\nExpected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ECHO_DEC_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>543.21</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoDecimal() {
        Invocation invocation = new Invocation("echoDecimal");
        invocation.setParameter("PDECIMAL", new BigDecimal("23.9"));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_DECIMAL_RESULT));
        assertTrue("Control document not same as instance document.\nExpected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ECHO_DECIMAL_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>23.9</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoNumber() {
        Invocation invocation = new Invocation("echoNumber");
        invocation.setParameter("PNUMBER", BigDecimal.valueOf(17));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_NUMBER_RESULT));
        assertTrue("Control document not same as instance document.\nExpected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ECHO_NUMBER_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>17</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoVarchar() {
        Invocation invocation = new Invocation("echoVarchar");
        invocation.setParameter("PVARCHAR", "this is a varchar test");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_VARCHAR_RESULT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ECHO_VARCHAR_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>this is a varchar test</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoVarchar2() {
        Invocation invocation = new Invocation("echoVarchar2");
        invocation.setParameter("PINPUTVARCHAR", "this is a varchar2 test");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_VARCHAR2_RESULT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ECHO_VARCHAR2_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>this is a varchar2 test</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoChar() {
        Invocation invocation = new Invocation("echoChar");
        invocation.setParameter("PINPUTCHAR", "Q");
        Operation op = xrService.getOperation(invocation.getName());
        // something goes wrong with invoke
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_CHAR_RESULT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ECHO_CHAR_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>Q</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoReal() {
        Invocation invocation = new Invocation("echoReal");
        invocation.setParameter("PREAL", new Float("3.14159"));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_REAL_RESULT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ECHO_REAL_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>3.14159</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoFloat() {
        Invocation invocation = new Invocation("echoFloat");
        invocation.setParameter("PINPUTFLOAT", new Float("31415.926"));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_FLOAT_RESULT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ECHO_FLOAT_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>31415.926</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoDouble() {
        Invocation invocation = new Invocation("echoDouble");
        invocation.setParameter("PDOUBLE", new Double("314.15926"));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_DOUBLE_RESULT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ECHO_DOUBLE_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>314.15926</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoDate() throws ParseException {
        Invocation invocation = new Invocation("echoDate");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        invocation.setParameter("PINPUTDATE", format.parse("20091203"));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_DATE_RESULT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ECHO_DATE_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>2009-12-03T00:00:00.0</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoTimestamp() throws ParseException {
        Invocation invocation = new Invocation("echoTimestamp");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd:hhmmss.SSS");
        invocation.setParameter("PINPUTTS", format.parse("20091204:091923.123"));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_TS_RESULT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ECHO_TS_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>2009-12-04T09:19:23.123</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoClob() throws ParseException, SQLException {
        Invocation invocation = new Invocation("echoClob");
        invocation.setParameter("PINPUTCLOB", "This is a Clob test");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_CLOB_RESULT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ECHO_CLOB_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result>This is a Clob test</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoBlob() throws ParseException {
        Invocation invocation = new Invocation("echoBlob");
        byte[] testBytes = "This is a test".getBytes();
        invocation.setParameter("PINPUTBLOB", testBytes);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(((XMLRoot)result).getObject(), doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_BLOB_RESULT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ECHO_BLOB_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result xsi:type=\"xsd:base64Binary\" " +
                 "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                 "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                 "5647687063794270637942684948526C6333513D" +
              "</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoLong() throws ParseException {
        Invocation invocation = new Invocation("echoLong");
        byte[] testBytes = "This is another test".getBytes();
        invocation.setParameter("PLONG", testBytes);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(((XMLRoot)result).getObject(), doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_LONG_RESULT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ECHO_LONG_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result xsi:type=\"xsd:base64Binary\" " +
                 "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                 "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                 "564768706379427063794268626D3930614756794948526C6333513D" +
              "</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoLongRaw() throws ParseException {
        Invocation invocation = new Invocation("echoLongRaw");
        byte[] testBytes = ("This is yet another test (long stringggggggggggggggggggggggggggggggggggggg" +
                "ggggggggggggggggggggggggggggggggggggggg").getBytes();
        invocation.setParameter("PLONGRAW", testBytes);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(((XMLRoot)result).getObject(), doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_LONGRAW_RESULT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ECHO_LONGRAW_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result xsi:type=\"xsd:base64Binary\" " +
                 "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                 "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                 "5647687063794270637942355A585167595735766447686C636942305A584E30494368736232356E49484E30636D6C755A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32646E5A32633D" +
              "</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void echoRaw() throws ParseException {
        Invocation invocation = new Invocation("echoRaw");
        byte[] testBytes = "This is yet another test!".getBytes();
        invocation.setParameter("PRAW", testBytes);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(((XMLRoot)result).getObject(), doc);
        Document controlDoc = xmlParser.parse(new StringReader(ECHO_RAW_RESULT));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ECHO_RAW_RESULT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simple-xml-format>" +
           "<simple-xml>" +
              "<result xsi:type=\"xsd:base64Binary\" " +
                 "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                 "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                 "5647687063794270637942355A585167595735766447686C636942305A584E3049513D3D" +
              "</result>" +
           "</simple-xml>" +
        "</simple-xml-format>";
}