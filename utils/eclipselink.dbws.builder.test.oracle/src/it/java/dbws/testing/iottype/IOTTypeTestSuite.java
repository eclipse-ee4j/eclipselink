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
//     Mike Norman - June 10 2011, created DDL parser package
//     David McCann - July 2011, visit tests
package dbws.testing.iottype;

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

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests TableType where the database table is indexed and contains
 * a UROWID type.
 *
 */
public class IOTTypeTestSuite extends DBWSTestSuite {

    static final String CREATE_INDEXED_TABLE =
        "CREATE TABLE INDEXEDTABLETYPE (" +
            "\nID NUMERIC(4) NOT NULL," +
            "\nNAME VARCHAR(25)," +
            "\nRID UROWID," +
            "\nPRIMARY KEY (ID)" +
        "\n)" +
        "\nORGANIZATION INDEX" +
        "\nPCTTHRESHOLD 2" +
        "\nSTORAGE (" +
            "\nINITIAL 4K" +
            "\nNEXT 2K" +
            "\nPCTINCREASE 0" +
            "\nMINEXTENTS 1" +
            "\nMAXEXTENTS 1" +
        "\n)" +
        "\nOVERFLOW STORAGE (" +
            "\nINITIAL 4K" +
            "\nNEXT 2K" +
            "\nPCTINCREASE 0" +
            "\nMINEXTENTS 1" +
            "\nMAXEXTENTS 1" +
        "\n)";
    static final String[] POPULATE_INDEXED_TABLE = new String[] {
        "INSERT INTO INDEXEDTABLETYPE (ID, NAME, RID) VALUES (1, 'mike', '*EcLiPseLiNk1')",
        "INSERT INTO INDEXEDTABLETYPE (ID, NAME, RID) VALUES (2, 'merrick', '*EcLiPseLiNk2')",
        "INSERT INTO INDEXEDTABLETYPE (ID, NAME, RID) VALUES (3, 'rick', '*EcLiPseLiNk3')"
    };
    static final String DROP_INDEXED_TABLE =
        "DROP TABLE INDEXEDTABLETYPE";

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
            runDdl(conn, CREATE_INDEXED_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_INDEXED_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_INDEXED_TABLE[i]);
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
                "<property name=\"projectName\">tabletypeurowid</property>" +
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
              "tableNamePattern=\"INDEXEDTABLETYPE\" " +
            "/>" +
          "</dbws-builder>";
        builder = null;
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_INDEXED_TABLE, ddlDebug);
        }
    }

    @Test
    public void findByPrimaryKeyTest() {
        Invocation invocation = new Invocation("findByPrimaryKey_IndexedtabletypeType");
        invocation.setParameter("id", 1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ONE_PERSON_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void findAllTest() {
        Invocation invocation = new Invocation("findAll_IndexedtabletypeType");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Element ec = doc.createElement("tabletypeurowid-collection");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(ALL_PEOPLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String ONE_PERSON_XML =
        REGULAR_XML_HEADER +
        "<indexedtabletypeType xmlns=\"urn:tabletypeurowid\">" +
          "<id>1</id>" +
          "<name>mike</name>" +
          "<rid>*EcLiPseLiNk1</rid>" +
        "</indexedtabletypeType>";

    public static final String ALL_PEOPLE_XML =
        REGULAR_XML_HEADER +
        "<tabletypeurowid-collection>" +
          "<indexedtabletypeType xmlns=\"urn:tabletypeurowid\">" +
            "<id>1</id>" +
            "<name>mike</name>" +
            "<rid>*EcLiPseLiNk1</rid>" +
          "</indexedtabletypeType>" +
          "<indexedtabletypeType xmlns=\"urn:tabletypeurowid\">" +
            "<id>2</id>" +
            "<name>merrick</name>" +
            "<rid>*EcLiPseLiNk2</rid>" +
          "</indexedtabletypeType>" +
          "<indexedtabletypeType xmlns=\"urn:tabletypeurowid\">" +
            "<id>3</id>" +
            "<name>rick</name>" +
            "<rid>*EcLiPseLiNk3</rid>" +
          "</indexedtabletypeType>" +
        "</tabletypeurowid-collection>";
}
