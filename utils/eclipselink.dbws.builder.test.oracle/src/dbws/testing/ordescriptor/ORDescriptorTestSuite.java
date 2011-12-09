/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David McCann - November 16, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.ordescriptor;

//javase imports
import java.util.Vector;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.eclipse.persistence.internal.xr.XRDynamicClassLoader.COLLECTION_WRAPPER_SUFFIX;
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
    	"CREATE OR REPLACE PACKAGE ORPACKAGE AS" +
            "\nTYPE TBL1 IS TABLE OF VARCHAR2(111) INDEX BY BINARY_INTEGER;" +
    	    "\nTYPE TBL2 IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;" +
            "\nTYPE ARECORD IS RECORD (" +
    	        "\nT1 TBL1," +
                "\nT2 TBL2," +
    	        "\nT3 BOOLEAN" +
            "\n);" +
    	    "\nTYPE TBL3 IS TABLE OF ARECORD INDEX BY PLS_INTEGER;" +
            "\nTYPE TBL4 IS TABLE OF TBL2 INDEX BY PLS_INTEGER;" +
    	    "\nTYPE TBL3 IS TABLE OF ARECORD INDEX BY PLS_INTEGER;" +
    	    "\nTYPE TBL4 IS TABLE OF TBL2 INDEX BY PLS_INTEGER;" +
    	    "\nPROCEDURE P1(SIMPLARRAY IN TBL1, FOO IN VARCHAR2);" +
    	    "\nPROCEDURE P2(OLD IN TBL2, NEW IN TBL2);" +
    	    "\nPROCEDURE P4(REC IN ARECORD);" +
    	    "\nPROCEDURE P5(OLDREC IN ARECORD, NEWREC OUT ARECORD);" +
    	    "\nPROCEDURE P7(SIMPLARRAY IN TBL1, FOO IN VARCHAR2);" +
    	    "\nPROCEDURE P7(SIMPLARRAY IN TBL1, FOO IN VARCHAR2, BAR IN VARCHAR2);" +
    	    "\nPROCEDURE P8(FOO IN VARCHAR2);" +
    	    "\nPROCEDURE P8(FOO IN VARCHAR2, BAR IN VARCHAR2);" +
    	    "\nFUNCTION F2(OLD IN TBL4, SIMPLARRAY IN TBL1) RETURN TBL2;" +
    	    "\nFUNCTION F4(RECARRAY IN TBL3, OLDREC IN ARECORD) RETURN TBL3;" +
    	"\nEND ORPACKAGE;";

    static final String CREATE_PACKAGE_BODY_ORPACKAGE =
        "\nCREATE OR REPLACE PACKAGE BODY ORPACKAGE AS" +
            "\nPROCEDURE P1(SIMPLARRAY IN TBL1, FOO IN VARCHAR2) AS" +
            "\nBEGIN" +
                "\nNULL;" +
            "\nEND P1;" +
            "\nPROCEDURE P2(OLD IN TBL2, NEW IN TBL2) AS" +
            "\nBEGIN" +
                "\nNULL;" +
            "\nEND P2;" +
            "\nPROCEDURE P4(REC IN ARECORD) AS" +
            "\nBEGIN" +
                "\nNULL;" +
            "\nEND P4;" +
            "\nPROCEDURE P5(OLDREC IN ARECORD, NEWREC OUT ARECORD) AS" +
            "\nBEGIN" +
                "\nNEWREC.T1 := OLDREC.T1;" +
                "\nNEWREC.T2 := OLDREC.T2;" +
                "\nNEWREC.T3 := OLDREC.T3;" +
             "\nEND P5;" +
             "\nPROCEDURE P7(SIMPLARRAY IN TBL1, FOO IN VARCHAR2) AS" +
             "\nBEGIN" +
                 "\nNULL;" +
             "\nEND P7;" +
             "\nPROCEDURE P7(SIMPLARRAY IN TBL1, FOO IN VARCHAR2, BAR IN VARCHAR2) AS" +
             "\nBEGIN" +
                 "\nNULL;" +
             "\nEND P7;" +
             "\nPROCEDURE P8(FOO IN VARCHAR2) AS" +
             "\nBEGIN" +
                 "\nNULL;" +
             "\nEND P8;" +
             "\nPROCEDURE P8(FOO IN VARCHAR2, BAR IN VARCHAR2) AS" +
             "\nBEGIN" +
                 "\nNULL;" +
             "\nEND P8;" +
             "\nFUNCTION F2(OLD IN TBL4, SIMPLARRAY IN TBL1) RETURN TBL2 IS" +
             "\nBEGIN" +
                 "\nRETURN OLD;" +
             "\nEND F2;" +
             "\nFUNCTION F4(RECARRAY IN TBL3, OLDREC IN ARECORD) RETURN TBL3 IS" +
             "\nBEGIN" +
                 "\nRETURN RECARRAY;" +
             "\nEND F4;" +
         "\nEND ORPACKAGE;";

    static final String CREATE_TYPE_ORPACKAGE_TBL1 =
    	"CREATE OR REPLACE TYPE ORPACKAGE_TBL1 AS TABLE OF VARCHAR2(111)";
    static final String CREATE_TYPE_ORPACKAGE_TBL2 =
    	"CREATE OR REPLACE TYPE ORPACKAGE_TBL2 AS TABLE OF NUMBER";
    static final String CREATE_TYPE_ORPACKAGE_ARECORD =
        "CREATE OR REPLACE TYPE ORPACKAGE_ARECORD AS OBJECT (" +
    	      "\nT1 ORPACKAGE_TBL1," +
    	      "\nT2 ORPACKAGE_TBL2," +
    	      "\nT3 INTEGER" +
    	 "\n)";
    static final String CREATE_TYPE_ORPACKAGE_TBL3 =
    	"CREATE OR REPLACE TYPE ORPACKAGE_TBL3 AS TABLE OF ORPACKAGE_ARECORD";
    static final String CREATE_TYPE_ORPACKAGE_TBL4 =
    	"CREATE OR REPLACE TYPE ORPACKAGE_TBL4 AS TABLE OF ORPACKAGE_TBL2";

    static final String DROP_PACKAGE_ORPACKAGE =
        "DROP PACKAGE ORPACKAGE";
    static final String DROP_PACKAGE_BODY_ORPACKAGE =
        "DROP PACKAGE BODY ORPACKAGE";
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
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            runDdl(conn, DROP_PACKAGE_BODY_ORPACKAGE, ddlDebug);
            runDdl(conn, DROP_PACKAGE_ORPACKAGE, ddlDebug);
            runDdl(conn, DROP_TYPE_ORPACKAGE_TBL4, ddlDebug);
            runDdl(conn, DROP_TYPE_ORPACKAGE_TBL3, ddlDebug);
            runDdl(conn, DROP_TYPE_ORPACKAGE_ARECORD, ddlDebug);
            runDdl(conn, DROP_TYPE_ORPACKAGE_TBL2, ddlDebug);
            runDdl(conn, DROP_TYPE_ORPACKAGE_TBL1, ddlDebug);
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
    	ClassDescriptor tbl2Desc = null;
    	ClassDescriptor tbl4Desc = null;
        for (ClassDescriptor cDesc : builder.getOrProject().getOrderedDescriptors()) {
        	String alias = cDesc.getAlias();
        	if (alias.equals(TBL4_DESCRIPTOR_ALIAS)) {
        		tbl4Desc = cDesc;
        	} else if (alias.equals(TBL2_DESCRIPTOR_ALIAS)) {
        		tbl2Desc = cDesc;
        	}
        }
        assertNotNull("No descriptor was found with alias [" + TBL4_DESCRIPTOR_ALIAS + "]", tbl4Desc);
        assertNotNull("No descriptor was found with alias [" + TBL2_DESCRIPTOR_ALIAS + "]", tbl2Desc);

        tbl2Asserts(tbl2Desc);
        tbl4Asserts(tbl4Desc);
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
        assertTrue("Wrong Java class name.  Expected [" + TBL1_DESCRIPTOR_JAVACLASSNAME + "] but was [" + tbl1Descriptor.getJavaClassName() + "]", tbl1Descriptor.getJavaClassName().equals(TBL1_DESCRIPTOR_JAVACLASSNAME));
        Vector<DatabaseMapping> mappings = tbl1Descriptor.getMappings();
        assertTrue("Wrong number of mappings.  Expected [1] but was [" + mappings.size() + "]", mappings.size() == 1);
        DatabaseMapping mapping = mappings.get(0);
        assertTrue("Incorrect mapping attribute name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME + "] but was [" + mapping.getAttributeName() + "]", mapping.getAttributeName().equals(BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME));
        assertTrue("Incorrect mapping field name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME + "] but was [" + mapping.getField().getName() + "]", mapping.getField().getName().equals(BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME));
        assertTrue("Incorrect mapping type.  Expected [AbstractCompositeDirectCollection mapping], but was [" + mapping.getClass().getName() + "]",  mapping.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping = (ArrayMapping)mapping;
        assertTrue("Wrong structure name for mapping.  Expected [" + TBL1_COMPATIBLETYPE + "] but was [" + arrayMapping.getStructureName() + "]", arrayMapping.getStructureName().equals(TBL1_COMPATIBLETYPE));
    }

    protected void tbl2Asserts(ClassDescriptor tbl2Descriptor) {
        assertTrue("Wrong Java class name.  Expected [" + TBL2_DESCRIPTOR_JAVACLASSNAME + "] but was [" + tbl2Descriptor.getJavaClassName() + "]", tbl2Descriptor.getJavaClassName().equals(TBL2_DESCRIPTOR_JAVACLASSNAME));
        Vector<DatabaseMapping> mappings = tbl2Descriptor.getMappings();
        assertTrue("Wrong number of mappings.  Expected [1] but was [" + mappings.size() + "]", mappings.size() == 1);
        DatabaseMapping mapping = mappings.get(0);
        assertTrue("Incorrect mapping attribute name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME + "] but was [" + mapping.getAttributeName() + "]", mapping.getAttributeName().equals(BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME));
        assertTrue("Incorrect mapping field name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME + "] but was [" + mapping.getField().getName() + "]", mapping.getField().getName().equals(BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME));
        assertTrue("Incorrect mapping type.  Expected [AbstractCompositeDirectCollection mapping], but was [" + mapping.getClass().getName() + "]", mapping.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping = (ArrayMapping)mapping;
        assertTrue("Wrong structure name for mapping.  Expected [" + TBL2_COMPATIBLETYPE + "] but was [" + arrayMapping.getStructureName() + "]", arrayMapping.getStructureName().equals(TBL2_COMPATIBLETYPE));
    }

    protected void tbl3Asserts(ClassDescriptor tbl3Descriptor) {
        assertTrue("Wrong Java class name.  Expected [" + TBL3_DESCRIPTOR_JAVACLASSNAME + "] but was [" + tbl3Descriptor.getJavaClassName() + "]", tbl3Descriptor.getJavaClassName().equals(TBL3_DESCRIPTOR_JAVACLASSNAME));
        Vector<DatabaseMapping> mappings = tbl3Descriptor.getMappings();
        assertTrue("Wrong number of mappings.  Expected [1] but was [" + mappings.size() + "]", mappings.size() == 1);
        DatabaseMapping mapping = mappings.get(0);
        assertTrue("Incorrect mapping attribute name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME + "] but was [" + mapping.getAttributeName() + "]", mapping.getAttributeName().equals(BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME));
        assertTrue("Incorrect mapping field name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME + "] but was [" + mapping.getField().getName() + "]", mapping.getField().getName().equals(BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME));
        assertTrue("Incorrect mapping type.  Expected [AbstractCompositeCollection mapping] but was [" + mapping.getClass().getName() + "]", mapping.isAbstractCompositeCollectionMapping());

        ObjectArrayMapping oArrayMapping = (ObjectArrayMapping)mapping;
        assertTrue("Incorrect structure name.  Expected [" + TBL3_COMPATIBLETYPE + "] but was [" + oArrayMapping.getStructureName() + "]", oArrayMapping.getStructureName().equals(TBL3_COMPATIBLETYPE));
        assertTrue("Incorrect reference class name.  Expected [" + ARECORD_DESCRIPTOR_JAVACLASSNAME + "] but was [" + oArrayMapping.getReferenceClassName() + "]", oArrayMapping.getReferenceClassName().equals(ARECORD_DESCRIPTOR_JAVACLASSNAME));
    }
    protected void tbl4Asserts(ClassDescriptor tbl4Descriptor) {
        assertTrue("Wrong Java class name.  Expected [" +TBL4_DESCRIPTOR_JAVACLASSNAME + "] but was [" + tbl4Descriptor.getJavaClassName() + "]", tbl4Descriptor.getJavaClassName().equals(TBL4_DESCRIPTOR_JAVACLASSNAME));
        Vector<DatabaseMapping> mappings = tbl4Descriptor.getMappings();
        assertTrue("Wrong number of mappings.  Expected [1] but was [" + mappings.size() + "]", mappings.size() == 1);
        DatabaseMapping mapping = mappings.get(0);
        assertTrue("Incorrect mapping attribute name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME + "] but was [" + mapping.getAttributeName() + "]", mapping.getAttributeName().equals(BaseDBWSBuilderHelper.ITEMS_MAPPING_ATTRIBUTE_NAME));
        assertTrue("Incorrect mapping field name.  Expected [" + BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME + "] but was [" + mapping.getField().getName() + "]", mapping.getField().getName().equals(BaseDBWSBuilderHelper.ITEMS_MAPPING_FIELD_NAME));
        assertTrue("Incorrect mapping type.  Expected [isAbstractCompositeDirectCollectionMapping mapping] but was [" + mapping.getClass().getName() + "]", mapping.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping2 = (ArrayMapping)mapping;
        assertTrue("Incorrect structure name.  Expected [" + TBL4_COMPATIBLETYPE + "] but was [" +arrayMapping2.getStructureName() + "]", arrayMapping2.getStructureName().equals(TBL4_COMPATIBLETYPE));
    }
    protected void aRecordAsserts(ClassDescriptor aRecordDescriptor) {
        assertTrue("Wrong Java class name.  Expected [" + ARECORD_DESCRIPTOR_JAVACLASSNAME + "] but was [" + aRecordDescriptor.getJavaClassName() + "]", aRecordDescriptor.getJavaClassName().equals(ARECORD_DESCRIPTOR_JAVACLASSNAME));
        Vector<DatabaseMapping> mappings = aRecordDescriptor.getMappings();
        assertTrue("Wrong number of mappings.  Expected [3] but was [" + mappings.size() + "]", mappings.size() == 3);
        DatabaseMapping dm1 = mappings.get(0);
        assertTrue("Incorrect mapping attribute name.  Expected [t1] but was [" + dm1.getAttributeName() + "]", dm1.getAttributeName().equals("t1"));
        assertTrue("Incorrect mapping type.  Expected [AbstractCompositeDirectCollection mapping] but was [" + dm1.getClass().getName() +"]", dm1.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping1 = (ArrayMapping)dm1;
        assertTrue("Incorrect attribute element class.  Expected [" + TBL1_COMPATIBLETYPE + "] but was [" + arrayMapping1.getStructureName() + "]", arrayMapping1.getStructureName().equals(TBL1_COMPATIBLETYPE));
        DatabaseMapping dm2 = mappings.get(1);
        assertTrue("Incorrect mapping attribute name.  Expected [t2] but was [" + dm2.getAttributeName() + "]", dm2.getAttributeName().equals("t2"));
        assertTrue("Incorrect mapping type.  Expected [AbstractCompositeDirectCollection mapping ]but was [" + dm2.getClass().getName() +"]", dm2.isAbstractCompositeDirectCollectionMapping());
        ArrayMapping arrayMapping2 = (ArrayMapping)dm2;
        assertTrue("Incorrect structure name.  Expected [" + TBL2_COMPATIBLETYPE + "] but was [" + arrayMapping2.getStructureName() + "]", arrayMapping2.getStructureName().equals(TBL2_COMPATIBLETYPE));
        DatabaseMapping dm3 = mappings.get(2);
        assertTrue("Incorrect mapping attribute name.  Expected [t3] but was [" + dm3.getAttributeName() + "]", dm3.getAttributeName().equals("t3"));
        assertTrue("Incorrect mapping type.  Expected [DirectToField mapping] but was [" + dm3.getClass().getName() + "]", dm3.isDirectToFieldMapping());
    }
}