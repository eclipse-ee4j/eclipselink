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
//     David McCann - September 08, 2011 - 2.4 - Initial implementation
package dbws.testing.plsqlrecord;

//javase imports
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
 * Tests PL/SQL procedures with simple and complex arguments.
 *
 */
public class PLSQLRecordTestSuite extends DBWSTestSuite {
    static final String MTAB1_TYPE = "TYPE PACKAGE1_MTAB1";
    static final String NRECORD_TYPE = "TYPE PACKAGE1_NRECORD";
    static final String MRECORD_TYPE = "TYPE PACKAGE1_MRECORD";
    static final String EMPREC_TYPE = "TYPE EMP_RECORD_PACKAGE_EMPREC";

    static final String CREATE_EMPTYPE_TABLE =
            """
                    CREATE TABLE EMPTYPEX (
                    EMPNO NUMERIC(4) NOT NULL,
                    ENAME VARCHAR(25),
                    PRIMARY KEY (EMPNO)
                    )""";
    static final String[] POPULATE_EMPTYPE_TABLE = new String[] {
        "INSERT INTO EMPTYPEX (EMPNO, ENAME) VALUES (69, 'Holly')",
        "INSERT INTO EMPTYPEX (EMPNO, ENAME) VALUES (70, 'Brooke')",
        "INSERT INTO EMPTYPEX (EMPNO, ENAME) VALUES (71, 'Patty')"
    };
    static final String DROP_EMPTYPE_TABLE =
        "DROP TABLE EMPTYPEX";

    static final String CREATE_EMP_RECORD_PACKAGE =
            """
                    create or replace PACKAGE EMP_RECORD_PACKAGE AS
                    type EmpRec is record (emp_id   EMPTYPEX.EMPNO%TYPE,
                    emp_name EMPTYPEX.ENAME%TYPE
                    );
                    function get_emp_record (pId in number) return EmpRec;
                    END EMP_RECORD_PACKAGE;""";
    static final String DROP_EMP_RECORD_PACKAGE =
        "DROP PACKAGE EMP_RECORD_PACKAGE";

    static final String CREATE_EMP_RECORD_PACKAGE_BODY =
            """
                    create or replace PACKAGE BODY EMP_RECORD_PACKAGE AS
                    function get_emp_record (pId in number) return EmpRec AS
                    myEmp EmpRec;
                    l_empno EMPTYPEX.EMPNO%TYPE;
                    l_ename EMPTYPEX.ENAME%TYPE;
                    cursor c_emp is select empno, ename from EMPTYPEX where empno = pId;
                    BEGIN
                    open c_emp;
                    fetch c_emp into l_empno, l_ename;
                    close c_emp;
                    myEmp.emp_id := l_empno;
                    myEmp.emp_name := l_ename;
                    return myEmp;
                    END get_emp_record;
                    END EMP_RECORD_PACKAGE;""";
    static final String DROP_EMP_RECORD_PACKAGE_BODY =
        "DROP PACKAGE BODY EMP_RECORD_PACKAGE";

    static final String CREATE_PACKAGE1_PACKAGE =
            """
                    CREATE OR REPLACE PACKAGE PACKAGE1 AS
                    TYPE MTAB1 IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;
                    TYPE NRECORD IS RECORD (
                    N1 VARCHAR2(10),
                    N2 DECIMAL(7,2)
                    );
                    TYPE MRECORD IS RECORD (
                    M1 MTAB1
                    );
                    PROCEDURE GETNEWREC(NEWREC OUT NRECORD);
                    PROCEDURE COPYREC(ORIGINALREC IN NRECORD, NEWREC OUT NRECORD, SUFFIX IN VARCHAR2);
                    PROCEDURE GETRECWITHTABLE(ORIGINALTAB IN MTAB1, NEWREC OUT MRECORD);
                    FUNCTION COPYREC2(ORIGINALREC IN NRECORD, SUFFIX IN VARCHAR2) RETURN NRECORD;
                    FUNCTION GETRECWITHTABLE2(ORIGINALTAB IN MTAB1) RETURN MRECORD;
                    END PACKAGE1;""";
    static final String CREATE_PACKAGE1_BODY =
            """
                    CREATE OR REPLACE PACKAGE BODY PACKAGE1 AS
                    PROCEDURE GETNEWREC(NEWREC OUT NRECORD) AS
                    BEGIN
                    NEWREC.N1 := 'new record';
                    NEWREC.N2 := 100.11;
                    END GETNEWREC;
                    PROCEDURE COPYREC(ORIGINALREC IN NRECORD, NEWREC OUT NRECORD, SUFFIX IN VARCHAR2) AS
                    BEGIN
                    NEWREC.N1 := CONCAT(ORIGINALREC.N1, SUFFIX);
                    NEWREC.N2 := ORIGINALREC.N2 + 0.1;
                    END COPYREC;
                    PROCEDURE GETRECWITHTABLE(ORIGINALTAB IN MTAB1, NEWREC OUT MRECORD) AS
                    BEGIN
                    NEWREC.M1 := ORIGINALTAB;
                    END GETRECWITHTABLE;
                    FUNCTION COPYREC2(ORIGINALREC IN NRECORD, SUFFIX IN VARCHAR2) RETURN NRECORD IS
                    newrec NRECORD;
                    BEGIN
                    newrec.N1 := CONCAT(ORIGINALREC.N1, SUFFIX);
                    newrec.N2 := ORIGINALREC.N2 + 0.1;
                    RETURN newrec;
                    END COPYREC2;
                    FUNCTION GETRECWITHTABLE2(ORIGINALTAB IN MTAB1) RETURN MRECORD IS
                    NEWREC MRECORD;
                    BEGIN
                    NEWREC.M1 := ORIGINALTAB;
                    RETURN NEWREC;
                    END GETRECWITHTABLE2;
                    END PACKAGE1;""";

    static final String DROP_PACKAGE1_PACKAGE =
        "DROP PACKAGE PACKAGE1";
    static final String DROP_PACKAGE1_PACKAGE_BODY =
        "DROP PACKAGE BODY PACKAGE1";

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
            runDdl(conn, CREATE_PACKAGE1_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE1_BODY, ddlDebug);
            runDdl(conn, CREATE_EMPTYPE_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (String s : POPULATE_EMPTYPE_TABLE) {
                    stmt.addBatch(s);
                }
                stmt.executeBatch();
            } catch (SQLException e) {
                if (ddlDebug) {
                    e.printStackTrace();
                }
            }
            runDdl(conn, CREATE_EMP_RECORD_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_EMP_RECORD_PACKAGE_BODY, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">PLSQLRecord</property>" +
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
                  "name=\"GetNewRecordTest\" " +
                  "catalogPattern=\"PACKAGE1\" " +
                  "procedurePattern=\"GETNEWREC\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyRecordTest\" " +
                  "catalogPattern=\"PACKAGE1\" " +
                  "procedurePattern=\"COPYREC\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyRecordTest2\" " +
                  "catalogPattern=\"PACKAGE1\" " +
                  "procedurePattern=\"COPYREC2\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"GetRecordWithTableTest\" " +
                  "catalogPattern=\"PACKAGE1\" " +
                  "procedurePattern=\"GETRECWITHTABLE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"GetRecordWithTableTest2\" " +
                  "catalogPattern=\"PACKAGE1\" " +
                  "procedurePattern=\"GETRECWITHTABLE2\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"TestRecWithPercentTypeField\" " +
                  "catalogPattern=\"EMP_RECORD_PACKAGE\" " +
                  "procedurePattern=\"get_emp_record\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");

          // execute shadow type ddl to generate JDBC equivalents of PL/SQL types
          ArrayList<String> ddls = new ArrayList<>();
          for (String ddl : builder.getTypeDDL()) {
              ddls.add(ddl);
          }
          // execute the DDLs in order to avoid dependency issues
          for (int j = 0; j < 4; j++) {
              switch (j) {
                case 0:
                    executeDDLForString(ddls, MTAB1_TYPE);
                    break;
                case 1:
                    executeDDLForString(ddls, NRECORD_TYPE);
                    break;
                case 2:
                    executeDDLForString(ddls, MRECORD_TYPE);
                    break;
                default:
                    executeDDLForString(ddls, EMPREC_TYPE);
                    break;
              }
          }
    }

    /**
     * Execute the DDL in the provided list containing the given DDL string.
     *
     */
    protected static void executeDDLForString(List<String> ddls, String ddlString) {
        for (String ddl : ddls) {
            if (ddl.contains(ddlString)) {
                runDdl(conn, ddl, ddlDebug);
                break;
            }
        }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_PACKAGE1_PACKAGE_BODY, ddlDebug);
            runDdl(conn, DROP_PACKAGE1_PACKAGE, ddlDebug);
            runDdl(conn, DROP_EMP_RECORD_PACKAGE_BODY, ddlDebug);
            runDdl(conn, DROP_EMP_RECORD_PACKAGE, ddlDebug);
            runDdl(conn, DROP_EMPTYPE_TABLE, ddlDebug);

            // drop shadow type ddl
            for (String ddl : builder.getTypeDropDDL()) {
                // may need to strip off trailing ';'
                try {
                    int lastIdx = ddl.lastIndexOf(";");
                    if (lastIdx == (ddl.length() - 1)) {
                        ddl = ddl.substring(0, ddl.length() - 1);
                    }
                } catch (Exception xxx) {}
                runDdl(conn, ddl, ddlDebug);
            }
        }
    }

    @Test
    public void getNewRecordTest() {
        Invocation invocation = new Invocation("GetNewRecordTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RECORD_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String RECORD_XML =
        STANDALONE_XML_HEADER +
        "<package1_nrecordType xmlns=\"urn:PLSQLRecord\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<n1>new record</n1>" +
          "<n2>100.11</n2>" +
        "</package1_nrecordType>";

    /**
     * StoredProcedure test.
     * Copies n1 to new record.n1 appending '.copy'.
     * Copies n2 to new record.n2 adding 0.1 to the amount.
     */
    @Test
    public void copyRecordTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputRec = unmarshaller.unmarshal(new StringReader(INPUTRECORD_XML));
        Invocation invocation = new Invocation("CopyRecordTest");
        invocation.setParameter("ORIGINALREC", inputRec);
        invocation.setParameter("SUFFIX", ".copy");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTRECORD_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String INPUTRECORD_XML =
        STANDALONE_XML_HEADER +
        "<package1_nrecordType xmlns=\"urn:PLSQLRecord\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<n1>data</n1>" +
          "<n2>100.00</n2>" +
        "</package1_nrecordType>";
    public static final String OUTPUTRECORD_XML =
        STANDALONE_XML_HEADER +
        "<package1_nrecordType xmlns=\"urn:PLSQLRecord\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<n1>data.copy</n1>" +
          "<n2>100.1</n2>" +
        "</package1_nrecordType>";

    /**
     * StoredFunction test.
     * Copies n1 to new record.n1 appending '.copy'.
     * Copies n2 to new record.n2 adding 0.1 to the amount.
     */
    @Test
    public void copyRecordTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputRec = unmarshaller.unmarshal(new StringReader(INPUTRECORD_XML));
        Invocation invocation = new Invocation("CopyRecordTest2");
        invocation.setParameter("ORIGINALREC", inputRec);
        invocation.setParameter("SUFFIX", ".copy");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTRECORD_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    /**
     * StoredProcedure test.
     */
    @Test
    public void getRecordWithTableTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTable = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Invocation invocation = new Invocation("GetRecordWithTableTest");
        invocation.setParameter("ORIGINALTAB", inputTable);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTRECORDWITHTABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String TABLE_XML =
        STANDALONE_XML_HEADER +
        "<package1_mtab1Type xmlns=\"urn:PLSQLRecord\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>666</item>" +
        "</package1_mtab1Type>";
    public static final String OUTPUTRECORDWITHTABLE_XML =
        STANDALONE_XML_HEADER +
        "<package1_mrecordType xmlns=\"urn:PLSQLRecord\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<m1>" +
              "<item>666</item>" +
          "</m1>" +
        "</package1_mrecordType>";

    /**
     * StoredFunction test.
     */
    @Test
    public void getRecordWithTableTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTable = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Invocation invocation = new Invocation("GetRecordWithTableTest2");
        invocation.setParameter("ORIGINALTAB", inputTable);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTRECORDWITHTABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void testRecordWithPercentTypeField() {
        Invocation invocation = new Invocation("TestRecWithPercentTypeField");
        invocation.setParameter("pId", 69);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(EMPREC_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String EMPREC_XML =
      "<emp_record_package_emprecType xmlns=\"urn:PLSQLRecord\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<emp_id>69</emp_id>" +
          "<emp_name>Holly</emp_name>" +
      "</emp_record_package_emprecType>";

}
