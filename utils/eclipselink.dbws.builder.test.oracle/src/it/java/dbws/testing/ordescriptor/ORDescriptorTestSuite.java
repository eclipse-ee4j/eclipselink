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
//     David McCann - November 16, 2011 - 2.4 - Initial implementation
package dbws.testing.ordescriptor;

//javase imports
import java.util.List;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.structures.ArrayMapping;
import org.eclipse.persistence.mappings.structures.ObjectArrayMapping;
import org.eclipse.persistence.tools.dbws.BaseDBWSBuilderHelper;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests OXM Descriptor building.
 *
 */
public class ORDescriptorTestSuite extends DBWSTestSuite {
    public static final String TBL1_COMPATIBLETYPE = "ORPACKAGE_TBL1";
    public static final String TBL1_DATABASETYPE = "ORPACKAGE.TBL1";
    public static final String TBL1_DESCRIPTOR_ALIAS = TBL1_COMPATIBLETYPE.toLowerCase();
    public static final String TBL1_DESCRIPTOR_JAVACLASSNAME = TBL1_DATABASETYPE.toLowerCase() + COLLECTION_WRAPPER_SUFFIX;

    public static final String TBL2_COMPATIBLETYPE = "ORPACKAGE_TBL2";
    public static final String TBL2_DATABASETYPE = "ORPACKAGE.TBL2";
    public static final String TBL2_DESCRIPTOR_ALIAS = TBL2_COMPATIBLETYPE.toLowerCase();
    public static final String TBL2_DESCRIPTOR_JAVACLASSNAME = TBL2_DATABASETYPE.toLowerCase() + COLLECTION_WRAPPER_SUFFIX;

    public static final String TBL3_COMPATIBLETYPE = "ORPACKAGE_TBL3";
    public static final String TBL3_DATABASETYPE = "ORPACKAGE.TBL3";
    public static final String TBL3_DESCRIPTOR_ALIAS = TBL3_COMPATIBLETYPE.toLowerCase();
    public static final String TBL3_DESCRIPTOR_JAVACLASSNAME = TBL3_DATABASETYPE.toLowerCase() + COLLECTION_WRAPPER_SUFFIX;

    public static final String TBL4_COMPATIBLETYPE = "ORPACKAGE_TBL4";
    public static final String TBL4_DATABASETYPE = "ORPACKAGE.TBL4";
    public static final String TBL4_DESCRIPTOR_ALIAS = TBL4_COMPATIBLETYPE.toLowerCase();
    public static final String TBL4_DESCRIPTOR_JAVACLASSNAME = TBL4_DATABASETYPE.toLowerCase() + COLLECTION_WRAPPER_SUFFIX;

    public static final String ARECORD_COMPATIBLETYPE = "ORPACKAGE_ARECORD";
    public static final String ARECORD_DATABASETYPE = "ORPACKAGE.ARECORD";
    public static final String ARECORD_DESCRIPTOR_ALIAS = ARECORD_COMPATIBLETYPE.toLowerCase();
    public static final String ARECORD_DESCRIPTOR_JAVACLASSNAME = ARECORD_DATABASETYPE.toLowerCase();

    static final String CREATE_PACKAGE_ORPACKAGE =
            """
                    CREATE OR REPLACE PACKAGE ORPACKAGE AS
                    TYPE TBL1 IS TABLE OF VARCHAR2(111) INDEX BY BINARY_INTEGER;
                    TYPE TBL2 IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;
                    TYPE ARECORD IS RECORD (
                    T1 TBL1,
                    T2 TBL2,
                    T3 BOOLEAN
                    );
                    TYPE TBL3 IS TABLE OF ARECORD INDEX BY PLS_INTEGER;
                    TYPE TBL4 IS TABLE OF TBL2 INDEX BY PLS_INTEGER;
                    PROCEDURE P1(SIMPLARRAY IN TBL1, FOO IN VARCHAR2);
                    PROCEDURE P2(OLD IN TBL2, NEW IN TBL2);
                    PROCEDURE P4(REC IN ARECORD);
                    PROCEDURE P5(OLDREC IN ARECORD, NEWREC OUT ARECORD);
                    PROCEDURE P7(SIMPLARRAY IN TBL1, FOO IN VARCHAR2);
                    PROCEDURE P7(SIMPLARRAY IN TBL1, FOO IN VARCHAR2, BAR IN VARCHAR2);
                    PROCEDURE P8(FOO IN VARCHAR2);
                    PROCEDURE P8(FOO IN VARCHAR2, BAR IN VARCHAR2);
                    FUNCTION F2(OLD IN TBL2, SIMPLARRAY IN TBL1) RETURN TBL2;
                    FUNCTION F4(RECARRAY IN TBL3, OLDREC IN ARECORD) RETURN TBL3;
                    END ORPACKAGE;""";

    static final String CREATE_PACKAGE_BODY_ORPACKAGE =
            """

                    CREATE OR REPLACE PACKAGE BODY ORPACKAGE AS
                    PROCEDURE P1(SIMPLARRAY IN TBL1, FOO IN VARCHAR2) AS
                    BEGIN
                    NULL;
                    END P1;
                    PROCEDURE P2(OLD IN TBL2, NEW IN TBL2) AS
                    BEGIN
                    NULL;
                    END P2;
                    PROCEDURE P4(REC IN ARECORD) AS
                    BEGIN
                    NULL;
                    END P4;
                    PROCEDURE P5(OLDREC IN ARECORD, NEWREC OUT ARECORD) AS
                    BEGIN
                    NEWREC.T1 := OLDREC.T1;
                    NEWREC.T2 := OLDREC.T2;
                    NEWREC.T3 := OLDREC.T3;
                    END P5;
                    PROCEDURE P7(SIMPLARRAY IN TBL1, FOO IN VARCHAR2) AS
                    BEGIN
                    NULL;
                    END P7;
                    PROCEDURE P7(SIMPLARRAY IN TBL1, FOO IN VARCHAR2, BAR IN VARCHAR2) AS
                    BEGIN
                    NULL;
                    END P7;
                    PROCEDURE P8(FOO IN VARCHAR2) AS
                    BEGIN
                    NULL;
                    END P8;
                    PROCEDURE P8(FOO IN VARCHAR2, BAR IN VARCHAR2) AS
                    BEGIN
                    NULL;
                    END P8;
                    FUNCTION F2(OLD IN TBL2, SIMPLARRAY IN TBL1) RETURN TBL2 IS
                    BEGIN
                    RETURN OLD;
                    END F2;
                    FUNCTION F4(RECARRAY IN TBL3, OLDREC IN ARECORD) RETURN TBL3 IS
                    BEGIN
                    RETURN RECARRAY;
                    END F4;
                    END ORPACKAGE;""";

    static final String CREATE_TYPE_ORPACKAGE_TBL1 =
        "CREATE OR REPLACE TYPE ORPACKAGE_TBL1 AS TABLE OF VARCHAR2(111)";
    static final String CREATE_TYPE_ORPACKAGE_TBL2 =
        "CREATE OR REPLACE TYPE ORPACKAGE_TBL2 AS TABLE OF NUMBER";
    static final String CREATE_TYPE_ORPACKAGE_ARECORD =
            """
                    CREATE OR REPLACE TYPE ORPACKAGE_ARECORD AS OBJECT (
                    T1 ORPACKAGE_TBL1,
                    T2 ORPACKAGE_TBL2,
                    T3 INTEGER
                    )""";
    static final String CREATE_TYPE_ORPACKAGE_TBL3 =
        "CREATE OR REPLACE TYPE ORPACKAGE_TBL3 AS TABLE OF ORPACKAGE_ARECORD";
    static final String CREATE_TYPE_ORPACKAGE_TBL4 =
        "CREATE OR REPLACE TYPE ORPACKAGE_TBL4 AS TABLE OF ORPACKAGE_TBL2";

    static final String DROP_PACKAGE_ORPACKAGE =
        "DROP PACKAGE ORPACKAGE";
    static final String DROP_TYPE_ORPACKAGE_TBL1=
        "DROP TYPE ORPACKAGE_TBL1 FORCE";
    static final String DROP_TYPE_ORPACKAGE_TBL2=
           "DROP TYPE ORPACKAGE_TBL2 FORCE";
    static final String DROP_TYPE_ORPACKAGE_ARECORD=
           "DROP TYPE ORPACKAGE_ARECORD FORCE";
    static final String DROP_TYPE_ORPACKAGE_TBL3=
        "DROP TYPE ORPACKAGE_TBL3 FORCE";
    static final String DROP_TYPE_ORPACKAGE_TBL4=
           "DROP TYPE ORPACKAGE_TBL4 FORCE";

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
            runDdl(conn, CREATE_TYPE_ORPACKAGE_TBL1, ddlDebug);
            runDdl(conn, CREATE_TYPE_ORPACKAGE_TBL2, ddlDebug);
            runDdl(conn, CREATE_TYPE_ORPACKAGE_ARECORD, ddlDebug);
            runDdl(conn, CREATE_TYPE_ORPACKAGE_TBL3, ddlDebug);
            runDdl(conn, CREATE_TYPE_ORPACKAGE_TBL4, ddlDebug);
            runDdl(conn, CREATE_PACKAGE_ORPACKAGE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE_BODY_ORPACKAGE, ddlDebug);
        }

        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">ObjectTypeTests</property>" +
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
              "<plsql-procedure " +
                  "name=\"PTest\" " +
                  "catalogPattern=\"ORPACKAGE\" " +
                  "procedurePattern=\"P%\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"FTest\" " +
                  "catalogPattern=\"ORPACKAGE\" " +
                  "procedurePattern=\"F%\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_TYPE_ORPACKAGE_TBL1, ddlDebug);
            runDdl(conn, DROP_TYPE_ORPACKAGE_TBL2, ddlDebug);
            runDdl(conn, DROP_TYPE_ORPACKAGE_ARECORD, ddlDebug);
            runDdl(conn, DROP_TYPE_ORPACKAGE_TBL3, ddlDebug);
            runDdl(conn, DROP_TYPE_ORPACKAGE_TBL4, ddlDebug);
            runDdl(conn, DROP_PACKAGE_ORPACKAGE, ddlDebug);
        }
    }

    // TEST METHODS
    @Test
    public void p1Test() {
        ClassDescriptor tbl1Desc = null;
        for (ClassDescriptor cDesc : builder.getOrProject().getOrderedDescriptors()) {
            if (cDesc.getAlias().equals(TBL1_DESCRIPTOR_ALIAS)) {
                tbl1Desc = cDesc;
                break;
            }
        }
        assertNotNull("No descriptor was found with alias [" + TBL1_DESCRIPTOR_ALIAS + "]", tbl1Desc);
        tbl1Asserts(tbl1Desc);
    }

    @Test
    public void p2test() {
        ClassDescriptor tbl2Desc = null;
        for (ClassDescriptor cDesc : builder.getOrProject().getOrderedDescriptors()) {
            if (cDesc.getAlias().equals(TBL2_DESCRIPTOR_ALIAS)) {
                tbl2Desc = cDesc;
                break;
            }
        }
        assertNotNull("No descriptor was found with alias [" + TBL2_DESCRIPTOR_ALIAS + "]", tbl2Desc);
        tbl2Asserts(tbl2Desc);
    }

    @Test
    public void p4test() {
        ClassDescriptor tbl1Desc = null;
        ClassDescriptor tbl2Desc = null;
        ClassDescriptor aRecDesc = null;
        for (ClassDescriptor cDesc : builder.getOrProject().getOrderedDescriptors()) {
            String alias = cDesc.getAlias();
            if (alias.equals(TBL1_DESCRIPTOR_ALIAS)) {
                tbl1Desc = cDesc;
            } else if (alias.equals(TBL2_DESCRIPTOR_ALIAS)) {
                tbl2Desc = cDesc;
            } else if (alias.equals(ARECORD_DESCRIPTOR_ALIAS)) {
                aRecDesc = cDesc;
            }
        }
        assertNotNull("No descriptor was found with alias [" + TBL1_DESCRIPTOR_ALIAS + "]", tbl1Desc);
        assertNotNull("No descriptor was found with alias [" + TBL2_DESCRIPTOR_ALIAS + "]", tbl2Desc);
        assertNotNull("No descriptor was found with alias [" + ARECORD_DESCRIPTOR_ALIAS + "]", aRecDesc);

        aRecordAsserts(aRecDesc);
        tbl1Asserts(tbl1Desc);
        tbl2Asserts(tbl2Desc);
    }

    @Test
    public void p5test() {
        ClassDescriptor tbl1Desc = null;
        ClassDescriptor tbl2Desc = null;
        ClassDescriptor aRecDesc = null;
        for (ClassDescriptor cDesc : builder.getOrProject().getOrderedDescriptors()) {
            String alias = cDesc.getAlias();
            if (alias.equals(TBL1_DESCRIPTOR_ALIAS)) {
                tbl1Desc =  cDesc;
            } else if (alias.equals(TBL2_DESCRIPTOR_ALIAS)) {
                tbl2Desc = cDesc;
            } else if (alias.equals(ARECORD_DESCRIPTOR_ALIAS)) {
                aRecDesc = cDesc;
            }
        }
        assertNotNull("No descriptor was found with alias [" + TBL1_DESCRIPTOR_ALIAS + "]", tbl1Desc);
        assertNotNull("No descriptor was found with alias [" + TBL2_DESCRIPTOR_ALIAS + "]", tbl2Desc);
        assertNotNull("No descriptor was found with alias [" + ARECORD_DESCRIPTOR_ALIAS + "]", aRecDesc);

        aRecordAsserts(aRecDesc);
        tbl1Asserts(tbl1Desc);
        tbl2Asserts(tbl2Desc);
    }

    @Test
    public void p7test() {
        ClassDescriptor tbl1Desc = null;
        for (ClassDescriptor cDesc : builder.getOrProject().getOrderedDescriptors()) {
            String alias = cDesc.getAlias();
            if (alias.equals(TBL1_DESCRIPTOR_ALIAS)) {
                tbl1Desc = cDesc;
            }
        }
        assertNotNull("No descriptor was found with alias [" + TBL1_DESCRIPTOR_ALIAS + "]", tbl1Desc);
        tbl1Asserts(tbl1Desc);
    }

    @Test
    public void f2test() {
        ClassDescriptor tbl1Desc = null;
        ClassDescriptor tbl2Desc = null;
        for (ClassDescriptor cDesc : builder.getOrProject().getOrderedDescriptors()) {
            String alias = cDesc.getAlias();
            if (alias.equals(TBL1_DESCRIPTOR_ALIAS)) {
                tbl1Desc = cDesc;
            } else if (alias.equals(TBL2_DESCRIPTOR_ALIAS)) {
                tbl2Desc = cDesc;
            }
        }
        assertNotNull("No descriptor was found with alias [" + TBL1_DESCRIPTOR_ALIAS + "]", tbl1Desc);
        assertNotNull("No descriptor was found with alias [" + TBL2_DESCRIPTOR_ALIAS + "]", tbl2Desc);
        tbl1Asserts(tbl1Desc);
        tbl2Asserts(tbl2Desc);
    }

    @Test
    public void f4Test() {
        ClassDescriptor tbl1Desc = null;
        ClassDescriptor tbl2Desc = null;
        ClassDescriptor tbl3Desc = null;
        ClassDescriptor aRecDesc = null;
        for (ClassDescriptor cDesc : builder.getOrProject().getOrderedDescriptors()) {
            String alias = cDesc.getAlias();
            if (alias.equals(TBL1_DESCRIPTOR_ALIAS)) {
                tbl1Desc = cDesc;
            } else if (alias.equals(TBL2_DESCRIPTOR_ALIAS)) {
                tbl2Desc = cDesc;
            } else if (alias.equals(TBL3_DESCRIPTOR_ALIAS)) {
                tbl3Desc = cDesc;
            } else if (alias.equals(ARECORD_DESCRIPTOR_ALIAS)) {
                aRecDesc =  cDesc;
            }
        }
        assertNotNull("No descriptor was found with alias [" + TBL1_DESCRIPTOR_ALIAS + "]", tbl1Desc);
        assertNotNull("No descriptor was found with alias [" + TBL2_DESCRIPTOR_ALIAS + "]", tbl2Desc);
        assertNotNull("No descriptor was found with alias [" + TBL3_DESCRIPTOR_ALIAS + "]", tbl3Desc);
        assertNotNull("No descriptor was found with alias [" + ARECORD_DESCRIPTOR_ALIAS + "]", aRecDesc);

        tbl3Asserts(tbl3Desc);
        aRecordAsserts(aRecDesc);
        tbl1Asserts(tbl1Desc);
        tbl2Asserts(tbl2Desc);
    }

    // ASSERT METHODS
    protected void tbl1Asserts(ClassDescriptor tbl1Descriptor) {
        assertEquals("Wrong Java class name.  Expected [" + TBL1_DESCRIPTOR_JAVACLASSNAME + "] but was [" + tbl1Descriptor.getJavaClassName() + "]", tbl1Descriptor.getJavaClassName(), TBL1_DESCRIPTOR_JAVACLASSNAME);
        List<DatabaseMapping> mappings = tbl1Descriptor.getMappings();
        assertEquals("Wrong number of mappings.  Expected [1] but was [" + mappings.size() + "]", 1, mappings.size());
        DatabaseMapping mapping = mappings.get(0);
        assertEquals("Incorrect mapping attribute name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME + "] but was [" + mapping.getAttributeName() + "]", BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME, mapping.getAttributeName());
        assertEquals("Incorrect mapping field name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME + "] but was [" + mapping.getField().getName() + "]", BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME, mapping.getField().getName());
        assertTrue("Incorrect mapping type.  Expected [AbstractCompositeDirectCollection mapping], but was [" + mapping.getClass().getName() + "]",  mapping.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping = (ArrayMapping)mapping;
        assertEquals("Wrong structure name for mapping.  Expected [" + TBL1_COMPATIBLETYPE + "] but was [" + arrayMapping.getStructureName() + "]", TBL1_COMPATIBLETYPE, arrayMapping.getStructureName());
    }

    protected void tbl2Asserts(ClassDescriptor tbl2Descriptor) {
        assertEquals("Wrong Java class name.  Expected [" + TBL2_DESCRIPTOR_JAVACLASSNAME + "] but was [" + tbl2Descriptor.getJavaClassName() + "]", tbl2Descriptor.getJavaClassName(), TBL2_DESCRIPTOR_JAVACLASSNAME);
        List<DatabaseMapping> mappings = tbl2Descriptor.getMappings();
        assertEquals("Wrong number of mappings.  Expected [1] but was [" + mappings.size() + "]", 1, mappings.size());
        DatabaseMapping mapping = mappings.get(0);
        assertEquals("Incorrect mapping attribute name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME + "] but was [" + mapping.getAttributeName() + "]", BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME, mapping.getAttributeName());
        assertEquals("Incorrect mapping field name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME + "] but was [" + mapping.getField().getName() + "]", BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME, mapping.getField().getName());
        assertTrue("Incorrect mapping type.  Expected [AbstractCompositeDirectCollection mapping], but was [" + mapping.getClass().getName() + "]", mapping.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping = (ArrayMapping)mapping;
        assertEquals("Wrong structure name for mapping.  Expected [" + TBL2_COMPATIBLETYPE + "] but was [" + arrayMapping.getStructureName() + "]", TBL2_COMPATIBLETYPE, arrayMapping.getStructureName());
    }

    protected void tbl3Asserts(ClassDescriptor tbl3Descriptor) {
        assertEquals("Wrong Java class name.  Expected [" + TBL3_DESCRIPTOR_JAVACLASSNAME + "] but was [" + tbl3Descriptor.getJavaClassName() + "]", tbl3Descriptor.getJavaClassName(), TBL3_DESCRIPTOR_JAVACLASSNAME);
        List<DatabaseMapping> mappings = tbl3Descriptor.getMappings();
        assertEquals("Wrong number of mappings.  Expected [1] but was [" + mappings.size() + "]", 1, mappings.size());
        DatabaseMapping mapping = mappings.get(0);
        assertEquals("Incorrect mapping attribute name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME + "] but was [" + mapping.getAttributeName() + "]", BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME, mapping.getAttributeName());
        assertEquals("Incorrect mapping field name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME + "] but was [" + mapping.getField().getName() + "]", BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME, mapping.getField().getName());
        assertTrue("Incorrect mapping type.  Expected [AbstractCompositeCollection mapping] but was [" + mapping.getClass().getName() + "]", mapping.isAbstractCompositeCollectionMapping());

        ObjectArrayMapping oArrayMapping = (ObjectArrayMapping)mapping;
        assertEquals("Incorrect structure name.  Expected [" + TBL3_COMPATIBLETYPE + "] but was [" + oArrayMapping.getStructureName() + "]", TBL3_COMPATIBLETYPE, oArrayMapping.getStructureName());
        assertEquals("Incorrect reference class name.  Expected [" + ARECORD_DESCRIPTOR_JAVACLASSNAME + "] but was [" + oArrayMapping.getReferenceClassName() + "]", oArrayMapping.getReferenceClassName(), ARECORD_DESCRIPTOR_JAVACLASSNAME);
    }
    protected void tbl4Asserts(ClassDescriptor tbl4Descriptor) {
        assertEquals("Wrong Java class name.  Expected [" + TBL4_DESCRIPTOR_JAVACLASSNAME + "] but was [" + tbl4Descriptor.getJavaClassName() + "]", tbl4Descriptor.getJavaClassName(), TBL4_DESCRIPTOR_JAVACLASSNAME);
        List<DatabaseMapping> mappings = tbl4Descriptor.getMappings();
        assertEquals("Wrong number of mappings.  Expected [1] but was [" + mappings.size() + "]", 1, mappings.size());
        DatabaseMapping mapping = mappings.get(0);
        assertEquals("Incorrect mapping attribute name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME + "] but was [" + mapping.getAttributeName() + "]", BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME, mapping.getAttributeName());
        assertEquals("Incorrect mapping field name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME + "] but was [" + mapping.getField().getName() + "]", BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME, mapping.getField().getName());
        assertTrue("Incorrect mapping type.  Expected [isAbstractCompositeDirectCollectionMapping mapping] but was [" + mapping.getClass().getName() + "]", mapping.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping2 = (ArrayMapping)mapping;
        assertEquals("Incorrect structure name.  Expected [" + TBL4_COMPATIBLETYPE + "] but was [" + arrayMapping2.getStructureName() + "]", TBL4_COMPATIBLETYPE, arrayMapping2.getStructureName());
    }
    protected void aRecordAsserts(ClassDescriptor aRecordDescriptor) {
        assertEquals("Wrong Java class name.  Expected [" + ARECORD_DESCRIPTOR_JAVACLASSNAME + "] but was [" + aRecordDescriptor.getJavaClassName() + "]", aRecordDescriptor.getJavaClassName(), ARECORD_DESCRIPTOR_JAVACLASSNAME);
        List<DatabaseMapping> mappings = aRecordDescriptor.getMappings();
        assertEquals("Wrong number of mappings.  Expected [3] but was [" + mappings.size() + "]", 3, mappings.size());
        DatabaseMapping dm1 = mappings.get(0);
        assertEquals("Incorrect mapping attribute name.  Expected [t1] but was [" + dm1.getAttributeName() + "]", "t1", dm1.getAttributeName());
        assertTrue("Incorrect mapping type.  Expected [AbstractCompositeDirectCollection mapping] but was [" + dm1.getClass().getName() +"]", dm1.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping1 = (ArrayMapping)dm1;
        assertEquals("Incorrect attribute element class.  Expected [" + TBL1_COMPATIBLETYPE + "] but was [" + arrayMapping1.getStructureName() + "]", TBL1_COMPATIBLETYPE, arrayMapping1.getStructureName());
        DatabaseMapping dm2 = mappings.get(1);
        assertEquals("Incorrect mapping attribute name.  Expected [t2] but was [" + dm2.getAttributeName() + "]", "t2", dm2.getAttributeName());
        assertTrue("Incorrect mapping type.  Expected [AbstractCompositeDirectCollection mapping ]but was [" + dm2.getClass().getName() +"]", dm2.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping2 = (ArrayMapping)dm2;
        assertEquals("Incorrect structure name.  Expected [" + TBL2_COMPATIBLETYPE + "] but was [" + arrayMapping2.getStructureName() + "]", TBL2_COMPATIBLETYPE, arrayMapping2.getStructureName());
        DatabaseMapping dm3 = mappings.get(2);
        assertEquals("Incorrect mapping attribute name.  Expected [t3] but was [" + dm3.getAttributeName() + "]", "t3", dm3.getAttributeName());
        assertTrue("Incorrect mapping type.  Expected [DirectToField mapping] but was [" + dm3.getClass().getName() + "]", dm3.isDirectToFieldMapping());
    }
}
