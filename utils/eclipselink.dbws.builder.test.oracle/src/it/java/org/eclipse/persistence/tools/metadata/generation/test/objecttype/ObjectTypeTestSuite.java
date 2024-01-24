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
package org.eclipse.persistence.tools.metadata.generation.test.objecttype;


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
import java.util.ArrayList;
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
public class ObjectTypeTestSuite {
    static final String CREATE_PHONE_TYPE =
            """
                    CREATE OR REPLACE TYPE DBWS_PHONE_TYPE AS OBJECT (
                    HOME VARCHAR2(20),
                    CELL VARCHAR2(20)
                    )""";
    static final String CREATE_A_CONTACT_TYPE =
            """
                    CREATE OR REPLACE TYPE A_CONTACT_TYPE AS OBJECT (
                    ADDRESS VARCHAR2(40),
                    PHONE DBWS_PHONE_TYPE
                    )""";
    static final String CREATE_PHONE_TYPE_PROC =
            """
                    CREATE OR REPLACE PROCEDURE CREATE_PHONE_TYPE(HOME_NUMBER IN VARCHAR2, CELL_NUMBER IN VARCHAR2, RESULT OUT DBWS_PHONE_TYPE) AS
                    BEGIN
                    RESULT := DBWS_PHONE_TYPE(HOME_NUMBER, CELL_NUMBER);
                    END CREATE_PHONE_TYPE;""";
    static final String CREATE_CONTACT_TYPE_PROC =
            """
                    CREATE OR REPLACE PROCEDURE CREATE_CONTACT_TYPE(ADDRESS IN VARCHAR2, PHONE IN DBWS_PHONE_TYPE, RESULT OUT A_CONTACT_TYPE) AS
                    BEGIN
                    RESULT := A_CONTACT_TYPE(ADDRESS, PHONE);
                    END CREATE_CONTACT_TYPE;""";

    static final String DROP_CREATE_PHONE_TYPE_PROC =
        "DROP PROCEDURE CREATE_PHONE_TYPE";
    static final String DROP_CREATE_CONTACT_TYPE_PROC =
        "DROP PROCEDURE CREATE_CONTACT_TYPE";
    static final String DROP_PHONE_TYPE =
        "DROP TYPE DBWS_PHONE_TYPE FORCE";
    static final String DROP_CONTACT_TYPE =
        "DROP TYPE A_CONTACT_TYPE FORCE";

    static boolean ddlCreate = false;
    static boolean ddlDrop = false;
    static boolean ddlDebug = false;

    @SuppressWarnings("rawtypes")
    static List dbProcedures;
    static DatabaseTypeBuilder dbTypeBuilder;

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
            runDdl(conn, CREATE_PHONE_TYPE, ddlDebug);
            runDdl(conn, CREATE_A_CONTACT_TYPE, ddlDebug);
            runDdl(conn, CREATE_PHONE_TYPE_PROC, ddlDebug);
            runDdl(conn, CREATE_CONTACT_TYPE_PROC, ddlDebug);
        }

        ArrayList<String> schemas    = new ArrayList<>();
        ArrayList<String> procedures = new ArrayList<>();

        schemas.add("TOPLEVEL");
        procedures.add("CREATE_PHONE_TYPE");
        schemas.add("TOPLEVEL");
        procedures.add("CREATE_CONTACT_TYPE");

        // use DatabaseTypeBuilder to generate a list of ProcedureTypes
        dbTypeBuilder = new DatabaseTypeBuilder();
        try {
            dbProcedures = dbTypeBuilder.buildProcedures(conn, schemas, procedures);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_CREATE_PHONE_TYPE_PROC, ddlDebug);
            runDdl(conn, DROP_CREATE_CONTACT_TYPE_PROC, ddlDebug);
            runDdl(conn, DROP_CONTACT_TYPE, ddlDebug);
            runDdl(conn, DROP_PHONE_TYPE, ddlDebug);
        }
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testJPAObjectMetadata() {
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
                       <orm:named-stored-procedure-query name="CREATE_CONTACT_TYPE" procedure-name="CREATE_CONTACT_TYPE" returns-result-set="false">
                          <orm:parameter mode="IN" name="ADDRESS" type="java.lang.String" class="java.lang.String" jdbc-type="12" jdbc-type-name="VARCHAR"/>
                          <orm:parameter mode="IN" name="PHONE" type="metadatagen.Dbws_phone_type" class="metadatagen.Dbws_phone_type" jdbc-type="2002" jdbc-type-name="DBWS_PHONE_TYPE"/>
                          <orm:parameter mode="OUT" name="RESULT" type="metadatagen.A_contact_type" class="metadatagen.A_contact_type" jdbc-type="2002" jdbc-type-name="A_CONTACT_TYPE"/>
                       </orm:named-stored-procedure-query>
                       <orm:named-stored-procedure-query name="CREATE_PHONE_TYPE" procedure-name="CREATE_PHONE_TYPE" returns-result-set="false">
                          <orm:parameter mode="IN" name="HOME_NUMBER" type="java.lang.String" class="java.lang.String" jdbc-type="12" jdbc-type-name="VARCHAR"/>
                          <orm:parameter mode="IN" name="CELL_NUMBER" type="java.lang.String" class="java.lang.String" jdbc-type="12" jdbc-type-name="VARCHAR"/>
                          <orm:parameter mode="OUT" name="RESULT" type="metadatagen.Dbws_phone_type" class="metadatagen.Dbws_phone_type" jdbc-type="2002" jdbc-type-name="DBWS_PHONE_TYPE"/>
                       </orm:named-stored-procedure-query>
                       <orm:oracle-object name="DBWS_PHONE_TYPE" java-type="metadatagen.Dbws_phone_type">
                          <orm:field name="HOME" database-type="VARCHAR_TYPE"/>
                          <orm:field name="CELL" database-type="VARCHAR_TYPE"/>
                       </orm:oracle-object>
                       <orm:oracle-object name="A_CONTACT_TYPE" java-type="metadatagen.A_contact_type">
                          <orm:field name="ADDRESS" database-type="VARCHAR_TYPE"/>
                          <orm:field name="PHONE" database-type="DBWS_PHONE_TYPE"/>
                       </orm:oracle-object>
                       <orm:embeddable class="metadatagen.Dbws_phone_type" access="VIRTUAL">
                          <orm:struct name="DBWS_PHONE_TYPE">
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
                             <orm:structure name="phone" attribute-type="metadatagen.Dbws_phone_type" target-class="metadatagen.Dbws_phone_type"/>
                          </orm:attributes>
                       </orm:embeddable>
                    </orm:entity-mappings>""";
}
