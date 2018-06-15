/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     David McCann - June 2013 - Initial Implementation
package dbws.testing.namingtransformer;

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
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DefaultNamingConventionTransformer;

//testing imports
import dbws.testing.DBWSTestSuite;

/**
 * Test use of a custom naming convention transformer.
 *
 */
public class NamingTransformerTestSuite extends DBWSTestSuite {

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
        builder = new DBWSBuilder();
        builder.setTopNamingConventionTransformer(new DBWSNamingConventionTransformer());
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_SIMPLE_TABLE, ddlDebug);
        }
    }

    @Test
    public void findByPrimaryKeyTest() throws WSDLException {
        Invocation invocation = new Invocation("findByPrimaryKey_SimpletableType");
        invocation.setParameter("id", 3);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ANOTHER_PERSON_XML));
        assertTrue("Control document not same as instance document.  Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String ANOTHER_PERSON_XML =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<simpletablexType xmlns=\"urn:simpletable\" id=\"3\">" +
          "<name>rick</name>" +
        "</simpletablexType>";

    /**
     * Inner class used for testing NamingConventionTransformer
     *
     */
    static class DBWSNamingConventionTransformer extends DefaultNamingConventionTransformer {

        @Override
        public String generateSchemaAlias(String tableName) {
            return super.generateSchemaAlias(tableName +"xType");
        }

        @Override
        public String generateElementAlias(String originalElementName) {
            return super.generateElementAlias(originalElementName.toLowerCase());
        }

        @Override
        public ElementStyle styleForElement(String elementName) {
            if ("id".equalsIgnoreCase(elementName)) {
                return ElementStyle.ATTRIBUTE;
            }
            if ("since".equalsIgnoreCase(elementName)) {
                return ElementStyle.NONE;
            }
            return ElementStyle.ELEMENT;
        }
    }
}
