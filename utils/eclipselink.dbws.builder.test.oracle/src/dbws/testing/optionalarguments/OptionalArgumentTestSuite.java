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
 *     David McCann - February 09, 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.optionalarguments;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import javax.wsdl.WSDLException;

import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import dbws.testing.DBWSTestSuite;

/**
 * Tests PL/SQL procedures with optional arguments.
 *
 */
public class OptionalArgumentTestSuite extends DBWSTestSuite {
    static final String CREATE_PHONE_TYPE =
        "CREATE OR REPLACE TYPE DBWS_PHONETYPE AS OBJECT (" +
            "\nHOME VARCHAR2(20)," +
            "\nCELL VARCHAR2(20)" +
        "\n)";
    static final String CREATE_PHONE_TYPE_TABLE =
        "CREATE OR REPLACE TYPE DBWS_PHONETYPE_TABLE AS TABLE OF DBWS_PHONETYPE";
    static final String CREATE_VCARRAY_VARRAY =
        "CREATE OR REPLACE TYPE DBWS_VCARRAY AS VARRAY(4) OF VARCHAR2(20)";
    static final String CREATE_OPTIONALARG_PACKAGE =
        "CREATE OR REPLACE PACKAGE OPTIONALARG AS" +
            "\nPROCEDURE OPTIONAL_ARG1(X IN PLS_INTEGER DEFAULT NULL, Y IN BOOLEAN, Z IN PLS_INTEGER DEFAULT NULL, Q OUT VARCHAR2);" +
            "\nPROCEDURE OPTIONAL_ARG2(X IN DBWS_VCARRAY DEFAULT NULL, Q OUT VARCHAR2);" +
            "\nPROCEDURE OPTIONAL_ARG3(X IN DBWS_PHONETYPE DEFAULT NULL, Q OUT VARCHAR2);" +
            "\nPROCEDURE OPTIONAL_ARG4(X IN DBWS_PHONETYPE_TABLE DEFAULT NULL, Q OUT VARCHAR2);" +
        "\nEND OPTIONALARG;";
    static final String CREATE_OPTIONALARG_BODY =
        "CREATE OR REPLACE PACKAGE BODY OPTIONALARG AS" +
            "\nPROCEDURE OPTIONAL_ARG1(X IN PLS_INTEGER DEFAULT NULL, Y IN BOOLEAN, Z IN PLS_INTEGER DEFAULT NULL, Q OUT VARCHAR2) AS" +
            "\nA PLS_INTEGER := X;" +
            "\nB PLS_INTEGER := Z;" +
            "\nC BOOLEAN;" +
            "\nBEGIN" +
                "\nIF A IS NULL THEN" +
                    "\nA := '-1';" +
                "\nEND IF;" +
                "\nIF B IS NULL THEN" +
	                "\nB := '-1';" +
	            "\nEND IF;" +
                "\nQ := CONCAT(A, ', ');" +
                "\nQ := CONCAT(Q, B);" +
	        "\nEND OPTIONAL_ARG1;" +
            "\nPROCEDURE OPTIONAL_ARG2(X IN DBWS_VCARRAY DEFAULT NULL, Q OUT VARCHAR2) AS" +
            "\nBEGIN" +
	            "\nIF X IS NULL THEN" +
			        "\nQ := 'null';" +
	            "\nELSE" + 
			        "\nQ := 'not null';" +
			    "\nEND IF;" +
	        "\nEND OPTIONAL_ARG2;" +
            "\nPROCEDURE OPTIONAL_ARG3(X IN DBWS_PHONETYPE DEFAULT NULL, Q OUT VARCHAR2) AS" +
            "\nBEGIN" +
	            "\nIF X IS NULL THEN" +
			        "\nQ := 'null';" +
	            "\nELSE" + 
			        "\nQ := CONCAT(X.HOME, ', ');" +
			        "\nQ := CONCAT(Q, X.CELL);" +
			    "\nEND IF;" +
	        "\nEND OPTIONAL_ARG3;" +
            "\nPROCEDURE OPTIONAL_ARG4(X IN DBWS_PHONETYPE_TABLE DEFAULT NULL, Q OUT VARCHAR2) AS" +
	        "\nPHONE1 DBWS_PHONETYPE;" +
	        "\nPHONE2 DBWS_PHONETYPE;" +
            "\nBEGIN" +
	            "\nIF X IS NULL THEN" +
			        "\nQ := 'null';" +
	            "\nELSE" +
			        "\nQ := CONCAT(X(1).HOME, ', ');" +
			        "\nQ := CONCAT(Q, X(1).CELL);" +
			        "\nQ := CONCAT(Q, ' - ');" +
			        "\nQ := CONCAT(Q, X(2).HOME);" +
			        "\nQ := CONCAT(Q, ', ');" +
			        "\nQ := CONCAT(Q, X(2).CELL);" +
			    "\nEND IF;" +
	        "\nEND OPTIONAL_ARG4;" +
        "\nEND OPTIONALARG;";

    static final String DROP_OPTIONALARG_BODY =
        "DROP PACKAGE BODY OPTIONALARG";
    static final String DROP_OPTIONALARG_PACKAGE =
        "DROP PACKAGE OPTIONALARG";
    static final String DROP_DBWS_VCARRAY_TYPE =
        "DROP TYPE DBWS_VCARRAY";
    static final String DROP_DBWS_PHONE_TYPE =
        "DROP TYPE DBWS_PHONETYPE";
    static final String DROP_DBWS_PHONE_TYPE_TABLE =
        "DROP TYPE DBWS_PHONETYPE_TABLE";

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
            runDdl(conn, CREATE_PHONE_TYPE, ddlDebug);
            runDdl(conn, CREATE_PHONE_TYPE_TABLE, ddlDebug);
            runDdl(conn, CREATE_OPTIONALARG_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_OPTIONALARG_BODY, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
          "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "<properties>" +
                "<property name=\"projectName\">optionalArgs</property>" +
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
                "name=\"OptionalArgTest1\" " +
                "catalogPattern=\"OPTIONALARG\" " +
                "procedurePattern=\"OPTIONAL_ARG1\" " +
                "isSimpleXMLFormat=\"true\" " +
            "/>" +
            "<plsql-procedure " +
	            "name=\"OptionalArgTest2\" " +
	            "catalogPattern=\"OPTIONALARG\" " +
	            "procedurePattern=\"OPTIONAL_ARG2\" " +
	            "isSimpleXMLFormat=\"true\" " +
	        "/>" +
            "<plsql-procedure " +
	            "name=\"OptionalArgTest3\" " +
	            "catalogPattern=\"OPTIONALARG\" " +
	            "procedurePattern=\"OPTIONAL_ARG3\" " +
	            "isSimpleXMLFormat=\"true\" " +
	        "/>" +
            "<plsql-procedure " +
	            "name=\"OptionalArgTest4\" " +
	            "catalogPattern=\"OPTIONALARG\" " +
	            "procedurePattern=\"OPTIONAL_ARG4\" " +
	            "isSimpleXMLFormat=\"true\" " +
	        "/>" +
          "</dbws-builder>";
          builder = new DBWSBuilder();
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_OPTIONALARG_BODY, ddlDebug);
            runDdl(conn, DROP_OPTIONALARG_PACKAGE, ddlDebug);
            runDdl(conn, DROP_DBWS_VCARRAY_TYPE, ddlDebug);
            runDdl(conn, DROP_DBWS_PHONE_TYPE_TABLE, ddlDebug);
            runDdl(conn, DROP_DBWS_PHONE_TYPE, ddlDebug);
        }
    }

    /**
     * Tests handing in second optional arg.  
     * Expects -1 for 'X'.
     */
    @Test
    public void optionalArgTest1() {
        Invocation invocation = new Invocation("OptionalArgTest1");
        invocation.setParameter("Z", 2);
        invocation.setParameter("Y", true);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OPTIONAL_ARG_RESULT1_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String OPTIONAL_ARG_RESULT1_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
		  "<simple-xml>" +
		    "<result>-1, 2</result>" +
		  "</simple-xml>" +
	    "</simple-xml-format>";

    /**
     * Tests handing in first optional arg.
     * Expects -1 for 'Z'.
     */
    @Test
    public void optionalArgTest2() {
        Invocation invocation = new Invocation("OptionalArgTest1");
        invocation.setParameter("X", 2);
        invocation.setParameter("Y", true);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OPTIONAL_ARG_RESULT2_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String OPTIONAL_ARG_RESULT2_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
		  "<simple-xml>" +
		    "<result>2, -1</result>" +
		  "</simple-xml>" +
	    "</simple-xml-format>";

    /**
     * Tests handing in no optional args.
     * Expects -1 for 'X' & 'Z'.
     */
    @Test
    public void optionalArgTest3() {
        Invocation invocation = new Invocation("OptionalArgTest1");
        invocation.setParameter("Y", true);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OPTIONAL_ARG_RESULT3_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String OPTIONAL_ARG_RESULT3_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
		  "<simple-xml>" +
		    "<result>-1, -1</result>" +
		  "</simple-xml>" +
	    "</simple-xml-format>";

    @Test
    public void optionalArgTest4() {
        Invocation invocation = new Invocation("OptionalArgTest1");
        invocation.setParameter("X", 66);
        invocation.setParameter("Z", 99);
        invocation.setParameter("Y", true);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OPTIONAL_ARG_RESULT5_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String OPTIONAL_ARG_RESULT5_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
		  "<simple-xml>" +
		    "<result>66, 99</result>" +
		  "</simple-xml>" +
	    "</simple-xml-format>";

    /**
     * Tests VArray default.  
     * Expects 'null'.
     */
    @Test
    public void optionalVArrayArgTest1() {
        Invocation invocation = new Invocation("OptionalArgTest2");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NULL_RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String NULL_RESULT_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
		  "<simple-xml>" +
		    "<result>null</result>" +
		  "</simple-xml>" +
	    "</simple-xml-format>";
    
    /**
     * Tests VArray default.  
     * Expects 'not-null'.
     */
    @Test
    public void optionalVArrayArgTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object input = unmarshaller.unmarshal(new StringReader(INPUT_XML));
        Invocation invocation = new Invocation("OptionalArgTest2");
        invocation.setParameter("X", input);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NOT_NULL_RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String INPUT_XML =
        REGULAR_XML_HEADER +
        "<dbws_vcarrayType xmlns=\"urn:optionalArgs\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<item>one</item>" +
            "<item>two</item>" +
        "</dbws_vcarrayType>";
    static String NOT_NULL_RESULT_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
		  "<simple-xml>" +
		    "<result>not null</result>" +
		  "</simple-xml>" +
	    "</simple-xml-format>";

    /**
     * Tests Object default.  
     * Expects 'null'.
     */
    @Test
    public void optionalObjectArgTest1() {
        Invocation invocation = new Invocation("OptionalArgTest3");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NULL_RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    
    /**
     * Tests Object default.  
     * Expects '(613)123-1234, (902)678-6789'.
     */
    @Test
    public void optionalObjectArgTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object input = unmarshaller.unmarshal(new StringReader(PHONE_INPUT_XML));
        Invocation invocation = new Invocation("OptionalArgTest3");
        invocation.setParameter("X", input);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(PHONE_RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String PHONE_INPUT_XML =
        REGULAR_XML_HEADER +
        "<dbws_phonetypeType xmlns=\"urn:optionalArgs\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<home>(613)123-1234</home>" +
            "<cell>(902)678-6789</cell>" +
        "</dbws_phonetypeType>";
    static String PHONE_RESULT_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
		  "<simple-xml>" +
		    "<result>(613)123-1234, (902)678-6789</result>" +
		  "</simple-xml>" +
	    "</simple-xml-format>";

    /**
     * Tests ObjectTable default.  
     * Expects 'null'.
     */
    @Test
    public void optionalObjectTableArgTest1() {
        Invocation invocation = new Invocation("OptionalArgTest4");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NULL_RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    /**
     * Tests ObjectTable default.  
     * Expects ''.
     */
    @Test
    public void optionalObjectTableArgTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object input = unmarshaller.unmarshal(new StringReader(PHONE_TABLE_INPUT_XML));
        Invocation invocation = new Invocation("OptionalArgTest4");
        invocation.setParameter("X", input);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(PHONE_TABLE_RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    static String PHONE_TABLE_INPUT_XML =
        REGULAR_XML_HEADER +
        "<dbws_phonetype_tableType xmlns=\"urn:optionalArgs\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        	"<item>" +
	            "<home>(613)123-1234</home>" +
	            "<cell>(902)678-6789</cell>" +
        	"</item>" +
        	"<item>" +
	            "<home>(613)234-4567</home>" +
	            "<cell>(902)987-9876</cell>" +
        	"</item>" +
        "</dbws_phonetype_tableType>";
    static String PHONE_TABLE_RESULT_XML =
        REGULAR_XML_HEADER +
        "<simple-xml-format>" +
		  "<simple-xml>" +
		    "<result>(613)123-1234, (902)678-6789 - (613)234-4567, (902)987-9876</result>" +
		  "</simple-xml>" +
	    "</simple-xml-format>";
}
