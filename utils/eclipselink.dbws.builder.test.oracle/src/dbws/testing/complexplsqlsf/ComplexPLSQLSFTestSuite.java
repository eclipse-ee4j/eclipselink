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
    static final String CREATE_A_PHONE2_TYPE =
        "CREATE OR REPLACE TYPE A_PHONE2_TYPE AS OBJECT (" +
            "\nHOME VARCHAR2(20)," +
            "\nCELL VARCHAR2(20)" +
        "\n)";
    static final String CREATE_COMPLEXPKG2_TAB1_TYPE =
        "CREATE OR REPLACE TYPE COMPLEXPKG2_TAB1 AS TABLE OF VARCHAR2(20)";
    static final String CREATE_COMPLEXPKG2_SIMPLERECORD_TYPE =
        "CREATE OR REPLACE TYPE COMPLEXPKG2_SIMPLERECORD AS OBJECT (" +
            "\nSR1 VARCHAR2(20)," +
            "\nSR2 VARCHAR2(20)" +
        "\n)";
    static final String CREATE_COMPLEXPKG2_PACKAGE =
        "CREATE OR REPLACE PACKAGE COMPLEXPKG2 AS" +
            "\nTYPE TAB1 IS TABLE OF VARCHAR2(20) INDEX BY BINARY_INTEGER;" +
            "\nTYPE SIMPLERECORD IS RECORD (" +
	            "\nSR1 VARCHAR2(20)," +
	            "\nSR2 VARCHAR2(20)" +
	        "\n);" +
            "\nFUNCTION TABLETOVARRAY(OLDTAB IN TAB1) RETURN VARCHAR2ARRAY;" +
            "\nFUNCTION TABLESTOVARRAY(OLDTAB IN TAB1, OLDTAB2 IN TAB1) RETURN VARCHAR2ARRAY;" +
            "\nFUNCTION VARRAYTOTABLE(OLDVARRAY IN VARCHAR2ARRAY) RETURN TAB1;" +
            "\nFUNCTION VARRAYSTOTABLE(OLDVARRAY IN VARCHAR2ARRAY, OLDVARRAY2 IN VARCHAR2ARRAY) RETURN TAB1;" +
            "\nFUNCTION PHONETOTABLE(APHONE IN A_PHONE2_TYPE) RETURN TAB1;" +
            "\nFUNCTION PHONEANDVARRAYTOTABLE(APHONE IN A_PHONE2_TYPE, OLDVARRAY IN VARCHAR2ARRAY) RETURN TAB1;" +
            "\nFUNCTION TABLETOPHONE(OLDTAB IN TAB1) RETURN A_PHONE2_TYPE;" +
            "\nFUNCTION TABLEANDVARRAYTOPHONE(OLDTAB IN TAB1, OLDVARRAY IN VARCHAR2ARRAY) RETURN A_PHONE2_TYPE;" +
            "\nFUNCTION TABLEANDVARRAYTOVARRAY(OLDTAB IN TAB1, OLDVARRAY IN VARCHAR2ARRAY) RETURN VARCHAR2ARRAY;" +
            "\nFUNCTION TABLEANDVARRAYTOTABLE(OLDTAB IN TAB1, OLDVARRAY IN VARCHAR2ARRAY) RETURN TAB1;" +
            "\nFUNCTION RECORDTOVARRAY(OLDREC IN SIMPLERECORD) RETURN VARCHAR2ARRAY;" +
            "\nFUNCTION RECORDTOPHONE(OLDREC IN SIMPLERECORD) RETURN A_PHONE2_TYPE;" +
            "\nFUNCTION VARRAYTORECORD(OLDVARRAY IN VARCHAR2ARRAY) RETURN SIMPLERECORD;" +
            "\nFUNCTION PHONETORECORD(APHONE IN A_PHONE2_TYPE) RETURN SIMPLERECORD;" +
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
            "\nFUNCTION PHONETOTABLE(APHONE IN A_PHONE2_TYPE) RETURN TAB1 AS" +
            "\nNEWTAB TAB1;" + 
            "\nBEGIN" +
                "\nNEWTAB(1) := APHONE.HOME;" +
                "\nNEWTAB(2) := APHONE.CELL;" +
                "\nRETURN NEWTAB;" +
            "\nEND PHONETOTABLE;" +
            "\nFUNCTION PHONEANDVARRAYTOTABLE(APHONE IN A_PHONE2_TYPE, OLDVARRAY IN VARCHAR2ARRAY) RETURN TAB1 AS" +
            "\nNEWTAB TAB1;" + 
            "\nBEGIN" +
                "\nNEWTAB(1) := APHONE.HOME;" +
                "\nNEWTAB(2) := APHONE.CELL;" +
                "\nNEWTAB(3) := OLDVARRAY(1);" +
                "\nNEWTAB(4) := OLDVARRAY(2);" +
                "\nRETURN NEWTAB;" +
            "\nEND PHONEANDVARRAYTOTABLE;" +
            "\nFUNCTION TABLETOPHONE(OLDTAB IN TAB1) RETURN A_PHONE2_TYPE AS" +
            "\nAPHONE A_PHONE2_TYPE;" +
            "\nBEGIN" +
                "\nAPHONE := A_PHONE2_TYPE(OLDTAB(1), OLDTAB(2));" +
                "\nRETURN APHONE;" + 
            "\nEND TABLETOPHONE;" +
            "\nFUNCTION TABLEANDVARRAYTOPHONE(OLDTAB IN TAB1, OLDVARRAY IN VARCHAR2ARRAY) RETURN A_PHONE2_TYPE AS" +
            "\nAPHONE A_PHONE2_TYPE;" +
            "\nBEGIN" +
                "\nAPHONE := A_PHONE2_TYPE(OLDTAB(1), OLDVARRAY(1));" +
                "\nRETURN APHONE;" + 
            "\nEND TABLEANDVARRAYTOPHONE;" +
            "\nFUNCTION TABLEANDVARRAYTOVARRAY(OLDTAB IN TAB1, OLDVARRAY IN VARCHAR2ARRAY) RETURN VARCHAR2ARRAY AS" +
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
                "\nNEWVARRAY(4) := OLDVARRAY(1);" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(5) := OLDVARRAY(2);" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(6) := OLDVARRAY(3);" +
                "\nRETURN NEWVARRAY;" + 
            "\nEND TABLEANDVARRAYTOVARRAY;" +
            "\nFUNCTION TABLEANDVARRAYTOTABLE(OLDTAB IN TAB1, OLDVARRAY IN VARCHAR2ARRAY) RETURN TAB1 AS" +
            "\nNEWTAB TAB1;" + 
            "\nBEGIN" +
                "\nNEWTAB(1) := OLDTAB(1);" +
                "\nNEWTAB(2) := OLDTAB(2);" +
                "\nNEWTAB(3) := OLDTAB(3);" +
                "\nNEWTAB(4) := OLDVARRAY(1);" +
                "\nNEWTAB(5) := OLDVARRAY(2);" +
                "\nNEWTAB(6) := OLDVARRAY(3);" +
                "\nRETURN NEWTAB;" +
            "\nEND TABLEANDVARRAYTOTABLE;" +
            "\nFUNCTION RECORDTOVARRAY(OLDREC IN SIMPLERECORD) RETURN VARCHAR2ARRAY AS" +
            "\nNEWVARRAY VARCHAR2ARRAY;" +
            "\nBEGIN" +
                "\nNEWVARRAY := VARCHAR2ARRAY();" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(1) := OLDREC.SR1;" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(2) := OLDREC.SR2;" +
                "\nRETURN NEWVARRAY;" +
            "\nEND RECORDTOVARRAY;" +
            "\nFUNCTION RECORDTOPHONE(OLDREC IN SIMPLERECORD) RETURN A_PHONE2_TYPE AS" +
            "\nAPHONE A_PHONE2_TYPE;" +
            "\nBEGIN" +
                "\nAPHONE := A_PHONE2_TYPE(OLDREC.SR1, OLDREC.SR2);" +
                "\nRETURN APHONE;" +
            "\nEND RECORDTOPHONE;" +
            "\nFUNCTION VARRAYTORECORD(OLDVARRAY IN VARCHAR2ARRAY) RETURN SIMPLERECORD AS" +
            "\nNEWREC SIMPLERECORD;" +
	        "\nBEGIN" +
                "\nNEWREC.SR1 := OLDVARRAY(1);" +
                "\nNEWREC.SR2 := OLDVARRAY(2);" +
                "\nRETURN NEWREC;" +
	        "\nEND VARRAYTORECORD;" +
            "\nFUNCTION PHONETORECORD(APHONE IN A_PHONE2_TYPE) RETURN SIMPLERECORD AS" +
            "\nNEWREC SIMPLERECORD;" +
            "\nBEGIN" +
                "\nNEWREC.SR1 := APHONE.HOME;" +
                "\nNEWREC.SR2 := APHONE.CELL;" +
                "\nRETURN NEWREC;" +
            "\nEND PHONETORECORD;" +
        "\nEND COMPLEXPKG2;";
    static final String DROP_COMPLEXPKG2_PACKAGE =
        "DROP PACKAGE COMPLEXPKG2";
    static final String DROP_COMPLEXPKG2_PACKAGE_BODY =
        "DROP PACKAGE BODY COMPLEXPKG2";
    static final String DROP_COMPLEXPKG2_TAB1_TYPE =
        "DROP TYPE COMPLEXPKG2_TAB1";
    static final String DROP_COMPLEXPKG2_SIMPLERECORD_TYPE =
        "DROP TYPE COMPLEXPKG2_SIMPLERECORD";
    static final String DROP_VARCHAR2ARRAY_TYPE =
        "DROP TYPE VARCHAR2ARRAY";
    static final String DROP_A_PHONE2_TYPE =        "DROP TYPE A_PHONE2_TYPE";
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
        	runDdl(conn, CREATE_A_PHONE2_TYPE, ddlDebug);        	runDdl(conn, CREATE_COMPLEXPKG2_SIMPLERECORD_TYPE, ddlDebug);
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
              "<plsql-procedure " +
                  "name=\"PhoneToTableTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"PHONETOTABLE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"PhoneAndVArrayToTableTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"PHONEANDVARRAYTOTABLE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TableToPhoneTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"TABLETOPHONE\" " +
                  "returnType=\"a_phone2_typeType\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TableAndVArrayToPhoneTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"TABLEANDVARRAYTOPHONE\" " +
                  "returnType=\"a_phone2_typeType\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TableAndVArrayToVArrayTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"TABLEANDVARRAYTOVARRAY\" " +
                  "returnType=\"varchar2arrayType\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TableAndVArrayToTableTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"TABLEANDVARRAYTOTABLE\" " +
              "/>" +
              "<plsql-procedure " +
	              "name=\"RecordToVArrayTest\" " +
	              "catalogPattern=\"COMPLEXPKG2\" " +
	              "procedurePattern=\"RECORDTOVARRAY\" " +
                  "returnType=\"varchar2arrayType\" " +
	          "/>" +
	          "<plsql-procedure " +
	              "name=\"RecordToPhoneTest\" " +
	              "catalogPattern=\"COMPLEXPKG2\" " +
	              "procedurePattern=\"RECORDTOPHONE\" " +
                  "returnType=\"a_phone2_typeType\" " +
	          "/>" +
              "<plsql-procedure " +
	              "name=\"VArrayToRecordTest\" " +
	              "catalogPattern=\"COMPLEXPKG2\" " +
	              "procedurePattern=\"VARRAYTORECORD\" " +
	          "/>" +
	          "<plsql-procedure " +
	              "name=\"PhoneToRecordTest\" " +
	              "catalogPattern=\"COMPLEXPKG2\" " +
	              "procedurePattern=\"PHONETORECORD\" " +
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
        	runDdl(conn, DROP_COMPLEXPKG2_SIMPLERECORD_TYPE, ddlDebug);
        	runDdl(conn, DROP_COMPLEXPKG2_TAB1_TYPE, ddlDebug);
            runDdl(conn, DROP_A_PHONE2_TYPE, ddlDebug);
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

    @Test
    public void objectTypeToTableTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputPhone1 = unmarshaller.unmarshal(new StringReader(APHONE_XML));
        Invocation invocation = new Invocation("PhoneToTableTest");
        invocation.setParameter("APHONE", inputPhone1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(PHONE_TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void objectTypeAndVArrayToTableTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputPhone = unmarshaller.unmarshal(new StringReader(APHONE_XML));
        Object inputVArray = unmarshaller.unmarshal(new StringReader(PHONE_VARRAY_XML));
        Invocation invocation = new Invocation("PhoneAndVArrayToTableTest");
        invocation.setParameter("APHONE", inputPhone);
        invocation.setParameter("OLDVARRAY", inputVArray);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(PHONE_AND_VARRAY_TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void tableToObjectTypeTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTable1 = unmarshaller.unmarshal(new StringReader(PHONE_TABLE_XML));
        Invocation invocation = new Invocation("TableToPhoneTest");
        invocation.setParameter("OLDTAB", inputTable1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(APHONE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void tableAndVArrayToObjectTypeTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab = unmarshaller.unmarshal(new StringReader(PHONE_TABLE_XML));
        Object inputVArray = unmarshaller.unmarshal(new StringReader(PHONE_VARRAY_XML));
        Invocation invocation = new Invocation("TableAndVArrayToPhoneTest");
        invocation.setParameter("OLDTAB", inputTab);
        invocation.setParameter("OLDVARRAY", inputVArray);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(APHONE2_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void tableAndVArrayToVArrayTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Object inputVArray = unmarshaller.unmarshal(new StringReader(VARRAY2_XML));
        Invocation invocation = new Invocation("TableAndVArrayToVArrayTest");
        invocation.setParameter("OLDTAB", inputTab);
        invocation.setParameter("OLDVARRAY", inputVArray);
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
    public void tableAndVArrayToTableTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Object inputVArray = unmarshaller.unmarshal(new StringReader(VARRAY2_XML));
        Invocation invocation = new Invocation("TableAndVArrayToTableTest");
        invocation.setParameter("OLDTAB", inputTab);
        invocation.setParameter("OLDVARRAY", inputVArray);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TABLE3_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    
    @Test
    public void recordToVArrayTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputRec1 = unmarshaller.unmarshal(new StringReader(SIMPLE_RECORD_XML));
        Invocation invocation = new Invocation("RecordToVArrayTest");
        invocation.setParameter("OLDREC", inputRec1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(PHONE_VARRAY_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void recordToPhoneTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputRec1 = unmarshaller.unmarshal(new StringReader(SIMPLE_RECORD2_XML));
        Invocation invocation = new Invocation("RecordToPhoneTest");
        invocation.setParameter("OLDREC", inputRec1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(APHONE2_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    
    @Test
    public void varrayToRecordTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputVArray = unmarshaller.unmarshal(new StringReader(PHONE_VARRAY_XML));
        Invocation invocation = new Invocation("VArrayToRecordTest");
        invocation.setParameter("OLDVARRAY", inputVArray);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(SIMPLE_RECORD_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void phoneToRecordTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputPhone = unmarshaller.unmarshal(new StringReader(APHONE2_XML));
        Invocation invocation = new Invocation("PhoneToRecordTest");
        invocation.setParameter("APHONE", inputPhone);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(SIMPLE_RECORD2_XML));
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
    public static final String PHONE_TABLE_XML =
        STANDALONE_XML_HEADER +
        "<COMPLEXPKG2_TAB1 xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>(613)111-2222</item>" +
          "<item>(613)222-3333</item>" +
        "</COMPLEXPKG2_TAB1>";
    
    public static final String PHONE_VARRAY_XML =
        STANDALONE_XML_HEADER +
        "<varchar2arrayType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>(613)333-4444</item>" +
          "<item>(613)444-5555</item>" +
        "</varchar2arrayType>";

    public static final String PHONE_AND_VARRAY_TABLE_XML =
        STANDALONE_XML_HEADER +
        "<COMPLEXPKG2_TAB1 xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>(613)111-2222</item>" +
          "<item>(613)222-3333</item>" +
          "<item>(613)333-4444</item>" +
          "<item>(613)444-5555</item>" +
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
    public static final String APHONE_XML =
        STANDALONE_XML_HEADER +
        "<a_phone2_typeType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<home>(613)111-2222</home>" +
          "<cell>(613)222-3333</cell>" +
        "</a_phone2_typeType>";
    
    public static final String APHONE2_XML =
        STANDALONE_XML_HEADER +
        "<a_phone2_typeType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<home>(613)111-2222</home>" +
          "<cell>(613)333-4444</cell>" +
        "</a_phone2_typeType>";
    public static final String SIMPLE_RECORD_XML =
	    STANDALONE_XML_HEADER +
	    "<COMPLEXPKG2_SIMPLERECORD xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	      "<sr1>(613)333-4444</sr1>" +
	      "<sr2>(613)444-5555</sr2>" +
	    "</COMPLEXPKG2_SIMPLERECORD>";
    public static final String SIMPLE_RECORD2_XML =
	    STANDALONE_XML_HEADER +
	    "<COMPLEXPKG2_SIMPLERECORD xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	      "<sr1>(613)111-2222</sr1>" +
	      "<sr2>(613)333-4444</sr2>" +
	    "</COMPLEXPKG2_SIMPLERECORD>";
}
