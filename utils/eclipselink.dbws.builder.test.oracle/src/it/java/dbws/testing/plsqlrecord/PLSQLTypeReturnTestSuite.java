/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle  - Initial implementation
 */
package dbws.testing.plsqlrecord;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//java eXtension imports
import javax.wsdl.WSDLException;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

//test imports
import dbws.testing.DBWSTestSuite;

public class PLSQLTypeReturnTestSuite extends DBWSTestSuite {
    static final String EMPREC_TYPE = "TYPE EMP_RECORD_PACKAGE_EMPREC";

    static final String CREATE_EMPTYPE_TABLE = "CREATE TABLE EMPTYPEX1 (" + "\nEMPNO NUMERIC(4) NOT NULL,"
            + "\nENAME VARCHAR(25)," + "\nPRIMARY KEY (EMPNO)" + "\n)";
    static final String[] POPULATE_EMPTYPE_TABLE = new String[] {
            "INSERT INTO EMPTYPEX1 (EMPNO, ENAME) VALUES (69, 'Holly')",
            "INSERT INTO EMPTYPEX1 (EMPNO, ENAME) VALUES (70, 'Brooke')",
            "INSERT INTO EMPTYPEX1 (EMPNO, ENAME) VALUES (71, 'Patty')" };
    static final String DROP_EMPTYPE_TABLE = "DROP TABLE EMPTYPEX1";

    static final String CREATE_EMP_RECORD_PACKAGE = "CREATE PACKAGE EMP_RECORD_PACKAGE IS\r\n" + 
    		"FUNCTION get_emp_record(l_empno in number) return EMPTYPEX1.ENAME%TYPE;\r\n" + 
    		"END EMP_RECORD_PACKAGE;";
    static final String DROP_EMP_RECORD_PACKAGE = "DROP PACKAGE EMP_RECORD_PACKAGE";

    static final String DROP_EMP_RECORD_PACKAGE_BODY = "DROP PACKAGE BODY EMP_RECORD_PACKAGE";

    static final String CREATE_EMP_RECORD_PACKAGE_BODY = "create or replace PACKAGE BODY EMP_RECORD_PACKAGE IS\r\n" + 
            "function get_emp_record(l_empno in number) return EMPTYPEX1.ENAME%TYPE\r\n" + 
            "is\r\n" + 
            "ename_result EMPTYPEX1.ENAME%TYPE;\r\n" + 
            "BEGIN\r\n" + 
            "SELECT ENAME into ename_result\r\n" + 
            "FROM EMPTYPEX1\r\n" + 
            "WHERE \r\n" + 
            "EMPNO = l_empno;\r\n" + 
            "return ename_result;\r\n" + 
            "END;\r\n" + 
            "END EMP_RECORD_PACKAGE;\r\n";

    static boolean ddlCreate = true;
    static boolean ddlDrop = true;
    static boolean ddlDebug = true;

    @BeforeClass
    public static void setUp() throws WSDLException {
        if (conn == null) {
            try {
                conn = buildConnection();
            } catch (Exception e) {
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
            runDdl(conn, CREATE_EMP_RECORD_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_EMP_RECORD_PACKAGE_BODY, ddlDebug);
            runDdl(conn, CREATE_EMPTYPE_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_EMPTYPE_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_EMPTYPE_TABLE[i]);
                }
                stmt.executeBatch();
            } catch (SQLException e) {
                if (ddlDebug) {
                    e.printStackTrace();
                }
            }
        }
        DBWS_BUILDER_XML_USERNAME =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                  "<properties>" +
                      "<property name=\"projectName\">PLSQLRecord</property>" +
                      "<property name=\"logLevel\">on</property>" +
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
                      "name=\"TestRecWithPercentTypeField\" " +
                      "catalogPattern=\"EMP_RECORD_PACKAGE\" " +
                      "procedurePattern=\"get_emp_record\" " +
                  "/>" +
                "</dbws-builder>";

        builder = null;
        DBWSTestSuite.setUp(".");

        // execute shadow type ddl to generate JDBC equivalents of PL/SQL types
        ArrayList<String> ddls = new ArrayList<String>();
        for (String ddl : builder.getTypeDDL()) {
            ddls.add(ddl);
        }
        // execute the DDLs in order to avoid dependency issues
        executeDDLForString(ddls, EMPREC_TYPE);
    }

    /**
     * Execute the DDL in the provided list containing the given DDL string.
     * 
     */
    protected static void executeDDLForString(List<String> ddls, String ddlString) {
        for (int i = 0; i < ddls.size(); i++) {
            String ddl = ddls.get(i);
            if (ddl.contains(ddlString)) {
                runDdl(conn, ddl, ddlDebug);
                break;
            }
        }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
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
                } catch (Exception xxx) {
                }
                runDdl(conn, ddl, ddlDebug);
            }
        }
    }

    @Test
    public void testRecWithPercentTypeField() {
        Invocation invocation = new Invocation("TestRecWithPercentTypeField");
        invocation.setParameter("l_empno", 69);
        Operation op = xrService.getOperation(invocation.getName());
        System.out.println("XRSERVICE  " + xrService);
        System.out.println("invocation  " + invocation.getName());
        System.out.println("op  " + invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(EMPREC_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc),
                comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String EMPREC_XML = "<emp_record_package_emprecType xmlns=\"urn:PLSQLRecord\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
            + "<emp_id>69</emp_id>" + "<emp_name>Holly</emp_name>" + "</emp_record_package_emprecType>";

}
