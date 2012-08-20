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
 *     David McCann - Aug.02, 2012 - 2.4.1 - Initial implementation
 ******************************************************************************/
package dbws.testing.attachedbinary;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests binary attachments.
 * 
 */
public class AttachedBinaryBuilderTestSuite extends DBWSTestSuite {
	static final String PROJECT_NAME = "attachedbinary"; 
	static final String BUILDER_FILE = stageDir + "/dbws-builder-attachedbinary.xml";
	static final String WSDL_LOC = "http://localhost:7001/attachedbinary/attachedbinary?wsdl"; 
	
    public static final String CREATE_TABLE =
        "CREATE TABLE ATTACHEDBINARY (" +
            "ID NUMBER NOT NULL," +
            "NAME VARCHAR(80)," +
            "B BLOB," +
            "PRIMARY KEY (ID)" +
        ")";

    public static final String[] POPULATE_TABLE = new String[] {
        "INSERT INTO ATTACHEDBINARY(ID, NAME, B) VALUES (1, 'one', '010101010101010101010101010101')",
        "INSERT INTO ATTACHEDBINARY(ID, NAME, B) VALUES (2, 'two', '020202020202020202020202020202')",
        "INSERT INTO ATTACHEDBINARY(ID, NAME, B) VALUES (3, 'three', '030303030303030303030303030303')"
    };
    
    public static final String CREATE_FUNCTION = 
        "CREATE OR REPLACE FUNCTION GETBLOBBYID(PK IN NUMBER) RETURN BLOB IS" +
        "\nblb BLOB := EMPTY_BLOB();" +
        "\nBEGIN" +
            "\nSELECT B INTO blb FROM ATTACHEDBINARY WHERE ID=PK;" +
            "\nRETURN(BLB);" +
            "\nEXCEPTION" + 
                "\nWHEN NO_DATA_FOUND THEN" +
                    "\nRAISE_APPLICATION_ERROR(-20001, 'GETBLOBBYID FAILED WITH NO DATA FOUND');" +
        "\nEND;";

    public static final String DROP_TABLE =
        "DROP TABLE ATTACHEDBINARY";
    public static final String DROP_FUNCTION =
        "DROP FUNCTION GETBLOBBYID";

    @BeforeClass
    public static void setUp() {
        DBWSTestSuite.setupTest(BUILDER_FILE, BUILDER_XML);
        if (ddlCreate) {
            runDdl(conn, CREATE_TABLE, ddlDebug);
            runDdl(conn, CREATE_FUNCTION, ddlDebug);
        }
    }
    
    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_TABLE, ddlDebug);
            runDdl(conn, DROP_FUNCTION, ddlDebug);
        }
    }

    @Test
    public void testBuild() {
        DBWSTestSuite.testBuild(PROJECT_NAME, BUILDER_FILE);
    }
    
    static final String BUILDER_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
          "<properties>\n" +
            "<property name=\"projectName\">" + PROJECT_NAME + "</property>\n" +
            "<property name=\"logLevel\">off</property>\n" +
            "<property name=\"username\">" + username + "</property>\n" +
            "<property name=\"password\">" + password + "</property>\n" +
            "<property name=\"url\">" + url + "</property>\n" +
            "<property name=\"driver\">" + driver + "</property>\n" +
            "<property name=\"platformClassname\">" + platform + "</property>\n" +
            "<property name=\"dataSource\">" + datasource + "</property>\n" +
            "<property name=\"wsdlLocationURI\">" + WSDL_LOC + "</property>\n" +
          "</properties>\n" +
          "<table \n" + 
              "tableNamePattern=\"ATTACHEDBINARY\" \n" +
              "binaryAttachment=\"true\" \n" +
              "attachmentType=\"swaref\"> \n" +
              "<procedure name=\"getBLOBById\" \n" +
                  "isCollection=\"false\" \n" +
                  "returnType=\"ab:attachedbinaryType\" \n" +
                  "procedurePattern=\"GETBLOBBYID\" \n" +
                  "catalogPattern=\"TOPLEVEL\" \n" +
                  "binaryAttachment=\"true\" \n" +
                  "attachmentType=\"swaref\"/> \n" +
          "</table>\n" +
        "</dbws-builder>";
}