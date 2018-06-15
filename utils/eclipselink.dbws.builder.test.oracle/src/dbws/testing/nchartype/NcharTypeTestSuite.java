/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package dbws.testing.nchartype;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;

import javax.wsdl.WSDLException;

import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import dbws.testing.DBWSTestSuite;

public class NcharTypeTestSuite extends DBWSTestSuite {

    static final String CREATE_PKG =
        "CREATE OR REPLACE PACKAGE NCHARTYPE AS" +
            "\nPROCEDURE FindByJob(J IN NVARCHAR2, R OUT EMPTYPE);" +
        "\nEND NCHARTYPE;";
    static final String CREATE_PKG_BODY =
        "CREATE OR REPLACE PACKAGE BODY NCHARTYPE AS" +
            "\nPROCEDURE FindByJob(J IN NVARCHAR2, R OUT EMPTYPE) AS" +
            "\nBEGIN" +
                "\nSELECT VALUE(E) INTO R FROM EMPTYPE_TABLE E WHERE JOB LIKE J;" +
            "\nEND;" +
        "\nEND NCHARTYPE;";
    static final String CREATE_TYPE =
        "CREATE OR REPLACE TYPE EMPTYPE AS OBJECT (" +
            "\nEMPNO decimal(4,0)," +
            "\nENAME nvarchar2(10)," +
            "\nJOB nchar(9)," +
            "\nMGR decimal(4,0)," +
            "\nSAL decimal(7,2)," +
            "\nCOMM decimal(7,2)," +
            "\nDEPTNO decimal(2)" +
        "\n);";
    static final String CREATE_TYPE_TABLE =
            "CREATE TABLE EMPTYPE_TABLE OF EMPTYPE";
    static String[] POPULATE_SP_TABLE = new String[] {
        "INSERT INTO EMPTYPE_TABLE VALUES (7369,'SMITH','CLERK',7902,800,NULL,20)",
        "INSERT INTO EMPTYPE_TABLE VALUES (7499,'ALLEN','SALESMAN',7698,1600,300,30)",
        "INSERT INTO EMPTYPE_TABLE VALUES (7521,'WARD','SALESMAN',7698,1250,500,30)"
    };
    static final String DROP_TYPE =
        "DROP TYPE EMPTYPE";
    static final String DROP_TYPE_TABLE =
        "DROP TABLE EMPTYPE_TABLE";
    static final String DROP_PKG_BODY =
        "DROP PACKAGE BODY NCHARTYPE";
    static final String DROP_PKG =
        "DROP PACKAGE NCHARTYPE";

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
            runDdl(conn, CREATE_TYPE, ddlDebug);
            runDdl(conn, CREATE_TYPE_TABLE, ddlDebug);
            runDdl(conn, CREATE_PKG, ddlDebug);
            runDdl(conn, CREATE_PKG_BODY, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (String batch : POPULATE_SP_TABLE) {
                    stmt.addBatch(batch);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                if (ddlDebug) {
                    e.printStackTrace();
                }
            }
        }
        DBWS_BUILDER_XML_USERNAME =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
          "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "<properties>" +
                "<property name=\"projectName\">SP</property>" +
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
                "name=\"FindByJobTest\" " +
                "catalogPattern=\"NCHARTYPE\" " +
                "procedurePattern=\"FindByJob\" " +
                "isCollection=\"true\" " +
                "isSimpleXMLFormat=\"true\" " +
                "simpleXMLFormatTag=\"simplesp-rows\" " +
                "xmlTag=\"simplesp-row\" " +
            "/>" +
          "</dbws-builder>";
        builder = null;
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            runDdl(conn, DROP_PKG_BODY, ddlDebug);
            runDdl(conn, DROP_PKG, ddlDebug);
            runDdl(conn, DROP_TYPE_TABLE, ddlDebug);
            runDdl(conn, DROP_TYPE, ddlDebug);
        }
    }

    @Test
    public void findByJobTest() {
        Invocation invocation = new Invocation("FindByJobTest");
        invocation.setParameter("J", "CL%");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ALL_SIMPLESP_CLERK_ROWS_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String ALL_SIMPLESP_CLERK_ROWS_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<simplesp-rows xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"simple-xml-format\">" +
            "<simplesp-row>" +
                "<emptypeType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:SP\">" +
                    "<empno>7369</empno>" +
                    "<ename>SMITH</ename>" +
                    "<job>CLERK    </job>" +
                    "<mgr>7902</mgr>" +
                    "<sal>800</sal>" +
                    "<comm xsi:nil=\"true\"/>" +
                    "<deptno>20</deptno>" +
                "</emptypeType>" +
            "</simplesp-row>" +
        "</simplesp-rows>";
}
