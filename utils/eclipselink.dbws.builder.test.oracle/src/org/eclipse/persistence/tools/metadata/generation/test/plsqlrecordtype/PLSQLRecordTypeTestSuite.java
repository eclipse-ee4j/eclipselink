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
        "CREATE OR REPLACE TYPE A_PHONE_TYPE AS OBJECT (" +
            "\nHOME VARCHAR2(20)," +
            "\nCELL VARCHAR2(20)" +
        "\n)";
    static final String CREATE_A_PHONE_TYPE_TABLE =
        "CREATE OR REPLACE TYPE A_PHONE_TYPE_TABLE AS TABLE OF A_PHONE_TYPE";
    static final String CREATE_A_PHONE_TYPE_VARRAY =
        "CREATE OR REPLACE TYPE A_PHONE_TYPE_VARRAY AS VARRAY(10) OF A_PHONE_TYPE";

    static final String CREATE_A_CONTACT_TYPE =
        "CREATE OR REPLACE TYPE A_CONTACT_TYPE AS OBJECT (" +
            "\nADDRESS VARCHAR2(40)," +
            "\nPHONE A_PHONE_TYPE" +
        "\n)";
    static final String CREATE_PACKAGE2_PACKAGE =
        "CREATE OR REPLACE PACKAGE PACKAGE2 AS" +
            "\nTYPE TAB1 IS TABLE OF VARCHAR(20) INDEX BY BINARY_INTEGER;" +
            "\nTYPE A_PHONE_TYPE_COL IS TABLE OF A_PHONE_TYPE INDEX BY BINARY_INTEGER;" +
            "\nTYPE EMPREC IS RECORD (EMP_ID NUMERIC(4), EMP_NAME VARCHAR2(25));" +
            "\nTYPE COMPLEXRECORD IS RECORD (" +
                "\nCR1 NUMERIC(4)," +
                "\nCR2 A_CONTACT_TYPE," +
                "\nCR3 A_PHONE_TYPE_VARRAY," +
                "\nCR4 A_PHONE_TYPE_TABLE" +
            "\n);" +
            "\nTYPE MORECOMPLEXRECORD IS RECORD (" +
                "\nMCR1 TAB1," +
                "\nMCR2 A_PHONE_TYPE_COL" +
            "\n);" +
            "\nTYPE SUPERCOMPLEXRECORD IS RECORD (" +
                "\nSCR1 EMPREC" +
            "\n);" +
            "\nPROCEDURE COPYRECORD(OLDREC IN EMPREC, NEWREC OUT EMPREC);" +
            "\nFUNCTION COPYRECORD_FUNC(OLDREC IN EMPREC) RETURN EMPREC;" +
            "\nPROCEDURE COPYCOMPLEXRECORD(OLDREC IN COMPLEXRECORD, NEWREC OUT COMPLEXRECORD);" +
            "\nFUNCTION COPYCOMPLEXRECORD_FUNC(OLDREC IN COMPLEXRECORD) RETURN COMPLEXRECORD;" +
            "\nFUNCTION COPYMORECOMPLEXRECORD_FUNC(OLDREC IN MORECOMPLEXRECORD) RETURN MORECOMPLEXRECORD;" +
            "\nPROCEDURE COPYSUPERCOMPLEXRECORD(OLDREC IN SUPERCOMPLEXRECORD, NEWREC OUT SUPERCOMPLEXRECORD);" +
        "\nEND PACKAGE2;";

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

        List<String> procedurePatterns = new ArrayList<String>();
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
        "   <orm:named-plsql-stored-procedure-query name=\"COPYRECORD\" procedure-name=\"PACKAGE2.COPYRECORD\">\n" +
        "      <orm:parameter direction=\"IN\" name=\"OLDREC\" database-type=\"PACKAGE2.EMPREC\"/>\n" +
        "      <orm:parameter direction=\"OUT\" name=\"NEWREC\" database-type=\"PACKAGE2.EMPREC\"/>\n" +
        "   </orm:named-plsql-stored-procedure-query>\n" +
        "   <orm:named-plsql-stored-procedure-query name=\"COPYCOMPLEXRECORD\" procedure-name=\"PACKAGE2.COPYCOMPLEXRECORD\">\n" +
        "      <orm:parameter direction=\"IN\" name=\"OLDREC\" database-type=\"PACKAGE2.COMPLEXRECORD\"/>\n" +
        "      <orm:parameter direction=\"OUT\" name=\"NEWREC\" database-type=\"PACKAGE2.COMPLEXRECORD\"/>\n" +
        "   </orm:named-plsql-stored-procedure-query>\n" +
        "   <orm:named-plsql-stored-procedure-query name=\"COPYSUPERCOMPLEXRECORD\" procedure-name=\"PACKAGE2.COPYSUPERCOMPLEXRECORD\">\n" +
        "      <orm:parameter direction=\"IN\" name=\"OLDREC\" database-type=\"PACKAGE2.SUPERCOMPLEXRECORD\"/>\n" +
        "      <orm:parameter direction=\"OUT\" name=\"NEWREC\" database-type=\"PACKAGE2.SUPERCOMPLEXRECORD\"/>\n" +
        "   </orm:named-plsql-stored-procedure-query>\n" +
        "   <orm:named-plsql-stored-function-query name=\"COPYRECORD_FUNC\" function-name=\"PACKAGE2.COPYRECORD_FUNC\">\n" +
        "      <orm:parameter direction=\"IN\" name=\"OLDREC\" database-type=\"PACKAGE2.EMPREC\"/>\n" +
        "      <orm:return-parameter name=\"RESULT\" database-type=\"PACKAGE2.EMPREC\"/>\n" +
        "   </orm:named-plsql-stored-function-query>\n" +
        "   <orm:named-plsql-stored-function-query name=\"COPYCOMPLEXRECORD_FUNC\" function-name=\"PACKAGE2.COPYCOMPLEXRECORD_FUNC\">\n" +
        "      <orm:parameter direction=\"IN\" name=\"OLDREC\" database-type=\"PACKAGE2.COMPLEXRECORD\"/>\n" +
        "      <orm:return-parameter name=\"RESULT\" database-type=\"PACKAGE2.COMPLEXRECORD\"/>\n" +
        "   </orm:named-plsql-stored-function-query>\n" +
        "   <orm:named-plsql-stored-function-query name=\"COPYMORECOMPLEXRECORD_FUNC\" function-name=\"PACKAGE2.COPYMORECOMPLEXRECORD_FUNC\">\n" +
        "      <orm:parameter direction=\"IN\" name=\"OLDREC\" database-type=\"PACKAGE2.MORECOMPLEXRECORD\"/>\n" +
        "      <orm:return-parameter name=\"RESULT\" database-type=\"PACKAGE2.MORECOMPLEXRECORD\"/>\n" +
        "   </orm:named-plsql-stored-function-query>\n" +
        "   <orm:oracle-object name=\"A_PHONE_TYPE\" java-type=\"metadatagen.A_phone_type\">\n" +
        "      <orm:field name=\"HOME\" database-type=\"VARCHAR_TYPE\"/>\n" +
        "      <orm:field name=\"CELL\" database-type=\"VARCHAR_TYPE\"/>\n" +
        "   </orm:oracle-object>\n" +
        "   <orm:oracle-object name=\"A_CONTACT_TYPE\" java-type=\"metadatagen.A_contact_type\">\n" +
        "      <orm:field name=\"ADDRESS\" database-type=\"VARCHAR_TYPE\"/>\n" +
        "      <orm:field name=\"PHONE\" database-type=\"A_PHONE_TYPE\"/>\n" +
        "   </orm:oracle-object>\n" +
        "   <orm:oracle-array name=\"A_PHONE_TYPE_VARRAY\" java-type=\"metadatagen.A_phone_type_varray\" nested-type=\"A_PHONE_TYPE\"/>\n" +
        "   <orm:oracle-array name=\"A_PHONE_TYPE_TABLE\" java-type=\"metadatagen.A_phone_type_table\" nested-type=\"A_PHONE_TYPE\"/>\n" +
        "   <orm:plsql-record name=\"PACKAGE2.EMPREC\" compatible-type=\"PACKAGE2_EMPREC\" java-type=\"package2.Emprec\">\n" +
        "      <orm:field name=\"EMP_ID\" database-type=\"NUMERIC_TYPE\"/>\n" +
        "      <orm:field name=\"EMP_NAME\" database-type=\"VARCHAR_TYPE\"/>\n" +
        "   </orm:plsql-record>\n" +
        "   <orm:plsql-record name=\"PACKAGE2.COMPLEXRECORD\" compatible-type=\"PACKAGE2_COMPLEXRECORD\" java-type=\"package2.Complexrecord\">\n" +
        "      <orm:field name=\"CR1\" database-type=\"NUMERIC_TYPE\"/>\n" +
        "      <orm:field name=\"CR2\" database-type=\"A_CONTACT_TYPE\"/>\n" +
        "      <orm:field name=\"CR3\" database-type=\"A_PHONE_TYPE_VARRAY\"/>\n" +
        "      <orm:field name=\"CR4\" database-type=\"A_PHONE_TYPE_TABLE\"/>\n" +
        "   </orm:plsql-record>\n" +
        "   <orm:plsql-record name=\"PACKAGE2.MORECOMPLEXRECORD\" compatible-type=\"PACKAGE2_MORECOMPLEXRECORD\" java-type=\"package2.Morecomplexrecord\">\n" +
        "      <orm:field name=\"MCR1\" database-type=\"PACKAGE2.TAB1\"/>\n" +
        "      <orm:field name=\"MCR2\" database-type=\"PACKAGE2.A_PHONE_TYPE_COL\"/>\n" +
        "   </orm:plsql-record>\n" +
        "   <orm:plsql-record name=\"PACKAGE2.SUPERCOMPLEXRECORD\" compatible-type=\"PACKAGE2_SUPERCOMPLEXRECORD\" java-type=\"package2.Supercomplexrecord\">\n" +
        "      <orm:field name=\"SCR1\" database-type=\"PACKAGE2.EMPREC\"/>\n" +
        "   </orm:plsql-record>\n" +
        "   <orm:plsql-table name=\"PACKAGE2.TAB1\" compatible-type=\"PACKAGE2_TAB1\" java-type=\"package2.Tab1\" nested-type=\"VARCHAR_TYPE\" nested-table=\"false\"/>\n" +
        "   <orm:plsql-table name=\"PACKAGE2.A_PHONE_TYPE_COL\" compatible-type=\"PACKAGE2_A_PHONE_TYPE_COL\" java-type=\"package2.A_phone_type_col\" nested-type=\"A_PHONE_TYPE\" nested-table=\"false\"/>\n" +
        "   <orm:embeddable class=\"package2.Emprec\" access=\"VIRTUAL\">\n" +
        "      <orm:struct name=\"PACKAGE2_EMPREC\">\n" +
        "         <orm:field>EMP_ID</orm:field>\n" +
        "         <orm:field>EMP_NAME</orm:field>\n" +
        "      </orm:struct>\n" +
        "      <orm:attributes>\n" +
        "         <orm:basic name=\"emp_id\" attribute-type=\"java.math.BigInteger\">\n" +
        "            <orm:column name=\"EMP_ID\"/>\n" +
        "         </orm:basic>\n" +
        "         <orm:basic name=\"emp_name\" attribute-type=\"java.lang.String\">\n" +
        "            <orm:column name=\"EMP_NAME\"/>\n" +
        "         </orm:basic>\n" +
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
        "   <orm:embeddable class=\"metadatagen.A_contact_type\" access=\"VIRTUAL\">\n" +
        "      <orm:struct name=\"A_CONTACT_TYPE\">\n" +
        "         <orm:field>ADDRESS</orm:field>\n" +
        "         <orm:field>PHONE</orm:field>\n" +
        "      </orm:struct>\n" +
        "      <orm:attributes>\n" +
        "         <orm:basic name=\"address\" attribute-type=\"java.lang.String\">\n" +
        "            <orm:column name=\"ADDRESS\"/>\n" +
        "         </orm:basic>\n" +
        "         <orm:structure name=\"phone\" attribute-type=\"metadatagen.A_phone_type\" target-class=\"metadatagen.A_phone_type\"/>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "   <orm:embeddable class=\"metadatagen.A_phone_type_varray\" access=\"VIRTUAL\">\n" +
        "      <orm:attributes>\n" +
        "         <orm:array name=\"items\" target-class=\"metadatagen.A_phone_type\" attribute-type=\"java.util.ArrayList\" database-type=\"A_PHONE_TYPE\">\n" +
        "            <orm:column name=\"ITEMS\"/>\n" +
        "         </orm:array>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "   <orm:embeddable class=\"metadatagen.A_phone_type_table\" access=\"VIRTUAL\">\n" +
        "      <orm:attributes>\n" +
        "         <orm:array name=\"items\" target-class=\"metadatagen.A_phone_type\" attribute-type=\"java.util.ArrayList\" database-type=\"A_PHONE_TYPE\">\n" +
        "            <orm:column name=\"ITEMS\"/>\n" +
        "         </orm:array>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "   <orm:embeddable class=\"package2.Complexrecord\" access=\"VIRTUAL\">\n" +
        "      <orm:struct name=\"PACKAGE2_COMPLEXRECORD\">\n" +
        "         <orm:field>CR1</orm:field>\n" +
        "         <orm:field>CR2</orm:field>\n" +
        "         <orm:field>CR3</orm:field>\n" +
        "         <orm:field>CR4</orm:field>\n" +
        "      </orm:struct>\n" +
        "      <orm:attributes>\n" +
        "         <orm:basic name=\"cr1\" attribute-type=\"java.math.BigInteger\">\n" +
        "            <orm:column name=\"CR1\"/>\n" +
        "         </orm:basic>\n" +
        "         <orm:structure name=\"cr2\" attribute-type=\"metadatagen.A_contact_type\" target-class=\"metadatagen.A_contact_type\"/>\n" +
        "         <orm:array name=\"cr3\" target-class=\"A_PHONE_TYPE_VARRAY\" attribute-type=\"java.util.ArrayList\" database-type=\"A_PHONE_TYPE_VARRAY\">\n" +
        "            <orm:column name=\"CR3\"/>\n" +
        "         </orm:array>\n" +
        "         <orm:array name=\"cr4\" target-class=\"metadatagen.A_phone_type\" attribute-type=\"java.util.ArrayList\" database-type=\"A_PHONE_TYPE\">\n" +
        "            <orm:column name=\"CR4\"/>\n" +
        "         </orm:array>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "   <orm:embeddable class=\"package2.Tab1\" access=\"VIRTUAL\">\n" +
        "      <orm:attributes>\n" +
        "         <orm:array name=\"items\" target-class=\"PACKAGE2_TAB1\" attribute-type=\"java.util.ArrayList\" database-type=\"PACKAGE2_TAB1\">\n" +
        "            <orm:column name=\"ITEMS\"/>\n" +
        "         </orm:array>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "   <orm:embeddable class=\"package2.A_phone_type_col\" access=\"VIRTUAL\">\n" +
        "      <orm:attributes>\n" +
        "         <orm:array name=\"items\" target-class=\"metadatagen.A_phone_type\" attribute-type=\"java.util.ArrayList\" database-type=\"PACKAGE2_A_PHONE_TYPE_COL\">\n" +
        "            <orm:column name=\"ITEMS\"/>\n" +
        "         </orm:array>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "   <orm:embeddable class=\"package2.Morecomplexrecord\" access=\"VIRTUAL\">\n" +
        "      <orm:struct name=\"PACKAGE2_MORECOMPLEXRECORD\">\n" +
        "         <orm:field>MCR1</orm:field>\n" +
        "         <orm:field>MCR2</orm:field>\n" +
        "      </orm:struct>\n" +
        "      <orm:attributes>\n" +
        "         <orm:embedded name=\"mcr1\" attribute-type=\"package2.Tab1\"/>\n" +
        "         <orm:embedded name=\"mcr2\" attribute-type=\"package2.A_phone_type_col\"/>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "   <orm:embeddable class=\"package2.Supercomplexrecord\" access=\"VIRTUAL\">\n" +
        "      <orm:struct name=\"PACKAGE2_SUPERCOMPLEXRECORD\">\n" +
        "         <orm:field>SCR1</orm:field>\n" +
        "      </orm:struct>\n" +
        "      <orm:attributes>\n" +
        "         <orm:embedded name=\"scr1\" attribute-type=\"package2.Emprec\"/>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "</orm:entity-mappings>";
}
