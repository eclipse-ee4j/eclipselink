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
 *     David McCann - November 17, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.complexplsqlsp;

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
 * Tests PL/SQL stored procedures with complex PL/SQL and advanced
 * JDBC args.
 *
 */
public class ComplexPLSQLSPTestSuite extends DBWSTestSuite {

    static final String CREATE_VARCHARARRAY_VARRAY =
        "CREATE OR REPLACE TYPE VARCHARARRAY AS VARRAY(10) OF VARCHAR2(20)";
    static final String CREATE_COMPLEXPKG_TAB1_TYPE =
        "CREATE OR REPLACE TYPE COMPLEXPKG_TAB1 AS TABLE OF VARCHAR2(20)";
    static final String CREATE_COMPLEXPKG_PACKAGE =
        "CREATE OR REPLACE PACKAGE COMPLEXPKG AS" +
            "\nTYPE TAB1 IS TABLE OF VARCHAR2(20) INDEX BY BINARY_INTEGER;" +
            "\nPROCEDURE TABLETOVARRAY(OLDTAB IN TAB1, NEWVARRAY OUT VARCHARARRAY);" +
            "\nPROCEDURE TABLESTOVARRAY(OLDTAB IN TAB1, OLDTAB2 IN TAB1, NEWVARRAY OUT VARCHARARRAY);" +
            "\nPROCEDURE VARRAYTOTABLE(OLDVARRAY IN VARCHARARRAY, NEWTAB OUT TAB1);" +
            "\nPROCEDURE VARRAYSTOTABLE(OLDVARRAY IN VARCHARARRAY, OLDVARRAY2 IN VARCHARARRAY, NEWTAB OUT TAB1);" +
        "\nEND COMPLEXPKG;";
    static final String CREATE_COMPLEXPKG_BODY =
        "CREATE OR REPLACE PACKAGE BODY COMPLEXPKG AS" +
            "\nPROCEDURE TABLETOVARRAY(OLDTAB IN TAB1, NEWVARRAY OUT VARCHARARRAY) AS" +
            "\nBEGIN" +
                "\nNEWVARRAY := VARCHARARRAY();" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(1) := OLDTAB(1);" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(2) := OLDTAB(2);" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(3) := OLDTAB(3);" +
            "\nEND TABLETOVARRAY;" +
            "\nPROCEDURE TABLESTOVARRAY(OLDTAB IN TAB1, OLDTAB2 IN TAB1, NEWVARRAY OUT VARCHARARRAY) AS" +
            "\nBEGIN" +
                "\nNEWVARRAY := VARCHARARRAY();" +
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
            "\nEND TABLESTOVARRAY;" +
            "\nPROCEDURE VARRAYTOTABLE(OLDVARRAY IN VARCHARARRAY, NEWTAB OUT TAB1) AS" +
            "\nBEGIN" +
                "\nNEWTAB(1) := OLDVARRAY(1);" +
                "\nNEWTAB(2) := OLDVARRAY(2);" +
                "\nNEWTAB(3) := OLDVARRAY(3);" +
            "\nEND VARRAYTOTABLE;" +
            "\nPROCEDURE VARRAYSTOTABLE(OLDVARRAY IN VARCHARARRAY, OLDVARRAY2 IN VARCHARARRAY, NEWTAB OUT TAB1) AS" +
            "\nBEGIN" +
                "\nNEWTAB(1) := OLDVARRAY(1);" +
                "\nNEWTAB(2) := OLDVARRAY(2);" +
                "\nNEWTAB(3) := OLDVARRAY(3);" +
                "\nNEWTAB(4) := OLDVARRAY2(1);" +
                "\nNEWTAB(5) := OLDVARRAY2(2);" +
                "\nNEWTAB(6) := OLDVARRAY2(3);" +
            "\nEND VARRAYSTOTABLE;" +
        "\nEND COMPLEXPKG;";
    static final String DROP_COMPLEXPKG_PACKAGE =
        "DROP PACKAGE COMPLEXPKG";
    static final String DROP_COMPLEXPKG_PACKAGE_BODY =
        "DROP PACKAGE BODY COMPLEXPKG";
    static final String DROP_COMPLEXPKG_TAB1_TYPE =
        "DROP TYPE COMPLEXPKG_TAB1";
    static final String DROP_VARCHARARRAY_TYPE =
        "DROP TYPE VARCHARARRAY";

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
        	runDdl(conn, CREATE_VARCHARARRAY_VARRAY, ddlDebug);
        	runDdl(conn, CREATE_COMPLEXPKG_PACKAGE, ddlDebug);
        	runDdl(conn, CREATE_COMPLEXPKG_BODY, ddlDebug);
        	runDdl(conn, CREATE_COMPLEXPKG_TAB1_TYPE, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">ComplexPLSQLSP</property>" +
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
                  "catalogPattern=\"COMPLEXPKG\" " +
                  "procedurePattern=\"TABLETOVARRAY\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TablesToVArrayTest\" " +
                  "catalogPattern=\"COMPLEXPKG\" " +
                  "procedurePattern=\"TABLESTOVARRAY\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"VArrayToTableTest\" " +
                  "catalogPattern=\"COMPLEXPKG\" " +
                  "procedurePattern=\"VARRAYTOTABLE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"VArraysToTableTest\" " +
                  "catalogPattern=\"COMPLEXPKG\" " +
                  "procedurePattern=\"VARRAYSTOTABLE\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
        	runDdl(conn, DROP_COMPLEXPKG_PACKAGE_BODY, ddlDebug);
            runDdl(conn, DROP_COMPLEXPKG_PACKAGE, ddlDebug);
            runDdl(conn, DROP_COMPLEXPKG_TAB1_TYPE, ddlDebug);
            runDdl(conn, DROP_VARCHARARRAY_TYPE, ddlDebug);
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
        "<COMPLEXPKG_TAB1 xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foo</item>" +
          "<item>bar</item>" +
          "<item>blah</item>" +
        "</COMPLEXPKG_TAB1>";

    public static final String TABLE2_XML =
        STANDALONE_XML_HEADER +
        "<COMPLEXPKG_TAB1 xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foobar</item>" +
          "<item>barfoo</item>" +
          "<item>blahblah</item>" +
        "</COMPLEXPKG_TAB1>";

    public static final String TABLE3_XML =
        STANDALONE_XML_HEADER +
        "<COMPLEXPKG_TAB1 xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foo</item>" +
          "<item>bar</item>" +
          "<item>blah</item>" +
          "<item>foobar</item>" +
          "<item>barfoo</item>" +
          "<item>blahblah</item>" +
        "</COMPLEXPKG_TAB1>";


    public static final String VARRAY_XML =
        STANDALONE_XML_HEADER +
        "<varchararrayType xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foo</item>" +
          "<item>bar</item>" +
          "<item>blah</item>" +
        "</varchararrayType>";

    public static final String VARRAY2_XML =
        STANDALONE_XML_HEADER +
        "<varchararrayType xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foobar</item>" +
          "<item>barfoo</item>" +
          "<item>blahblah</item>" +
        "</varchararrayType>";

    public static final String VARRAY3_XML =
        STANDALONE_XML_HEADER +
        "<varchararrayType xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foo</item>" +
          "<item>bar</item>" +
          "<item>blah</item>" +
          "<item>foobar</item>" +
          "<item>barfoo</item>" +
          "<item>blahblah</item>" +
        "</varchararrayType>";
}
