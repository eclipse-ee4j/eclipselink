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
//     David McCann - June 25, 2012 - 2.4.1 - Initial implementation
package dbws.testing.blobtype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//java eXtension imports
import javax.activation.DataHandler;
import javax.wsdl.WSDLException;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests PL/SQL procedures and functions returning BLOB data.
 *
 */
public class BlobTypeTestSuite extends DBWSTestSuite {

    // BLOB table
    static final String CREATE_BLOBDATA_TABLE =
        "CREATE TABLE BLOBDATA (" +
            "\nID NUMERIC(4) NOT NULL," +
            "\nNAME VARCHAR(80)," +
            "\nB BLOB," +
            "\nPRIMARY KEY (ID)" +
        "\n)";

    static final String INSERT_INTO_BLOBDATA = "INSERT INTO BLOBDATA VALUES(?, ?, ?)";

    static final String DROP_BLOBDATA_TABLE =
        "DROP TABLE BLOBDATA";

    // Top-level procedure/function
    static final String CREATE_PROCEDURE =
        "\nCREATE OR REPLACE PROCEDURE PLEASEGETBLOB(ARG1 IN NUMBER, ARG2 OUT BLOB) AS" +
        "\nBEGIN" +
          "\nSELECT B INTO ARG2 FROM BLOBDATA WHERE ID = ARG1;" +
        "\nEND PLEASEGETBLOB;";
    static final String DROP_PROCEDURE =
        "DROP PROCEDURE PLEASEGETBLOB";
    static final String CREATE_FUNCTION =
        "\nCREATE OR REPLACE FUNCTION PLEASERETURNBLOB(ARG1 IN NUMBER) RETURN BLOB AS" +
          "\nreturnBlob BLOB;" +
        "\nBEGIN" +
          "\nSELECT B INTO returnBlob FROM BLOBDATA WHERE ID = ARG1;" +
          "\nRETURN (returnBlob);" +
        "\nEND PLEASERETURNBLOB;";
    static final String DROP_FUNCTION =
        "DROP FUNCTION PLEASERETURNBLOB";

    // Package
    static final String CREATE_BLOBDATA_PACKAGE =
        "CREATE OR REPLACE PACKAGE BLOBDATA_PKG AS" +
            "\nPROCEDURE GETBLOB(ARG1 IN NUMBER, ARG2 OUT BLOB);" +
            "\nFUNCTION RETURNBLOB(ARG1 IN NUMBER) RETURN BLOB;" +
        "\nEND BLOBDATA_PKG;";
    static final String CREATE_BLOBDATA_BODY =
        "CREATE OR REPLACE PACKAGE BODY BLOBDATA_PKG AS" +
            "\nPROCEDURE GETBLOB(ARG1 IN NUMBER, ARG2 OUT BLOB) AS" +
            "\nBEGIN" +
              "\nSELECT B INTO ARG2 FROM BLOBDATA WHERE ID = ARG1;" +
            "\nEND GETBLOB;" +
            "\nFUNCTION RETURNBLOB(ARG1 IN NUMBER) RETURN BLOB IS" +
              "\nreturnBlob BLOB;" +
            "\nBEGIN" +
              "\nSELECT B INTO returnBlob FROM BLOBDATA WHERE ID = ARG1;" +
              "\nRETURN returnBlob;" +
            "\nEND RETURNBLOB;" +
        "\nEND BLOBDATA_PKG;";
    static final String DROP_BLOBDATA_BODY =
        "DROP PACKAGE BODY BLOBDATA_PKG";
    static final String DROP_BLOBDATA_PACKAGE =
        "DROP PACKAGE BLOBDATA_PKG";

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
            runDdl(conn, CREATE_BLOBDATA_TABLE, ddlDebug);
            runDdl(conn, CREATE_PROCEDURE, ddlDebug);
            runDdl(conn, CREATE_FUNCTION, ddlDebug);
            runDdl(conn, CREATE_BLOBDATA_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_BLOBDATA_BODY, ddlDebug);
            initTestValues(conn);
        }
        username = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">BlobTypeTests</property>" +
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
              "<procedure " +
                  "name=\"TopLevelProcedureTest\" " +
                  "procedurePattern=\"PLEASEGETBLOB\" " +
                  "attachmentType=\"MTOM\" " +
                  "binaryAttachment=\"true\" " +
              "/>" +
              "<procedure " +
                  "name=\"TopLevelFunctionTest\" " +
                  "procedurePattern=\"PLEASERETURNBLOB\" " +
                  "attachmentType=\"MTOM\" " +
                  "binaryAttachment=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"GetBLOBForID\" " +
                  "catalogPattern=\"BLOBDATA_PKG\" " +
                  "procedurePattern=\"GETBLOB\" " +
                  "attachmentType=\"MTOM\" " +
                  "binaryAttachment=\"true\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"ReturnBLOBForID\" " +
                  "catalogPattern=\"BLOBDATA_PKG\" " +
                  "procedurePattern=\"RETURNBLOB\" " +
                  "attachmentType=\"MTOM\" " +
                  "binaryAttachment=\"true\" " +
              "/>" +
            "</dbws-builder>";
          builder = new DBWSBuilder();
          DBWSTestSuite.setUp(".");
    }

    private static void initTestValues(Connection conn) {
        try {
            PreparedStatement pStmt = conn.prepareStatement(INSERT_INTO_BLOBDATA);
            pStmt.setInt(1, 1);
            pStmt.setString(2, "3343_bytes.jpg");
            pStmt.setBlob(3, BlobTypeTestSuite.class.getResourceAsStream("/dbws/testing/blobtype/3343_bytes.jpg"));
            pStmt.execute();
            pStmt.setInt(1, 2);
            pStmt.setString(2, "32179_bytes.jpg");
            pStmt.setBlob(3, BlobTypeTestSuite.class.getResourceAsStream("/dbws/testing/blobtype/32179_bytes.jpg"));
            pStmt.execute();
            pStmt.setInt(1, 3);
            pStmt.setString(2, "924732_bytes.jpg");
            pStmt.setBlob(3, BlobTypeTestSuite.class.getResourceAsStream("/dbws/testing/blobtype/924732_bytes.jpg"));
            pStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }

    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_PROCEDURE, ddlDebug);
            runDdl(conn, DROP_FUNCTION, ddlDebug);
            runDdl(conn, DROP_BLOBDATA_BODY, ddlDebug);
            runDdl(conn, DROP_BLOBDATA_PACKAGE, ddlDebug);
            runDdl(conn, DROP_BLOBDATA_TABLE, ddlDebug);
        }
    }

    // Test PL/SQL StoredProcedure
    @Test
    public void testGetSmallBLOB() {
        invokeAndAssert(new Invocation("GetBLOBForID"), 1);
    }
    @Test
    public void testGetMediumBLOB() {
        invokeAndAssert(new Invocation("GetBLOBForID"), 2);
    }
    @Test
    public void testGetLargeBLOB() {
        invokeAndAssert(new Invocation("GetBLOBForID"), 3);
    }

    // Test PL/SQL StoredFunction
    @Test
    public void testReturnSmallBLOB() {
        invokeAndAssert(new Invocation("ReturnBLOBForID"), 1);
    }
    @Test
    public void testReturnMediumBLOB() {
        invokeAndAssert(new Invocation("ReturnBLOBForID"), 2);
    }
    @Test
    public void testReturnLargeBLOB() {
        invokeAndAssert(new Invocation("ReturnBLOBForID"), 3);
    }

    // Test top-level StoredProcedure
    @Test
    public void testTopLevelGetSmBlob() {
        invokeAndAssert(new Invocation("TopLevelProcedureTest"), 1);
    }
    @Test
    public void testTopLevelGetMdBlob() {
        invokeAndAssert(new Invocation("TopLevelProcedureTest"), 2);
    }
    @Test
    public void testTopLevelGetLgBlob() {
        invokeAndAssert(new Invocation("TopLevelProcedureTest"), 3);
    }

    // Test top-level StoredFunction
    @Test
    public void testTopLevelReturnSmBlob() {
        invokeAndAssert(new Invocation("TopLevelFunctionTest"), 1);
    }
    @Test
    public void testTopLevelReturnMdBlob() {
        invokeAndAssert(new Invocation("TopLevelFunctionTest"), 2);
    }
    @Test
    public void testTopLevelReturnLgBlob() {
        invokeAndAssert(new Invocation("TopLevelFunctionTest"), 3);
    }

    /**
     * Invoke the operation and perform assertions on the result.
     */
    protected void invokeAndAssert(Invocation invocation, int argVal) {
        invocation.setParameter("ARG1", argVal);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);

        DataHandler dataHandler = (DataHandler)result;
        ByteArrayInputStream bais;
        try {
            bais = (ByteArrayInputStream)dataHandler.getInputStream();
            byte[] ref = new byte[bais.available()];
            int count = bais.read(ref);
            switch(argVal) {
            case 1:
                assertEquals("wrong number of bytes returned", 3343, count);
                break;
            case 2:
                assertEquals("wrong number of bytes returned", 32179, count);
                break;
            default:
                assertEquals("wrong number of bytes returned", 924732, count);
            }
        } catch (IOException e) {
            fail("An unexpected exception occurred: " + e.getMessage());
        }
    }
}
