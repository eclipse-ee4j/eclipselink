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
package org.eclipse.persistence.tools.metadata.generation.test.typerowtype;


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
import org.eclipse.persistence.tools.oracleddl.metadata.CompositeDatabaseType;
import org.eclipse.persistence.tools.oracleddl.metadata.PLSQLPackageType;
import org.eclipse.persistence.tools.oracleddl.metadata.ProcedureType;
import org.eclipse.persistence.tools.oracleddl.metadata.TableType;
import org.eclipse.persistence.tools.oracleddl.parser.ParseException;
import org.eclipse.persistence.tools.oracleddl.util.DatabaseTypeBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 * Tests metadata generation involving %TYPE and %ROWTYPE.
 */
public class TypeRowTypeTestSuite {
    static final String CREATE_EMPTYPE_TABLE =
            """
                    CREATE TABLE EMPTYPEX (
                    EMPNO NUMERIC(4) NOT NULL,
                    ENAME VARCHAR(25),
                    PRIMARY KEY (EMPNO)
                    )""";

    static final String CREATE_EMP_RECORD_PACKAGE =
            """
                    create or replace PACKAGE EMP_RECORD_PACKAGE AS
                    type EmpRec is record (emp_id   EMPTYPEX.EMPNO%TYPE,
                    emp_name EMPTYPEX.ENAME%TYPE
                    );
                    function get_emp_record (pId in number) return EmpRec;
                    END EMP_RECORD_PACKAGE;""";

    static final String CREATE_ROWTYPE_TEST_PACKAGE =
            """
                    CREATE OR REPLACE PACKAGE RTYPE_PKG AS
                    PROCEDURE testProc(PARAM1 IN INTEGER, PARAM2 OUT EMPTYPEX%ROWTYPE);
                    FUNCTION testFunc(PARAM1 IN INTEGER) RETURN EMPTYPEX%ROWTYPE;
                    END RTYPE_PKG;""";

    // shadow JDBC type for PL/SQL record
    static final String CREATE_EMPREC_TYPE =
        "CREATE OR REPLACE TYPE EMP_RECORD_PACKAGE_EmpRec AS OBJECT(EMP_ID NUMERIC(4), EMP_NAME VARCHAR2(25));";

    static final String DROP_EMPTYPE_TABLE = "DROP TABLE EMPTYPEX";
    static final String DROP_EMP_RECORD_PACKAGE = "DROP PACKAGE EMP_RECORD_PACKAGE";
    static final String DROP_EMPREC_TYPE = "DROP TYPE EMP_RECORD_PACKAGE_EMPREC FORCE";
    static final String DROP_RTYPE_PKG = "DROP PACKAGE RTYPE_PKG";

    static boolean ddlCreate = false;
    static boolean ddlDrop = false;
    static boolean ddlDebug = false;

    static List<CompositeDatabaseType> dbTypes;
    static DatabaseTypeBuilder dbTypeBuilder;

    @SuppressWarnings({ "unchecked", "rawtypes" })
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
            runDdl(conn, CREATE_EMPTYPE_TABLE, ddlDebug);
            runDdl(conn, CREATE_EMPREC_TYPE, ddlDebug);
            runDdl(conn, CREATE_EMP_RECORD_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_ROWTYPE_TEST_PACKAGE, ddlDebug);
        }

        String schema = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);

        List<String> procedurePatterns = new ArrayList<>();
        procedurePatterns.add("get_emp_record");
        procedurePatterns.add("testProc");
        procedurePatterns.add("testFunc");

        // use DatabaseTypeBuilder to generate a lists of TableTypes and PackageTypes
        dbTypeBuilder = new DatabaseTypeBuilder();
        try {
            // process the table
            List<TableType> dbTables = dbTypeBuilder.buildTables(conn, schema, "EMPTYPEX");

            // process the "EMP_RECORD_PACKAGE" package
            List<ProcedureType> empRecPkgProcedures = new ArrayList<>();
            List<PLSQLPackageType> packages = dbTypeBuilder.buildPackages(conn, schema, "EMP_RECORD_PACKAGE");
            for (PLSQLPackageType pkgType : packages) {
                // now get the desired procedures/functions from the processed package
                for (ProcedureType procType : pkgType.getProcedures()) {
                    if (procedurePatterns.contains(procType.getProcedureName())) {
                        empRecPkgProcedures.add(procType);
                    }
                }
            }
            // process the "RTYPE_PKG" package
            List<ProcedureType> rtypePkgProcedures = new ArrayList<>();
            packages = dbTypeBuilder.buildPackages(conn, schema, "RTYPE_PKG");
            for (PLSQLPackageType pkgType : packages) {
                // now get the desired procedures/functions from the processed package
                for (ProcedureType procType : pkgType.getProcedures()) {
                    if (procedurePatterns.contains(procType.getProcedureName())) {
                        rtypePkgProcedures.add(procType);
                    }
                }
            }

            // combine tables and procedures to pass to the metadata generator
            dbTypes = new ArrayList();
            dbTypes.addAll(dbTables);
            dbTypes.addAll(empRecPkgProcedures);
            dbTypes.addAll(rtypePkgProcedures);
        } catch (ParseException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred: " + e.getMessage());
        }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_EMP_RECORD_PACKAGE, ddlDebug);
            runDdl(conn, DROP_EMPREC_TYPE, ddlDebug);
            runDdl(conn, DROP_EMPTYPE_TABLE, ddlDebug);
            runDdl(conn, DROP_RTYPE_PKG, ddlDebug);
        }
    }

    @Test
    public void testJPARowTypeMetadata() {
        if (dbTypes == null || dbTypes.isEmpty()) {
            fail("No types were generated.");
        }
        XMLEntityMappings mappings = null;
        try {
            JPAMetadataGenerator gen = new JPAMetadataGenerator(DEFAULT_PACKAGE_NAME, databasePlatform);
            mappings = gen.generateXmlEntityMappings(dbTypes);
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
        Document controlDoc = xmlParser.parse(new StringReader(mappingMetadata));
        removeEmptyTextNodes(controlDoc);
        assertTrue("Metadata comparison failed.  Expected:\n" + documentToString(controlDoc) + "\nActual\n" + documentToString(testDoc), comparer.isNodeEqual(controlDoc, testDoc));
    }

    static final String mappingMetadata =
            """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <orm:entity-mappings xsi:schemaLocation="http://www.eclipse.org/eclipselink/xsds/persistence/orm org/eclipse/persistence/jpa/eclipselink_orm_2_5.xsd"     xmlns:orm="http://www.eclipse.org/eclipselink/xsds/persistence/orm"      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                       <orm:named-plsql-stored-procedure-query name="testProc" procedure-name="RTYPE_PKG.testProc">
                          <orm:parameter direction="IN" name="PARAM1" database-type="INTEGER_TYPE"/>
                          <orm:parameter direction="OUT" name="PARAM2" database-type="EMPTYPEX%ROWTYPE"/>
                       </orm:named-plsql-stored-procedure-query>
                       <orm:named-plsql-stored-function-query name="get_emp_record" function-name="EMP_RECORD_PACKAGE.get_emp_record">
                          <orm:parameter direction="IN" name="pId" database-type="NUMERIC_TYPE"/>
                          <orm:return-parameter name="RESULT" database-type="EMP_RECORD_PACKAGE.EmpRec"/>
                       </orm:named-plsql-stored-function-query>
                       <orm:named-plsql-stored-function-query name="testFunc" function-name="RTYPE_PKG.testFunc">
                          <orm:parameter direction="IN" name="PARAM1" database-type="INTEGER_TYPE"/>
                          <orm:return-parameter name="RESULT" database-type="EMPTYPEX%ROWTYPE"/>
                       </orm:named-plsql-stored-function-query>
                       <orm:plsql-record name="EMP_RECORD_PACKAGE.EmpRec" compatible-type="EMP_RECORD_PACKAGE_EmpRec" java-type="emp_record_package.Emprec">
                          <orm:field name="emp_id" database-type="NUMERIC_TYPE"/>
                          <orm:field name="emp_name" database-type="VARCHAR_TYPE"/>
                       </orm:plsql-record>
                       <orm:plsql-record name="EMPTYPEX%ROWTYPE" compatible-type="EMPTYPEX_ROWTYPE" java-type="Emptypex_rowtype">
                          <orm:field name="EMPNO" database-type="NUMERIC_TYPE"/>
                          <orm:field name="ENAME" database-type="VARCHAR_TYPE"/>
                       </orm:plsql-record>
                       <orm:entity class="metadatagen.Emptypex" access="VIRTUAL">
                          <orm:table name="EMPTYPEX"/>
                          <orm:attributes>
                             <orm:id name="empno" attribute-type="java.math.BigInteger">
                                <orm:column name="EMPNO"/>
                             </orm:id>
                             <orm:basic name="ename" attribute-type="java.lang.String">
                                <orm:column name="ENAME"/>
                             </orm:basic>
                          </orm:attributes>
                       </orm:entity>
                       <orm:embeddable class="emp_record_package.Emprec" access="VIRTUAL">
                          <orm:struct name="EMP_RECORD_PACKAGE_EmpRec">
                             <orm:field>emp_id</orm:field>
                             <orm:field>emp_name</orm:field>
                          </orm:struct>
                          <orm:attributes>
                             <orm:basic name="emp_id" attribute-type="java.math.BigInteger">
                                <orm:column name="emp_id"/>
                             </orm:basic>
                             <orm:basic name="emp_name" attribute-type="java.lang.String">
                                <orm:column name="emp_name"/>
                             </orm:basic>
                          </orm:attributes>
                       </orm:embeddable>
                       <orm:embeddable class="Emptypex_rowtype" access="VIRTUAL">
                          <orm:struct name="EMPTYPEX_ROWTYPE">
                             <orm:field>EMPNO</orm:field>
                             <orm:field>ENAME</orm:field>
                          </orm:struct>
                          <orm:attributes>
                             <orm:basic name="empno" attribute-type="java.math.BigInteger">
                                <orm:column name="EMPNO"/>
                             </orm:basic>
                             <orm:basic name="ename" attribute-type="java.lang.String">
                                <orm:column name="ENAME"/>
                             </orm:basic>
                          </orm:attributes>
                       </orm:embeddable>
                    </orm:entity-mappings>""";
}
