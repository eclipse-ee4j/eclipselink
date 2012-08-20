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
 *     David McCann - Aug.15, 2012 - 2.4.1 - Initial implementation
 ******************************************************************************/
package dbws.testing.simplesql;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests building a web service using SQL.
 * 
 */
public class SimpleSQLBuilderTestSuite extends DBWSTestSuite {
    static final String PROJECT_NAME = "simplesql"; 
    static final String BUILDER_FILE = stageDir + "/dbws-builder-simplesql.xml";
    static final String WSDL_LOC = "http://localhost:7001/simplesql/simplesql?wsdl"; 
	
    public static final String CREATE_TABLE =
        "CREATE TABLE SIMPLESQL (" +
            "id NUMBER NOT NULL," +
            "name VARCHAR2(25)," +
            "since DATE," +
            "PRIMARY KEY (id)" +
        ")";
    
    public static final String[] POPULATE_TABLE = new String[] {
        "INSERT INTO SIMPLESQL (id, name, since) VALUES (1, 'mike', to_date('2001-12-25','YYYY-MM-DD'))",
        "INSERT INTO SIMPLESQL (id, name, since) VALUES (2, 'blaise',to_date('2001-12-25','YYYY-MM-DD'))",
        "INSERT INTO SIMPLESQL (id, name, since) VALUES (3, 'rick',to_date('2001-12-25','YYYY-MM-DD'))"
    };

    public static final String DROP_TABLE =
        "DROP TABLE SIMPLESQL";
    
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
          "<sql " +
              "name=\"count-info\" " +
              "isCollection=\"false\" " +
              "simpleXMLFormatTag=\"simple-sql\" " +
              "xmlTag=\"count-info\">\n" +
              "<text><![CDATA[select count(*) as \"COUNT\" from SIMPLESQL]]></text>" +
          "</sql>" +
        "</dbws-builder>";
}