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
    static final String CREATE_A_PHONE_TYPE_VARRAY =
        "CREATE OR REPLACE TYPE A_PHONE_TYPE_VARRAY AS VARRAY(10) OF A_PHONE_TYPE";
    static final String CREATE_A_CONTACT_TYPE =
        "CREATE OR REPLACE TYPE A_CONTACT_TYPE AS OBJECT (" +
            "\nADDRESS VARCHAR2(40)," +
            "\nPHONE A_PHONE_TYPE" +
        "\n)";
    static final String CREATE_A_CUSTOMER_TYPE =
        "CREATE OR REPLACE TYPE A_CUSTOMER_TYPE AS OBJECT (" +
            "\nNAME VARCHARARRAY," +
            "\nAGE NUMBER(3)," +
            "\nCONTACT A_CONTACT_TYPE" +
        "\n)";
    static final String CREATE_COMPLEXPKG_TAB1_TYPE =
        "CREATE OR REPLACE TYPE COMPLEXPKG_TAB1 AS TABLE OF VARCHAR2(20)";
    static final String CREATE_COMPLEXPKG_SIMPLERECORD_TYPE =
        "CREATE OR REPLACE TYPE COMPLEXPKG_SIMPLERECORD AS OBJECT (" +
            "\nSR1 VARCHAR2(20)," +
            "\nSR2 VARCHAR2(20)" +
        "\n)";
    static final String CREATE_COMPLEXPKG_COMPLEXRECORD_TYPE =
        "CREATE OR REPLACE TYPE COMPLEXPKG_COMPLEXRECORD AS OBJECT (" +
            "\nCR1 NUMBER," +
            "\nCR2 A_CONTACT_TYPE," +
            "\nCR3 VARCHARARRAY," +
            "\nCR4 A_PHONE_TYPE_TABLE" +
        "\n)";
    static final String CREATE_COMPLEXPKG_MORECOMPLEXRECORD_TYPE =
        "CREATE OR REPLACE TYPE COMPLEXPKG_MORECOMPLEXRECORD AS OBJECT (" +
            "\nMCR1 A_PHONE_TYPE_VARRAY" +
        "\n)";
    static final String CREATE_COMPLEXPKG_PACKAGE =
        "CREATE OR REPLACE PACKAGE COMPLEXPKG AS" +
            "\nTYPE TAB1 IS TABLE OF VARCHAR2(20) INDEX BY BINARY_INTEGER;" +
            "\nTYPE SIMPLERECORD IS RECORD (" +
                "\nSR1 VARCHAR2(20)," +
                "\nSR2 VARCHAR2(20)" +
            "\n);" +
            "\nTYPE COMPLEXRECORD IS RECORD (" +
	            "\nCR1 NUMBER," +
	            "\nCR2 A_CONTACT_TYPE," +
	            "\nCR3 VARCHARARRAY," +
	            "\nCR4 A_PHONE_TYPE_TABLE" +
	        "\n);" +
            "\nTYPE MORECOMPLEXRECORD IS RECORD (" +
	            "\nMCR1 A_PHONE_TYPE_VARRAY" +
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
            "\nPROCEDURE CREATE_A_CUSTOMER(ACUSTOMER OUT A_CUSTOMER_TYPE);" +
			"\nPROCEDURE CREATE_COMPLEXRECORD(ACONTACT IN A_CONTACT_TYPE, ANUMBER IN NUMBER, AVARRAY IN VARCHARARRAY, APHONETYPETABLE IN A_PHONE_TYPE_TABLE, ACOMPLEXREC OUT COMPLEXRECORD);" +
            "\nPROCEDURE CREATE_PHONETYPEVARRAY(APHONETYPE1 IN A_PHONE_TYPE, APHONETYPE2 IN A_PHONE_TYPE, PHONETYPEARRAY OUT A_PHONE_TYPE_VARRAY);" +
            "\nPROCEDURE CREATE_MORECOMPLEXRECORD(APHONETYPE1 IN A_PHONE_TYPE, APHONETYPE2 IN A_PHONE_TYPE, MORECOMPLEXREC OUT MORECOMPLEXRECORD);" +
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
			"\nPROCEDURE CREATE_A_CUSTOMER(ACUSTOMER OUT A_CUSTOMER_TYPE) AS" +
            "\nACONTACT A_CONTACT_TYPE;" +
			"\nNEWVARRAY VARCHARARRAY;" +
			"\nBEGIN" +
                "\nNEWVARRAY := VARCHARARRAY();" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(1) := 'John';" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(2) := 'Q';" +
                "\nNEWVARRAY.EXTEND;" +
                "\nNEWVARRAY(3) := 'Oracle';" +
				"\nACONTACT := A_CONTACT_TYPE('1234 Somewhere St, Ottawa, ON', A_PHONE_TYPE('(613)555-1234', '(613)555-4321'));" +
			    "\nACUSTOMER := A_CUSTOMER_TYPE(NEWVARRAY, 41, ACONTACT);" +
			"\nEND CREATE_A_CUSTOMER;" +
			"\nPROCEDURE CREATE_COMPLEXRECORD(ACONTACT IN A_CONTACT_TYPE, ANUMBER IN NUMBER, AVARRAY IN VARCHARARRAY, APHONETYPETABLE IN A_PHONE_TYPE_TABLE, ACOMPLEXREC OUT COMPLEXRECORD) AS" +
			"\nBEGIN" +
			    "\nACOMPLEXREC.CR1 := ANUMBER;" +
			    "\nACOMPLEXREC.CR2 := ACONTACT;" +
			    "\nACOMPLEXREC.CR3 := AVARRAY;" +
			    "\nACOMPLEXREC.CR4 := APHONETYPETABLE;" +
			"\nEND CREATE_COMPLEXRECORD;" +
            "\nPROCEDURE CREATE_PHONETYPEVARRAY(APHONETYPE1 IN A_PHONE_TYPE, APHONETYPE2 IN A_PHONE_TYPE, PHONETYPEARRAY OUT A_PHONE_TYPE_VARRAY) AS" +
			"\nBEGIN" +
		        "\nPHONETYPEARRAY := A_PHONE_TYPE_VARRAY();" +
		        "\nPHONETYPEARRAY.EXTEND;" +
		        "\nPHONETYPEARRAY(1) := APHONETYPE1;" +
		        "\nPHONETYPEARRAY.EXTEND;" +
		        "\nPHONETYPEARRAY(2) := APHONETYPE2;" +
			"\nEND CREATE_PHONETYPEVARRAY;" +
            "\nPROCEDURE CREATE_MORECOMPLEXRECORD(APHONETYPE1 IN A_PHONE_TYPE, APHONETYPE2 IN A_PHONE_TYPE, MORECOMPLEXREC OUT MORECOMPLEXRECORD) AS" +
            "\nPHONETYPEARRAY A_PHONE_TYPE_VARRAY;" +
            "\nBEGIN" +
                "\nCREATE_PHONETYPEVARRAY(APHONETYPE1, APHONETYPE2, PHONETYPEARRAY);" +
                "\nMORECOMPLEXREC.MCR1 := PHONETYPEARRAY;" +
			"\nEND CREATE_MORECOMPLEXRECORD;" +
        "\nEND COMPLEXPKG;";
    static final String DROP_COMPLEXPKG_PACKAGE =
        "DROP PACKAGE COMPLEXPKG";
    static final String DROP_COMPLEXPKG_PACKAGE_BODY =
        "DROP PACKAGE BODY COMPLEXPKG";
    static final String DROP_COMPLEXPKG_TAB1_TYPE =
        "DROP TYPE COMPLEXPKG_TAB1";
    static final String DROP_COMPLEXPKG_SIMPLERECORD_TYPE =
        "DROP TYPE COMPLEXPKG_SIMPLERECORD";
    static final String DROP_COMPLEXPKG_COMPLEXRECORD_TYPE =
        "DROP TYPE COMPLEXPKG_COMPLEXRECORD";
    static final String DROP_VARCHARARRAY_TYPE =
        "DROP TYPE VARCHARARRAY";
    static final String DROP_A_PHONE_TYPE = 
        "DROP TYPE A_PHONE_TYPE";
    static final String DROP_A_CUSTOMER_TYPE = 
        "DROP TYPE A_CUSTOMER_TYPE";
    static final String DROP_A_CONTACT_TYPE = 
        "DROP TYPE A_CONTACT_TYPE";
    static final String DROP_A_PHONE_TYPE_TABLE = 
        "DROP TYPE A_PHONE_TYPE_TABLE";
    static final String DROP_A_PHONE_TYPE_VARRAY = 
        "DROP TYPE A_PHONE_TYPE_VARRAY";
    static final String DROP_COMPLEXPKG_MORECOMPLEXRECORD_TYPE =
        "DROP TYPE COMPLEXPKG_MORECOMPLEXRECORD";

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
        	runDdl(conn, CREATE_A_PHONE_TYPE_TABLE, ddlDebug);
        	runDdl(conn, CREATE_A_PHONE_TYPE_VARRAY, ddlDebug);
        	runDdl(conn, CREATE_A_CONTACT_TYPE, ddlDebug);
        	runDdl(conn, CREATE_A_CUSTOMER_TYPE, ddlDebug);
        	runDdl(conn, CREATE_COMPLEXPKG_SIMPLERECORD_TYPE, ddlDebug);
        	runDdl(conn, CREATE_COMPLEXPKG_COMPLEXRECORD_TYPE, ddlDebug);
        	runDdl(conn, CREATE_COMPLEXPKG_MORECOMPLEXRECORD_TYPE, ddlDebug);
        	runDdl(conn, CREATE_COMPLEXPKG_TAB1_TYPE, ddlDebug);
        	runDdl(conn, CREATE_COMPLEXPKG_PACKAGE, ddlDebug);
        	runDdl(conn, CREATE_COMPLEXPKG_BODY, ddlDebug);
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
              "<plsql-procedure " +
		          "name=\"CreateCustomerTest\" " +
		          "catalogPattern=\"COMPLEXPKG\" " +
		          "procedurePattern=\"CREATE_A_CUSTOMER\" " +
		      "/>" +
              "<plsql-procedure " +
		          "name=\"CreateComplexRecordTest\" " +
		          "catalogPattern=\"COMPLEXPKG\" " +
		          "procedurePattern=\"CREATE_COMPLEXRECORD\" " +
		      "/>" +
              "<plsql-procedure " +
		          "name=\"CreatePhoneTypeVArrayTest\" " +
		          "catalogPattern=\"COMPLEXPKG\" " +
		          "procedurePattern=\"CREATE_PHONETYPEVARRAY\" " +
		      "/>" +
              "<plsql-procedure " +
		          "name=\"CreateMoreComplexRecordTest\" " +
		          "catalogPattern=\"COMPLEXPKG\" " +
		          "procedurePattern=\"CREATE_MORECOMPLEXRECORD\" " +
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
            runDdl(conn, DROP_COMPLEXPKG_SIMPLERECORD_TYPE, ddlDebug);
            runDdl(conn, DROP_COMPLEXPKG_COMPLEXRECORD_TYPE, ddlDebug);
            runDdl(conn, DROP_COMPLEXPKG_MORECOMPLEXRECORD_TYPE, ddlDebug);
            runDdl(conn, DROP_A_CUSTOMER_TYPE, ddlDebug);
            runDdl(conn, DROP_A_CONTACT_TYPE, ddlDebug);
        	runDdl(conn, DROP_A_PHONE_TYPE_VARRAY, ddlDebug);
            runDdl(conn, DROP_A_PHONE_TYPE_TABLE, ddlDebug);
            runDdl(conn, DROP_A_PHONE_TYPE, ddlDebug);
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

    @Test
    public void createContactTest() {
        Invocation invocation = new Invocation("CreateCustomerTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(CUSTOMER_TYPE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void createComplexRecordTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputContact= unmarshaller.unmarshal(new StringReader(CONTACT_TYPE_XML));
        Object inputVArray = unmarshaller.unmarshal(new StringReader(VARRAY_XML));
        Object inputPhoneTable = unmarshaller.unmarshal(new StringReader(PHONE_TYPE_TABLE_XML));
        Invocation invocation = new Invocation("CreateComplexRecordTest");
        invocation.setParameter("ACONTACT", inputContact);
        invocation.setParameter("ANUMBER", 66);
        invocation.setParameter("AVARRAY", inputVArray);
        invocation.setParameter("APHONETYPETABLE", inputPhoneTable);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(COMPLEX_RECORD_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void createPhoneTypeVArrayTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputPhone1 = unmarshaller.unmarshal(new StringReader(APHONE_XML));
        Object inputPhone2 = unmarshaller.unmarshal(new StringReader(APHONE2_XML));
        Invocation invocation = new Invocation("CreatePhoneTypeVArrayTest");
        invocation.setParameter("APHONETYPE1", inputPhone1);
        invocation.setParameter("APHONETYPE2", inputPhone2);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(APHONETYPEARRAY_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void createMoreComplexRecordTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputPhone1 = unmarshaller.unmarshal(new StringReader(APHONE_XML));
        Object inputPhone2 = unmarshaller.unmarshal(new StringReader(APHONE2_XML));
        Invocation invocation = new Invocation("CreateMoreComplexRecordTest");
        invocation.setParameter("APHONETYPE1", inputPhone1);
        invocation.setParameter("APHONETYPE2", inputPhone2);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(MORECOMPLEX_RECORD_XML));
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
    public static final String APHONETYPEARRAY_XML =
        STANDALONE_XML_HEADER +
        "<a_phone_type_varrayType xmlns=\"urn:ComplexPLSQLSP\">" +
          "<item>" +
          	"<home>(613)111-2222</home>" +
          	"<cell>(613)222-3333</cell>" +
          "</item>" +
          "<item>" +
          	"<home>(613)111-2222</home>" +
          	"<cell>(613)333-4444</cell>" +
          "</item>" +
        "</a_phone_type_varrayType>";
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
    public static final String CUSTOMER_TYPE_XML = 
        STANDALONE_XML_HEADER +
        "<a_customer_typeType xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<name>John</name>" +
          "<name>Q</name>" +
          "<name>Oracle</name>" +
          "<age>41</age>" + 
          "<contact>" +
            "<address>1234 Somewhere St, Ottawa, ON</address>" +
            "<phone>" +
              "<home>(613)555-1234</home>" +
              "<cell>(613)555-4321</cell>" +
            "</phone>" +
          "</contact>" +
        "</a_customer_typeType>";
    public static final String CONTACT_TYPE_XML = 
        STANDALONE_XML_HEADER +
        "<a_contact_typeType xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	        "<address>1234 Somewhere St, Ottawa, ON</address>" +
	        "<phone>" +
	          "<home>(613)555-1234</home>" +
	          "<cell>(613)555-4321</cell>" +
	        "</phone>" +
        "</a_contact_typeType>";
    public static final String CONTACT_TYPE2_XML = 
        STANDALONE_XML_HEADER +
        "<a_contact_typeType xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
	        "<address>4321 Anywhere Rd, Ottawa, ON</address>" +
	        "<phone>" +
	          "<home>(401)111-2345</home>" +
	          "<cell>(401)222-5432</cell>" +
	        "</phone>" +
        "</a_contact_typeType>";
    
    public static final String COMPLEX_RECORD_XML = 
        STANDALONE_XML_HEADER +
        "<COMPLEXPKG_COMPLEXRECORD xmlns=\"urn:ComplexPLSQLSP\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<cr1>66</cr1>" +
          "<cr2>" +
            "<address>1234 Somewhere St, Ottawa, ON</address>" +
            "<phone>" +
              "<home>(613)555-1234</home>" +
              "<cell>(613)555-4321</cell>" +
            "</phone>" +
          "</cr2>" +
          "<cr3>" +
            "<item>foo</item>" +
            "<item>bar</item>" +
            "<item>blah</item>" +
          "</cr3>" +
          "<cr4>" +
            "<item>" +
              "<home>(613)333-4444</home>" +
              "<cell>(613)444-5555</cell>" +
            "</item>" +
            "<item>" +
              "<home>(613)111-2222</home>" +
              "<cell>(613)222-3333</cell>" +
            "</item>" +
          "</cr4>" +
        "</COMPLEXPKG_COMPLEXRECORD>";

    public static final String MORECOMPLEX_RECORD_XML = 
        STANDALONE_XML_HEADER +
        "<COMPLEXPKG_MORECOMPLEXRECORD xmlns=\"urn:ComplexPLSQLSP\">" +
            "<mcr1>" +
              "<item>" +
              	"<home>(613)111-2222</home>" +
                "<cell>(613)222-3333</cell>" +
              "</item>" +
              "<item>" +
              	"<home>(613)111-2222</home>" +
              	"<cell>(613)333-4444</cell>" +
              "</item>" +
            "</mcr1>" +
        "</COMPLEXPKG_MORECOMPLEXRECORD>";
}