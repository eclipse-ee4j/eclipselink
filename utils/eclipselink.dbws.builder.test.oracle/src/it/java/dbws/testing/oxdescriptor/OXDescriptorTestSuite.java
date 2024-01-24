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
//     David McCann - November 14, 2011 - 2.4 - Initial implementation
package dbws.testing.oxdescriptor;

//javase imports
import java.math.BigDecimal;
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
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.tools.dbws.BaseDBWSBuilderHelper;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests OXM Descriptor building.
 *
 */
public class OXDescriptorTestSuite extends DBWSTestSuite {
    public static final String TBL1_COMPATIBLETYPE = "OXPACKAGE_TBL1";
    public static final String TBL1_DATABASETYPE = "OXPACKAGE.TBL1";
    public static final String TBL1_DESCRIPTOR_ALIAS = TBL1_COMPATIBLETYPE.toLowerCase();
    public static final String TBL1_DESCRIPTOR_JAVACLASSNAME = TBL1_DATABASETYPE.toLowerCase() + COLLECTION_WRAPPER_SUFFIX;

    public static final String TBL2_COMPATIBLETYPE = "OXPACKAGE_TBL2";
    public static final String TBL2_DATABASETYPE = "OXPACKAGE.TBL2";
    public static final String TBL2_DESCRIPTOR_ALIAS = TBL2_COMPATIBLETYPE.toLowerCase();
    public static final String TBL2_DESCRIPTOR_JAVACLASSNAME = TBL2_DATABASETYPE.toLowerCase() + COLLECTION_WRAPPER_SUFFIX;

    public static final String TBL3_COMPATIBLETYPE = "OXPACKAGE_TBL3";
    public static final String TBL3_DATABASETYPE = "OXPACKAGE.TBL3";
    public static final String TBL3_DESCRIPTOR_ALIAS = TBL3_COMPATIBLETYPE.toLowerCase();
    public static final String TBL3_DESCRIPTOR_JAVACLASSNAME = TBL3_DATABASETYPE.toLowerCase() + COLLECTION_WRAPPER_SUFFIX;

    public static final String TBL4_COMPATIBLETYPE = "OXPACKAGE_TBL4";
    public static final String TBL4_DATABASETYPE = "OXPACKAGE.TBL4";
    public static final String TBL4_DESCRIPTOR_ALIAS = TBL4_COMPATIBLETYPE.toLowerCase();
    public static final String TBL4_DESCRIPTOR_JAVACLASSNAME = TBL4_DATABASETYPE.toLowerCase() + COLLECTION_WRAPPER_SUFFIX;

    public static final String ARECORD_COMPATIBLETYPE = "OXPACKAGE_ARECORD";
    public static final String ARECORD_DATABASETYPE = "OXPACKAGE.ARECORD";
    public static final String ARECORD_DESCRIPTOR_ALIAS = ARECORD_COMPATIBLETYPE.toLowerCase();
    public static final String ARECORD_DESCRIPTOR_JAVACLASSNAME = ARECORD_DATABASETYPE.toLowerCase();

    static final String CREATE_PACKAGE_OXPACKAGE =
            """
                    CREATE OR REPLACE PACKAGE OXPACKAGE AS
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
                    END OXPACKAGE;""";

    static final String CREATE_PACKAGE_BODY_OXPACKAGE =
            """

                    CREATE OR REPLACE PACKAGE BODY OXPACKAGE AS
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
                    END OXPACKAGE;""";

    static final String CREATE_TYPE_OXPACKAGE_TBL1 =
        "CREATE OR REPLACE TYPE OXPACKAGE_TBL1 AS TABLE OF VARCHAR2(111)";
    static final String CREATE_TYPE_OXPACKAGE_TBL2 =
        "CREATE OR REPLACE TYPE OXPACKAGE_TBL2 AS TABLE OF NUMBER";
    static final String CREATE_TYPE_OXPACKAGE_ARECORD =
            """
                    CREATE OR REPLACE TYPE OXPACKAGE_ARECORD AS OBJECT (
                    T1 OXPACKAGE_TBL1,
                    T2 OXPACKAGE_TBL2,
                    T3 INTEGER
                    )""";
    static final String CREATE_TYPE_OXPACKAGE_TBL3 =
        "CREATE OR REPLACE TYPE OXPACKAGE_TBL3 AS TABLE OF OXPACKAGE_ARECORD";
    static final String CREATE_TYPE_OXPACKAGE_TBL4 =
        "CREATE OR REPLACE TYPE OXPACKAGE_TBL4 AS TABLE OF OXPACKAGE_TBL2";

    static final String DROP_PACKAGE_OXPACKAGE =
        "DROP PACKAGE OXPACKAGE";
    static final String DROP_TYPE_OXPACKAGE_TBL1=
        "DROP TYPE OXPACKAGE_TBL1 FORCE";
    static final String DROP_TYPE_OXPACKAGE_TBL2=
           "DROP TYPE OXPACKAGE_TBL2 FORCE";
    static final String DROP_TYPE_OXPACKAGE_ARECORD=
           "DROP TYPE OXPACKAGE_ARECORD FORCE";
    static final String DROP_TYPE_OXPACKAGE_TBL3=
        "DROP TYPE OXPACKAGE_TBL3 FORCE";
    static final String DROP_TYPE_OXPACKAGE_TBL4=
           "DROP TYPE OXPACKAGE_TBL4 FORCE";

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
            runDdl(conn, CREATE_TYPE_OXPACKAGE_TBL1, ddlDebug);
            runDdl(conn, CREATE_TYPE_OXPACKAGE_TBL2, ddlDebug);
            runDdl(conn, CREATE_TYPE_OXPACKAGE_ARECORD, ddlDebug);
            runDdl(conn, CREATE_TYPE_OXPACKAGE_TBL3, ddlDebug);
            runDdl(conn, CREATE_TYPE_OXPACKAGE_TBL4, ddlDebug);
            runDdl(conn, CREATE_PACKAGE_OXPACKAGE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE_BODY_OXPACKAGE, ddlDebug);
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
                  "catalogPattern=\"OXPACKAGE\" " +
                  "procedurePattern=\"P%\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"FTest\" " +
                  "catalogPattern=\"OXPACKAGE\" " +
                  "procedurePattern=\"F%\" " +
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_TYPE_OXPACKAGE_TBL1, ddlDebug);
            runDdl(conn, DROP_TYPE_OXPACKAGE_TBL2, ddlDebug);
            runDdl(conn, DROP_TYPE_OXPACKAGE_ARECORD, ddlDebug);
            runDdl(conn, DROP_TYPE_OXPACKAGE_TBL3, ddlDebug);
            runDdl(conn, DROP_TYPE_OXPACKAGE_TBL4, ddlDebug);
            runDdl(conn, DROP_PACKAGE_OXPACKAGE, ddlDebug);
        }
    }

    // TEST METHODS
    @Test
    public void p1Test() {
        XMLDescriptor tbl1Desc = null;
        for (ClassDescriptor cDesc : builder.getOxProject().getOrderedDescriptors()) {
            if (cDesc.getAlias().equals(TBL1_DESCRIPTOR_ALIAS)) {
                tbl1Desc = (XMLDescriptor) cDesc;
                break;
            }
        }
        assertNotNull("No descriptor was found with alias [" + TBL1_DESCRIPTOR_ALIAS + "]", tbl1Desc);
        tbl1Asserts(tbl1Desc);
    }

    @Test
    public void p2test() {
        XMLDescriptor tbl2Desc = null;
        for (ClassDescriptor cDesc : builder.getOxProject().getOrderedDescriptors()) {
            if (cDesc.getAlias().equals(TBL2_DESCRIPTOR_ALIAS)) {
                tbl2Desc = (XMLDescriptor) cDesc;
                break;
            }
        }
        assertNotNull("No descriptor was found with alias [" + TBL2_DESCRIPTOR_ALIAS + "]", tbl2Desc);
        tbl2Asserts(tbl2Desc);
    }

    @Test
    public void p4test() {
        XMLDescriptor tbl1Desc = null;
        XMLDescriptor tbl2Desc = null;
        XMLDescriptor aRecDesc = null;
        for (ClassDescriptor cDesc : builder.getOxProject().getOrderedDescriptors()) {
            String alias = cDesc.getAlias();
            if (alias.equals(TBL1_DESCRIPTOR_ALIAS)) {
                tbl1Desc = (XMLDescriptor) cDesc;
            } else if (alias.equals(TBL2_DESCRIPTOR_ALIAS)) {
                tbl2Desc = (XMLDescriptor) cDesc;
            } else if (alias.equals(ARECORD_DESCRIPTOR_ALIAS)) {
                aRecDesc = (XMLDescriptor) cDesc;
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
        XMLDescriptor tbl1Desc = null;
        XMLDescriptor tbl2Desc = null;
        XMLDescriptor aRecDesc = null;
        for (ClassDescriptor cDesc : builder.getOxProject().getOrderedDescriptors()) {
            String alias = cDesc.getAlias();
            if (alias.equals(TBL1_DESCRIPTOR_ALIAS)) {
                tbl1Desc = (XMLDescriptor) cDesc;
            } else if (alias.equals(TBL2_DESCRIPTOR_ALIAS)) {
                tbl2Desc = (XMLDescriptor) cDesc;
            } else if (alias.equals(ARECORD_DESCRIPTOR_ALIAS)) {
                aRecDesc = (XMLDescriptor) cDesc;
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
        XMLDescriptor tbl1Desc = null;
        for (ClassDescriptor cDesc : builder.getOxProject().getOrderedDescriptors()) {
            String alias = cDesc.getAlias();
            if (alias.equals(TBL1_DESCRIPTOR_ALIAS)) {
                tbl1Desc = (XMLDescriptor) cDesc;
                break;
            }
        }
        assertNotNull("No descriptor was found with alias [" + TBL1_DESCRIPTOR_ALIAS + "]", tbl1Desc);
        tbl1Asserts(tbl1Desc);
    }

    @Test
    public void f2test() {
        XMLDescriptor tbl1Desc = null;
        XMLDescriptor tbl2Desc = null;
        for (ClassDescriptor cDesc : builder.getOxProject().getOrderedDescriptors()) {
            String alias = cDesc.getAlias();
            if (alias.equals(TBL1_DESCRIPTOR_ALIAS)) {
                tbl1Desc = (XMLDescriptor) cDesc;
            } else if (alias.equals(TBL2_DESCRIPTOR_ALIAS)) {
                tbl2Desc = (XMLDescriptor) cDesc;
            }
        }
        assertNotNull("No descriptor was found with alias [" + TBL1_DESCRIPTOR_ALIAS + "]", tbl1Desc);
        assertNotNull("No descriptor was found with alias [" + TBL2_DESCRIPTOR_ALIAS + "]", tbl2Desc);
        tbl1Asserts(tbl1Desc);
        tbl2Asserts(tbl2Desc);
    }

    @Test
    public void f4Test() {
        XMLDescriptor tbl1Desc = null;
        XMLDescriptor tbl2Desc = null;
        XMLDescriptor tbl3Desc = null;
        XMLDescriptor aRecDesc = null;
        for (ClassDescriptor cDesc : builder.getOxProject().getOrderedDescriptors()) {
            String alias = cDesc.getAlias();
            if (alias.equals(TBL1_DESCRIPTOR_ALIAS)) {
                tbl1Desc = (XMLDescriptor) cDesc;
            } else if (alias.equals(TBL2_DESCRIPTOR_ALIAS)) {
                tbl2Desc = (XMLDescriptor) cDesc;
            } else if (alias.equals(TBL3_DESCRIPTOR_ALIAS)) {
                tbl3Desc = (XMLDescriptor) cDesc;
            } else if (alias.equals(ARECORD_DESCRIPTOR_ALIAS)) {
                aRecDesc = (XMLDescriptor) cDesc;
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
    protected void tbl1Asserts(XMLDescriptor tbl1Descriptor) {
        assertEquals("Wrong Java class name.  Expected [" + TBL1_DESCRIPTOR_JAVACLASSNAME + "] but was [" + tbl1Descriptor.getJavaClassName() + "]", tbl1Descriptor.getJavaClassName(), TBL1_DESCRIPTOR_JAVACLASSNAME);
        List<DatabaseMapping> mappings = tbl1Descriptor.getMappings();
        assertEquals("Wrong number of mappings.  Expected [1] but was [" + mappings.size() + "]", 1, mappings.size());
        DatabaseMapping mapping = mappings.get(0);
        assertEquals("Incorrect mapping attribute name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME + "] but was [" + mapping.getAttributeName() + "]", BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME, mapping.getAttributeName());
        assertTrue("Incorrect mapping type.  Expected [(XML) AbstractCompositeDirectCollection mapping], but was [" + mapping.getClass().getName() + "]", mapping.isXMLMapping() && mapping.isAbstractCompositeDirectCollectionMapping());
        XMLCompositeDirectCollectionMapping xdcm = (XMLCompositeDirectCollectionMapping)mapping;
        assertEquals("Incorrect mapping XPath.  Expected [item/text()] but was [" + xdcm.getXPath() + "]", "item/text()", xdcm.getXPath());
        assertEquals("Wrong component class for mapping.  Expected [" + String.class + "] but was [" + xdcm.getAttributeElementClass() + "]", xdcm.getAttributeElementClass(), String.class);
    }

    protected void tbl2Asserts(XMLDescriptor tbl2Descriptor) {
        assertEquals("Wrong Java class name.  Expected [" + TBL2_DESCRIPTOR_JAVACLASSNAME + "] but was [" + tbl2Descriptor.getJavaClassName() + "]", tbl2Descriptor.getJavaClassName(), TBL2_DESCRIPTOR_JAVACLASSNAME);
        List<DatabaseMapping> mappings = tbl2Descriptor.getMappings();
        assertEquals("Wrong number of mappings.  Expected [1] but was [" + mappings.size() + "]", 1, mappings.size());
        DatabaseMapping mapping = mappings.get(0);
        assertEquals("Incorrect mapping attribute name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME + "] but was [" + mapping.getAttributeName() + "]", BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME, mapping.getAttributeName());
        assertTrue("Incorrect mapping type.  Expected [(XML) AbstractCompositeDirectCollection mapping], but was [" + mapping.getClass().getName() + "]", mapping.isXMLMapping() && mapping.isAbstractCompositeDirectCollectionMapping());
        XMLCompositeDirectCollectionMapping xdcm = (XMLCompositeDirectCollectionMapping)mapping;
        assertEquals("Incorrect mapping XPath.  Expected [item/text()] but was [" + xdcm.getXPath() + "]", "item/text()", xdcm.getXPath());
        assertEquals("Wrong component class for mapping.  Expected [" + BigDecimal.class + "] but was [" + xdcm.getAttributeElementClass() + "]", xdcm.getAttributeElementClass(), BigDecimal.class);
    }

    protected void tbl3Asserts(XMLDescriptor tbl3Descriptor) {
        assertEquals("Wrong Java class name.  Expected [" + TBL3_DESCRIPTOR_JAVACLASSNAME + "] but was [" + tbl3Descriptor.getJavaClassName() + "]", tbl3Descriptor.getJavaClassName(), TBL3_DESCRIPTOR_JAVACLASSNAME);
        List<DatabaseMapping> mappings = tbl3Descriptor.getMappings();
        assertEquals("Wrong number of mappings.  Expected [1] but was [" + mappings.size() + "]", 1, mappings.size());
        DatabaseMapping mapping = mappings.get(0);
        assertEquals("Incorrect mapping attribute name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME + "] but was [" + mapping.getAttributeName() + "]", BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME, mapping.getAttributeName());
        assertTrue("Incorrect mapping type.  Expected [(XML) AbstractCompositeCollection mapping] but was [" + mapping.getClass().getName() + "]", mapping.isXMLMapping() && mapping.isAbstractCompositeCollectionMapping());
        XMLCompositeCollectionMapping xccm = (XMLCompositeCollectionMapping)mapping;
        assertEquals("Incorrect mapping XPath.  Expected [item] but was [" + xccm.getXPath() + "]", "item", xccm.getXPath());
        assertEquals("Incorrect reference class name.  Expected [" + ARECORD_DESCRIPTOR_JAVACLASSNAME + "] but was [" + xccm.getReferenceClassName() + "]", xccm.getReferenceClassName(), ARECORD_DESCRIPTOR_JAVACLASSNAME);
    }
    protected void tbl4Asserts(XMLDescriptor tbl4Descriptor) {
        assertEquals("Wrong Java class name.  Expected [" + TBL4_DESCRIPTOR_JAVACLASSNAME + "] but was [" + tbl4Descriptor.getJavaClassName() + "]", tbl4Descriptor.getJavaClassName(), TBL4_DESCRIPTOR_JAVACLASSNAME);
        List<DatabaseMapping> mappings = tbl4Descriptor.getMappings();
        assertEquals("Wrong number of mappings.  Expected [1] but was [" + mappings.size() + "]", 1, mappings.size());
        DatabaseMapping mapping = mappings.get(0);
        assertEquals("Incorrect mapping attribute name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME + "] but was [" + mapping.getAttributeName() + "]", BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME, mapping.getAttributeName());
        assertTrue("Incorrect mapping type.  Expected [AbstractCompositeCollection mapping] but was [" + mapping.getClass().getName() + "]", mapping.isAbstractCompositeCollectionMapping());
    }
    protected void aRecordAsserts(XMLDescriptor aRecordDescriptor) {
        assertEquals("Wrong Java class name.  Expected [" + ARECORD_DESCRIPTOR_JAVACLASSNAME + "] but was [" + aRecordDescriptor.getJavaClassName() + "]", aRecordDescriptor.getJavaClassName(), ARECORD_DESCRIPTOR_JAVACLASSNAME);
        List<DatabaseMapping> mappings = aRecordDescriptor.getMappings();
        assertEquals("Wrong number of mappings.  Expected [3] but was [" + mappings.size() + "]", 3, mappings.size());
        DatabaseMapping dm1 = mappings.get(0);
        assertEquals("Incorrect mapping attribute name.  Expected [t1] but was [" + dm1.getAttributeName() + "]", "t1", dm1.getAttributeName());
        assertTrue("Incorrect mapping type.  Expected [(XML) AbstractCompositeDirectCollection mapping] but was [" + dm1.getClass().getName() +"]", dm1.isXMLMapping() && dm1.isAbstractCompositeDirectCollectionMapping());
        XMLCompositeDirectCollectionMapping  xcom1 = (XMLCompositeDirectCollectionMapping)dm1;
        assertEquals("Incorrect mapping XPath.  Expected [t1/item/text()] but was [" + xcom1.getXPath() + "]", "t1/item/text()", xcom1.getXPath());
        assertEquals("Incorrect attribute element class.  Expected [" + String.class + "] but was [" + xcom1.getAttributeElementClass() + "]", xcom1.getAttributeElementClass(), String.class);
        DatabaseMapping dm2 = mappings.get(1);
        assertEquals("Incorrect mapping attribute name.  Expected [t2] but was [" + dm2.getAttributeName() + "]", "t2", dm2.getAttributeName());
        assertTrue("Incorrect mapping type.  Expected [(XML) AbstractCompositeDirectCollection mapping ]but was [" + dm2.getClass().getName() +"]", dm2.isXMLMapping() && dm2.isAbstractCompositeDirectCollectionMapping());
        XMLCompositeDirectCollectionMapping xcom2 = (XMLCompositeDirectCollectionMapping)dm2;
        assertEquals("Incorrect mapping XPath.  Expected [t2/item/text()] but was [" + xcom2.getXPath() + "]", "t2/item/text()", xcom2.getXPath());
        assertEquals("Incorrect attribute element class.  Expected [" + BigDecimal.class + "] but was [" + xcom2.getAttributeElementClass() + "]", xcom2.getAttributeElementClass(), BigDecimal.class);
        DatabaseMapping dm3 = mappings.get(2);
        assertEquals("Incorrect mapping attribute name.  Expected [t3] but was [" + dm3.getAttributeName() + "]", "t3", dm3.getAttributeName());
        assertTrue("Incorrect mapping type.  Expected [(XML) DirectToField mapping] but was [" + dm3.getClass().getName() + "]", dm3.isXMLMapping() && dm3.isDirectToFieldMapping());
        assertEquals("Incorrect mapping XPath.  Expected [t3/text()] but was [" + ((XMLDirectMapping) dm3).getXPath() + "]", "t3/text()", ((XMLDirectMapping) dm3).getXPath());
    }
}
