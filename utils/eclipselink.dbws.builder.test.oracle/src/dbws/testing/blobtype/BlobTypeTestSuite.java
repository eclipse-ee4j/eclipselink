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
 *     David McCann - June 25, 2012 - 2.4.1 - Initial implementation
 ******************************************************************************/
package dbws.testing.blobtype;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.IOException;

//java eXtension imports
import javax.activation.DataHandler;
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

//EclipseLink imports
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests PL/SQL procedures and functions returning BLOB data.
 * 
 * Assumptions:
 * <ul>
 * <li>User has privileges to create/drop Oracle DIRECTORY objects</li>
 * <li>The database server contains the files '3343_bytes.jpg', 
 * '32179_bytes.jpg', and '924732_bytes.jpg' in the system directory 
 * '/scratch/temp/bfile_dir'</li>
 * </ul>
 *  
 */
public class BlobTypeTestSuite extends DBWSTestSuite {

    // BFILE DRIECTORY
    static final String CREATE_BFILE_DIRECTORY =
        "CREATE OR REPLACE DIRECTORY bfile_dir AS '/scratch/temp/bfile_dir'";
    
    // BLOB table
    static final String CREATE_BLOBDATA_TABLE =
        "CREATE TABLE BLOBDATA (" +
            "\nID NUMERIC(4) NOT NULL," +
            "\nNAME VARCHAR(80)," +
            "\nB BLOB," +
            "\nPRIMARY KEY (ID)" +
        "\n)";
    static final String INIT_BLOBDATA_TABLE =
        "DECLARE" +
          "\nsrc_lob_sm  BFILE := BFILENAME('BFILE_DIR', '3343_bytes.jpg');" +
          "\nsrc_lob_md  BFILE := BFILENAME('BFILE_DIR', '32179_bytes.jpg');" +
          "\nsrc_lob_lg  BFILE := BFILENAME('BFILE_DIR', '924732_bytes.jpg');" +
          "\ndest_lob_sm BLOB;" +
          "\ndest_lob_md BLOB;" +
          "\ndest_lob_lg BLOB;" +
        "\nBEGIN" +
          "\nINSERT INTO BLOBDATA VALUES(1, '3343_bytes.jpg', EMPTY_BLOB()) RETURNING B INTO dest_lob_sm;" +
          "\nDBMS_LOB.OPEN(src_lob_sm, DBMS_LOB.LOB_READONLY);" +
          "\nDBMS_LOB.LoadFromFile( DEST_LOB => dest_lob_sm," +
                                   "\nSRC_LOB  => src_lob_sm," +
                                   "\nAMOUNT   => DBMS_LOB.GETLENGTH(src_lob_sm) );" +
          "\nDBMS_LOB.CLOSE(src_lob_sm);" +
          "\nINSERT INTO BLOBDATA VALUES(2, '32179_bytes.jpg', EMPTY_BLOB()) RETURNING B INTO dest_lob_md;" +
          "\nDBMS_LOB.OPEN(src_lob_md, DBMS_LOB.LOB_READONLY);" +
          "\nDBMS_LOB.LoadFromFile( DEST_LOB => dest_lob_md," +
                                   "\nSRC_LOB  => src_lob_md," +
                                   "\nAMOUNT   => DBMS_LOB.GETLENGTH(src_lob_md) );" +
          "\nDBMS_LOB.CLOSE(src_lob_md);" +
          "\nINSERT INTO BLOBDATA VALUES(3, '924732_bytes.jpg', EMPTY_BLOB()) RETURNING B INTO dest_lob_lg;" +
          "\nDBMS_LOB.OPEN(src_lob_lg, DBMS_LOB.LOB_READONLY);" +
          "\nDBMS_LOB.LoadFromFile( DEST_LOB => dest_lob_lg," +
                                   "\nSRC_LOB  => src_lob_lg," +
                                   "\nAMOUNT   => DBMS_LOB.GETLENGTH(src_lob_lg) );" +
          "\nDBMS_LOB.CLOSE(src_lob_lg);" +
          "\nCOMMIT;" +
        "\nEND;";
    
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
    static final String DROP_BFILE_DIRECTORY =
        "DROP DIRECTORY bfile_dir";

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
            runDdl(conn, CREATE_BFILE_DIRECTORY, ddlDebug);
            runDdl(conn, CREATE_BLOBDATA_TABLE, ddlDebug);
            runDdl(conn, INIT_BLOBDATA_TABLE, ddlDebug);
            runDdl(conn, CREATE_PROCEDURE, ddlDebug);
            runDdl(conn, CREATE_FUNCTION, ddlDebug);
            runDdl(conn, CREATE_BLOBDATA_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_BLOBDATA_BODY, ddlDebug);
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

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_PROCEDURE, ddlDebug);
            runDdl(conn, DROP_FUNCTION, ddlDebug);
            runDdl(conn, DROP_BLOBDATA_BODY, ddlDebug);
            runDdl(conn, DROP_BLOBDATA_PACKAGE, ddlDebug);
            runDdl(conn, DROP_BLOBDATA_TABLE, ddlDebug);
            runDdl(conn, DROP_BFILE_DIRECTORY, ddlDebug);
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
