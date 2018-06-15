/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     David McCann - December 17, 2013 - 2.6 - Initial implementation
package dbws.testing.plsqlrecord2;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//javase imports
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
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

/**
 * Tests PL/SQL procedures with simple and complex arguments.
 *
 */
public class PLSQLRecord2TestSuite extends DBWSTestSuite {

    static final String SCHEMA = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);

    static final String CREATE_EMPREC_TYPE =
        "create or replace TYPE pkgrec_wrapper2_empRecType AS OBJECT ("+
          "\np_empno NUMBER (4), p_ename VARCHAR2 (20)"+
        "\n)";

    static final String DROP_EMPREC_TYPE = "DROP TYPE pkgrec_wrapper2_empRecType";

    static final String CREATE_EMP_TABLE =
        "CREATE TABLE EMP ( EMPNO  NUMBER(4), ENAME  VARCHAR2(20 BYTE) )";

    static final String[] POPULATE_EMP_TABLE = new String[] {
        "INSERT INTO EMP (EMPNO, ENAME) VALUES (69, 'Holly')",
        "INSERT INTO EMP (EMPNO, ENAME) VALUES (70, 'Emily')",
        "INSERT INTO EMP (EMPNO, ENAME) VALUES (71, 'Patty')"
    };
    static final String DROP_EMP_TABLE =
        "DROP TABLE EMP";

    static final String CREATE_PACKAGE =
        "CREATE OR REPLACE PACKAGE " + SCHEMA + ".pkgRec_wrapper2 IS" +
           "\nTYPE empRecType IS RECORD (p_empno NUMBER (4), p_ename VARCHAR2 (20));" +
           "\nPROCEDURE sp_get_empRecord" +
           "\n(" +
               "\np_empno            IN  NUMBER," +
               "\no_return_status    OUT VARCHAR2," +
               "\no_return_message   OUT VARCHAR2," +
               "\np_emprec           OUT empRecType" +
           "\n);" +
        "\nEND pkgRec_wrapper2;";

    static final String DROP_PACKAGE =
        "DROP PACKAGE " + SCHEMA + ".pkgRec_wrapper2";

    static final String CREATE_PACKAGE_BODY =
        "CREATE OR REPLACE PACKAGE BODY " + SCHEMA + ".pkgRec_wrapper2 IS" +
           "\nPROCEDURE sp_get_empRecord (" +
               "\np_empno            IN     NUMBER," +
               "\no_return_status    OUT VARCHAR2," +
               "\no_return_message   OUT VARCHAR2," +
               "\np_emprec           OUT empRecType)" +
           "\nAS" +
              "\nl_return_status   VARCHAR2 (10);" +
           "\nBEGIN" +
               "\no_return_status := 'S';" +
               "\no_return_message := 'No Errors';" +

                "\nSELECT empno, ename" +
                "\nINTO p_emprec" +
                "\nFROM emp" +
                "\nWHERE empno = p_empno;" +
            "\nEND;" +
        "\n END pkgRec_wrapper2;";
    static final String DROP_PACKAGE_BODY =
        "DROP PACKAGE BODY " + SCHEMA + ".pkgRec_wrapper2";

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
            runDdl(conn, CREATE_EMP_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_EMP_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_EMP_TABLE[i]);
                }
                stmt.executeBatch();
            } catch (SQLException e) {
                if (ddlDebug) {
                    e.printStackTrace();
                }
            }
            runDdl(conn, CREATE_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE_BODY, ddlDebug);
            runDdl(conn, CREATE_EMPREC_TYPE, ddlDebug);
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
                  "name=\"GetEmpRecord\" " +
                  "schemaPattern=\"" + SCHEMA + "\" " +
                  "catalogPattern=\"pkgRec_wrapper2\" " +
                  "procedurePattern=\"sp_get_empRecord\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
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
            runDdl(conn, DROP_PACKAGE_BODY, ddlDebug);
            runDdl(conn, DROP_PACKAGE, ddlDebug);
            runDdl(conn, DROP_EMP_TABLE, ddlDebug);
            runDdl(conn, DROP_EMPREC_TYPE, ddlDebug);
        }
    }

    @Test
    public void getEmpRecordTest() {


        Invocation invocation = new Invocation("GetEmpRecord");
        invocation.setParameter("p_empno", 69);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.setFormattedOutput(true);
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RECORD_XML));
        removeEmptyTextNodes(doc);
        removeEmptyTextNodes(controlDoc);
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String RECORD_XML =
        STANDALONE_XML_HEADER +
        "\n<simple-xml-format>" +
        "\n<simple-xml>"+
        "\n<o_return_status>S</o_return_status>" +
        "\n<o_return_message>No Errors</o_return_message>" +
        "\n<pkgrec_wrapper2_emprectypeType xmlns=\"urn:PLSQLRecord\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
        "\n<p_empno>69</p_empno>" +
        "\n<p_ename>Holly</p_ename>" +
        "\n</pkgrec_wrapper2_emprectypeType>" +
        "\n</simple-xml>" +
        "\n</simple-xml-format>";
}
