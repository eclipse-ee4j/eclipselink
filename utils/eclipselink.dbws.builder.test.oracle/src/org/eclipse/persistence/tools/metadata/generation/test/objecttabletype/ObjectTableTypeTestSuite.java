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
//     David McCann - July 2013 - Initial Implementation
package org.eclipse.persistence.tools.metadata.generation.test.objecttabletype;


import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DATABASE_DDL_CREATE_KEY;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DATABASE_DDL_DEBUG_KEY;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DATABASE_DDL_DROP_KEY;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DEFAULT_DATABASE_DDL_CREATE;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DEFAULT_DATABASE_DDL_DEBUG;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DEFAULT_DATABASE_DDL_DROP;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DEFAULT_PACKAGE_NAME;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.comparer;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.conn;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.databasePlatform;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.documentToString;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.removeEmptyTextNodes;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.runDdl;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.xmlParser;
//javase imports
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsWriter;
import org.eclipse.persistence.tools.metadata.generation.JPAMetadataGenerator;
import org.eclipse.persistence.tools.metadata.generation.test.AllTests;
import org.eclipse.persistence.tools.oracleddl.parser.ParseException;
import org.eclipse.persistence.tools.oracleddl.util.DatabaseTypeBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 * Tests metadata generation from a ProcedureType.
 *
 */
public class ObjectTableTypeTestSuite {
    static final String CREATE_PHONE_TYPE =
        "CREATE OR REPLACE TYPE DBWS_PHONE_TYPE AS OBJECT (" +
            "\nHOME VARCHAR2(20)," +
            "\nCELL VARCHAR2(20)" +
        "\n)";
    static final String CREATE_PHONE_TYPE_TABLE =
        "CREATE OR REPLACE TYPE DBWS_PHONE_TYPE_TABLE AS TABLE OF DBWS_PHONE_TYPE";

    static final String CREATE_GET_PHONETYPE_TABLE_PROC =
        "CREATE OR REPLACE PROCEDURE GET_PHONETYPE_TABLE(PTABLE OUT DBWS_PHONE_TYPE_TABLE) AS" +
        "\nBEGIN" +
            "\nPTABLE := DBWS_PHONE_TYPE_TABLE();" +
            "\nPTABLE.EXTEND();" +
            "\nPTABLE(PTABLE.COUNT) := DBWS_PHONE_TYPE('613.288.1234', '613.858.4321');" +
            "\nPTABLE.EXTEND();" +
            "\nPTABLE(PTABLE.COUNT) := DBWS_PHONE_TYPE('613.288.0000', '613.585.2222');" +
        "\nEND GET_PHONETYPE_TABLE;";

    static final String DROP_GET_PHOETYPE_TABLE_PROC =
        "DROP PROCEDURE GET_PHONETYPE_TABLE";
    static final String DROP_PHONE_TYPE_TABLE =
        "DROP TYPE DBWS_PHONE_TYPE_TABLE FORCE";
    static final String DROP_PHONE_TYPE =
        "DROP TYPE DBWS_PHONE_TYPE FORCE";

    static boolean ddlCreate = false;
    static boolean ddlDrop = false;
    static boolean ddlDebug = false;

    @SuppressWarnings("rawtypes")
    static List dbProcedures;
    static DatabaseTypeBuilder dbTypeBuilder;

    @BeforeClass
    public static void setUp() throws ClassNotFoundException, SQLException{
        AllTests.setUp();

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
            runDdl(conn, CREATE_PHONE_TYPE, ddlDebug);
            runDdl(conn, CREATE_PHONE_TYPE_TABLE, ddlDebug);
            runDdl(conn, CREATE_GET_PHONETYPE_TABLE_PROC, ddlDebug);
        }

        // use DatabaseTypeBuilder to generate a list of ProcedureTypes
        dbTypeBuilder = new DatabaseTypeBuilder();
        try {
            dbProcedures = dbTypeBuilder.buildProcedures(conn, "TOPLEVEL", "GET_PHONETYPE_TABLE");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_GET_PHOETYPE_TABLE_PROC, ddlDebug);
            runDdl(conn, DROP_PHONE_TYPE_TABLE, ddlDebug);
            runDdl(conn, DROP_PHONE_TYPE, ddlDebug);
        }
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testJPAObjectTableMetadata() {
        if (dbProcedures == null || dbProcedures.isEmpty()) {
            fail("No types were generated.");
        }
        XMLEntityMappings mappings = null;
        try {
            JPAMetadataGenerator gen = new JPAMetadataGenerator(DEFAULT_PACKAGE_NAME, databasePlatform);
            mappings = gen.generateXmlEntityMappings(dbProcedures);
        } catch (Exception x) {
            fail("An unexpected exception occurred: " + x.getMessage());
        }
        if (mappings == null) {
            fail("No JPA metadata was generated");
        }
        ByteArrayOutputStream metadata = new ByteArrayOutputStream();
        XMLEntityMappingsWriter.write(mappings, metadata);
        Document testDoc = xmlParser.parse(new StringReader(metadata.toString()));
        removeEmptyTextNodes(testDoc);
        Document controlDoc = xmlParser.parse(new StringReader(arraymetadata));
        removeEmptyTextNodes(controlDoc);
        assertTrue("Metadata comparison failed.  Expected:\n" + documentToString(controlDoc) + "\nActual\n" + documentToString(testDoc), comparer.isNodeEqual(controlDoc, testDoc));
    }

    static final String arraymetadata =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<orm:entity-mappings xsi:schemaLocation=\"http://www.eclipse.org/eclipselink/xsds/persistence/orm org/eclipse/persistence/jpa/eclipselink_orm_2_5.xsd\"" +
        "     xmlns:orm=\"http://www.eclipse.org/eclipselink/xsds/persistence/orm\" " +
        "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "   <orm:named-stored-procedure-query name=\"GET_PHONETYPE_TABLE\" procedure-name=\"GET_PHONETYPE_TABLE\" returns-result-set=\"false\">\n" +
        "      <orm:parameter mode=\"OUT\" name=\"PTABLE\" type=\"metadatagen.Dbws_phone_type_table\" class=\"metadatagen.Dbws_phone_type_table\" jdbc-type=\"2003\" jdbc-type-name=\"DBWS_PHONE_TYPE_TABLE\"/>\n" +
        "   </orm:named-stored-procedure-query>\n" +
        "   <orm:oracle-array name=\"DBWS_PHONE_TYPE_TABLE\" java-type=\"metadatagen.Dbws_phone_type_table\" nested-type=\"DBWS_PHONE_TYPE\"/>\n" +
        "   <orm:embeddable class=\"metadatagen.Dbws_phone_type_table\" access=\"VIRTUAL\">\n" +
        "      <orm:attributes>\n" +
        "         <orm:array name=\"items\" target-class=\"metadatagen.Dbws_phone_type\" attribute-type=\"java.util.ArrayList\" database-type=\"DBWS_PHONE_TYPE\">\n" +
        "            <orm:column name=\"ITEMS\"/>\n" +
        "         </orm:array>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "</orm:entity-mappings>";
}
