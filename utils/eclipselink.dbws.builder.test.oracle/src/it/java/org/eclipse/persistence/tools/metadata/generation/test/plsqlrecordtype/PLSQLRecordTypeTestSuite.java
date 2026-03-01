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
package org.eclipse.persistence.tools.metadata.generation.test.plsqlrecordtype;


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
public class PLSQLRecordTypeTestSuite {
    static final String CREATE_A_PHONE_TYPE =
            """
                    CREATE OR REPLACE TYPE A_PHONE_TYPE AS OBJECT (
                    HOME VARCHAR2(20),
                    CELL VARCHAR2(20)
                    )""";
    static final String CREATE_A_PHONE_TYPE_TABLE =
        "CREATE OR REPLACE TYPE A_PHONE_TYPE_TABLE AS TABLE OF A_PHONE_TYPE";
    static final String CREATE_A_PHONE_TYPE_VARRAY =
        "CREATE OR REPLACE TYPE A_PHONE_TYPE_VARRAY AS VARRAY(10) OF A_PHONE_TYPE";

    static final String CREATE_A_CONTACT_TYPE =
            """
                    CREATE OR REPLACE TYPE A_CONTACT_TYPE AS OBJECT (
                    ADDRESS VARCHAR2(40),
                    PHONE A_PHONE_TYPE
                    )""";
    static final String CREATE_PACKAGE2_PACKAGE =
            """
                    CREATE OR REPLACE PACKAGE PACKAGE2 AS
                    TYPE TAB1 IS TABLE OF VARCHAR(20) INDEX BY BINARY_INTEGER;
                    TYPE A_PHONE_TYPE_COL IS TABLE OF A_PHONE_TYPE INDEX BY BINARY_INTEGER;
                    TYPE EMPREC IS RECORD (EMP_ID NUMERIC(4), EMP_NAME VARCHAR2(25));
                    TYPE COMPLEXRECORD IS RECORD (
                    CR1 NUMERIC(4),
                    CR2 A_CONTACT_TYPE,
                    CR3 A_PHONE_TYPE_VARRAY,
                    CR4 A_PHONE_TYPE_TABLE
                    );
                    TYPE MORECOMPLEXRECORD IS RECORD (
                    MCR1 TAB1,
                    MCR2 A_PHONE_TYPE_COL
                    );
                    TYPE SUPERCOMPLEXRECORD IS RECORD (
                    SCR1 EMPREC
                    );
                    PROCEDURE COPYRECORD(OLDREC IN EMPREC, NEWREC OUT EMPREC);
                    FUNCTION COPYRECORD_FUNC(OLDREC IN EMPREC) RETURN EMPREC;
                    PROCEDURE COPYCOMPLEXRECORD(OLDREC IN COMPLEXRECORD, NEWREC OUT COMPLEXRECORD);
                    FUNCTION COPYCOMPLEXRECORD_FUNC(OLDREC IN COMPLEXRECORD) RETURN COMPLEXRECORD;
                    FUNCTION COPYMORECOMPLEXRECORD_FUNC(OLDREC IN MORECOMPLEXRECORD) RETURN MORECOMPLEXRECORD;
                    PROCEDURE COPYSUPERCOMPLEXRECORD(OLDREC IN SUPERCOMPLEXRECORD, NEWREC OUT SUPERCOMPLEXRECORD);
                    END PACKAGE2;""";

    // JDBC Shadow types for PL/SQL records
    static final String CREATE_PACKAGE2_EMPREC_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE2_EMPREC AS OBJECT(EMP_ID NUMERIC(4), EMP_NAME VARCHAR2(25));";
    static final String CREATE_COMPLEXRECORD_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE2_COMPLEXRECORD AS OBJECT(CR1 NUMERIC(4), CR2 A_CONTACT_TYPE);";

    static final String DROP_PACKAGE2_PACKAGE = "DROP PACKAGE PACKAGE2";
    static final String DROP_PACKAGE2_EMPREC_TYPE = "DROP TYPE PACKAGE2_EMPREC";
    static final String DROP_A_CONTACT_TYPE = "DROP TYPE A_CONTACT_TYPE";
    static final String DROP_A_PHONE_TYPE_TABLE = "DROP TYPE A_PHONE_TYPE_TABLE";
    static final String DROP_A_PHONE_TYPE_VARRAY = "DROP TYPE A_PHONE_TYPE_VARRAY";
    static final String DROP_A_PHONE_TYPE = "DROP TYPE A_PHONE_TYPE";

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
            runDdl(conn, CREATE_A_PHONE_TYPE_TABLE, ddlDebug);
            runDdl(conn, CREATE_A_PHONE_TYPE_VARRAY, ddlDebug);
            runDdl(conn, CREATE_A_CONTACT_TYPE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE2_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE2_EMPREC_TYPE, ddlDebug);
        }

        String schema = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);

        List<String> procedurePatterns = new ArrayList<>();
        procedurePatterns.add("COPYRECORD");
        procedurePatterns.add("COPYRECORD_FUNC");
        procedurePatterns.add("COPYCOMPLEXRECORD");
        procedurePatterns.add("COPYCOMPLEXRECORD_FUNC");
        procedurePatterns.add("COPYMORECOMPLEXRECORD_FUNC");
        procedurePatterns.add("COPYSUPERCOMPLEXRECORD");

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
            runDdl(conn, DROP_PACKAGE2_EMPREC_TYPE, ddlDebug);
            runDdl(conn, DROP_A_CONTACT_TYPE, ddlDebug);
            runDdl(conn, DROP_A_PHONE_TYPE_TABLE, ddlDebug);
            runDdl(conn, DROP_A_PHONE_TYPE_VARRAY, ddlDebug);
            runDdl(conn, DROP_A_PHONE_TYPE, ddlDebug);
        }
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testJPAPLSQLRecordMetadata() {
        if (dbProcedures == null || dbProcedures.isEmpty()) {
            fail("No types were generated.");
        }
        XMLEntityMappings mappings = null;
        try {
            JPAMetadataGenerator gen = new JPAMetadataGenerator(DEFAULT_PACKAGE_NAME, databasePlatform);
            mappings = gen.generateXmlEntityMappings(dbProcedures);
        } catch (Exception x) {
            x.printStackTrace();
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
                       <orm:named-plsql-stored-procedure-query name="COPYRECORD" procedure-name="PACKAGE2.COPYRECORD">
                          <orm:parameter direction="IN" name="OLDREC" database-type="PACKAGE2.EMPREC"/>
                          <orm:parameter direction="OUT" name="NEWREC" database-type="PACKAGE2.EMPREC"/>
                       </orm:named-plsql-stored-procedure-query>
                       <orm:named-plsql-stored-procedure-query name="COPYCOMPLEXRECORD" procedure-name="PACKAGE2.COPYCOMPLEXRECORD">
                          <orm:parameter direction="IN" name="OLDREC" database-type="PACKAGE2.COMPLEXRECORD"/>
                          <orm:parameter direction="OUT" name="NEWREC" database-type="PACKAGE2.COMPLEXRECORD"/>
                       </orm:named-plsql-stored-procedure-query>
                       <orm:named-plsql-stored-procedure-query name="COPYSUPERCOMPLEXRECORD" procedure-name="PACKAGE2.COPYSUPERCOMPLEXRECORD">
                          <orm:parameter direction="IN" name="OLDREC" database-type="PACKAGE2.SUPERCOMPLEXRECORD"/>
                          <orm:parameter direction="OUT" name="NEWREC" database-type="PACKAGE2.SUPERCOMPLEXRECORD"/>
                       </orm:named-plsql-stored-procedure-query>
                       <orm:named-plsql-stored-function-query name="COPYRECORD_FUNC" function-name="PACKAGE2.COPYRECORD_FUNC">
                          <orm:parameter direction="IN" name="OLDREC" database-type="PACKAGE2.EMPREC"/>
                          <orm:return-parameter name="RESULT" database-type="PACKAGE2.EMPREC"/>
                       </orm:named-plsql-stored-function-query>
                       <orm:named-plsql-stored-function-query name="COPYCOMPLEXRECORD_FUNC" function-name="PACKAGE2.COPYCOMPLEXRECORD_FUNC">
                          <orm:parameter direction="IN" name="OLDREC" database-type="PACKAGE2.COMPLEXRECORD"/>
                          <orm:return-parameter name="RESULT" database-type="PACKAGE2.COMPLEXRECORD"/>
                       </orm:named-plsql-stored-function-query>
                       <orm:named-plsql-stored-function-query name="COPYMORECOMPLEXRECORD_FUNC" function-name="PACKAGE2.COPYMORECOMPLEXRECORD_FUNC">
                          <orm:parameter direction="IN" name="OLDREC" database-type="PACKAGE2.MORECOMPLEXRECORD"/>
                          <orm:return-parameter name="RESULT" database-type="PACKAGE2.MORECOMPLEXRECORD"/>
                       </orm:named-plsql-stored-function-query>
                       <orm:oracle-object name="A_PHONE_TYPE" java-type="metadatagen.A_phone_type">
                          <orm:field name="HOME" database-type="VARCHAR_TYPE"/>
                          <orm:field name="CELL" database-type="VARCHAR_TYPE"/>
                       </orm:oracle-object>
                       <orm:oracle-object name="A_CONTACT_TYPE" java-type="metadatagen.A_contact_type">
                          <orm:field name="ADDRESS" database-type="VARCHAR_TYPE"/>
                          <orm:field name="PHONE" database-type="A_PHONE_TYPE"/>
                       </orm:oracle-object>
                       <orm:oracle-array name="A_PHONE_TYPE_VARRAY" java-type="metadatagen.A_phone_type_varray" nested-type="A_PHONE_TYPE"/>
                       <orm:oracle-array name="A_PHONE_TYPE_TABLE" java-type="metadatagen.A_phone_type_table" nested-type="A_PHONE_TYPE"/>
                       <orm:plsql-record name="PACKAGE2.EMPREC" compatible-type="PACKAGE2_EMPREC" java-type="package2.Emprec">
                          <orm:field name="EMP_ID" database-type="NUMERIC_TYPE"/>
                          <orm:field name="EMP_NAME" database-type="VARCHAR_TYPE"/>
                       </orm:plsql-record>
                       <orm:plsql-record name="PACKAGE2.COMPLEXRECORD" compatible-type="PACKAGE2_COMPLEXRECORD" java-type="package2.Complexrecord">
                          <orm:field name="CR1" database-type="NUMERIC_TYPE"/>
                          <orm:field name="CR2" database-type="A_CONTACT_TYPE"/>
                          <orm:field name="CR3" database-type="A_PHONE_TYPE_VARRAY"/>
                          <orm:field name="CR4" database-type="A_PHONE_TYPE_TABLE"/>
                       </orm:plsql-record>
                       <orm:plsql-record name="PACKAGE2.MORECOMPLEXRECORD" compatible-type="PACKAGE2_MORECOMPLEXRECORD" java-type="package2.Morecomplexrecord">
                          <orm:field name="MCR1" database-type="PACKAGE2.TAB1"/>
                          <orm:field name="MCR2" database-type="PACKAGE2.A_PHONE_TYPE_COL"/>
                       </orm:plsql-record>
                       <orm:plsql-record name="PACKAGE2.SUPERCOMPLEXRECORD" compatible-type="PACKAGE2_SUPERCOMPLEXRECORD" java-type="package2.Supercomplexrecord">
                          <orm:field name="SCR1" database-type="PACKAGE2.EMPREC"/>
                       </orm:plsql-record>
                       <orm:plsql-table name="PACKAGE2.TAB1" compatible-type="PACKAGE2_TAB1" java-type="package2.Tab1" nested-type="VARCHAR_TYPE" nested-table="false"/>
                       <orm:plsql-table name="PACKAGE2.A_PHONE_TYPE_COL" compatible-type="PACKAGE2_A_PHONE_TYPE_COL" java-type="package2.A_phone_type_col" nested-type="A_PHONE_TYPE" nested-table="false"/>
                       <orm:embeddable class="package2.Emprec" access="VIRTUAL">
                          <orm:struct name="PACKAGE2_EMPREC">
                             <orm:field>EMP_ID</orm:field>
                             <orm:field>EMP_NAME</orm:field>
                          </orm:struct>
                          <orm:attributes>
                             <orm:basic name="emp_id" attribute-type="java.math.BigInteger">
                                <orm:column name="EMP_ID"/>
                             </orm:basic>
                             <orm:basic name="emp_name" attribute-type="java.lang.String">
                                <orm:column name="EMP_NAME"/>
                             </orm:basic>
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
                       <orm:embeddable class="metadatagen.A_contact_type" access="VIRTUAL">
                          <orm:struct name="A_CONTACT_TYPE">
                             <orm:field>ADDRESS</orm:field>
                             <orm:field>PHONE</orm:field>
                          </orm:struct>
                          <orm:attributes>
                             <orm:basic name="address" attribute-type="java.lang.String">
                                <orm:column name="ADDRESS"/>
                             </orm:basic>
                             <orm:structure name="phone" attribute-type="metadatagen.A_phone_type" target-class="metadatagen.A_phone_type"/>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="metadatagen.A_phone_type_varray" access="VIRTUAL">
                          <orm:attributes>
                             <orm:array name="items" target-class="metadatagen.A_phone_type" attribute-type="java.util.ArrayList" database-type="A_PHONE_TYPE">
                                <orm:column name="ITEMS"/>
                             </orm:array>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="metadatagen.A_phone_type_table" access="VIRTUAL">
                          <orm:attributes>
                             <orm:array name="items" target-class="metadatagen.A_phone_type" attribute-type="java.util.ArrayList" database-type="A_PHONE_TYPE">
                                <orm:column name="ITEMS"/>
                             </orm:array>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="package2.Complexrecord" access="VIRTUAL">
                          <orm:struct name="PACKAGE2_COMPLEXRECORD">
                             <orm:field>CR1</orm:field>
                             <orm:field>CR2</orm:field>
                             <orm:field>CR3</orm:field>
                             <orm:field>CR4</orm:field>
                          </orm:struct>
                          <orm:attributes>
                             <orm:basic name="cr1" attribute-type="java.math.BigInteger">
                                <orm:column name="CR1"/>
                             </orm:basic>
                             <orm:structure name="cr2" attribute-type="metadatagen.A_contact_type" target-class="metadatagen.A_contact_type"/>
                             <orm:array name="cr3" target-class="A_PHONE_TYPE_VARRAY" attribute-type="java.util.ArrayList" database-type="A_PHONE_TYPE_VARRAY">
                                <orm:column name="CR3"/>
                             </orm:array>
                             <orm:array name="cr4" target-class="metadatagen.A_phone_type" attribute-type="java.util.ArrayList" database-type="A_PHONE_TYPE">
                                <orm:column name="CR4"/>
                             </orm:array>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="package2.Tab1" access="VIRTUAL">
                          <orm:attributes>
                             <orm:array name="items" target-class="PACKAGE2_TAB1" attribute-type="java.util.ArrayList" database-type="PACKAGE2_TAB1">
                                <orm:column name="ITEMS"/>
                             </orm:array>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="package2.A_phone_type_col" access="VIRTUAL">
                          <orm:attributes>
                             <orm:array name="items" target-class="metadatagen.A_phone_type" attribute-type="java.util.ArrayList" database-type="PACKAGE2_A_PHONE_TYPE_COL">
                                <orm:column name="ITEMS"/>
                             </orm:array>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="package2.Morecomplexrecord" access="VIRTUAL">
                          <orm:struct name="PACKAGE2_MORECOMPLEXRECORD">
                             <orm:field>MCR1</orm:field>
                             <orm:field>MCR2</orm:field>
                          </orm:struct>
                          <orm:attributes>
                             <orm:embedded name="mcr1" attribute-type="package2.Tab1"/>
                             <orm:embedded name="mcr2" attribute-type="package2.A_phone_type_col"/>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="package2.Supercomplexrecord" access="VIRTUAL">
                          <orm:struct name="PACKAGE2_SUPERCOMPLEXRECORD">
                             <orm:field>SCR1</orm:field>
                          </orm:struct>
                          <orm:attributes>
                             <orm:embedded name="scr1" attribute-type="package2.Emprec"/>
                          </orm:attributes>
                       </orm:embeddable>
                    </orm:entity-mappings>""";
}
