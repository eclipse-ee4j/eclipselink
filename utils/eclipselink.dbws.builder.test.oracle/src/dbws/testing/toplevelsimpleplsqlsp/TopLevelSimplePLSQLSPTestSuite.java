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
 *     David McCann - Novebmer 9, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.toplevelsimpleplsqlsp;

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
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests top-level procedures with PL/SQL arguments.
 *
 */
public class TopLevelSimplePLSQLSPTestSuite extends DBWSTestSuite {

    static final String CREATE_BOOL_PROC =
        "CREATE OR REPLACE PROCEDURE TOPLEVEL_BOOL_TEST(X IN BOOLEAN, Y OUT VARCHAR2) AS" +
        "\nBEGIN" +
            "\nIF X = TRUE THEN" +
                "\nY := 'true';" +
            "\nELSE" +
                "\nY := 'false';" +
            "\nEND IF;"+
        "\nEND TOPLEVEL_BOOL_TEST;";
    static final String CREATE_BOOL_IN_PROC =
        "CREATE OR REPLACE PROCEDURE TOPLEVEL_BOOL_IN_TEST(X IN BOOLEAN) AS" +
        "\nBEGIN" +
            "\nNULL;"+
        "\nEND TOPLEVEL_BOOL_IN_TEST;";
    static final String CREATE_BINARY_INT_PROC =
        "CREATE OR REPLACE PROCEDURE TOPLEVEL_BINARY_INT_TEST(X IN BINARY_INTEGER, Y OUT BINARY_INTEGER) AS" +
        "\nBEGIN" +
            "\nY := X;" +
        "\nEND TOPLEVEL_BINARY_INT_TEST;";
    static final String CREATE_PLS_INT_PROC =
        "CREATE OR REPLACE PROCEDURE TOPLEVEL_PLS_INT_TEST(X IN PLS_INTEGER, Y OUT PLS_INTEGER) AS" +
        "\nBEGIN" +
            "\nY := X;" +
        "\nEND TOPLEVEL_PLS_INT_TEST;";
    static final String CREATE_NATURAL_PROC =
        "CREATE OR REPLACE PROCEDURE TOPLEVEL_NATURAL_TEST(X IN NATURAL, Y OUT NATURAL) AS" +
        "\nBEGIN" +
            "\nY := X;" +
        "\nEND TOPLEVEL_NATURAL_TEST;";
    static final String CREATE_POSITIVE_PROC =
        "CREATE OR REPLACE PROCEDURE TOPLEVEL_POSITIVE_TEST(X IN POSITIVE, Y OUT POSITIVE) AS" +
        "\nBEGIN" +
            "\nY := X;" +
        "\nEND TOPLEVEL_POSITIVE_TEST;";

    static final String CREATE_SIGNTYPE_PROC =
        "CREATE OR REPLACE PROCEDURE TOPLEVEL_SIGNTYPE_TEST(X IN SIGNTYPE, Y OUT VARCHAR2) AS" +
        "\nBEGIN" +
	        "\nIF X = -1 THEN" +
		        "\nY := 'negative';" +
		    "\nELSIF X = 1 THEN" +
		        "\nY := 'positive';" +
		    "\nELSE" +
		        "\nY := 'zero';" +
		    "\nEND IF;"+
        "\nEND TOPLEVEL_SIGNTYPE_TEST;";

    static final String DROP_BOOL_PROC =
        "DROP PROCEDURE TOPLEVEL_BOOL_TEST";
    static final String DROP_BOOL_IN_PROC =
        "DROP PROCEDURE TOPLEVEL_BOOL_IN_TEST";
    static final String DROP_BINARY_INT_PROC =
        "DROP PROCEDURE TOPLEVEL_BINARY_INT_TEST";
    static final String DROP_PLS_INT_PROC =
        "DROP PROCEDURE TOPLEVEL_PLS_INT_TEST";
    static final String DROP_NATURAL_PROC =
        "DROP PROCEDURE TOPLEVEL_NATURAL_TEST";
    static final String DROP_POSITIVE_PROC =
        "DROP PROCEDURE TOPLEVEL_POSITIVE_TEST";
    static final String DROP_SIGNTYPE_PROC =
        "DROP PROCEDURE TOPLEVEL_SIGNTYPE_TEST";

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
            runDdl(conn, CREATE_BOOL_PROC, ddlDebug);
            runDdl(conn, CREATE_BOOL_IN_PROC, ddlDebug);
            runDdl(conn, CREATE_BINARY_INT_PROC, ddlDebug);
            runDdl(conn, CREATE_PLS_INT_PROC, ddlDebug);
            runDdl(conn, CREATE_NATURAL_PROC, ddlDebug);
            runDdl(conn, CREATE_POSITIVE_PROC, ddlDebug);
            runDdl(conn, CREATE_SIGNTYPE_PROC, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">TopLevelSimplePLSQLSP</property>" +
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
                  "name=\"testBoolean\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"TOPLEVEL_BOOL_TEST\" " +
                  "isSimpleXMLFormat=\"true\" " +
               "/>" +
               "<plsql-procedure " +
                   "name=\"testBooleanIn\" " +
                   "catalogPattern=\"TOPLEVEL\" " +
                   "procedurePattern=\"TOPLEVEL_BOOL_IN_TEST\" " +
                   "isSimpleXMLFormat=\"true\" " +
                "/>" +
               "<plsql-procedure " +
                   "name=\"testBinaryInt\" " +
                   "catalogPattern=\"TOPLEVEL\" " +
                   "procedurePattern=\"TOPLEVEL_BINARY_INT_TEST\" " +
                   "isSimpleXMLFormat=\"true\" " +
		       "/>" +
               "<plsql-procedure " +
                   "name=\"testPLSInt\" " +
                   "catalogPattern=\"TOPLEVEL\" " +
                   "procedurePattern=\"TOPLEVEL_PLS_INT_TEST\" " +
                   "isSimpleXMLFormat=\"true\" " +
		       "/>" +
               "<plsql-procedure " +
                   "name=\"testNatural\" " +
                   "catalogPattern=\"TOPLEVEL\" " +
                   "procedurePattern=\"TOPLEVEL_NATURAL_TEST\" " +
                   "isSimpleXMLFormat=\"true\" " +
		       "/>" +
               "<plsql-procedure " +
                   "name=\"testPositive\" " +
                   "catalogPattern=\"TOPLEVEL\" " +
                   "procedurePattern=\"TOPLEVEL_POSITIVE_TEST\" " +
                   "isSimpleXMLFormat=\"true\" " +
		       "/>" +
			   "<plsql-procedure " +
                   "name=\"testSignType\" " +
                   "catalogPattern=\"TOPLEVEL\" " +
                   "procedurePattern=\"TOPLEVEL_SIGNTYPE_TEST\" " +
                   "isSimpleXMLFormat=\"true\" " +
				"/>" +
            "</dbws-builder>";
          builder = new DBWSBuilder();
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_BOOL_PROC, ddlDebug);
            runDdl(conn, DROP_BOOL_IN_PROC, ddlDebug);
            runDdl(conn, DROP_BINARY_INT_PROC, ddlDebug);
            runDdl(conn, DROP_PLS_INT_PROC, ddlDebug);
            runDdl(conn, DROP_NATURAL_PROC, ddlDebug);
            runDdl(conn, DROP_POSITIVE_PROC, ddlDebug);
            runDdl(conn, DROP_SIGNTYPE_PROC, ddlDebug);
        }
    }

    @Test
    public void testBoolean() {
        Invocation invocation = new Invocation("testBoolean");
        invocation.setParameter("X", Integer.valueOf(0));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TEST_BOOLEAN_RESULT));
        assertTrue("Control document not same as instance document. Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String TEST_BOOLEAN_RESULT =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
            "<simple-xml>" +
                "<result>false</result>" +
            "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void testBooleanIn() {
        Invocation invocation = new Invocation("testBooleanIn");
        invocation.setParameter("X", Integer.valueOf(0));
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TEST_BOOLEAN_IN_RESULT));
        assertTrue("Control document not same as instance document. Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String TEST_BOOLEAN_IN_RESULT =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
            "<simple-xml>" +
                "<result>1</result>" +
            "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void testBinaryInt() {
        Invocation invocation = new Invocation("testBinaryInt");
        invocation.setParameter("X", -1234567890);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NEGATIVE_INTEGER_RESULT));
        assertTrue("Control document not same as instance document. Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void testPLSInt() {
        Invocation invocation = new Invocation("testPLSInt");
        invocation.setParameter("X", -1234567890);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NEGATIVE_INTEGER_RESULT));
        assertTrue("Control document not same as instance document. Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String NEGATIVE_INTEGER_RESULT =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
            "<simple-xml>" +
                "<result>-1234567890</result>" +
            "</simple-xml>" +
        "</simple-xml-format>";

    @Test
    public void testNatural() {
        Invocation invocation = new Invocation("testNatural");
        invocation.setParameter("X", 66);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(SIXTY_SIX_RESULT));
        assertTrue("Control document not same as instance document. Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void testPositive() {
        Invocation invocation = new Invocation("testPositive");
        invocation.setParameter("X", 66);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(SIXTY_SIX_RESULT));
        assertTrue("Control document not same as instance document. Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String SIXTY_SIX_RESULT =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
            "<simple-xml>" +
                "<result>66</result>" +
            "</simple-xml>" +
        "</simple-xml-format>";


    @Test
    public void testSignType() {
        Invocation invocation = new Invocation("testSignType");
        invocation.setParameter("X", -1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NEGATIVE_SIGN_TYPE_RESULT));
        assertTrue("Control document not same as instance document. Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String NEGATIVE_SIGN_TYPE_RESULT =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
            "<simple-xml>" +
                "<result>negative</result>" +
            "</simple-xml>" +
        "</simple-xml-format>";

}