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
package org.eclipse.persistence.tools.metadata.generation.test.xmltype;


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
 * Tests metadata generation involving XMLType.
 *
 */
public class XMLTypeTestSuite {

    static final String CREATE_DBWS_XML_WRAPPER_TYPE =
        "CREATE OR REPLACE TYPE DBWS_XML_WRAPPER AS OBJECT (" +
            "\nxmltext VARCHAR2(100)" +
        ")";

    static final String CREATE_XMLTYPETESTPKG_PACKAGE =
        "CREATE OR REPLACE PACKAGE XMLTYPETESTPKG AS" +
            "\nPROCEDURE GET_XMLTYPE(W IN DBWS_XML_WRAPPER, X OUT XMLTYPE);" +
            "\nFUNCTION RETURN_XMLTYPE(W IN DBWS_XML_WRAPPER) RETURN XMLTYPE;" +
        "\nEND XMLTYPETESTPKG;";

    static final String DROP_XMLTYPETESTPKG_PACKAGE = "DROP PACKAGE XMLTYPETESTPKG";
    static final String DROP_DBWS_XML_WRAPPER_TYPE = "DROP TYPE DBWS_XML_WRAPPER FORCE";

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
            runDdl(conn, CREATE_DBWS_XML_WRAPPER_TYPE, ddlDebug);
            runDdl(conn, CREATE_XMLTYPETESTPKG_PACKAGE, ddlDebug);
        }

        String schema = System.getProperty(DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);

        List<String> procedurePatterns = new ArrayList<String>();
        procedurePatterns.add("GET_XMLTYPE");
        procedurePatterns.add("RETURN_XMLTYPE");

        // use DatabaseTypeBuilder to generate a list of PackageTypes
        dbTypeBuilder = new DatabaseTypeBuilder();
        dbProcedures = new ArrayList();
        try {
            // process the package
            List<PLSQLPackageType> packages = dbTypeBuilder.buildPackages(conn, schema, "XMLTYPETESTPKG");
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
            runDdl(conn, DROP_XMLTYPETESTPKG_PACKAGE, ddlDebug);
            runDdl(conn, DROP_DBWS_XML_WRAPPER_TYPE, ddlDebug);
        }
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testJPAXMLTypeMetadata() {
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
        "   <orm:named-plsql-stored-procedure-query name=\"GET_XMLTYPE\" procedure-name=\"XMLTYPETESTPKG.GET_XMLTYPE\">\n" +
        "      <orm:parameter direction=\"IN\" name=\"W\" database-type=\"DBWS_XML_WRAPPER\"/>\n" +
        "      <orm:parameter direction=\"OUT\" name=\"X\" database-type=\"XMLType\"/>\n" +
        "   </orm:named-plsql-stored-procedure-query>\n" +
        "   <orm:named-plsql-stored-function-query name=\"RETURN_XMLTYPE\" function-name=\"XMLTYPETESTPKG.RETURN_XMLTYPE\">\n" +
        "      <orm:parameter direction=\"IN\" name=\"W\" database-type=\"DBWS_XML_WRAPPER\"/>\n" +
        "      <orm:return-parameter name=\"RESULT\" database-type=\"XMLType\"/>\n" +
        "   </orm:named-plsql-stored-function-query>\n" +
        "   <orm:oracle-object name=\"DBWS_XML_WRAPPER\" java-type=\"metadatagen.Dbws_xml_wrapper\">\n" +
        "      <orm:field name=\"xmltext\" database-type=\"VARCHAR_TYPE\"/>\n" +
        "   </orm:oracle-object>\n" +
        "   <orm:embeddable class=\"metadatagen.Dbws_xml_wrapper\" access=\"VIRTUAL\">\n" +
        "      <orm:struct name=\"DBWS_XML_WRAPPER\">\n" +
        "         <orm:field>xmltext</orm:field>\n" +
        "      </orm:struct>\n" +
        "      <orm:attributes>\n" +
        "         <orm:basic name=\"xmltext\" attribute-type=\"java.lang.String\">\n" +
        "            <orm:column name=\"xmltext\"/>\n" +
        "         </orm:basic>\n" +
        "      </orm:attributes>\n" +
        "   </orm:embeddable>\n" +
        "</orm:entity-mappings>";
}
