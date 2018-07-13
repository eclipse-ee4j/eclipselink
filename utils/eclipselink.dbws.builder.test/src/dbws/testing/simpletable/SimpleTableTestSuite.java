/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - May 2008, created DBWS test package
package dbws.testing.simpletable;

//javase imports
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;

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

//testing imports
import dbws.testing.DBWSTestSuite;

public class SimpleTableTestSuite extends DBWSTestSuite {

    static final String CREATE_SIMPLE_TABLE =
        "CREATE TABLE IF NOT EXISTS simpletable (" +
            "\nID NUMERIC," +
            "\nNAME varchar(25)," +
            "\nSINCE date," +
            "\nPRIMARY KEY (ID)" +
        "\n)";
    static String[] POPULATE_SIMPLE_TABLE = new String[] {
        "INSERT INTO simpletable (ID, NAME, SINCE) VALUES (1, 'mike', '2001-12-25')",
        "INSERT INTO simpletable (ID, NAME, SINCE) VALUES (2, 'merrick','2001-12-25')",
        "INSERT INTO simpletable (ID, NAME, SINCE) VALUES (3, 'rick','2001-12-25')"
    };
    static final String DROP_SIMPLE_TABLE =
        "DROP TABLE simpletable";

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
            runDdl(conn, CREATE_SIMPLE_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_SIMPLE_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_SIMPLE_TABLE[i]);
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
                "<property name=\"projectName\">simpletable</property>" +
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
            "<table " +
              "schemaPattern=\"%\" " +
              "tableNamePattern=\"simpletable\" " +
            "/>" +
          "</dbws-builder>";
        builder = null;
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_SIMPLE_TABLE, ddlDebug);
        }
    }

    @Test
    public void findByPrimaryKeyTest() {
        Invocation invocation = new Invocation("findByPrimaryKey_SimpletableType");
        invocation.setParameter("id", 1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ONE_PERSON_XML));
        assertTrue("control document not same as instance document", comparer.isNodeEqual(
            controlDoc, doc));
    }
    public static final String ONE_PERSON_XML =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simpletableType xmlns=\"urn:simpletable\">" +
          "<id>1</id>" +
          "<name>mike</name>" +
          "<since>2001-12-25</since>" +
        "</simpletableType>";
}
