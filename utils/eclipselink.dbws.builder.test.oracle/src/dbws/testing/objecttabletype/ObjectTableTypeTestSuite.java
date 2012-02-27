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
 *     David McCann - October 24, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.objecttabletype;


//javase imports
import java.io.StringReader;
import org.w3c.dom.Document;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Project;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests Oracle Object Table types.
 *
 */
public class ObjectTableTypeTestSuite extends DBWSTestSuite {
	static final String PERSON_TYPE_ALIAS = "dbws_persontype";
	static final String PERSON_TYPE_CLASSNAME = "objecttabletypetests.Dbws_persontype";
	static final String PERSON_TYPE_TABLE_ALIAS = "dbws_persontype_table";
	static final String PERSON_TYPE_TABLE_CLASSNAME = "objecttabletypetests.Dbws_persontype_table_CollectionWrapper";

    static final String CREATE_PERSONTYPE =
        "CREATE OR REPLACE TYPE DBWS_PERSONTYPE AS OBJECT (" +
            "\nNAME VARCHAR2(20)," +
            "\nAGE NUMBER," +
            "\nGENDER VARCHAR2(1)," +
            "\nINCARCERATED DATE" +
        "\n)";
    static final String CREATE_PERSONTYPE_TABLE =
        "CREATE OR REPLACE TYPE DBWS_PERSONTYPE_TABLE AS TABLE OF DBWS_PERSONTYPE";
    static final String CREATE_MYEMPOBJECT =
        "CREATE OR REPLACE TYPE DBWS_MYEMPOBJECT AS OBJECT (" +
            "\nEMPNO NUMBER(4)," +
            "\nENAME VARCHAR2(10)" +
        "\n)";
    static final String CREATE_MYEMPOBJECT_TABLE =
        "CREATE OR REPLACE TYPE DBWS_MYEMPOBJECT_TABLE AS TABLE OF DBWS_MYEMPOBJECT";
    static final String CREATE_GROUPTYPE =
        "CREATE OR REPLACE TYPE DBWS_GROUPTYPE AS OBJECT (" +
            "\nNAME VARCHAR2(20)," +
            "\nECOUNT NUMBER," +
            "\nETABLE DBWS_MYEMPOBJECT_TABLE" +
        "\n)";
    static final String CREATE_GET_PERSONTYPE_PROC =
        "CREATE OR REPLACE PROCEDURE GET_PERSONTYPE_TABLE(PTABLE OUT DBWS_PERSONTYPE_TABLE) AS" +
        "\nBEGIN" +
            "\nPTABLE := DBWS_PERSONTYPE_TABLE();" +
            "\nPTABLE.EXTEND;" +
            "\nPTABLE(PTABLE.COUNT) := DBWS_PERSONTYPE('BUBBLES', 32, 'M', " +
                "TO_DATE('1990-11-19 00:00:00','YYYY-MM-DD HH24:MI:SS'));" +
            "\nPTABLE.EXTEND;" +
            "\nPTABLE(PTABLE.COUNT) := DBWS_PERSONTYPE('RICKY', 33, 'M', " +
                "TO_DATE('1985-10-01 00:00:00','YYYY-MM-DD HH24:MI:SS'));" +
            "\nPTABLE.EXTEND;" +
            "\nPTABLE(PTABLE.COUNT) := DBWS_PERSONTYPE('JULIAN', 35, 'M', " +
                "TO_DATE('1988-02-07 00:00:00','YYYY-MM-DD HH24:MI:SS'));" +
            "\nPTABLE.EXTEND;" +
            "\nPTABLE(PTABLE.COUNT) := DBWS_PERSONTYPE('SARAH', 25, 'F', " +
                "TO_DATE('2002-05-12 00:00:00','YYYY-MM-DD HH24:MI:SS'));" +
            "\nPTABLE.EXTEND;" +
            "\nPTABLE(PTABLE.COUNT) := DBWS_PERSONTYPE('J-ROC', 27, 'M', " +
                "TO_DATE('1998-12-17 00:00:00','YYYY-MM-DD HH24:MI:SS'));" +
        "\nEND GET_PERSONTYPE_TABLE;";
    static final String CREATE_GET_PERSONTYPE2_FUNC =
        "CREATE OR REPLACE FUNCTION GET_PERSONTYPE_TABLE2 RETURN DBWS_PERSONTYPE_TABLE AS" +
        "\nL_DATA DBWS_PERSONTYPE_TABLE;" +
        "\nBEGIN" +
            "\nGET_PERSONTYPE_TABLE(L_DATA);" +
            "\nRETURN L_DATA;" +
        "\nEND GET_PERSONTYPE_TABLE2;";
    static final String CREATE_GROUPTYPE_PROC =
        "CREATE OR REPLACE PROCEDURE CREATE_GROUPTYPE(GNAME IN VARCHAR2, NEWGROUP OUT DBWS_GROUPTYPE) AS" +
        "\nETABLE DBWS_MYEMPOBJECT_TABLE;" +
        "\nBEGIN" +
            "\nETABLE := DBWS_MYEMPOBJECT_TABLE();" +
            "\nETABLE.EXTEND;" +
            "\nETABLE(ETABLE.COUNT) := DBWS_MYEMPOBJECT(20, 'COREY');" +
            "\nETABLE.EXTEND;" +
            "\nETABLE(ETABLE.COUNT) := DBWS_MYEMPOBJECT(33, 'RICKY');" +
            "\nETABLE.EXTEND;" +
            "\nETABLE(ETABLE.COUNT) := DBWS_MYEMPOBJECT(32, 'BUBBLES');" +
            "\nNEWGROUP := DBWS_GROUPTYPE(GNAME, 3, ETABLE);" +
        "\nEND CREATE_GROUPTYPE;";
    static final String CREATE_ADD_PERSONTYPE_TO_TABLE_PROC =
        "CREATE OR REPLACE PROCEDURE ADD_PERSONTYPE_TO_TABLE(PTYPETOADD IN DBWS_PERSONTYPE, OLDTABLE IN DBWS_PERSONTYPE_TABLE, NEWTABLE OUT DBWS_PERSONTYPE_TABLE) AS" +
        "\nBEGIN" +
          "\nNEWTABLE := OLDTABLE;" +
          "\nNEWTABLE.EXTEND;" +
          "\nNEWTABLE(NEWTABLE.COUNT) := PTYPETOADD;" +
        "\nEND ADD_PERSONTYPE_TO_TABLE;";
    static final String CREATE_ADD_PERSONTYPE_TO_TABLE2_FUNC =
        "CREATE OR REPLACE FUNCTION ADD_PERSONTYPE_TO_TABLE2(PTYPETOADD IN DBWS_PERSONTYPE, OLDTABLE IN DBWS_PERSONTYPE_TABLE) RETURN DBWS_PERSONTYPE_TABLE AS" +
        "\nNEWTABLE DBWS_PERSONTYPE_TABLE;" +
        "\nBEGIN" +
            "\nADD_PERSONTYPE_TO_TABLE(PTYPETOADD, OLDTABLE, NEWTABLE);" +
            "\nRETURN NEWTABLE;" +
        "\nEND ADD_PERSONTYPE_TO_TABLE2;";

    static final String DROP_GET_PERSONTYPE_TABLE =
        "DROP PROCEDURE GET_PERSONTYPE_TABLE";
    static final String DROP_GET_PERSONTYPE2_FUNC =
        "DROP FUNCTION GET_PERSONTYPE_TABLE2";
    static final String DROP_ADD_PERSONTYPE_TO_TABLE_PROC =
        "DROP PROCEDURE ADD_PERSONTYPE_TO_TABLE";
    static final String DROP_ADD_PERSONTYPE_TO_TABLE2_FUNC =
        "DROP FUNCTION ADD_PERSONTYPE_TO_TABLE2";
    static final String DROP_GROUPTYPE_PROC =
        "DROP PROCEDURE CREATE_GROUPTYPE";
    static final String DROP_GROUPTYPE =
        "DROP TYPE DBWS_GROUPTYPE";
    static final String DROP_MYEMPOBJECT_TABLE =
        "DROP TYPE DBWS_MYEMPOBJECT_TABLE";
    static final String DROP_MYEMPOBJECT =
        "DROP TYPE DBWS_MYEMPOBJECT";
    static final String DROP_PERSONTYPE_TABLE =
        "DROP TYPE DBWS_PERSONTYPE_TABLE";
    static final String DROP_PERSONTYPE =
        "DROP TYPE DBWS_PERSONTYPE";

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
            runDdl(conn, CREATE_PERSONTYPE, ddlDebug);
            runDdl(conn, CREATE_PERSONTYPE_TABLE, ddlDebug);
            runDdl(conn, CREATE_MYEMPOBJECT, ddlDebug);
            runDdl(conn, CREATE_MYEMPOBJECT_TABLE, ddlDebug);
            runDdl(conn, CREATE_GROUPTYPE, ddlDebug);
            runDdl(conn, CREATE_GROUPTYPE_PROC, ddlDebug);
            runDdl(conn, CREATE_GET_PERSONTYPE_PROC, ddlDebug);
            runDdl(conn, CREATE_GET_PERSONTYPE2_FUNC, ddlDebug);
            runDdl(conn, CREATE_ADD_PERSONTYPE_TO_TABLE_PROC, ddlDebug);
            runDdl(conn, CREATE_ADD_PERSONTYPE_TO_TABLE2_FUNC, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">ObjectTableTypeTests</property>" +
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
              "<procedure " +
                  "name=\"GetPersonTypeTable\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GET_PERSONTYPE_TABLE\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"dbws_persontype_tableType\" " +
              "/>" +
              "<procedure " +
                  "name=\"GetPersonTypeTable2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"GET_PERSONTYPE_TABLE2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"dbws_persontype_tableType\" " +
              "/>" +
              "<procedure " +
                  "name=\"AddPersonTypeToTable\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"ADD_PERSONTYPE_TO_TABLE\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"dbws_persontype_tableType\" " +
              "/>" +
              "<procedure " +
                  "name=\"AddPersonTypeToTable2\" " +
                  "catalogPattern=\"TOPLEVEL\" " +
                  "procedurePattern=\"ADD_PERSONTYPE_TO_TABLE2\" " +
                  "isAdvancedJDBC=\"true\" " +
                  "returnType=\"dbws_persontype_tableType\" " +
              "/>" +
              "<procedure " +
	              "name=\"CreateGroupType\" " +
	              "catalogPattern=\"TOPLEVEL\" " +
	              "procedurePattern=\"CREATE_GROUPTYPE\" " +
	              "isAdvancedJDBC=\"true\" " +
	              "returnType=\"dbws_grouptypeType\" " +
	          "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_ADD_PERSONTYPE_TO_TABLE2_FUNC, ddlDebug);
            runDdl(conn, DROP_ADD_PERSONTYPE_TO_TABLE_PROC, ddlDebug);
            runDdl(conn, DROP_GET_PERSONTYPE2_FUNC, ddlDebug);
            runDdl(conn, DROP_GET_PERSONTYPE_TABLE, ddlDebug);
            runDdl(conn, DROP_GROUPTYPE_PROC, ddlDebug);
            runDdl(conn, DROP_GROUPTYPE, ddlDebug);
            runDdl(conn, DROP_MYEMPOBJECT_TABLE, ddlDebug);
            runDdl(conn, DROP_MYEMPOBJECT, ddlDebug);
            runDdl(conn, DROP_PERSONTYPE_TABLE, ddlDebug);
            runDdl(conn, DROP_PERSONTYPE, ddlDebug);
        }
    }

    @Test
    public void getPersonTypeTable() {
        Invocation invocation = new Invocation("GetPersonTypeTable");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void getPersonTypeTable2() {
        Invocation invocation = new Invocation("GetPersonTypeTable2");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(RESULT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String RESULT_XML =
        REGULAR_XML_HEADER +
        "<dbws_persontype_tableType xmlns=\"urn:ObjectTableTypeTests\">" +
            "<item>" +
                "<name>BUBBLES</name>" +
                "<age>32</age>" +
                "<gender>M</gender>" +
                "<incarcerated>1990-11-19</incarcerated>" +
            "</item>" +
            "<item>" +
                "<name>RICKY</name>" +
                "<age>33</age>" +
                "<gender>M</gender>" +
                "<incarcerated>1985-10-01</incarcerated>" +
            "</item>" +
            "<item>" +
                "<name>JULIAN</name>" +
                "<age>35</age>" +
                "<gender>M</gender>" +
                "<incarcerated>1988-02-07</incarcerated>" +
            "</item>" +
            "<item>" +
                "<name>SARAH</name>" +
                "<age>25</age>" +
                "<gender>F</gender>" +
                "<incarcerated>2002-05-12</incarcerated>" +
            "</item>" +
            "<item>" +
                "<name>J-ROC</name>" +
                "<age>27</age>" +
                "<gender>M</gender>" +
                "<incarcerated>1998-12-17</incarcerated>" +
            "</item>" +
        "</dbws_persontype_tableType>";

    @Test
    public void addPersonTypeToTable() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object personType = unmarshaller.unmarshal(new StringReader(PTYPE_INPUT_XML));
        Object tableType = unmarshaller.unmarshal(new StringReader(PTABLE_INPUT_XML));

        Invocation invocation = new Invocation("AddPersonTypeToTable");
        invocation.setParameter("PTYPETOADD", personType);
        invocation.setParameter("OLDTABLE", tableType);

        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NEW_PTABLE_OUTPUT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    @Test
    public void addPersonTypeToTable2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object personType = unmarshaller.unmarshal(new StringReader(PTYPE_INPUT_XML));
        Object tableType = unmarshaller.unmarshal(new StringReader(PTABLE_INPUT_XML));

        Invocation invocation = new Invocation("AddPersonTypeToTable2");
        invocation.setParameter("PTYPETOADD", personType);
        invocation.setParameter("OLDTABLE", tableType);

        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NEW_PTABLE_OUTPUT_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    static String PTYPE_INPUT_XML =
        REGULAR_XML_HEADER +
        "<dbws_persontypeType xmlns=\"urn:ObjectTableTypeTests\">" +
            "<name>COREY</name>" +
            "<age>20</age>" +
            "<gender>M</gender>" +
            "<incarcerated>1997-12-09</incarcerated>" +
        "</dbws_persontypeType>";
    static String PTYPE_INPUT2_XML =
        REGULAR_XML_HEADER +
        "<dbws_persontypeType xmlns=\"urn:ObjectTableTypeTests\">" +
	        "<name>RICKY</name>" +
	        "<age>33</age>" +
	        "<gender>M</gender>" +
	        "<incarcerated>1985-10-01</incarcerated>" +
        "</dbws_persontypeType>";
    static String PTYPE_INPUT3_XML =
        REGULAR_XML_HEADER +
        "<dbws_persontypeType xmlns=\"urn:ObjectTableTypeTests\">" +
	        "<name>BUBBLES</name>" +
	        "<age>32</age>" +
	        "<gender>M</gender>" +
	        "<incarcerated>1990-11-19</incarcerated>" +
        "</dbws_persontypeType>";

    static String PTABLE_INPUT_XML =
        REGULAR_XML_HEADER +
        "<dbws_persontype_tableType xmlns=\"urn:ObjectTableTypeTests\">" +
            "<item>" +
                "<name>BUBBLES</name>" +
                "<age>32</age>" +
                "<gender>M</gender>" +
                "<incarcerated>1990-11-19</incarcerated>" +
            "</item>" +
            "<item>" +
                "<name>RICKY</name>" +
                "<age>33</age>" +
                "<gender>M</gender>" +
                "<incarcerated>1985-10-01</incarcerated>" +
            "</item>" +
            "<item>" +
                "<name>JULIAN</name>" +
                "<age>35</age>" +
                "<gender>M</gender>" +
                "<incarcerated>1988-02-07</incarcerated>" +
            "</item>" +
            "<item>" +
                "<name>SARAH</name>" +
                "<age>25</age>" +
                "<gender>F</gender>" +
                "<incarcerated>2002-05-12</incarcerated>" +
            "</item>" +
            "<item>" +
                "<name>J-ROC</name>" +
                "<age>27</age>" +
                "<gender>M</gender>" +
                "<incarcerated>1998-12-17</incarcerated>" +
            "</item>" +
        "</dbws_persontype_tableType>";

    static String NEW_PTABLE_OUTPUT_XML =
        REGULAR_XML_HEADER +
        "<dbws_persontype_tableType xmlns=\"urn:ObjectTableTypeTests\">" +
            "<item>" +
                "<name>BUBBLES</name>" +
                "<age>32</age>" +
                "<gender>M</gender>" +
                "<incarcerated>1990-11-19</incarcerated>" +
            "</item>" +
            "<item>" +
                "<name>RICKY</name>" +
                "<age>33</age>" +
                "<gender>M</gender>" +
                "<incarcerated>1985-10-01</incarcerated>" +
            "</item>" +
            "<item>" +
                "<name>JULIAN</name>" +
                "<age>35</age>" +
                "<gender>M</gender>" +
                "<incarcerated>1988-02-07</incarcerated>" +
            "</item>" +
            "<item>" +
                "<name>SARAH</name>" +
                "<age>25</age>" +
                "<gender>F</gender>" +
                "<incarcerated>2002-05-12</incarcerated>" +
            "</item>" +
            "<item>" +
                "<name>J-ROC</name>" +
                "<age>27</age>" +
                "<gender>M</gender>" +
                "<incarcerated>1998-12-17</incarcerated>" +
            "</item>" +
            "<item>" +
                "<name>COREY</name>" +
                "<age>20</age>" +
                "<gender>M</gender>" +
                "<incarcerated>1997-12-09</incarcerated>" +
            "</item>" +
        "</dbws_persontype_tableType>";

    @Test
    public void validateJavaClassName() {
    	Project orProject = builder.getOrProject();
    	ClassDescriptor personTypeORDesc = orProject.getDescriptorForAlias(PERSON_TYPE_ALIAS);
    	assertNotNull("No OR descriptor found for alias [" + PERSON_TYPE_ALIAS + "]", personTypeORDesc);
    	assertEquals("Expected class name [" + PERSON_TYPE_CLASSNAME + "] but was [" + personTypeORDesc.getJavaClassName() + "]", personTypeORDesc.getJavaClassName(), PERSON_TYPE_CLASSNAME);
    	ClassDescriptor personTypeTableORDesc = orProject.getDescriptorForAlias(PERSON_TYPE_TABLE_ALIAS);
    	assertNotNull("No OR descriptor found for alias [" + PERSON_TYPE_TABLE_ALIAS + "]", personTypeTableORDesc);
    	assertEquals("Expected class name [" + PERSON_TYPE_TABLE_CLASSNAME + "] but was [" + personTypeTableORDesc.getJavaClassName() + "]", personTypeTableORDesc.getJavaClassName(), PERSON_TYPE_TABLE_CLASSNAME);
    	
    	Project oxProject = builder.getOxProject();
    	ClassDescriptor personTypeOXDesc = oxProject.getDescriptorForAlias(PERSON_TYPE_ALIAS);
    	assertNotNull("No OX descriptor found for alias [" + PERSON_TYPE_ALIAS + "]", personTypeOXDesc);
    	assertEquals("Expected class name [" + PERSON_TYPE_CLASSNAME + "] but was [" + personTypeOXDesc.getJavaClassName() + "]", personTypeOXDesc.getJavaClassName(), PERSON_TYPE_CLASSNAME);
    	ClassDescriptor personTypeTableOXDesc = oxProject.getDescriptorForAlias(PERSON_TYPE_TABLE_ALIAS);
    	assertNotNull("No OX descriptor found for alias [" + PERSON_TYPE_TABLE_ALIAS + "]", personTypeTableOXDesc);
    	assertEquals("Expected class name [" + PERSON_TYPE_TABLE_CLASSNAME + "] but was [" + personTypeTableOXDesc.getJavaClassName() + "]", personTypeTableOXDesc.getJavaClassName(), PERSON_TYPE_TABLE_CLASSNAME);
    }
    
    @Test
    public void createGroupTypeTest() {
        Invocation invocation = new Invocation("CreateGroupType");
        invocation.setParameter("GNAME", "MyNewGroup");

        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NEW_ETABLE_OUTPUT2_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    
    static String ETYPE_INPUT_XML =
        REGULAR_XML_HEADER +
        "<dbws_myempobjectType xmlns=\"urn:ObjectTableTypeTests\">" +
            "<empno>20</empno>" +
            "<ename>COREY</ename>" +
        "</dbws_myempobjectType>";
    static String ETYPE_INPUT2_XML =
        REGULAR_XML_HEADER +
        "<dbws_myempobjectType xmlns=\"urn:ObjectTableTypeTests\">" +
            "<empno>33</empno>" +
	        "<ename>RICKY</ename>" +
        "</dbws_myempobjectType>";
    static String ETYPE_INPUT3_XML =
        REGULAR_XML_HEADER +
        "<dbws_myempobjectType xmlns=\"urn:ObjectTableTypeTests\">" +
            "<empno>32</empno>" +
	        "<ename>BUBBLES</ename>" +
        "</dbws_myempobjectType>";
    static String NEW_ETABLE_OUTPUT2_XML =
        REGULAR_XML_HEADER +
	    "<dbws_grouptypeType xmlns=\"urn:ObjectTableTypeTests\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
		    "<name>MyNewGroup</name>" +
		    "<ecount>3</ecount>" +
		    "<etable>" +
			    "<item>" +
			        "<empno>20</empno>" +
				    "<ename>COREY</ename>" +
			    "</item>" +
	            "<item>" +
			        "<empno>33</empno>" +
				    "<ename>RICKY</ename>" +
	            "</item>" +
	            "<item>" +
			        "<empno>32</empno>" +
				    "<ename>BUBBLES</ename>" +
	            "</item>" +
		    "</etable>" +
	    "</dbws_grouptypeType>";
}