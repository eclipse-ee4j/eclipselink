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
//     David McCann - December 08, 2011 - 2.4 - Initial implementation
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
            """
                    CREATE OR REPLACE TYPE A_PHONE2_TYPE AS OBJECT (
                    HOME VARCHAR2(20),
                    CELL VARCHAR2(20)
                    )""";
    static final String CREATE_A_PHONE2_TYPE_TABLE =
        "CREATE OR REPLACE TYPE A_PHONE2_TYPE_TABLE AS TABLE OF A_PHONE2_TYPE";
    static final String CREATE_A_PHONE2_TYPE_VARRAY =
        "CREATE OR REPLACE TYPE A_PHONE2_TYPE_VARRAY AS VARRAY(10) OF A_PHONE2_TYPE";
    static final String CREATE_A_CONTACT2_TYPE =
            """
                    CREATE OR REPLACE TYPE A_CONTACT2_TYPE AS OBJECT (
                    ADDRESS VARCHAR2(40),
                    PHONE A_PHONE2_TYPE
                    )""";
    static final String CREATE_A_CUSTOMER2_TYPE =
            """
                    CREATE OR REPLACE TYPE A_CUSTOMER2_TYPE AS OBJECT (
                    NAME VARCHAR2ARRAY,
                    AGE NUMBER(3),
                    CONTACT A_CONTACT2_TYPE
                    )""";
    static final String CREATE_COMPLEXPKG2_TAB1_TYPE =
        "CREATE OR REPLACE TYPE COMPLEXPKG2_TAB1 AS TABLE OF VARCHAR2(20)";
    static final String CREATE_COMPLEXPKG2_SIMPLERECORD_TYPE =
            """
                    CREATE OR REPLACE TYPE COMPLEXPKG2_SIMPLERECORD AS OBJECT (
                    SR1 VARCHAR2(20),
                    SR2 VARCHAR2(20)
                    )""";
    static final String CREATE_COMPLEXPKG2_COMPLEXRECORD_TYPE =
            """
                    CREATE OR REPLACE TYPE COMPLEXPKG2_COMPLEXRECORD AS OBJECT (
                    CR1 NUMBER,
                    CR2 A_CONTACT2_TYPE,
                    CR3 VARCHAR2ARRAY,
                    CR4 A_PHONE2_TYPE_TABLE
                    )""";
    static final String CREATE_COMPLEXPKG2_MORECOMPLEXRECORD_TYPE =
            """
                    CREATE OR REPLACE TYPE COMPLEXPKG2_MORECOMPLEXRECORD AS OBJECT (
                    MCR1 A_PHONE2_TYPE_VARRAY
                    )""";
    static final String CREATE_COMPLEXPKG2_PACKAGE =
            """
                    CREATE OR REPLACE PACKAGE COMPLEXPKG2 AS
                    TYPE TAB1 IS TABLE OF VARCHAR2(20) INDEX BY BINARY_INTEGER;
                    TYPE SIMPLERECORD IS RECORD (
                    SR1 VARCHAR2(20),
                    SR2 VARCHAR2(20)
                    );
                    TYPE COMPLEXRECORD IS RECORD (
                    CR1 NUMBER,
                    CR2 A_CONTACT2_TYPE,
                    CR3 VARCHAR2ARRAY,
                    CR4 A_PHONE2_TYPE_TABLE
                    );
                    TYPE MORECOMPLEXRECORD IS RECORD (
                    MCR1 A_PHONE2_TYPE_VARRAY
                    );
                    FUNCTION TABLETOVARRAY(OLDTAB IN TAB1) RETURN VARCHAR2ARRAY;
                    FUNCTION TABLESTOVARRAY(OLDTAB IN TAB1, OLDTAB2 IN TAB1) RETURN VARCHAR2ARRAY;
                    FUNCTION VARRAYTOTABLE(OLDVARRAY IN VARCHAR2ARRAY) RETURN TAB1;
                    FUNCTION VARRAYSTOTABLE(OLDVARRAY IN VARCHAR2ARRAY, OLDVARRAY2 IN VARCHAR2ARRAY) RETURN TAB1;
                    FUNCTION PHONETOTABLE(APHONE IN A_PHONE2_TYPE) RETURN TAB1;
                    FUNCTION PHONEANDVARRAYTOTABLE(APHONE IN A_PHONE2_TYPE, OLDVARRAY IN VARCHAR2ARRAY) RETURN TAB1;
                    FUNCTION TABLETOPHONE(OLDTAB IN TAB1) RETURN A_PHONE2_TYPE;
                    FUNCTION TABLEANDVARRAYTOPHONE(OLDTAB IN TAB1, OLDVARRAY IN VARCHAR2ARRAY) RETURN A_PHONE2_TYPE;
                    FUNCTION TABLEANDVARRAYTOVARRAY(OLDTAB IN TAB1, OLDVARRAY IN VARCHAR2ARRAY) RETURN VARCHAR2ARRAY;
                    FUNCTION TABLEANDVARRAYTOTABLE(OLDTAB IN TAB1, OLDVARRAY IN VARCHAR2ARRAY) RETURN TAB1;
                    FUNCTION RECORDTOVARRAY(OLDREC IN SIMPLERECORD) RETURN VARCHAR2ARRAY;
                    FUNCTION RECORDTOPHONE(OLDREC IN SIMPLERECORD) RETURN A_PHONE2_TYPE;
                    FUNCTION VARRAYTORECORD(OLDVARRAY IN VARCHAR2ARRAY) RETURN SIMPLERECORD;
                    FUNCTION PHONETORECORD(APHONE IN A_PHONE2_TYPE) RETURN SIMPLERECORD;
                    FUNCTION PLSQLTOPHONETYPETABLE(OLDREC IN SIMPLERECORD, OLDTAB IN TAB1) RETURN A_PHONE2_TYPE_TABLE;
                    FUNCTION PHONETYPETABLETOPLSQL(APHONETYPETABLE IN A_PHONE2_TYPE_TABLE) RETURN SIMPLERECORD;
                    FUNCTION CREATECUSTOMER RETURN A_CUSTOMER2_TYPE;
                    FUNCTION CREATE_COMPLEXRECORD(ACONTACT IN A_CONTACT2_TYPE, ANUMBER IN NUMBER, AVARRAY IN VARCHAR2ARRAY, APHONETYPETABLE IN A_PHONE2_TYPE_TABLE) RETURN COMPLEXRECORD;
                    FUNCTION CREATE_PHONETYPEVARRAY(APHONETYPE1 IN A_PHONE2_TYPE, APHONETYPE2 IN A_PHONE2_TYPE) RETURN A_PHONE2_TYPE_VARRAY;
                    FUNCTION CREATE_MORECOMPLEXRECORD(APHONETYPE1 IN A_PHONE2_TYPE, APHONETYPE2 IN A_PHONE2_TYPE) RETURN MORECOMPLEXRECORD;
                    END COMPLEXPKG2;""";
    static final String CREATE_COMPLEXPKG2_BODY =
            """
                    CREATE OR REPLACE PACKAGE BODY COMPLEXPKG2 AS
                    FUNCTION TABLETOVARRAY(OLDTAB IN TAB1) RETURN VARCHAR2ARRAY AS
                    NEWVARRAY VARCHAR2ARRAY;
                    BEGIN
                    NEWVARRAY := VARCHAR2ARRAY();
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(1) := OLDTAB(1);
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(2) := OLDTAB(2);
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(3) := OLDTAB(3);
                    RETURN NEWVARRAY;
                    END TABLETOVARRAY;
                    FUNCTION TABLESTOVARRAY(OLDTAB IN TAB1, OLDTAB2 IN TAB1) RETURN VARCHAR2ARRAY AS
                    NEWVARRAY VARCHAR2ARRAY;
                    BEGIN
                    NEWVARRAY := VARCHAR2ARRAY();
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(1) := OLDTAB(1);
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(2) := OLDTAB(2);
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(3) := OLDTAB(3);
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(4) := OLDTAB2(1);
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(5) := OLDTAB2(2);
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(6) := OLDTAB2(3);
                    RETURN NEWVARRAY;
                    END TABLESTOVARRAY;
                    FUNCTION VARRAYTOTABLE(OLDVARRAY IN VARCHAR2ARRAY) RETURN TAB1 AS
                    NEWTAB TAB1;
                    BEGIN
                    NEWTAB(1) := OLDVARRAY(1);
                    NEWTAB(2) := OLDVARRAY(2);
                    NEWTAB(3) := OLDVARRAY(3);
                    RETURN NEWTAB;
                    END VARRAYTOTABLE;
                    FUNCTION VARRAYSTOTABLE(OLDVARRAY IN VARCHAR2ARRAY, OLDVARRAY2 IN VARCHAR2ARRAY) RETURN TAB1 AS
                    NEWTAB TAB1;
                    BEGIN
                    NEWTAB(1) := OLDVARRAY(1);
                    NEWTAB(2) := OLDVARRAY(2);
                    NEWTAB(3) := OLDVARRAY(3);
                    NEWTAB(4) := OLDVARRAY2(1);
                    NEWTAB(5) := OLDVARRAY2(2);
                    NEWTAB(6) := OLDVARRAY2(3);
                    RETURN NEWTAB;
                    END VARRAYSTOTABLE;
                    FUNCTION PHONETOTABLE(APHONE IN A_PHONE2_TYPE) RETURN TAB1 AS
                    NEWTAB TAB1;
                    BEGIN
                    NEWTAB(1) := APHONE.HOME;
                    NEWTAB(2) := APHONE.CELL;
                    RETURN NEWTAB;
                    END PHONETOTABLE;
                    FUNCTION PHONEANDVARRAYTOTABLE(APHONE IN A_PHONE2_TYPE, OLDVARRAY IN VARCHAR2ARRAY) RETURN TAB1 AS
                    NEWTAB TAB1;
                    BEGIN
                    NEWTAB(1) := APHONE.HOME;
                    NEWTAB(2) := APHONE.CELL;
                    NEWTAB(3) := OLDVARRAY(1);
                    NEWTAB(4) := OLDVARRAY(2);
                    RETURN NEWTAB;
                    END PHONEANDVARRAYTOTABLE;
                    FUNCTION TABLETOPHONE(OLDTAB IN TAB1) RETURN A_PHONE2_TYPE AS
                    APHONE A_PHONE2_TYPE;
                    BEGIN
                    APHONE := A_PHONE2_TYPE(OLDTAB(1), OLDTAB(2));
                    RETURN APHONE;
                    END TABLETOPHONE;
                    FUNCTION TABLEANDVARRAYTOPHONE(OLDTAB IN TAB1, OLDVARRAY IN VARCHAR2ARRAY) RETURN A_PHONE2_TYPE AS
                    APHONE A_PHONE2_TYPE;
                    BEGIN
                    APHONE := A_PHONE2_TYPE(OLDTAB(1), OLDVARRAY(1));
                    RETURN APHONE;
                    END TABLEANDVARRAYTOPHONE;
                    FUNCTION TABLEANDVARRAYTOVARRAY(OLDTAB IN TAB1, OLDVARRAY IN VARCHAR2ARRAY) RETURN VARCHAR2ARRAY AS
                    NEWVARRAY VARCHAR2ARRAY;
                    BEGIN
                    NEWVARRAY := VARCHAR2ARRAY();
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(1) := OLDTAB(1);
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(2) := OLDTAB(2);
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(3) := OLDTAB(3);
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(4) := OLDVARRAY(1);
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(5) := OLDVARRAY(2);
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(6) := OLDVARRAY(3);
                    RETURN NEWVARRAY;
                    END TABLEANDVARRAYTOVARRAY;
                    FUNCTION TABLEANDVARRAYTOTABLE(OLDTAB IN TAB1, OLDVARRAY IN VARCHAR2ARRAY) RETURN TAB1 AS
                    NEWTAB TAB1;
                    BEGIN
                    NEWTAB(1) := OLDTAB(1);
                    NEWTAB(2) := OLDTAB(2);
                    NEWTAB(3) := OLDTAB(3);
                    NEWTAB(4) := OLDVARRAY(1);
                    NEWTAB(5) := OLDVARRAY(2);
                    NEWTAB(6) := OLDVARRAY(3);
                    RETURN NEWTAB;
                    END TABLEANDVARRAYTOTABLE;
                    FUNCTION RECORDTOVARRAY(OLDREC IN SIMPLERECORD) RETURN VARCHAR2ARRAY AS
                    NEWVARRAY VARCHAR2ARRAY;
                    BEGIN
                    NEWVARRAY := VARCHAR2ARRAY();
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(1) := OLDREC.SR1;
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(2) := OLDREC.SR2;
                    RETURN NEWVARRAY;
                    END RECORDTOVARRAY;
                    FUNCTION RECORDTOPHONE(OLDREC IN SIMPLERECORD) RETURN A_PHONE2_TYPE AS
                    APHONE A_PHONE2_TYPE;
                    BEGIN
                    APHONE := A_PHONE2_TYPE(OLDREC.SR1, OLDREC.SR2);
                    RETURN APHONE;
                    END RECORDTOPHONE;
                    FUNCTION VARRAYTORECORD(OLDVARRAY IN VARCHAR2ARRAY) RETURN SIMPLERECORD AS
                    NEWREC SIMPLERECORD;
                    BEGIN
                    NEWREC.SR1 := OLDVARRAY(1);
                    NEWREC.SR2 := OLDVARRAY(2);
                    RETURN NEWREC;
                    END VARRAYTORECORD;
                    FUNCTION PHONETORECORD(APHONE IN A_PHONE2_TYPE) RETURN SIMPLERECORD AS
                    NEWREC SIMPLERECORD;
                    BEGIN
                    NEWREC.SR1 := APHONE.HOME;
                    NEWREC.SR2 := APHONE.CELL;
                    RETURN NEWREC;
                    END PHONETORECORD;
                    FUNCTION PLSQLTOPHONETYPETABLE(OLDREC IN SIMPLERECORD, OLDTAB IN TAB1) RETURN A_PHONE2_TYPE_TABLE AS
                    APHONE1 A_PHONE2_TYPE;
                    APHONE2 A_PHONE2_TYPE;
                    APHONETYPETABLE A_PHONE2_TYPE_TABLE;
                    BEGIN
                    APHONE1 := RECORDTOPHONE(OLDREC);
                    APHONE2 := TABLETOPHONE(OLDTAB);
                    APHONETYPETABLE := A_PHONE2_TYPE_TABLE();
                    APHONETYPETABLE.EXTEND;
                    APHONETYPETABLE(APHONETYPETABLE.COUNT) := APHONE1;
                    APHONETYPETABLE.EXTEND;
                    APHONETYPETABLE(APHONETYPETABLE.COUNT) := APHONE2;
                    RETURN APHONETYPETABLE;
                    END PLSQLTOPHONETYPETABLE;
                    FUNCTION PHONETYPETABLETOPLSQL(APHONETYPETABLE IN A_PHONE2_TYPE_TABLE) RETURN SIMPLERECORD AS
                    APHONE1 A_PHONE2_TYPE;
                    APHONE2 A_PHONE2_TYPE;
                    NEWREC SIMPLERECORD;
                    BEGIN
                    APHONE1 := APHONETYPETABLE(1);
                    APHONE2 := APHONETYPETABLE(2);
                    NEWREC.SR1 := APHONE2.HOME;
                    NEWREC.SR2 := APHONE1.HOME;
                    RETURN NEWREC;
                    END PHONETYPETABLETOPLSQL;
                    FUNCTION CREATECUSTOMER RETURN A_CUSTOMER2_TYPE AS
                    ACONTACT A_CONTACT2_TYPE;
                    ACUSTOMER A_CUSTOMER2_TYPE;
                    NEWVARRAY VARCHAR2ARRAY;
                    BEGIN
                    NEWVARRAY := VARCHAR2ARRAY();
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(1) := 'John';
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(2) := 'Q';
                    NEWVARRAY.EXTEND;
                    NEWVARRAY(3) := 'Oracle';
                    ACONTACT := A_CONTACT2_TYPE('1234 Somewhere St, Ottawa, ON', A_PHONE2_TYPE('(613)555-1234', '(613)555-4321'));
                    ACUSTOMER := A_CUSTOMER2_TYPE(NEWVARRAY, 41, ACONTACT);
                    RETURN ACUSTOMER;
                    END CREATECUSTOMER;
                    FUNCTION CREATE_COMPLEXRECORD(ACONTACT IN A_CONTACT2_TYPE, ANUMBER IN NUMBER, AVARRAY IN VARCHAR2ARRAY, APHONETYPETABLE IN A_PHONE2_TYPE_TABLE) RETURN COMPLEXRECORD AS
                    ACOMPLEXREC COMPLEXRECORD;
                    BEGIN
                    ACOMPLEXREC.CR1 := ANUMBER;
                    ACOMPLEXREC.CR2 := ACONTACT;
                    ACOMPLEXREC.CR3 := AVARRAY;
                    ACOMPLEXREC.CR4 := APHONETYPETABLE;
                    RETURN ACOMPLEXREC;
                    END CREATE_COMPLEXRECORD;
                    FUNCTION CREATE_PHONETYPEVARRAY(APHONETYPE1 IN A_PHONE2_TYPE, APHONETYPE2 IN A_PHONE2_TYPE) RETURN A_PHONE2_TYPE_VARRAY AS
                    PHONETYPEARRAY A_PHONE2_TYPE_VARRAY;
                    BEGIN
                    PHONETYPEARRAY := A_PHONE2_TYPE_VARRAY();
                    PHONETYPEARRAY.EXTEND;
                    PHONETYPEARRAY(1) := APHONETYPE1;
                    PHONETYPEARRAY.EXTEND;
                    PHONETYPEARRAY(2) := APHONETYPE2;
                    RETURN PHONETYPEARRAY;
                    END CREATE_PHONETYPEVARRAY;
                    FUNCTION CREATE_MORECOMPLEXRECORD(APHONETYPE1 IN A_PHONE2_TYPE, APHONETYPE2 IN A_PHONE2_TYPE) RETURN MORECOMPLEXRECORD AS
                    MORECOMPLEXREC MORECOMPLEXRECORD;
                    BEGIN
                    MORECOMPLEXREC.MCR1 := CREATE_PHONETYPEVARRAY(APHONETYPE1, APHONETYPE2);
                    RETURN MORECOMPLEXREC;
                    END CREATE_MORECOMPLEXRECORD;
                    END COMPLEXPKG2;""";
    static final String DROP_COMPLEXPKG2_PACKAGE =
        "DROP PACKAGE COMPLEXPKG2";
    static final String DROP_COMPLEXPKG2_PACKAGE_BODY =
        "DROP PACKAGE BODY COMPLEXPKG2";
    static final String DROP_COMPLEXPKG2_TAB1_TYPE =
        "DROP TYPE COMPLEXPKG2_TAB1";
    static final String DROP_COMPLEXPKG2_SIMPLERECORD_TYPE =
        "DROP TYPE COMPLEXPKG2_SIMPLERECORD";
    static final String DROP_COMPLEXPKG2_COMPLEXRECORD_TYPE =
        "DROP TYPE COMPLEXPKG2_COMPLEXRECORD";
    static final String DROP_VARCHAR2ARRAY_TYPE =
        "DROP TYPE VARCHAR2ARRAY";
    static final String DROP_A_PHONE2_TYPE =
        "DROP TYPE A_PHONE2_TYPE";
    static final String DROP_A_CUSTOMER2_TYPE =
        "DROP TYPE A_CUSTOMER2_TYPE";
    static final String DROP_A_CONTACT2_TYPE =
        "DROP TYPE A_CONTACT2_TYPE";
    static final String DROP_A_PHONE2_TYPE_TABLE =
        "DROP TYPE A_PHONE2_TYPE_TABLE";
    static final String DROP_A_PHONE2_TYPE_VARRAY =
        "DROP TYPE A_PHONE2_TYPE_VARRAY";
    static final String DROP_COMPLEXPKG2_MORECOMPLEXRECORD_TYPE =
        "DROP TYPE COMPLEXPKG2_MORECOMPLEXRECORD";

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
            runDdl(conn, CREATE_A_PHONE2_TYPE, ddlDebug);
            runDdl(conn, CREATE_A_PHONE2_TYPE_TABLE, ddlDebug);
            runDdl(conn, CREATE_A_PHONE2_TYPE_VARRAY, ddlDebug);
            runDdl(conn, CREATE_A_CONTACT2_TYPE, ddlDebug);
            runDdl(conn, CREATE_A_CUSTOMER2_TYPE, ddlDebug);
            runDdl(conn, CREATE_COMPLEXPKG2_SIMPLERECORD_TYPE, ddlDebug);
            runDdl(conn, CREATE_COMPLEXPKG2_COMPLEXRECORD_TYPE, ddlDebug);
            runDdl(conn, CREATE_COMPLEXPKG2_MORECOMPLEXRECORD_TYPE, ddlDebug);
            runDdl(conn, CREATE_COMPLEXPKG2_TAB1_TYPE, ddlDebug);
            runDdl(conn, CREATE_COMPLEXPKG2_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_COMPLEXPKG2_BODY, ddlDebug);
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
              "/>" +
              "<plsql-procedure " +
                  "name=\"TablesToVArrayTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"TABLESTOVARRAY\" " +
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
              "/>" +
              "<plsql-procedure " +
                  "name=\"TableAndVArrayToPhoneTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"TABLEANDVARRAYTOPHONE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TableAndVArrayToVArrayTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"TABLEANDVARRAYTOVARRAY\" " +
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
              "/>" +
              "<plsql-procedure " +
                  "name=\"RecordToPhoneTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"RECORDTOPHONE\" " +
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
              "<plsql-procedure " +
                  "name=\"PLSQLToPhoneTypeTableTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"PLSQLTOPHONETYPETABLE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"PhoneTypeTableToPLSQLTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"PHONETYPETABLETOPLSQL\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CreateCustomerTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"CREATECUSTOMER\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CreateComplexRecordTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"CREATE_COMPLEXRECORD\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CreatePhoneTypeVArrayTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"CREATE_PHONETYPEVARRAY\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CreateMoreComplexRecordTest\" " +
                  "catalogPattern=\"COMPLEXPKG2\" " +
                  "procedurePattern=\"CREATE_MORECOMPLEXRECORD\" " +
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
            runDdl(conn, DROP_COMPLEXPKG2_COMPLEXRECORD_TYPE, ddlDebug);
            runDdl(conn, DROP_COMPLEXPKG2_MORECOMPLEXRECORD_TYPE, ddlDebug);
            runDdl(conn, DROP_A_CUSTOMER2_TYPE, ddlDebug);
            runDdl(conn, DROP_A_CONTACT2_TYPE, ddlDebug);
            runDdl(conn, DROP_A_PHONE2_TYPE_VARRAY, ddlDebug);
            runDdl(conn, DROP_A_PHONE2_TYPE_TABLE, ddlDebug);
            runDdl(conn, DROP_A_PHONE2_TYPE, ddlDebug);
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
        "<complexpkg2_tab1Type xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foo</item>" +
          "<item>bar</item>" +
          "<item>blah</item>" +
        "</complexpkg2_tab1Type>";

    public static final String TABLE2_XML =
        STANDALONE_XML_HEADER +
        "<complexpkg2_tab1Type xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foobar</item>" +
          "<item>barfoo</item>" +
          "<item>blahblah</item>" +
        "</complexpkg2_tab1Type>";

    public static final String TABLE3_XML =
        STANDALONE_XML_HEADER +
        "<complexpkg2_tab1Type xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>foo</item>" +
          "<item>bar</item>" +
          "<item>blah</item>" +
          "<item>foobar</item>" +
          "<item>barfoo</item>" +
          "<item>blahblah</item>" +
        "</complexpkg2_tab1Type>";
    public static final String PHONE_TABLE_XML =
        STANDALONE_XML_HEADER +
        "<complexpkg2_tab1Type xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>(613)111-2222</item>" +
          "<item>(613)222-3333</item>" +
        "</complexpkg2_tab1Type>";

    public static final String PHONE_VARRAY_XML =
        STANDALONE_XML_HEADER +
        "<varchar2arrayType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>(613)333-4444</item>" +
          "<item>(613)444-5555</item>" +
        "</varchar2arrayType>";

    public static final String PHONE_AND_VARRAY_TABLE_XML =
        STANDALONE_XML_HEADER +
        "<complexpkg2_tab1Type xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>(613)111-2222</item>" +
          "<item>(613)222-3333</item>" +
          "<item>(613)333-4444</item>" +
          "<item>(613)444-5555</item>" +
        "</complexpkg2_tab1Type>";

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
    public static final String APHONETYPEARRAY_XML =
        STANDALONE_XML_HEADER +
        "<a_phone2_type_varrayType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>" +
              "<home>(613)111-2222</home>" +
              "<cell>(613)222-3333</cell>" +
          "</item>" +
          "<item>" +
              "<home>(613)111-2222</home>" +
              "<cell>(613)333-4444</cell>" +
          "</item>" +
        "</a_phone2_type_varrayType>";
    public static final String SIMPLE_RECORD_XML =
        STANDALONE_XML_HEADER +
        "<complexpkg2_simplerecordType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<sr1>(613)333-4444</sr1>" +
          "<sr2>(613)444-5555</sr2>" +
        "</complexpkg2_simplerecordType>";
    public static final String SIMPLE_RECORD2_XML =
        STANDALONE_XML_HEADER +
        "<complexpkg2_simplerecordType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<sr1>(613)111-2222</sr1>" +
          "<sr2>(613)333-4444</sr2>" +
        "</complexpkg2_simplerecordType>";
    public static final String PHONE_TYPE_TABLE_XML =
        STANDALONE_XML_HEADER +
        "<a_phone2_type_tableType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>" +
            "<home>(613)333-4444</home>" +
            "<cell>(613)444-5555</cell>" +
          "</item>" +
          "<item>" +
            "<home>(613)111-2222</home>" +
            "<cell>(613)222-3333</cell>" +
          "</item>" +
        "</a_phone2_type_tableType>";
    public static final String CUSTOMER_TYPE_XML =
        STANDALONE_XML_HEADER +
        "<a_customer2_typeType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
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
        "</a_customer2_typeType>";
    public static final String CONTACT_TYPE_XML =
        STANDALONE_XML_HEADER +
        "<a_contact2_typeType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
            "<address>1234 Somewhere St, Ottawa, ON</address>" +
            "<phone>" +
              "<home>(613)555-1234</home>" +
              "<cell>(613)555-4321</cell>" +
            "</phone>" +
        "</a_contact2_typeType>";
    public static final String COMPLEX_RECORD_XML =
        STANDALONE_XML_HEADER +
        "<complexpkg2_complexrecordType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
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
        "</complexpkg2_complexrecordType>";

    public static final String MORECOMPLEX_RECORD_XML =
        STANDALONE_XML_HEADER +
        "<complexpkg2_morecomplexrecordType xmlns=\"urn:ComplexPLSQLSF\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
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
        "</complexpkg2_morecomplexrecordType>";
}
