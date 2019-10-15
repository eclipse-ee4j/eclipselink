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
package dbws.testing.inlinebinary;

//javase imports
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

public class InlineBinaryTestSuite extends DBWSTestSuite {

    static final String CREATE_INLINE_TABLE =
        "CREATE TABLE IF NOT EXISTS inlinebinary (" +
            "\nID DECIMAL(7,0) NOT NULL," +
            "\nNAME VARCHAR(80)," +
            "\nB BLOB," +
            "\nPRIMARY KEY (ID)" +
        "\n)";
    static final String[] POPULATE_INLINE_TABLE = new String[] {
        "insert into inlinebinary(ID, NAME, B) values (1, 'one', 0x010101010101010101010101010101)",
        "insert into inlinebinary(ID, NAME, B) values (2, 'two', 0x020202020202020202020202020202)",
        "insert into inlinebinary(ID, NAME, B) values (3, 'three', 0x030303030303030303030303030303)"
    };
    static final String DROP_INLINE_TABLE =
        "DROP TABLE inlinebinary";

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
            runDdl(conn, CREATE_INLINE_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_INLINE_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_INLINE_TABLE[i]);
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
               "<property name=\"projectName\">inlinebinary</property>" +
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
               "tableNamePattern=\"inlinebinary\" " +
              "/>" +
           "</dbws-builder>";
        builder = null;
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_INLINE_TABLE, ddlDebug);
        }
    }

    @SuppressWarnings({"rawtypes"})
    @Test
    public void findAll() {
        Invocation invocation = new Invocation("findAll_InlinebinaryType");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("inlinebinary-collection");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(INLINEBINARY_COLLECTION_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String INLINEBINARY_COLLECTION_XML =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<inlinebinary-collection>" +
            "<inlinebinaryType xmlns=\"urn:inlinebinary\">" +
                "<id>1</id>" +
                "<name>one</name>" +
                "<b>AQEBAQEBAQEBAQEBAQEB</b>" +
            "</inlinebinaryType>" +
            "<inlinebinaryType xmlns=\"urn:inlinebinary\">" +
                "<id>2</id>" +
                "<name>two</name>" +
                "<b>AgICAgICAgICAgICAgIC</b>" +
            "</inlinebinaryType>" +
            "<inlinebinaryType xmlns=\"urn:inlinebinary\">" +
                "<id>3</id>" +
                "<name>three</name>" +
                "<b>AwMDAwMDAwMDAwMDAwMD</b>" +
            "</inlinebinaryType>" +
        "</inlinebinary-collection>";
}
