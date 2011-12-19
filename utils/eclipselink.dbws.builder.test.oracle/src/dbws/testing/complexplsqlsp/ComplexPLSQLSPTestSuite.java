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
    static final String CREATE_A_PHONE_TYPE =
        "CREATE OR REPLACE TYPE A_PHONE_TYPE AS OBJECT (" +
            "\nHOME VARCHAR2(20)," +
            "\nCELL VARCHAR2(20)" +
        "\n)";
    static final String CREATE_A_PHONE_TYPE_TABLE =
        "CREATE OR REPLACE TYPE A_PHONE_TYPE_TABLE AS TABLE OF A_PHONE_TYPE";
    static final String CREATE_COMPLEXPKG_TAB1_TYPE =
        "CREATE OR REPLACE TYPE COMPLEXPKG_TAB1 AS TABLE OF VARCHAR2(20)";
    static final String CREATE_COMPLEXPKG_SIMPLERECORD_TYPE =
        "CREATE OR REPLACE TYPE COMPLEXPKG_SIMPLERECORD AS OBJECT (" +
            "\nSR1 VARCHAR2(20)," +
            "\nSR2 VARCHAR2(20)" +
        "\n)";
    static final String CREATE_COMPLEXPKG_PACKAGE =
        "CREATE OR REPLACE PACKAGE COMPLEXPKG AS" +
            "\nTYPE TAB1 IS TABLE OF VARCHAR2(20) INDEX BY BINARY_INTEGER;" +
            "\nTYPE SIMPLERECORD IS RECORD (" +
                "\nSR1 VARCHAR2(20)," +
                "\nSR2 VARCHAR2(20)" +
            "\n);" +
            "\nPROCEDURE TABLETOVARRAY(OLDTAB IN TAB1, NEWVARRAY OUT VARCHARARRAY);" +
            "\nPROCEDURE TABLESTOVARRAY(OLDTAB IN TAB1, OLDTAB2 IN TAB1, NEWVARRAY OUT VARCHARARRAY);" +
            "\nPROCEDURE VARRAYTOTABLE(OLDVARRAY IN VARCHARARRAY, NEWTAB OUT TAB1);" +
            "\nPROCEDURE VARRAYSTOTABLE(OLDVARRAY IN VARCHARARRAY, OLDVARRAY2 IN VARCHARARRAY, NEWTAB OUT TAB1);" +
            "\nPROCEDURE PHONETOTABLE(APHONE IN A_PHONE_TYPE, NEWTAB OUT TAB1);" +
            "\nPROCEDURE PHONEANDVARRAYTOTABLE(APHONE IN A_PHONE_TYPE, OLDVARRAY IN VARCHARARRAY, NEWTAB OUT TAB1);" +
            "\nPROCEDURE TABLETOPHONE(OLDTAB IN TAB1, APHONE OUT A_PHONE_TYPE);" +
            "\nPROCEDURE TABLEANDVARRAYTOPHONE(OLDTAB IN TAB1, OLDVARRAY IN VARCHARARRAY, APHONE OUT A_PHONE_TYPE);" +
            "\nPROCEDURE TABLEANDVARRAYTOVARRAY(OLDTAB IN TAB1, OLDVARRAY IN VARCHARARRAY, NEWVARRAY OUT VARCHARARRAY);" +
            "\nPROCEDURE TABLEANDVARRAYTOTABLE(OLDTAB IN TAB1, OLDVARRAY IN VARCHARARRAY, NEWTAB OUT TAB1);" +
            "\nPROCEDURE RECORDTOVARRAY(OLDREC IN SIMPLERECORD, NEWVARRAY OUT VARCHARARRAY);" +
            "\nPROCEDURE RECORDTOPHONE(OLDREC IN SIMPLERECORD, APHONE OUT A_PHONE_TYPE);" +
            "\nPROCEDURE VARRAYTORECORD(OLDVARRAY IN VARCHARARRAY, NEWREC OUT SIMPLERECORD);" +
            "\nPROCEDURE PHONETORECORD(APHONE IN A_PHONE_TYPE, NEWREC OUT SIMPLERECORD);" +
            "\nPROCEDURE PLSQLTOPHONETYPETABLE(OLDREC IN SIMPLERECORD, OLDTAB IN TAB1, APHONETYPETABLE OUT A_PHONE_TYPE_TABLE);" +
            "\nPROCEDURE PHONETYPETABLETOPLSQL(APHONETYPETABLE IN A_PHONE_TYPE_TABLE, NEWREC OUT SIMPLERECORD);" +
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
            "\nPROCEDURE PHONETOTABLE(APHONE IN A_PHONE_TYPE, NEWTAB OUT TAB1) AS" +
            "\nBEGIN" +
                "\nNEWTAB(1) := APHONE.HOME;" +
                "\nNEWTAB(2) := APHONE.CELL;" +
            "\nEND PHONETOTABLE;" +
            "\nPROCEDURE PHONEANDVARRAYTOTABLE(APHONE IN A_PHONE_TYPE, OLDVARRAY IN VARCHARARRAY, NEWTAB OUT TAB1) AS" +
            "\nBEGIN" +
                "\nNEWTAB(1) := APHONE.HOME;" +
                "\nNEWTAB(2) := APHONE.CELL;" +
                "\nNEWTAB(3) := OLDVARRAY(1);" +
                "\nNEWTAB(4) := OLDVARRAY(2);" +
            "\nEND PHONEANDVARRAYTOTABLE;" +
            "\nPROCEDURE TABLETOPHONE(OLDTAB IN TAB1, APHONE OUT A_PHONE_TYPE) AS" +
            "\nBEGIN" +
                "\nAPHONE := A_PHONE_TYPE(OLDTAB(1), OLDTAB(2));" +
            "\nEND TABLETOPHONE;" +
            "\nPROCEDURE TABLEANDVARRAYTOPHONE(OLDTAB IN TAB1, OLDVARRAY IN VARCHARARRAY, APHONE OUT A_PHONE_TYPE) AS" +
            "\nBEGIN" +
                "\nAPHONE := A_PHONE_TYPE(OLDTAB(1), OLDVARRAY(1));" +
            "\nEND TABLEANDVARRAYTOPHONE;" +
            "\nPROCEDURE TABLEANDVARRAYTOVARRAY(OLDTAB IN TAB1, OLDVARRAY IN VARCHARARRAY, NEWVARRAY OUT VARCHARARRAY) AS" +
            "\nBEGIN" +
                "\nNEWVARRAY := VARCHARARRAY();" +
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
            "\nEND TABLEANDVARRAYTOVARRAY;" +
            "\nPROCEDURE TABLEANDVARRAYTOTABLE(OLDTAB IN TAB1, OLDVARRAY IN VARCHARARRAY, NEWTAB OUT TAB1) AS" +
            "\nBEGIN" +
                "\nNEWTAB(1) := OLDTAB(1);" +
                "\nNEWTAB(2) := OLDTAB(2);" +
                "\nNEWTAB(3) := OLDTAB(3);" +
                "\nNEWTAB(4) := OLDVARRAY(1);" +
                "\nNEWTAB(5) := OLDVARRAY(2);" +
                "\nNEWTAB(6) := OLDVARRAY(3);" +
            "\nEND TABLEANDVARRAYTOTABLE;" +
            "\nPROCEDURE RECORDTOVARRAY(OLDREC IN SIMPLERECORD, NEWVARRAY OUT VARCHARARRAY) AS" +
            "\nBEGIN" +
                "\nNEWVARRAY := VARCHARARRAY();" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(1) := OLDREC.SR1;" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(2) := OLDREC.SR2;" +
            "\nEND RECORDTOVARRAY;" +
            "\nPROCEDURE RECORDTOPHONE(OLDREC IN SIMPLERECORD, APHONE OUT A_PHONE_TYPE) AS" +
            "\nBEGIN" +
                "\nAPHONE := A_PHONE_TYPE(OLDREC.SR1, OLDREC.SR2);" +
            "\nEND RECORDTOPHONE;" +
            "\nPROCEDURE VARRAYTORECORD(OLDVARRAY IN VARCHARARRAY, NEWREC OUT SIMPLERECORD) AS" +
	        "\nBEGIN" +
                "\nNEWREC.SR1 := OLDVARRAY(1);" +
                "\nNEWREC.SR2 := OLDVARRAY(2);" +
	        "\nEND VARRAYTORECORD;" +
            "\nPROCEDURE PHONETORECORD(APHONE IN A_PHONE_TYPE, NEWREC OUT SIMPLERECORD) AS" +
            "\nBEGIN" +
                "\nNEWREC.SR1 := APHONE.HOME;" +
                "\nNEWREC.SR2 := APHONE.CELL;" +
            "\nEND PHONETORECORD;" +
            "\nPROCEDURE PLSQLTOPHONETYPETABLE(OLDREC IN SIMPLERECORD, OLDTAB IN TAB1, APHONETYPETABLE OUT A_PHONE_TYPE_TABLE) AS" +
            "\nAPHONE1 A_PHONE_TYPE;" +
            "\nAPHONE2 A_PHONE_TYPE;" +
            "\nBEGIN" +
                "\nRECORDTOPHONE(OLDREC, APHONE1);" +
                "\nTABLETOPHONE(OLDTAB, APHONE2);" +
                "\nAPHONETYPETABLE := A_PHONE_TYPE_TABLE();" +
                "\nAPHONETYPETABLE.EXTEND;" +
                "\nAPHONETYPETABLE(APHONETYPETABLE.COUNT) := APHONE1;" +
                "\nAPHONETYPETABLE.EXTEND;" +
                "\nAPHONETYPETABLE(APHONETYPETABLE.COUNT) := APHONE2;" +
            "\nEND PLSQLTOPHONETYPETABLE;" +
            "\nPROCEDURE PHONETYPETABLETOPLSQL(APHONETYPETABLE IN A_PHONE_TYPE_TABLE, NEWREC OUT SIMPLERECORD) AS" +
            "\nAPHONE1 A_PHONE_TYPE;" +
            "\nAPHONE2 A_PHONE_TYPE;" +
            "\nBEGIN" +
                "\nAPHONE1 := APHONETYPETABLE(1);" +
                "\nAPHONE2 := APHONETYPETABLE(2);" +
                "\nNEWREC.SR1 := APHONE2.HOME;" +
                "\nNEWREC.SR2 := APHONE1.HOME;" +
            "\nEND PHONETYPETABLETOPLSQL;" +
        "\nEND COMPLEXPKG;";
    static final String DROP_COMPLEXPKG_PACKAGE =
        "DROP PACKAGE COMPLEXPKG";
    static final String DROP_COMPLEXPKG_PACKAGE_BODY =
        "DROP PACKAGE BODY COMPLEXPKG";
    static final String DROP_COMPLEXPKG_TAB1_TYPE =
        "DROP TYPE COMPLEXPKG_TAB1";
    static final String DROP_COMPLEXPKG_SIMPLERECORD_TYPE =
        "DROP TYPE COMPLEXPKG_SIMPLERECORD";
    static final String DROP_VARCHARARRAY_TYPE =
        "DROP TYPE VARCHARARRAY";
    static final String DROP_A_PHONE_TYPE = 
        "DROP TYPE A_PHONE_TYPE";    static final String DROP_A_PHONE_TYPE_TABLE = 
        "DROP TYPE A_PHONE_TYPE_TABLE";

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
        	runDdl(conn, CREATE_A_PHONE_TYPE, ddlDebug);
        	runDdl(conn, CREATE_A_PHONE_TYPE_TABLE, ddlDebug);        	runDdl(conn, CREATE_COMPLEXPKG_SIMPLERECORD_TYPE, ddlDebug);
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
              "<plsql-procedure " +
                  "name=\"PhoneToTableTest\" " +
                  "catalogPattern=\"COMPLEXPKG\" " +
                  "procedurePattern=\"PHONETOTABLE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"PhoneAndVArrayToTableTest\" " +
                  "catalogPattern=\"COMPLEXPKG\" " +
                  "procedurePattern=\"PHONEANDVARRAYTOTABLE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TableToPhoneTest\" " +
                  "catalogPattern=\"COMPLEXPKG\" " +
                  "procedurePattern=\"TABLETOPHONE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TableAndVArrayToPhoneTest\" " +
                  "catalogPattern=\"COMPLEXPKG\" " +
                  "procedurePattern=\"TABLEANDVARRAYTOPHONE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TableAndVArrayToVArrayTest\" " +
                  "catalogPattern=\"COMPLEXPKG\" " +
                  "procedurePattern=\"TABLEANDVARRAYTOVARRAY\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TableAndVArrayToTableTest\" " +
                  "catalogPattern=\"COMPLEXPKG\" " +
                  "procedurePattern=\"TABLEANDVARRAYTOTABLE\" " +
              "/>" +
              "<plsql-procedure " +
	              "name=\"RecordToVArrayTest\" " +
	              "catalogPattern=\"COMPLEXPKG\" " +
	              "procedurePattern=\"RECORDTOVARRAY\" " +
	          "/>" +
              "<plsql-procedure " +
	              "name=\"RecordToPhoneTest\" " +
	              "catalogPattern=\"COMPLEXPKG\" " +
	              "procedurePattern=\"RECORDTOPHONE\" " +
	          "/>" +
              "<plsql-procedure " +
	              "name=\"VArrayToRecordTest\" " +
	              "catalogPattern=\"COMPLEXPKG\" " +
	              "procedurePattern=\"VARRAYTORECORD\" " +
	          "/>" +
              "<plsql-procedure " +
	              "name=\"PhoneToRecordTest\" " +
	              "catalogPattern=\"COMPLEXPKG\" " +
	              "procedurePattern=\"PHONETORECORD\" " +
	          "/>" +
              "<plsql-procedure " +
                  "name=\"PLSQLToPhoneTypeTableTest\" " +
                  "catalogPattern=\"COMPLEXPKG\" " +
                  "procedurePattern=\"PLSQLTOPHONETYPETABLE\" " +
              "/>" +
              "<plsql-procedure " +
	              "name=\"PhoneTypeTableToPLSQLTest\" " +
	              "catalogPattern=\"COMPLEXPKG\" " +
	              "procedurePattern=\"PHONETYPETABLETOPLSQL\" " +
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
            runDdl(conn, DROP_COMPLEXPKG_SIMPLERECORD_TYPE, ddlDebug);
            runDdl(conn, DROP_A_PHONE_TYPE_TABLE, ddlDebug);
            runDdl(conn, DROP_A_PHONE_TYPE, ddlDebug);
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

    @Test
    public void plsqlToPhoneTypeTableTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputRec = unmarshaller.unmarshal(new StringReader(SIMPLE_RECORD_XML));
        Object inputTab = unmarshaller.unmarshal(new StringReader(PHONE_TABLE_XML));
        Invocation invocation = new Invocation("PLSQLToPhoneTypeTableTest");
        invocation.setParameter("OLDREC", inputRec);
        invocation.setParameter("OLDTAB", inputTab);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(PHONE_TYPE_TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void phoneTypeTableToPLSQLTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputPTypeTable = unmarshaller.unmarshal(new StringReader(PHONE_TYPE_TABLE_XML));
        Invocation invocation = new Invocation("PhoneTypeTableToPLSQLTest");
        invocation.setParameter("APHONETYPETABLE", inputPTypeTable);
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
    public static final String PHONE_TABLE_XML =
        STANDALONE_XML_HEADER +
        "<COMPLEXPKG_TAB1 xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>(613)111-2222</item>" +
          "<item>(613)222-3333</item>" +
        "</COMPLEXPKG_TAB1>";
    public static final String PHONE_VARRAY_XML =
        STANDALONE_XML_HEADER +
        "<varchararrayType xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>(613)333-4444</item>" +
          "<item>(613)444-5555</item>" +
        "</varchararrayType>";
    public static final String PHONE_AND_VARRAY_TABLE_XML =
        STANDALONE_XML_HEADER +
        "<COMPLEXPKG_TAB1 xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>(613)111-2222</item>" +
          "<item>(613)222-3333</item>" +
          "<item>(613)333-4444</item>" +
          "<item>(613)444-5555</item>" +
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
    public static final String APHONE_XML =
        STANDALONE_XML_HEADER +
        "<a_phone_typeType xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<home>(613)111-2222</home>" +
          "<cell>(613)222-3333</cell>" +
        "</a_phone_typeType>";
    public static final String APHONE2_XML =
        STANDALONE_XML_HEADER +
        "<a_phone_typeType xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<home>(613)111-2222</home>" +
          "<cell>(613)333-4444</cell>" +
        "</a_phone_typeType>";
    public static final String SIMPLE_RECORD_XML =
	    STANDALONE_XML_HEADER +
	    "<COMPLEXPKG_SIMPLERECORD xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	      "<sr1>(613)333-4444</sr1>" +
	      "<sr2>(613)444-5555</sr2>" +
	    "</COMPLEXPKG_SIMPLERECORD>";
    public static final String SIMPLE_RECORD2_XML =
	    STANDALONE_XML_HEADER +
	    "<COMPLEXPKG_SIMPLERECORD xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	      "<sr1>(613)111-2222</sr1>" +
	      "<sr2>(613)333-4444</sr2>" +
	    "</COMPLEXPKG_SIMPLERECORD>";
    public static final String PHONE_TYPE_TABLE_XML = 
    	STANDALONE_XML_HEADER +
    	"<a_phone_type_tableType xmlns=\"urn:ComplexPLSQLSP\">" +
    	  "<item>" +
    	    "<home>(613)333-4444</home>" +
    	    "<cell>(613)444-5555</cell>" +
    	  "</item>" +
    	  "<item>" +
    	    "<home>(613)111-2222</home>" +
    	    "<cell>(613)222-3333</cell>" +
    	  "</item>" +
    	"</a_phone_type_tableType>";
}
