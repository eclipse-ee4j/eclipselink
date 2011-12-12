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
 *     David McCann - December 08, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.complexplsqlsf;

//javase imports
import java.io.StringReader;
import java.sql.SQLException;
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
 * Tests PL/SQL stored functions with complex PL/SQL and advanced
 * JDBC args.
 *
 */
public class ComplexPLSQLSFTestSuite extends DBWSTestSuite {

    static final String CREATE_VARCHAR2ARRAY_VARRAY =
        "CREATE OR REPLACE TYPE VARCHAR2ARRAY AS VARRAY(10) OF VARCHAR2(20)";
    static final String CREATE_COMPLEXPKG2_TAB1_TYPE =
        "CREATE OR REPLACE TYPE COMPLEXPKG2_TAB1 AS TABLE OF VARCHAR2(20)";
    static final String CREATE_COMPLEXPKG2_PACKAGE =
        "CREATE OR REPLACE PACKAGE COMPLEXPKG2 AS" +
            "\nTYPE TAB1 IS TABLE OF VARCHAR2(20) INDEX BY BINARY_INTEGER;" +
            "\nFUNCTION TABLETOVARRAY(OLDTAB IN TAB1) RETURN VARCHAR2ARRAY;" +
            "\nFUNCTION TABLESTOVARRAY(OLDTAB IN TAB1, OLDTAB2 IN TAB1) RETURN VARCHAR2ARRAY;" +
            "\nFUNCTION VARRAYTOTABLE(OLDVARRAY IN VARCHAR2ARRAY) RETURN TAB1;" +
            "\nFUNCTION VARRAYSTOTABLE(OLDVARRAY IN VARCHAR2ARRAY, OLDVARRAY2 IN VARCHAR2ARRAY) RETURN TAB1;" +
        "\nEND COMPLEXPKG2;";
    static final String CREATE_COMPLEXPKG2_BODY =
        "CREATE OR REPLACE PACKAGE BODY COMPLEXPKG2 AS" +
            "\nFUNCTION TABLETOVARRAY(OLDTAB IN TAB1) RETURN VARCHAR2ARRAY AS" +
            "\nNEWVARRAY VARCHAR2ARRAY;" +
            "\nBEGIN" +
                "\nNEWVARRAY := VARCHAR2ARRAY();" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(1) := OLDTAB(1);" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(2) := OLDTAB(2);" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(3) := OLDTAB(3);" +
                "\nRETURN NEWVARRAY;" + 
            "\nEND TABLETOVARRAY;" +            
            "\nFUNCTION TABLESTOVARRAY(OLDTAB IN TAB1, OLDTAB2 IN TAB1) RETURN VARCHAR2ARRAY AS" +
            "\nNEWVARRAY VARCHAR2ARRAY;" +
            "\nBEGIN" +
                "\nNEWVARRAY := VARCHAR2ARRAY();" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(1) := OLDTAB(1);" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(2) := OLDTAB(2);" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(3) := OLDTAB(3);" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(4) := OLDTAB2(1);" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(5) := OLDTAB2(2);" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(6) := OLDTAB2(3);" +
                "\nRETURN NEWVARRAY;" + 
            "\nEND TABLESTOVARRAY;" +            
            "\nFUNCTION VARRAYTOTABLE(OLDVARRAY IN VARCHAR2ARRAY) RETURN TAB1 AS" +
            "\nNEWTAB TAB1;" + 
            "\nBEGIN" +
                "\nNEWTAB(1) := OLDVARRAY(1);" +
                "\nNEWTAB(2) := OLDVARRAY(2);" +
                "\nNEWTAB(3) := OLDVARRAY(3);" +
                "\nRETURN NEWTAB;" + 
            "\nEND VARRAYTOTABLE;" +
            "\nFUNCTION VARRAYSTOTABLE(OLDVARRAY IN VARCHAR2ARRAY, OLDVARRAY2 IN VARCHAR2ARRAY) RETURN TAB1 AS" +
            "\nNEWTAB TAB1;" + 
            "\nBEGIN" +
                "\nNEWTAB(1) := OLDVARRAY(1);" +
                "\nNEWTAB(2) := OLDVARRAY(2);" +
                "\nNEWTAB(3) := OLDVARRAY(3);" +
                "\nNEWTAB(4) := OLDVARRAY2(1);" +
                "\nNEWTAB(5) := OLDVARRAY2(2);" +
                "\nNEWTAB(6) := OLDVARRAY2(3);" +
                "\nRETURN NEWTAB;" + 
            "\nEND VARRAYSTOTABLE;" +
        "\nEND COMPLEXPKG2;";
    static final String DROP_COMPLEXPKG2_PACKAGE =
        "DROP PACKAGE COMPLEXPKG2";
    static final String DROP_COMPLEXPKG2_PACKAGE_BODY =
        "DROP PACKAGE BODY COMPLEXPKG2";
    static final String DROP_COMPLEXPKG2_TAB1_TYPE =
        "DROP TYPE COMPLEXPKG2_TAB1";
    static final String DROP_VARCHAR2ARRAY_TYPE =
        "DROP TYPE VARCHAR2ARRAY";

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
           	runDdl(conn, CREATE_VARCHAR2ARRAY_VARRAY, ddlDebug);
        	runDdl(conn, CREATE_COMPLEXPKG2_PACKAGE, ddlDebug);
        	runDdl(conn, CREATE_COMPLEXPKG2_BODY, ddlDebug);
        	runDdl(conn, CREATE_COMPLEXPKG2_TAB1_TYPE, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">ComplexPLSQLSF</property>" +
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
                  "name=\"TableToVArrayTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"TABLETOVARRAY\" " +
                  "returnType=\"varchar2arrayType\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TablesToVArrayTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"TABLESTOVARRAY\" " +
                  "returnType=\"varchar2arrayType\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"VArrayToTableTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"VARRAYTOTABLE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"VArraysToTableTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"VARRAYSTOTABLE\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
        	runDdl(conn, DROP_COMPLEXPKG2_PACKAGE_BODY, ddlDebug);
        	runDdl(conn, DROP_COMPLEXPKG2_PACKAGE, ddlDebug);
        	runDdl(conn, DROP_COMPLEXPKG2_TAB1_TYPE, ddlDebug);
        	runDdl(conn, DROP_VARCHAR2ARRAY_TYPE, ddlDebug);
        }
    }

    @Test
    public void tableToVArrayTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab1 = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Invocation invocation = new Invocation("TableToVArrayTest");
        invocation.setParameter("OLDTAB", inputTab1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(VARRAY_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void tablesToVArrayTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab1 = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Object inputTab2 = unmarshaller.unmarshal(new StringReader(TABLE2_XML));
        Invocation invocation = new Invocation("TablesToVArrayTest");
        invocation.setParameter("OLDTAB", inputTab1);
        invocation.setParameter("OLDTAB2", inputTab2);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(VARRAY3_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void vArrayToTableTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputArray1 = unmarshaller.unmarshal(new StringReader(VARRAY_XML));
        Invocation invocation = new Invocation("VArrayToTableTest");
        invocation.setParameter("OLDVARRAY", inputArray1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    
    @Test
    public void vArraysToTableTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputArray1 = unmarshaller.unmarshal(new StringReader(VARRAY_XML));
        Object inputArray2 = unmarshaller.unmarshal(new StringReader(VARRAY2_XML));
        Invocation invocation = new Invocation("VArraysToTableTest");
        invocation.setParameter("OLDVARRAY", inputArray1);
        invocation.setParameter("OLDVARRAY2", inputArray2);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TABLE3_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    
    public static final String TABLE_XML =
        STANDALONE_XML_HEADER +
        "<COMPLEXPKG2_TAB1 xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foo</item>" +
          "<item>bar</item>" +
          "<item>blah</item>" +
        "</COMPLEXPKG2_TAB1>";

    public static final String TABLE2_XML =
        STANDALONE_XML_HEADER +
        "<COMPLEXPKG2_TAB1 xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foobar</item>" +
          "<item>barfoo</item>" +
          "<item>blahblah</item>" +
        "</COMPLEXPKG2_TAB1>";

    public static final String TABLE3_XML =
        STANDALONE_XML_HEADER +
        "<COMPLEXPKG2_TAB1 xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foo</item>" +
          "<item>bar</item>" +
          "<item>blah</item>" +
          "<item>foobar</item>" +
          "<item>barfoo</item>" +
          "<item>blahblah</item>" +
        "</COMPLEXPKG2_TAB1>";
    

    public static final String VARRAY_XML =
        STANDALONE_XML_HEADER +
        "<varchar2arrayType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foo</item>" +
          "<item>bar</item>" +
          "<item>blah</item>" +
        "</varchar2arrayType>";

    public static final String VARRAY2_XML =
        STANDALONE_XML_HEADER +
        "<varchar2arrayType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foobar</item>" +
          "<item>barfoo</item>" +
          "<item>blahblah</item>" +
        "</varchar2arrayType>";
    
    public static final String VARRAY3_XML =
        STANDALONE_XML_HEADER +
        "<varchar2arrayType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foo</item>" +
          "<item>bar</item>" +
          "<item>blah</item>" +
          "<item>foobar</item>" +
          "<item>barfoo</item>" +
          "<item>blahblah</item>" +
        "</varchar2arrayType>";
}
