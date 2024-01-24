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
package org.eclipse.persistence.tools.metadata.generation.test.tabletype;


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
 * Tests metadata generation from a TableType.
 */
public class TableTypeTestSuite {
    static final String CREATE_TABLETYPE_TABLE =
            """
                    CREATE TABLE TABLETYPE (
                    EMPNO DECIMAL(4,0) NOT NULL,
                    ENAME VARCHAR(10),
                    HIREDATE DATE,
                    PRIMARY KEY (EMPNO)
                    )""";
    static final String DROP_TABLETYPE_TABLE = "DROP TABLE TABLETYPE";

    static boolean ddlCreate = false;
    static boolean ddlDrop = false;
    static boolean ddlDebug = false;

    @SuppressWarnings("rawtypes")
    static List dbTables;
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
            runDdl(conn, CREATE_TABLETYPE_TABLE, ddlDebug);
        }

        String schema = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);

        // use DatabaseTypeBuilder to generate a list of TableTypes
        dbTypeBuilder = new DatabaseTypeBuilder();
        try {
            dbTables = dbTypeBuilder.buildTables(conn, schema, "TABLETYPE");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_TABLETYPE_TABLE, ddlDebug);
        }
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testJPATableMetadata() {
        if (dbTables == null || dbTables.isEmpty()) {
            fail("No types were generated.");
        }
        XMLEntityMappings mappings = null;
        try {
            JPAMetadataGenerator gen = new JPAMetadataGenerator(DEFAULT_PACKAGE_NAME, databasePlatform);
            mappings = gen.generateXmlEntityMappings(dbTables);
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
        Document controlDoc = xmlParser.parse(new StringReader(tableMetadata));
        removeEmptyTextNodes(controlDoc);
        assertTrue("Metadata comparison failed.  Expected:\n" + documentToString(controlDoc) + "\nActual\n" + documentToString(testDoc), comparer.isNodeEqual(controlDoc, testDoc));
    }

    static final String tableMetadata =
            """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <orm:entity-mappings xsi:schemaLocation="http://www.eclipse.org/eclipselink/xsds/persistence/orm org/eclipse/persistence/jpa/eclipselink_orm_2_5.xsd"     xmlns:orm="http://www.eclipse.org/eclipselink/xsds/persistence/orm"      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                       <orm:entity class="metadatagen.Tabletype" access="VIRTUAL">
                          <orm:table name="TABLETYPE"/>
                          <orm:attributes>
                             <orm:id name="empno" attribute-type="java.math.BigInteger">
                                <orm:column name="EMPNO"/>
                             </orm:id>
                             <orm:basic name="ename" attribute-type="java.lang.String">
                                <orm:column name="ENAME"/>
                             </orm:basic>
                             <orm:basic name="hiredate" attribute-type="java.sql.Date">
                                <orm:column name="HIREDATE"/>
                             </orm:basic>
                          </orm:attributes>
                       </orm:entity>
                    </orm:entity-mappings>""";

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testJPATableMetadataWithCRUDOps() {
        if (dbTables == null || dbTables.isEmpty()) {
            fail("No types were generated.");
        }
        XMLEntityMappings mappings = null;
        try {
            JPAMetadataGenerator gen = new JPAMetadataGenerator(DEFAULT_PACKAGE_NAME, databasePlatform, true);
            mappings = gen.generateXmlEntityMappings(dbTables);
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
        Document controlDoc = xmlParser.parse(new StringReader(crudtableMetadata));
        removeEmptyTextNodes(controlDoc);
        assertTrue("Metadata comparison failed.  Expected:\n" + documentToString(controlDoc) + "\nActual\n" + documentToString(testDoc), comparer.isNodeEqual(controlDoc, testDoc));
    }

    static final String crudtableMetadata =
            """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <orm:entity-mappings xsi:schemaLocation="http://www.eclipse.org/eclipselink/xsds/persistence/orm org/eclipse/persistence/jpa/eclipselink_orm_2_5.xsd"     xmlns:orm="http://www.eclipse.org/eclipselink/xsds/persistence/orm"      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                       <orm:entity class="metadatagen.Tabletype" access="VIRTUAL">
                          <orm:table name="TABLETYPE"/>
                          <orm:named-native-query name="findByPrimaryKey_TabletypeType" result-class="metadatagen.Tabletype">
                             <orm:query>SELECT * FROM TABLETYPE WHERE (EMPNO = ?1)</orm:query>
                          </orm:named-native-query>
                          <orm:named-native-query name="findAll_TabletypeType" result-class="metadatagen.Tabletype">
                             <orm:query>SELECT * FROM TABLETYPE</orm:query>
                          </orm:named-native-query>
                          <orm:named-native-query name="create_TabletypeType">
                             <orm:query>INSERT INTO TABLETYPE (EMPNO, ENAME, HIREDATE) VALUES (?, ?, ?)</orm:query>
                          </orm:named-native-query>
                          <orm:named-native-query name="update_TabletypeType">
                             <orm:query>UPDATE TABLETYPE SET ENAME = ?2, HIREDATE = ?3 WHERE (EMPNO = ?1)</orm:query>
                          </orm:named-native-query>
                          <orm:named-native-query name="delete_TabletypeType">
                             <orm:query>DELETE FROM TABLETYPE WHERE (EMPNO = ?1)</orm:query>
                          </orm:named-native-query>
                          <orm:attributes>
                             <orm:id name="empno" attribute-type="java.math.BigInteger">
                                <orm:column name="EMPNO"/>
                             </orm:id>
                             <orm:basic name="ename" attribute-type="java.lang.String">
                                <orm:column name="ENAME"/>
                             </orm:basic>
                             <orm:basic name="hiredate" attribute-type="java.sql.Date">
                                <orm:column name="HIREDATE"/>
                             </orm:basic>
                          </orm:attributes>
                       </orm:entity>
                    </orm:entity-mappings>""";
}
