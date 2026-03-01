/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
            """
                    CREATE OR REPLACE TYPE A_PHONE_TYPE AS OBJECT (
                    HOME VARCHAR2(20),
                    CELL VARCHAR2(20)
                    )""";
    static final String CREATE_PACKAGE2_PACKAGE =
            """
                    CREATE OR REPLACE PACKAGE PACKAGE2 AS
                    TYPE TAB1 IS TABLE OF VARCHAR2(111) INDEX BY BINARY_INTEGER;
                    TYPE ORECORD IS RECORD (
                    O1 VARCHAR2(10),
                    O2 DECIMAL(7,2)
                    );
                    TYPE TAB2 IS TABLE OF ORECORD INDEX BY BINARY_INTEGER;
                    TYPE TAB3 IS TABLE OF BOOLEAN INDEX BY BINARY_INTEGER;
                    TYPE NESTED_TABLE IS TABLE OF VARCHAR2(20);
                    TYPE A_PHONE_TYPE_COL IS TABLE OF A_PHONE_TYPE INDEX BY BINARY_INTEGER;
                    PROCEDURE COPYTABLE(OLDTAB IN TAB1, NEWTAB OUT TAB1);
                    FUNCTION COPYTABLE_FUNC(OLDTAB IN TAB1, NESTAB IN NESTED_TABLE) RETURN TAB1;
                    PROCEDURE COPYPHONECOLLECTION(OLDCOL IN A_PHONE_TYPE_COL, NEWCOL OUT A_PHONE_TYPE_COL);
                    PROCEDURE SETRECORD(INREC IN ORECORD, NEWTAB OUT TAB2);
                    PROCEDURE COPYBOOLEANTABLE(OLDTAB IN TAB3, NEWTAB OUT TAB3);
                    END PACKAGE2;""";

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

        List<String> procedurePatterns = new ArrayList<>();
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
            fail("No Jakarta Persistence metadata was generated");
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
            """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <orm:entity-mappings xsi:schemaLocation="http://www.eclipse.org/eclipselink/xsds/persistence/orm org/eclipse/persistence/jpa/eclipselink_orm_2_5.xsd"     xmlns:orm="http://www.eclipse.org/eclipselink/xsds/persistence/orm"      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                       <orm:named-plsql-stored-procedure-query name="COPYTABLE" procedure-name="PACKAGE2.COPYTABLE">
                          <orm:parameter direction="IN" name="OLDTAB" database-type="PACKAGE2.TAB1"/>
                          <orm:parameter direction="OUT" name="NEWTAB" database-type="PACKAGE2.TAB1"/>
                       </orm:named-plsql-stored-procedure-query>
                       <orm:named-plsql-stored-procedure-query name="COPYPHONECOLLECTION" procedure-name="PACKAGE2.COPYPHONECOLLECTION">
                          <orm:parameter direction="IN" name="OLDCOL" database-type="PACKAGE2.A_PHONE_TYPE_COL"/>
                          <orm:parameter direction="OUT" name="NEWCOL" database-type="PACKAGE2.A_PHONE_TYPE_COL"/>
                       </orm:named-plsql-stored-procedure-query>
                       <orm:named-plsql-stored-procedure-query name="SETRECORD" procedure-name="PACKAGE2.SETRECORD">
                          <orm:parameter direction="IN" name="INREC" database-type="PACKAGE2.ORECORD"/>
                          <orm:parameter direction="OUT" name="NEWTAB" database-type="PACKAGE2.TAB2"/>
                       </orm:named-plsql-stored-procedure-query>
                       <orm:named-plsql-stored-procedure-query name="COPYBOOLEANTABLE" procedure-name="PACKAGE2.COPYBOOLEANTABLE">
                          <orm:parameter direction="IN" name="OLDTAB" database-type="PACKAGE2.TAB3"/>
                          <orm:parameter direction="OUT" name="NEWTAB" database-type="PACKAGE2.TAB3"/>
                       </orm:named-plsql-stored-procedure-query>
                       <orm:named-plsql-stored-function-query name="COPYTABLE_FUNC" function-name="PACKAGE2.COPYTABLE_FUNC">
                          <orm:parameter direction="IN" name="OLDTAB" database-type="PACKAGE2.TAB1"/>
                          <orm:parameter direction="IN" name="NESTAB" database-type="PACKAGE2.NESTED_TABLE"/>
                          <orm:return-parameter name="RESULT" database-type="PACKAGE2.TAB1"/>
                       </orm:named-plsql-stored-function-query>
                       <orm:oracle-object name="A_PHONE_TYPE" java-type="metadatagen.A_phone_type">
                          <orm:field name="HOME" database-type="VARCHAR_TYPE"/>
                          <orm:field name="CELL" database-type="VARCHAR_TYPE"/>
                       </orm:oracle-object>
                       <orm:plsql-record name="PACKAGE2.ORECORD" compatible-type="PACKAGE2_ORECORD" java-type="package2.Orecord">
                          <orm:field name="O1" database-type="VARCHAR_TYPE"/>
                          <orm:field name="O2" database-type="DECIMAL_TYPE"/>
                       </orm:plsql-record>
                       <orm:plsql-table name="PACKAGE2.TAB1" compatible-type="PACKAGE2_TAB1" java-type="package2.Tab1" nested-type="VARCHAR_TYPE" nested-table="false"/>
                       <orm:plsql-table name="PACKAGE2.NESTED_TABLE" compatible-type="PACKAGE2_NESTED_TABLE" java-type="package2.Nested_table" nested-type="VARCHAR_TYPE" nested-table="true"/>
                       <orm:plsql-table name="PACKAGE2.A_PHONE_TYPE_COL" compatible-type="PACKAGE2_A_PHONE_TYPE_COL" java-type="package2.A_phone_type_col" nested-type="A_PHONE_TYPE" nested-table="false"/>
                       <orm:plsql-table name="PACKAGE2.TAB2" compatible-type="PACKAGE2_TAB2" java-type="package2.Tab2" nested-type="PACKAGE2.ORECORD" nested-table="false"/>
                       <orm:plsql-table name="PACKAGE2.TAB3" compatible-type="PACKAGE2_TAB3" java-type="package2.Tab3" nested-type="PLSQLBoolean" nested-table="false"/>
                       <orm:embeddable class="package2.Tab1" access="VIRTUAL">
                          <orm:attributes>
                             <orm:array name="items" target-class="PACKAGE2_TAB1" attribute-type="java.util.ArrayList" database-type="PACKAGE2_TAB1">
                                <orm:column name="ITEMS"/>
                             </orm:array>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="package2.Nested_table" access="VIRTUAL">
                          <orm:attributes>
                             <orm:array name="items" target-class="PACKAGE2_NESTED_TABLE" attribute-type="java.util.ArrayList" database-type="PACKAGE2_NESTED_TABLE">
                                <orm:column name="ITEMS"/>
                             </orm:array>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="metadatagen.A_phone_type" access="VIRTUAL">
                          <orm:struct name="A_PHONE_TYPE">
                             <orm:field>HOME</orm:field>
                             <orm:field>CELL</orm:field>
                          </orm:struct>
                          <orm:attributes>
                             <orm:basic name="home" attribute-type="java.lang.String">
                                <orm:column name="HOME"/>
                             </orm:basic>
                             <orm:basic name="cell" attribute-type="java.lang.String">
                                <orm:column name="CELL"/>
                             </orm:basic>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="package2.A_phone_type_col" access="VIRTUAL">
                          <orm:attributes>
                             <orm:array name="items" target-class="metadatagen.A_phone_type" attribute-type="java.util.ArrayList" database-type="PACKAGE2_A_PHONE_TYPE_COL">
                                <orm:column name="ITEMS"/>
                             </orm:array>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="package2.Orecord" access="VIRTUAL">
                          <orm:struct name="PACKAGE2_ORECORD">
                             <orm:field>O1</orm:field>
                             <orm:field>O2</orm:field>
                          </orm:struct>
                          <orm:attributes>
                             <orm:basic name="o1" attribute-type="java.lang.String">
                                <orm:column name="O1"/>
                             </orm:basic>
                             <orm:basic name="o2" attribute-type="java.math.BigDecimal">
                                <orm:column name="O2"/>
                             </orm:basic>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="package2.Tab2" access="VIRTUAL">
                          <orm:attributes>
                             <orm:array name="items" target-class="package2.Orecord" attribute-type="java.util.ArrayList" database-type="PACKAGE2_TAB2">
                                <orm:column name="ITEMS"/>
                             </orm:array>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="package2.Tab3" access="VIRTUAL">
                          <orm:attributes>
                             <orm:array name="items" target-class="PACKAGE2_TAB3" attribute-type="java.util.ArrayList" database-type="PACKAGE2_TAB3">
                                <orm:column name="ITEMS"/>
                             </orm:array>
                          </orm:attributes>
                       </orm:embeddable>
                    </orm:entity-mappings>""";
}
