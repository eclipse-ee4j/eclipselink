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
package org.eclipse.persistence.tools.metadata.generation.test.plsqlcollectiontype;


import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DATABASE_DDL_CREATE_KEY;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DATABASE_DDL_DEBUG_KEY;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DATABASE_DDL_DROP_KEY;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DATABASE_USERNAME_KEY;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DEFAULT_DATABASE_DDL_CREATE;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DEFAULT_DATABASE_DDL_DEBUG;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DEFAULT_DATABASE_DDL_DROP;
import static org.eclipse.persistence.tools.metadata.generation.test.AllTests.DEFAULT_DATABASE_USERNAME;
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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsWriter;
import org.eclipse.persistence.tools.metadata.generation.JPAMetadataGenerator;
import org.eclipse.persistence.tools.metadata.generation.test.AllTests;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLPackageType;
import org.eclipse.persistence.tools.oracleddl.metadata.ProcedureType;
import org.eclipse.persistence.tools.oracleddl.parser.ParseException;
import org.eclipse.persistence.tools.oracleddl.util.DatabaseTypeBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 * Tests metadata generation from a PLSQLCollectionType.
 *
 */
public class PLSQLCollectionTypeTestSuite {
    static final String CREATE_A_PHONE_TYPE =
        "CREATE OR REPLACE TYPE A_PHONE_TYPE AS OBJECT (" +
            "\nHOME VARCHAR2(20)," +
            "\nCELL VARCHAR2(20)" +
        "\n)";
    static final String CREATE_PACKAGE2_PACKAGE =
        "CREATE OR REPLACE PACKAGE PACKAGE2 AS" +
            "\nTYPE TAB1 IS TABLE OF VARCHAR2(111) INDEX BY BINARY_INTEGER;" +
            "\nTYPE ORECORD IS RECORD (" +
                "\nO1 VARCHAR2(10)," +
                "\nO2 DECIMAL(7,2)" +
            "\n);" +
            "\nTYPE TAB2 IS TABLE OF ORECORD INDEX BY BINARY_INTEGER;" +
            "\nTYPE TAB3 IS TABLE OF BOOLEAN INDEX BY BINARY_INTEGER;" +
            "\nTYPE NESTED_TABLE IS TABLE OF VARCHAR2(20);" +
            "\nTYPE A_PHONE_TYPE_COL IS TABLE OF A_PHONE_TYPE INDEX BY BINARY_INTEGER;" +
            "\nPROCEDURE COPYTABLE(OLDTAB IN TAB1, NEWTAB OUT TAB1);" +
            "\nFUNCTION COPYTABLE_FUNC(OLDTAB IN TAB1, NESTAB IN NESTED_TABLE) RETURN TAB1;" +
            "\nPROCEDURE COPYPHONECOLLECTION(OLDCOL IN A_PHONE_TYPE_COL, NEWCOL OUT A_PHONE_TYPE_COL);" +
            "\nPROCEDURE SETRECORD(INREC IN ORECORD, NEWTAB OUT TAB2);" +
            "\nPROCEDURE COPYBOOLEANTABLE(OLDTAB IN TAB3, NEWTAB OUT TAB3);" +
        "\nEND PACKAGE2;";

    // JDBC Shadow types for PL/SQL collections
    static final String CREATE_PACKAGE2_TAB1_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE2_TAB1 AS TABLE OF VARCHAR2(111)";
    static final String CREATE_A_PHONE_COLLECTION_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE2_A_PHONE_TYPE_COL AS TABLE OF A_PHONE_TYPE";

    static final String DROP_PACKAGE2_PACKAGE = "DROP PACKAGE PACKAGE2";
    static final String DROP_PACKAGE2_TAB1_TYPE = "DROP TYPE PACKAGE2_TAB1";
    static final String PACKAGE2_A_PHONE_TYPE_COLLECTION_TYPE = "DROP TYPE PACKAGE2_A_PHONE_TYPE_COL";

    static boolean ddlCreate = false;
    static boolean ddlDrop = false;
    static boolean ddlDebug = false;

    @SuppressWarnings("rawtypes")
    static List dbProcedures;
    static DatabaseTypeBuilder dbTypeBuilder;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @BeforeClass
    public static void setUp() throws ClassNotFoundException, SQLException {
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
            runDdl(conn, CREATE_A_PHONE_TYPE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE2_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE2_TAB1_TYPE, ddlDebug);
            runDdl(conn, CREATE_A_PHONE_COLLECTION_TYPE, ddlDebug);
        }

        String schema = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);

        List<String> procedurePatterns = new ArrayList<String>();
        procedurePatterns.add("COPYTABLE");
        procedurePatterns.add("COPYTABLE_FUNC");
        procedurePatterns.add("COPYPHONECOLLECTION");
        procedurePatterns.add("SETRECORD");
        procedurePatterns.add("COPYBOOLEANTABLE");


        // use DatabaseTypeBuilder to generate a list of PackageTypes
        dbTypeBuilder = new DatabaseTypeBuilder();
        dbProcedures = new ArrayList();
        try {
            // process the package
            List<PLSQLPackageType> packages = dbTypeBuilder.buildPackages(conn, schema, "PACKAGE2");
            for (PLSQLPackageType pkgType : packages) {
                // now get the desired procedures/functions from the processed package
                for (ProcedureType procType : pkgType.getProcedures()) {
                    if (procedurePatterns.contains(procType.getProcedureName())) {
                        dbProcedures.add(procType);
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_PACKAGE2_PACKAGE, ddlDebug);
            runDdl(conn, DROP_PACKAGE2_TAB1_TYPE, ddlDebug);
            runDdl(conn, PACKAGE2_A_PHONE_TYPE_COLLECTION_TYPE, ddlDebug);
        }
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testJPAPLSQLCollectionMetadata() {
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
        Document controlDoc = xmlParser.parse(new StringReader(typemetadata));
        removeEmptyTextNodes(controlDoc);
        assertTrue("Metadata comparison failed.  Expected:\n" + documentToString(controlDoc) + "\nActual\n" + documentToString(testDoc), comparer.isNodeEqual(controlDoc, testDoc));
    }

    static final String typemetadata =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<orm:entity-mappings xsi:schemaLocation=\"http://www.eclipse.org/eclipselink/xsds/persistence/orm org/eclipse/persistence/jpa/eclipselink_orm_2_5.xsd\"" +
        "     xmlns:orm=\"http://www.eclipse.org/eclipselink/xsds/persistence/orm\" " +
        "     xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "   <orm:named-plsql-stored-procedure-query name=\"COPYTABLE\" procedure-name=\"PACKAGE2.COPYTABLE\">\n" +
        "      <orm:parameter direction=\"IN\" name=\"OLDTAB\" database-type=\"PACKAGE2.TAB1\"/>\n" +
        "      <orm:parameter direction=\"OUT\" name=\"NEWTAB\" database-type=\"PACKAGE2.TAB1\"/>\n" +
        "   </orm:named-plsql-stored-procedure-query>\n" +
        "   <orm:named-plsql-stored-procedure-query name=\"COPYPHONECOLLECTION\" procedure-name=\"PACKAGE2.COPYPHONECOLLECTION\">\n" +
        "      <orm:parameter direction=\"IN\" name=\"OLDCOL\" database-type=\"PACKAGE2.A_PHONE_TYPE_COL\"/>\n" +
        "      <orm:parameter direction=\"OUT\" name=\"NEWCOL\" database-type=\"PACKAGE2.A_PHONE_TYPE_COL\"/>\n" +
        "   </orm:named-plsql-stored-procedure-query>\n" +
        "   <orm:named-plsql-stored-procedure-query name=\"SETRECORD\" procedure-name=\"PACKAGE2.SETRECORD\">\n" +
        "      <orm:parameter direction=\"IN\" name=\"INREC\" database-type=\"PACKAGE2.ORECORD\"/>\n" +
        "      <orm:parameter direction=\"OUT\" name=\"NEWTAB\" database-type=\"PACKAGE2.TAB2\"/>\n" +
        "   </orm:named-plsql-stored-procedure-query>\n" +
        "   <orm:named-plsql-stored-procedure-query name=\"COPYBOOLEANTABLE\" procedure-name=\"PACKAGE2.COPYBOOLEANTABLE\">\n" +
        "      <orm:parameter direction=\"IN\" name=\"OLDTAB\" database-type=\"PACKAGE2.TAB3\"/>\n" +
        "      <orm:parameter direction=\"OUT\" name=\"NEWTAB\" database-type=\"PACKAGE2.TAB3\"/>\n" +
        "   </orm:named-plsql-stored-procedure-query>\n" +
        "   <orm:named-plsql-stored-function-query name=\"COPYTABLE_FUNC\" function-name=\"PACKAGE2.COPYTABLE_FUNC\">\n" +
        "      <orm:parameter direction=\"IN\" name=\"OLDTAB\" database-type=\"PACKAGE2.TAB1\"/>\n" +
        "      <orm:parameter direction=\"IN\" name=\"NESTAB\" database-type=\"PACKAGE2.NESTED_TABLE\"/>\n" +
        "      <orm:return-parameter name=\"RESULT\" database-type=\"PACKAGE2.TAB1\"/>\n" +
        "   </orm:named-plsql-stored-function-query>\n" +
        "   <orm:oracle-object name=\"A_PHONE_TYPE\" java-type=\"metadatagen.A_phone_type\">\n" +
        "      <orm:field name=\"HOME\" database-type=\"VARCHAR_TYPE\"/>\n" +
        "      <orm:field name=\"CELL\" database-type=\"VARCHAR_TYPE\"/>\n" +
        "   </orm:oracle-object>\n" +
        "   <orm:plsql-record name=\"PACKAGE2.ORECORD\" compatible-type=\"PACKAGE2_ORECORD\" java-type=\"package2.Orecord\">\n" +
        "      <orm:field name=\"O1\" database-type=\"VARCHAR_TYPE\"/>\n" +
        "      <orm:field name=\"O2\" database-type=\"DECIMAL_TYPE\"/>\n" +
        "   </orm:plsql-record>\n" +
        "   <orm:plsql-table name=\"PACKAGE2.TAB1\" compatible-type=\"PACKAGE2_TAB1\" java-type=\"package2.Tab1\" nested-type=\"VARCHAR_TYPE\" nested-table=\"false\"/>\n" +
        "   <orm:plsql-table name=\"PACKAGE2.NESTED_TABLE\" compatible-type=\"PACKAGE2_NESTED_TABLE\" java-type=\"package2.Nested_table\" nested-type=\"VARCHAR_TYPE\" nested-table=\"true\"/>\n" +
        "   <orm:plsql-table name=\"PACKAGE2.A_PHONE_TYPE_COL\" compatible-type=\"PACKAGE2_A_PHONE_TYPE_COL\" java-type=\"package2.A_phone_type_col\" nested-type=\"A_PHONE_TYPE\" nested-table=\"false\"/>\n" +
        "   <orm:plsql-table name=\"PACKAGE2.TAB2\" compatible-type=\"PACKAGE2_TAB2\" java-type=\"package2.Tab2\" nested-type=\"PACKAGE2.ORECORD\" nested-table=\"false\"/>\n" +
        "   <orm:plsql-table name=\"PACKAGE2.TAB3\" compatible-type=\"PACKAGE2_TAB3\" java-type=\"package2.Tab3\" nested-type=\"PLSQLBoolean\" nested-table=\"false\"/>\n" +
        "   <orm:embeddable class=\"package2.Tab1\" access=\"VIRTUAL\">\n" +
        "      <orm:attributes>\n" +
        "         <orm:array name=\"items\" target-class=\"PACKAGE2_TAB1\" attribute-type=\"java.util.ArrayList\" database-type=\"PACKAGE2_TAB1\">\n" +
        "            <orm:column name=\"ITEMS\"/>\n" +
        "         </orm:array>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "   <orm:embeddable class=\"package2.Nested_table\" access=\"VIRTUAL\">\n" +
        "      <orm:attributes>\n" +
        "         <orm:array name=\"items\" target-class=\"PACKAGE2_NESTED_TABLE\" attribute-type=\"java.util.ArrayList\" database-type=\"PACKAGE2_NESTED_TABLE\">\n" +
        "            <orm:column name=\"ITEMS\"/>\n" +
        "         </orm:array>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "   <orm:embeddable class=\"metadatagen.A_phone_type\" access=\"VIRTUAL\">\n" +
        "      <orm:struct name=\"A_PHONE_TYPE\">\n" +
        "         <orm:field>HOME</orm:field>\n" +
        "         <orm:field>CELL</orm:field>\n" +
        "      </orm:struct>\n" +
        "      <orm:attributes>\n" +
        "         <orm:basic name=\"home\" attribute-type=\"java.lang.String\">\n" +
        "            <orm:column name=\"HOME\"/>\n" +
        "         </orm:basic>\n" +
        "         <orm:basic name=\"cell\" attribute-type=\"java.lang.String\">\n" +
        "            <orm:column name=\"CELL\"/>\n" +
        "         </orm:basic>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "   <orm:embeddable class=\"package2.A_phone_type_col\" access=\"VIRTUAL\">\n" +
        "      <orm:attributes>\n" +
        "         <orm:array name=\"items\" target-class=\"metadatagen.A_phone_type\" attribute-type=\"java.util.ArrayList\" database-type=\"PACKAGE2_A_PHONE_TYPE_COL\">\n" +
        "            <orm:column name=\"ITEMS\"/>\n" +
        "         </orm:array>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "   <orm:embeddable class=\"package2.Orecord\" access=\"VIRTUAL\">\n" +
        "      <orm:struct name=\"PACKAGE2_ORECORD\">\n" +
        "         <orm:field>O1</orm:field>\n" +
        "         <orm:field>O2</orm:field>\n" +
        "      </orm:struct>\n" +
        "      <orm:attributes>\n" +
        "         <orm:basic name=\"o1\" attribute-type=\"java.lang.String\">\n" +
        "            <orm:column name=\"O1\"/>\n" +
        "         </orm:basic>\n" +
        "         <orm:basic name=\"o2\" attribute-type=\"java.math.BigDecimal\">\n" +
        "            <orm:column name=\"O2\"/>\n" +
        "         </orm:basic>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "   <orm:embeddable class=\"package2.Tab2\" access=\"VIRTUAL\">\n" +
        "      <orm:attributes>\n" +
        "         <orm:array name=\"items\" target-class=\"package2.Orecord\" attribute-type=\"java.util.ArrayList\" database-type=\"PACKAGE2_TAB2\">\n" +
        "            <orm:column name=\"ITEMS\"/>\n" +
        "         </orm:array>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "   <orm:embeddable class=\"package2.Tab3\" access=\"VIRTUAL\">\n" +
        "      <orm:attributes>\n" +
        "         <orm:array name=\"items\" target-class=\"PACKAGE2_TAB3\" attribute-type=\"java.util.ArrayList\" database-type=\"PACKAGE2_TAB3\">\n" +
        "            <orm:column name=\"ITEMS\"/>\n" +
        "         </orm:array>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "</orm:entity-mappings>";
}
