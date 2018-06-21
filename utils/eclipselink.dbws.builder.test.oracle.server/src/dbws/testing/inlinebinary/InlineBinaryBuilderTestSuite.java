/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     David McCann - Aug.14, 2012 - 2.4.1 - Initial implementation
package dbws.testing.inlinebinary;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests inline binary.
 *
 */
public class InlineBinaryBuilderTestSuite extends DBWSTestSuite {
    static final String PROJECT_NAME = "inlinebinary";
    static final String BUILDER_FILE = stageDir + "/dbws-builder-inlinebinary.xml";
    static final String WSDL_LOC = "http://" + host + ":" + port + "/inlinebinary/inlinebinary?wsdl";

    public static final String CREATE_TABLE =
        "CREATE TABLE INLINEBINARY (" +
            "ID NUMBER NOT NULL," +
            "NAME VARCHAR(80)," +
            "B BLOB," +
            "PRIMARY KEY (ID)" +
        ")";

    public static final String[] POPULATE_TABLE = new String[] {
        "INSERT INTO INLINEBINARY(ID, NAME, B) VALUES (1, 'one', '010101010101010101010101010101')",
        "INSERT INTO INLINEBINARY(ID, NAME, B) VALUES (2, 'two', '020202020202020202020202020202')",
        "INSERT INTO INLINEBINARY(ID, NAME, B) VALUES (3, 'three', '030303030303030303030303030303')"
    };

    public static final String DROP_TABLE =
        "DROP TABLE INLINEBINARY";

    @BeforeClass
    public static void setUp() {
        DBWSTestSuite.setupTest(BUILDER_FILE, BUILDER_XML);
        if (ddlCreate) {
            runDdl(conn, CREATE_TABLE, ddlDebug);
        }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_TABLE, ddlDebug);
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
            "tableNamePattern=\"INLINEBINARY\">\n" +
          "</table>\n" +
        "</dbws-builder>";
}
