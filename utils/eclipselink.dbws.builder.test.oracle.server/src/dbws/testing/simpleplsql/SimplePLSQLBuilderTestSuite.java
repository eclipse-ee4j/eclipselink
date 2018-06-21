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
//     David McCann - Aug.15, 2012 - 2.4.1 - Initial implementation
package dbws.testing.simpleplsql;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests a simple PL/SQL stored procedure.
 *
 */
public class SimplePLSQLBuilderTestSuite extends DBWSTestSuite {
    static final String PROJECT_NAME = "simpleplsql";
    static final String BUILDER_FILE = stageDir + "/dbws-builder-simpleplsql.xml";
    static final String WSDL_LOC = "http://" + host + ":" + port + "/simpleplsql/simpleplsql?wsdl";

    public static final String CREATE_PACKAGE =
        "CREATE OR REPLACE PACKAGE SOMEPACKAGE AS \n" +
            "PROCEDURE SOMEPROC(ARG1 IN VARCHAR2, ARG2 OUT VARCHAR2); \n" +
        "END SOMEPACKAGE;";
    public static final String CREATE_PACKAGE_BODY =
        "CREATE OR REPLACE PACKAGE BODY SOMEPACKAGE AS \n" +
            "PROCEDURE SOMEPROC(ARG1 IN VARCHAR2, ARG2 OUT VARCHAR2) AS \n" +
            "BEGIN\n" +
                "ARG2 := ARG1;\n" +
            "END SOMEPROC;\n" +
        "END SOMEPACKAGE;";

    public static final String DROP_PACKAGE_BODY =
        "DROP PACKAGE BODY SOMEPACKAGE";
    public static final String DROP_PACKAGE =
        "DROP PACKAGE SOMEPACKAGE";

    @BeforeClass
    public static void setUp() {
        DBWSTestSuite.setupTest(BUILDER_FILE, BUILDER_XML);
        if (ddlCreate) {
            runDdl(conn, CREATE_PACKAGE, ddlDebug);
        }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_PACKAGE, ddlDebug);
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
          "<plsql-procedure \n" +
              "name=\"simpleplsql\" \n" +
              "catalogPattern=\"SOMEPACKAGE\" \n" +
              "procedurePattern=\"SOMEPROC\" /> \n" +
        "</dbws-builder>";
}
