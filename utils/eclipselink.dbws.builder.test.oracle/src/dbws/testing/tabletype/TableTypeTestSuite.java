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
//     Mike Norman - June 10 2011, created DDL parser package
//     David McCann - July 2011, visit tests
package dbws.testing.tabletype;

//javase imports
import java.io.StringReader;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.descriptors.TransformerBasedFieldTransformation;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRDynamicEntity;
import org.eclipse.persistence.mappings.transformers.ConstantTransformer;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.dbws.BaseDBWSBuilderHelper;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.oracle.OracleHelper;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests TableType where the database table contains all relevant scalar
 * types except UROWID, INTERVALDAYTOSECOND, and INTERVALYEARTOMINTH.
 *
 */
public class TableTypeTestSuite extends DBWSTestSuite {

    static final String CREATE_TABLETYPE_TABLE =
        "CREATE TABLE TABLETYPE (" +
            "\nID NUMERIC(4) NOT NULL," +
            "\nNAME VARCHAR(25)," +
            "\nDEPTNO DECIMAL(2,0)," +
            "\nDEPTNAME VARCHAR2(40)," +
            "\nSECTION CHAR(1)," +
            "\nSAL FLOAT," +
            "\nCOMMISSION REAL," +
            "\nSALES DOUBLE PRECISION," +
            "\nBINID BLOB," +
            "\nB BLOB," +
            "\nC CLOB," +
            "\nR RAW(3)," +
            "\nPRIMARY KEY (ID)" +
        "\n)";
    static final String[] POPULATE_TABLETYPE_TABLE = new String[] {
        "INSERT INTO TABLETYPE (ID, NAME, DEPTNO, DEPTNAME, SECTION, SAL, COMMISSION, SALES, BINID, B, C, R) VALUES (1, 'mike', 99, 'sales', 'a', 100000.80, 450.80, 10000.80, '1010', '010101010101010101010101010101', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', '010101')",
        "INSERT INTO TABLETYPE (ID, NAME, DEPTNO, DEPTNAME, SECTION, SAL, COMMISSION, SALES, BINID, B, C, R) VALUES (2, 'merrick', 98, 'delivery', 'f', 20000, 0, 0, '0101', '020202020202020202020202020202', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', '020202')",
        "INSERT INTO TABLETYPE (ID, NAME, DEPTNO, DEPTNAME, SECTION, SAL, COMMISSION, SALES, BINID, B, C, R) VALUES (3, 'rick', 99, 'sales', 'b', 98000.20, 150.20, 2000.20, '1110', '030303030303030303030303030303', 'cccccccccccccccccccccccccccccc', '030303')"
    };
    static final String DROP_TABLETYPE_TABLE =
        "DROP TABLE TABLETYPE";

    static final String CREATE_TABLETYPE2_TABLE =
        "CREATE TABLE TABLETYPE2 (" +
            "\nID NUMERIC(4) NOT NULL," +
            "\nLR LONG RAW," +
            "\nPRIMARY KEY (ID)" +
        "\n)";
    static final String[] POPULATE_TABLETYPE2_TABLE = new String[] {
        "INSERT INTO TABLETYPE2 (ID, LR) VALUES (66, '010101010101010101')",
        "INSERT INTO TABLETYPE2 (ID, LR) VALUES (67, '020202020202020202')",
        "INSERT INTO TABLETYPE2 (ID, LR) VALUES (68, '030303030303030303')"
    };
    static final String DROP_TABLETYPE2_TABLE =
        "DROP TABLE TABLETYPE2";

    static boolean ddlCreate = false;
    static boolean ddlDrop = false;
    static boolean ddlDebug = false;

    @BeforeClass
    public static void setUp() throws WSDLException, SecurityException, NoSuchFieldException,
        IllegalArgumentException, IllegalAccessException {
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
            runDdl(conn, CREATE_TABLETYPE_TABLE, ddlDebug);
            runDdl(conn, CREATE_TABLETYPE2_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_TABLETYPE_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_TABLETYPE_TABLE[i]);
                }
                stmt.executeBatch();
                stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_TABLETYPE2_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_TABLETYPE2_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
        }
        DBWS_BUILDER_XML_USERNAME =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
          "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
            "<properties>" +
                "<property name=\"projectName\">tabletype</property>" +
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
            "<table " +
              "schemaPattern=\"%\" " +
              "tableNamePattern=\"TABLETYPE\" " +
            "/>" +
            "<table " +
              "schemaPattern=\"%\" " +
               "tableNamePattern=\"TABLETYPE2\" " +
            "/>" +
          "</dbws-builder>";
        builder = new DBWSBuilder();
        OracleHelper builderHelper = new OracleHelper(builder);
        builder.setBuilderHelper(builderHelper);
        Field workbenchProj_field = BaseDBWSBuilderHelper.class.getDeclaredField("workbenchXMLProject");
        workbenchProj_field.setAccessible(true);
        ObjectPersistenceWorkbenchXMLProject workbenchXMLProject =
            (ObjectPersistenceWorkbenchXMLProject)workbenchProj_field.get(builderHelper);
        XMLTransformationMapping versionMapping =
            (XMLTransformationMapping)workbenchXMLProject.getDescriptor(Project.class).
                getMappings().get(0);
        TransformerBasedFieldTransformation versionTransformer =
            (TransformerBasedFieldTransformation)versionMapping.getFieldTransformations().get(0);
        Field transformerField =
            TransformerBasedFieldTransformation.class.getDeclaredField("transformer");
        transformerField.setAccessible(true);
        ConstantTransformer constantTransformer =
            (ConstantTransformer)transformerField.get(versionTransformer);
        constantTransformer.setValue("Eclipse Persistence Services - " + releaseVersion);
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_TABLETYPE_TABLE, ddlDebug);
            runDdl(conn, DROP_TABLETYPE2_TABLE, ddlDebug);
        }
    }

    @Test
    public void findByPrimaryKeyTest() {
        Invocation invocation = new Invocation("findByPrimaryKey_TabletypeType");
        invocation.setParameter("id", 1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(ONE_PERSON_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void findAllTest() {
        Invocation invocation = new Invocation("findAll_TabletypeType");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Element ec = doc.createElement("tabletype-collection");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(ALL_PEOPLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void getLongRawTest() {
        Invocation invocation = new Invocation("findAll_Tabletype2Type");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        Element ec = doc.createElement("tabletype2-collection");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(LONG_RAW_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void updateTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        InputSource inputSource = new InputSource(new StringReader(ONE_PERSON_XML));
        XRDynamicEntity firstPerson = (XRDynamicEntity)unmarshaller.unmarshal(inputSource);
        firstPerson.set("sal", 112000.99);
        firstPerson.set("c", "ababababababababababababababab");
        // TODO: don't update binary data until we figure out how to round trip
        //firstPerson.set("b", "101010101010101010101010101011");
        Invocation invocation = new Invocation("update_TabletypeType");
        invocation.setParameter("theInstance", firstPerson);
        Operation op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);

        invocation = new Invocation("findByPrimaryKey_TabletypeType");
        invocation.setParameter("id", 1);
        op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(UPDATED_PERSON_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "but was:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
        // reset person
        firstPerson.set("sal", 100000.8);
        firstPerson.set("c", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        //firstPerson.set("b", "010101010101010101010101010101");
        invocation = new Invocation("update_TabletypeType");
        invocation.setParameter("theInstance", firstPerson);
        op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);
        xrService.getORSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Test
    public void createAndDeleteTest() {
        // create a new person
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        InputSource inputSource = new InputSource(new StringReader(NEW_PERSON_XML));
        XRDynamicEntity newPerson = (XRDynamicEntity)unmarshaller.unmarshal(inputSource);
        newPerson.set("id", 99);
        newPerson.set("name", "Joe Black");
        newPerson.set("deptno", "22");
        newPerson.set("deptname", "Janitor");
        newPerson.set("section", "q");
        newPerson.set("sal", 19000);
        newPerson.set("commission", 333);
        newPerson.set("sales", 1.00);
        // TODO: don't update binary data until we figure out how to round trip
        //newPerson.set("binid", new String("1001").getBytes());
        //newPerson.set("b", new String("111101010101010101010101010101").getBytes());
        newPerson.set("c", "adadadadadadadadadadadadadadad");
        // TODO: don't update binary data until we figure out how to round trip
        //newPerson.set("r", new String("110").getBytes());
        //newPerson.set("lr", new String("111111010101010101").getBytes());

        Invocation invocation = new Invocation("create_TabletypeType");
        invocation.setParameter("theInstance", newPerson);
        Operation op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);
        // verify create call succeeded
        invocation = new Invocation("findByPrimaryKey_TabletypeType");
        invocation.setParameter("id", 99);
        op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("Result is null after create call", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NEW_PERSON_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "but was:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));

        // delete newly created person
        invocation = new Invocation("delete_TabletypeType");
        invocation.setParameter("id", 99);
        op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);
        // verify delete succeeded
        invocation = new Invocation("findByPrimaryKey_TabletypeType");
        invocation.setParameter("id", 99);
        op = xrService.getOperation(invocation.getName());
        result = op.invoke(xrService, invocation);
        assertNull("Result is not null after delete call", result);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testUpdateSQL() {
        xrService.getORSession().getActiveSession().getIdentityMapAccessor().initializeIdentityMaps();

        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        InputSource inputSource = new InputSource(new StringReader(ONE_PERSON_XML));
        XRDynamicEntity firstPerson = (XRDynamicEntity) unmarshaller.unmarshal(inputSource);
        Float originalSal = firstPerson.get("sal");
        String originalC = firstPerson.get("c");
        firstPerson.set("sal", 112000.99);
        firstPerson.set("c", "ababababababababababababababab");

        DatabaseQuery updateQuery = xrService.getORSession().getQuery("update_TabletypeType");

        // map property names to parameter binding values
        Map<String, Integer> propOrder = new HashMap<String, Integer>();
        propOrder.put("id", 1);
        propOrder.put("name", 2);
        propOrder.put("deptno", 3);
        propOrder.put("deptname", 4);
        propOrder.put("section", 5);
        propOrder.put("sal", 6);
        propOrder.put("commission", 7);
        propOrder.put("sales", 8);
        propOrder.put("binid", 9);
        propOrder.put("b", 10);
        propOrder.put("c", 11);
        propOrder.put("r", 12);

        List<String> args = new ArrayList<String>();
        Vector<DatabaseField> fieldVector = new Vector<DatabaseField>(12);
        List<Object> argVals = new ArrayList<Object>();

        for (String prop : firstPerson.getPropertiesMap().keySet()) {
            args.add(propOrder.get(prop).toString());
            argVals.add(firstPerson.get(prop));
            fieldVector.add(new DatabaseField(prop.toUpperCase(), "TABLETYPE"));
        }

        // by default, JPA will create a DataReadQuery, but we want a DataModifyQuery
        DataModifyQuery query = new DataModifyQuery();
        query.setIsUserDefined(updateQuery.isUserDefined());
        query.copyFromQuery(updateQuery);
        // Need to clone call, in case was executed as read.
        query.setDatasourceCall((Call) updateQuery.getDatasourceCall().clone());

        query.setArguments(args);
        query.setArgumentValues(argVals);
        ((SQLCall) query.getDatasourceCall()).setFields(fieldVector);

        // need to create/set a translation row
        AbstractRecord row = query.rowFromArguments(argVals, (AbstractSession) xrService.getORSession());
        query.setTranslationRow(row);
        query.prepareCall(xrService.getORSession().getActiveSession(), row);
        query.setSession((AbstractSession) xrService.getORSession().getActiveSession());

        // execute the update
        query.executeDatabaseQuery();

        // verify update operation
        DatabaseQuery findQuery = xrService.getORSession().getActiveSession().getQuery("findByPrimaryKey_TabletypeType");
        findQuery.setIsPrepared(false);
        args = new ArrayList<String>();
        fieldVector = new Vector<DatabaseField>(12);
        argVals = new ArrayList<Object>();

        argVals.add(1);
        args.add("1");
        fieldVector.add(new DatabaseField("ID", "TABLETYPE"));

        findQuery.setArguments(args);
        findQuery.setArgumentValues(argVals);
        ((SQLCall) findQuery.getDatasourceCall()).setFields(fieldVector);

        // need to create/set a translation row
        row = findQuery.rowFromArguments(argVals, (AbstractSession) xrService.getORSession().getActiveSession());
        findQuery.setTranslationRow(row);
        findQuery.prepareCall(xrService.getORSession().getActiveSession(), row);
        findQuery.setSession((AbstractSession) xrService.getORSession().getActiveSession());
        xrService.getORSession().getActiveSession().getIdentityMapAccessor().initializeIdentityMaps();

        // execute the FindByPk
        Object result = findQuery.executeDatabaseQuery();

        assertTrue("Expected Vector, but result was " + result.getClass().getName(), result instanceof Vector);
        Vector resultVector = (Vector) result;
        assertTrue("Expected vector of size 1, but was " + resultVector.size(), resultVector.size() == 1);
        result = resultVector.get(0);
        assertTrue("Expected TableType (XRDynamicEntity) but was " + result.getClass().getName(), result instanceof XRDynamicEntity);

        // verify that 'sal' and 'c' fields were updated successfully
        XRDynamicEntity tableTypeEntity = (XRDynamicEntity) result;
        assertTrue("Expected [sal] '112000.99' but was '" + tableTypeEntity.get("sal") + "'", Float.compare(((Float) tableTypeEntity.get("sal")), new Float(112000.99)) == 0);

        Character[] chars = tableTypeEntity.get("c");
        StringBuilder sb = new StringBuilder(chars.length);
        for (Character c : chars) {
            sb.append(c.charValue());
        }
        String charStr = sb.toString();
        assertTrue("Expected [c] 'ababababababababababababababab' but was '" + tableTypeEntity.get("c") + "'", charStr.equals("ababababababababababababababab"));

        // reset original value
        firstPerson.set("sal", originalSal);
        firstPerson.set("c", originalC);
        Invocation invocation = new Invocation("update_TabletypeType");
        invocation.setParameter("theInstance", firstPerson);
        Operation op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);
    }

    @Test
    public void testCreateAndDeleteSQL() {
        xrService.getORSession().getActiveSession().getIdentityMapAccessor().initializeIdentityMaps();
        DatabaseQuery createQuery = xrService.getORSession().getQuery("create_TabletypeType");

        // by default, JPA will create a DataReadQuery, but we want a DataModifyQuery
        DataModifyQuery query = new DataModifyQuery();
        query.setIsUserDefined(createQuery.isUserDefined());
        query.copyFromQuery(createQuery);
        // Need to clone call, in case was executed as read.
        query.setDatasourceCall((Call) createQuery.getDatasourceCall().clone());

        List<String> args = new ArrayList<String>();
        Vector<DatabaseField> fieldVector = new Vector<DatabaseField>(12);
        List<Object> argVals = new ArrayList<Object>();

        argVals.add(99);
        argVals.add("Joe Black");
        argVals.add("22");
        argVals.add("Janitor");
        argVals.add("q");
        argVals.add(19000);
        argVals.add(333);
        argVals.add(1.00);
        argVals.add("1111");
        argVals.add("040404040404040404040404040404");
        argVals.add("dddddddddddddddddddddddddddddd");
        argVals.add("040404");

        fieldVector.add(new DatabaseField("ID", "TABLETYPE"));
        fieldVector.add(new DatabaseField("NAME", "TABLETYPE"));
        fieldVector.add(new DatabaseField("DEPTNO", "TABLETYPE"));
        fieldVector.add(new DatabaseField("DEPTNAME", "TABLETYPE"));
        fieldVector.add(new DatabaseField("SECTION", "TABLETYPE"));
        fieldVector.add(new DatabaseField("SAL", "TABLETYPE"));
        fieldVector.add(new DatabaseField("COMMISSION", "TABLETYPE"));
        fieldVector.add(new DatabaseField("SALES", "TABLETYPE"));
        fieldVector.add(new DatabaseField("BINID", "TABLETYPE"));
        fieldVector.add(new DatabaseField("B", "TABLETYPE"));
        fieldVector.add(new DatabaseField("C", "TABLETYPE"));
        fieldVector.add(new DatabaseField("R", "TABLETYPE"));

        for (int i=1; i<=argVals.size(); i++) {
            args.add(String.valueOf(i));
        }

        query.setArguments(args);
        query.setArgumentValues(argVals);
        ((SQLCall) query.getDatasourceCall()).setFields(fieldVector);

        // need to create/set a translation row
        AbstractRecord row = query.rowFromArguments(argVals, (AbstractSession) xrService.getORSession());
        query.setTranslationRow(row);
        query.prepareCall(xrService.getORSession(), query.getTranslationRow());
        query.setSession((AbstractSession) xrService.getORSession());
        query.executeDatabaseQuery();

        // verify create call succeeded
        Invocation invocation = new Invocation("findByPrimaryKey_TabletypeType");
        invocation.setParameter("id", 99);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("Result is null after create call", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(NEW_PERSON2_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "but was:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));

        // delete
        DatabaseQuery deleteQuery = xrService.getORSession().getQuery("delete_TabletypeType");

        // by default, JPA will create a DataReadQuery, but we want a DataModifyQuery
        query = new DataModifyQuery();
        query.setIsUserDefined(deleteQuery.isUserDefined());
        query.copyFromQuery(deleteQuery);
        // Need to clone call, in case was executed as read.
        query.setDatasourceCall((Call) deleteQuery.getDatasourceCall().clone());

        args = new ArrayList<String>();
        fieldVector = new Vector<DatabaseField>(12);
        argVals = new ArrayList<Object>();

        argVals.add(99);
        args.add("1");
        fieldVector.add(new DatabaseField("ID", "TABLETYPE"));

        query.setArguments(args);
        query.setArgumentValues(argVals);
        ((SQLCall) query.getDatasourceCall()).setFields(fieldVector);

        // need to create/set a translation row
        row = query.rowFromArguments(argVals, (AbstractSession) xrService.getORSession());
        query.setTranslationRow(row);
        query.prepareCall(xrService.getORSession(), query.getTranslationRow());
        query.setSession((AbstractSession) xrService.getORSession());
        query.executeDatabaseQuery();

        // verify delete call succeeded
        result = op.invoke(xrService, invocation);
        assertNull("Result not null after delete call", result);
    }

    @Test
    public void validateSchema() {
        Document testDoc = xmlParser.parse(new StringReader(DBWS_SCHEMA_STREAM.toString()));
        Document controlDoc = xmlParser.parse(new StringReader(XSD));
        removeEmptyTextNodes(testDoc);
        removeEmptyTextNodes(controlDoc);
        assertTrue("Schema validation failed:\nExpected:" + documentToString(controlDoc) + "\nbut was:\n" + documentToString(testDoc), comparer.isNodeEqual(controlDoc, testDoc));
    }

    @Test
    public void validateWSDL() {
        Document testDoc = xmlParser.parse(new StringReader(DBWS_WSDL_STREAM.toString()));
        Document controlDoc = xmlParser.parse(new StringReader(WSDL));
        removeEmptyTextNodes(testDoc);
        removeEmptyTextNodes(controlDoc);
        assertTrue("WSDL validation failed:\nExpected:" + documentToString(controlDoc) + "\nbut was:\n" + documentToString(testDoc), comparer.isNodeEqual(controlDoc, testDoc));
    }

    @Test
    public void validateORProject() {
        Document testDoc = xmlParser.parse(new StringReader(DBWS_OR_STREAM.toString()));
        Document controlDoc = xmlParser.parse(new StringReader(OR_PROJECT));
        removeEmptyTextNodes(testDoc);
        removeEmptyTextNodes(controlDoc);
        assertTrue("OR Project validation failed:\nExpected:" + documentToString(controlDoc) + "\nbut was:\n" + documentToString(testDoc), comparer.isNodeEqual(controlDoc, testDoc));
    }

    @Test
    public void validateOXProject() {
        Document testDoc = xmlParser.parse(new StringReader(DBWS_OX_STREAM.toString()));
        Document controlDoc = xmlParser.parse(new StringReader(OX_PROJECT));
        removeEmptyTextNodes(testDoc);
        removeEmptyTextNodes(controlDoc);
        assertTrue("OX Project validation failed:\nExpected:" + documentToString(controlDoc) + "\nbut was:\n" + documentToString(testDoc), comparer.isNodeEqual(controlDoc, testDoc));
    }

    protected static final String XSD =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xmime=\"http://www.w3.org/2005/05/xmlmime\" targetNamespace=\"urn:tabletype\" xmlns=\"urn:tabletype\" elementFormDefault=\"qualified\">\n" +
           "<xsd:complexType name=\"tabletype2Type\">\n" +
              "<xsd:sequence>\n" +
                "<xsd:element name=\"id\" type=\"xsd:decimal\"/>\n" +
                "<xsd:element name=\"lr\" type=\"xsd:base64Binary\" minOccurs=\"0\" nillable=\"true\" xmime:expectedContentTypes=\"application/octet-stream\"/>\n" +
              "</xsd:sequence>\n" +
           "</xsd:complexType>\n" +
           "<xsd:complexType name=\"tabletypeType\">\n" +
              "<xsd:sequence>\n" +
                 "<xsd:element name=\"id\" type=\"xsd:decimal\"/>\n" +
                 "<xsd:element name=\"name\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>\n" +
                 "<xsd:element name=\"deptno\" type=\"xsd:decimal\" minOccurs=\"0\" nillable=\"true\"/>\n" +
                 "<xsd:element name=\"deptname\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>\n" +
                 "<xsd:element name=\"section\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>\n" +
                 "<xsd:element name=\"sal\" type=\"xsd:double\" minOccurs=\"0\" nillable=\"true\"/>\n" +
                 "<xsd:element name=\"commission\" type=\"xsd:double\" minOccurs=\"0\" nillable=\"true\"/>\n" +
                 "<xsd:element name=\"sales\" type=\"xsd:double\" minOccurs=\"0\" nillable=\"true\"/>\n" +
                 "<xsd:element name=\"binid\" type=\"xsd:base64Binary\" minOccurs=\"0\" nillable=\"true\" xmime:expectedContentTypes=\"application/octet-stream\"/>\n" +
                 "<xsd:element name=\"b\" type=\"xsd:base64Binary\" minOccurs=\"0\" nillable=\"true\" xmime:expectedContentTypes=\"application/octet-stream\"/>\n" +
                 "<xsd:element name=\"c\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>\n" +
                 "<xsd:element name=\"r\" type=\"xsd:base64Binary\" minOccurs=\"0\" nillable=\"true\" xmime:expectedContentTypes=\"application/octet-stream\"/>\n" +
              "</xsd:sequence>\n" +
           "</xsd:complexType>\n" +
           "<xsd:element name=\"tabletype2Type\" type=\"tabletype2Type\"/>\n" +
           "<xsd:element name=\"tabletypeType\" type=\"tabletypeType\"/>\n" +
        "</xsd:schema>";

    protected static final String WSDL =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<wsdl:definitions\n" +
             "name=\"tabletypeService\"\n" +
             "targetNamespace=\"urn:tabletypeService\"\n" +
             "xmlns:ns1=\"urn:tabletype\"\n" +
             "xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"\n" +
             "xmlns:tns=\"urn:tabletypeService\"\n" +
             "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
             "xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"\n" +
             ">\n" +
             "<wsdl:types>\n" +
                "<xsd:schema elementFormDefault=\"qualified\" targetNamespace=\"urn:tabletypeService\" xmlns:tns=\"urn:tabletypeService\"\n" +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
                "<xsd:import namespace=\"urn:tabletype\" schemaLocation=\"eclipselink-dbws-schema.xsd\"/>\n" +
                "<xsd:complexType name=\"findByPrimaryKey_Tabletype2TypeResponseType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"result\">\n" +
                         "<xsd:complexType>\n" +
                            "<xsd:sequence>\n" +
                               "<xsd:element ref=\"ns1:tabletype2Type\" minOccurs=\"0\"/>\n" +
                            "</xsd:sequence>\n" +
                         "</xsd:complexType>\n" +
                      "</xsd:element>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"create_Tabletype2TypeRequestType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"theInstance\">\n" +
                         "<xsd:complexType>\n" +
                            "<xsd:sequence>\n" +
                               "<xsd:element ref=\"ns1:tabletype2Type\"/>\n" +
                            "</xsd:sequence>\n" +
                         "</xsd:complexType>\n" +
                      "</xsd:element>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"findByPrimaryKey_Tabletype2TypeRequestType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"id\" type=\"xsd:decimal\"/>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"findAll_TabletypeTypeRequestType\"/>\n" +
                "<xsd:complexType name=\"findAll_Tabletype2TypeRequestType\"/>\n" +
                "<xsd:complexType name=\"delete_Tabletype2TypeRequestType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"id\" type=\"xsd:decimal\"/>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"update_TabletypeTypeRequestType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"theInstance\">\n" +
                         "<xsd:complexType>\n" +
                            "<xsd:sequence>\n" +
                               "<xsd:element ref=\"ns1:tabletypeType\"/>\n" +
                            "</xsd:sequence>\n" +
                         "</xsd:complexType>\n" +
                      "</xsd:element>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"findAll_TabletypeTypeResponseType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"result\">\n" +
                         "<xsd:complexType>\n" +
                            "<xsd:sequence>\n" +
                               "<xsd:element maxOccurs=\"unbounded\" minOccurs=\"0\" ref=\"ns1:tabletypeType\"/>\n" +
                            "</xsd:sequence>\n" +
                         "</xsd:complexType>\n" +
                      "</xsd:element>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"delete_TabletypeTypeRequestType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"id\" type=\"xsd:decimal\"/>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"findByPrimaryKey_TabletypeTypeResponseType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"result\">\n" +
                         "<xsd:complexType>\n" +
                            "<xsd:sequence>\n" +
                               "<xsd:element minOccurs=\"0\" ref=\"ns1:tabletypeType\"/>\n" +
                            "</xsd:sequence>\n" +
                         "</xsd:complexType>\n" +
                      "</xsd:element>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"findAll_Tabletype2TypeResponseType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"result\">\n" +
                         "<xsd:complexType>\n" +
                            "<xsd:sequence>\n" +
                               "<xsd:element ref=\"ns1:tabletype2Type\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
                            "</xsd:sequence>\n" +
                         "</xsd:complexType>\n" +
                      "</xsd:element>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"update_Tabletype2TypeRequestType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"theInstance\">\n" +
                         "<xsd:complexType>\n" +
                            "<xsd:sequence>\n" +
                               "<xsd:element ref=\"ns1:tabletype2Type\"/>\n" +
                            "</xsd:sequence>\n" +
                         "</xsd:complexType>\n" +
                      "</xsd:element>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"findByPrimaryKey_TabletypeTypeRequestType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"id\" type=\"xsd:decimal\"/>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"create_TabletypeTypeRequestType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"theInstance\">\n" +
                         "<xsd:complexType>\n" +
                            "<xsd:sequence>\n" +
                               "<xsd:element ref=\"ns1:tabletypeType\"/>\n" +
                            "</xsd:sequence>\n" +
                         "</xsd:complexType>\n" +
                      "</xsd:element>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:element name=\"update_TabletypeType\" type=\"tns:update_TabletypeTypeRequestType\"/>\n" +
                "<xsd:element name=\"create_Tabletype2Type\" type=\"tns:create_Tabletype2TypeRequestType\"/>\n" +
                "<xsd:element name=\"findAll_TabletypeTypeResponse\" type=\"tns:findAll_TabletypeTypeResponseType\"/>\n" +
                "<xsd:element name=\"update_Tabletype2Type\" type=\"tns:update_Tabletype2TypeRequestType\"/>\n" +
                "<xsd:element name=\"findByPrimaryKey_TabletypeType\" type=\"tns:findByPrimaryKey_TabletypeTypeRequestType\"/>\n" +
                "<xsd:element name=\"findAll_Tabletype2TypeResponse\" type=\"tns:findAll_Tabletype2TypeResponseType\"/>\n" +
                "<xsd:element name=\"delete_Tabletype2Type\" type=\"tns:delete_Tabletype2TypeRequestType\"/>\n" +
                "<xsd:element name=\"findAll_Tabletype2Type\" type=\"tns:findAll_Tabletype2TypeRequestType\"/>\n" +
                "<xsd:element name=\"create_TabletypeType\" type=\"tns:create_TabletypeTypeRequestType\"/>\n" +
                "<xsd:element name=\"findAll_TabletypeType\" type=\"tns:findAll_TabletypeTypeRequestType\"/>\n" +
                "<xsd:element name=\"findByPrimaryKey_TabletypeTypeResponse\" type=\"tns:findByPrimaryKey_TabletypeTypeResponseType\"/>\n" +
                "<xsd:element name=\"FaultType\">\n" +
                   "<xsd:complexType>\n" +
                      "<xsd:sequence>\n" +
                         "<xsd:element name=\"faultCode\" type=\"xsd:string\"/>\n" +
                         "<xsd:element name=\"faultString\" type=\"xsd:string\"/>\n" +
                      "</xsd:sequence>\n" +
                   "</xsd:complexType>\n" +
                "</xsd:element>\n" +
                "<xsd:element name=\"findByPrimaryKey_Tabletype2Type\" type=\"tns:findByPrimaryKey_Tabletype2TypeRequestType\"/>\n" +
                "<xsd:element name=\"EmptyResponse\">\n" +
                   "<xsd:complexType/>\n" +
                "</xsd:element>\n" +
                "<xsd:element name=\"findByPrimaryKey_Tabletype2TypeResponse\" type=\"tns:findByPrimaryKey_Tabletype2TypeResponseType\"/>\n" +
                "<xsd:element name=\"delete_TabletypeType\" type=\"tns:delete_TabletypeTypeRequestType\"/>\n" +
                "</xsd:schema>\n" +
              "</wsdl:types>\n" +
              "<wsdl:message name=\"delete_Tabletype2TypeRequest\">\n" +
                 "<wsdl:part name=\"delete_Tabletype2TypeRequest\" element=\"tns:delete_Tabletype2Type\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"update_TabletypeTypeRequest\">\n" +
                 "<wsdl:part name=\"update_TabletypeTypeRequest\" element=\"tns:update_TabletypeType\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findByPrimaryKey_TabletypeTypeRequest\">\n" +
                 "<wsdl:part name=\"findByPrimaryKey_TabletypeTypeRequest\" element=\"tns:findByPrimaryKey_TabletypeType\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"create_TabletypeTypeRequest\">\n" +
                 "<wsdl:part name=\"create_TabletypeTypeRequest\" element=\"tns:create_TabletypeType\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findAll_Tabletype2TypeRequest\">\n" +
                 "<wsdl:part name=\"findAll_Tabletype2TypeRequest\" element=\"tns:findAll_Tabletype2Type\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"update_Tabletype2TypeRequest\">\n" +
                 "<wsdl:part name=\"update_Tabletype2TypeRequest\" element=\"tns:update_Tabletype2Type\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"create_Tabletype2TypeRequest\">\n" +
                 "<wsdl:part name=\"create_Tabletype2TypeRequest\" element=\"tns:create_Tabletype2Type\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findAll_TabletypeTypeResponse\">\n" +
                 "<wsdl:part name=\"findAll_TabletypeTypeResponse\" element=\"tns:findAll_TabletypeTypeResponse\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"FaultType\">\n" +
                 "<wsdl:part name=\"fault\" element=\"tns:FaultType\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findByPrimaryKey_TabletypeTypeResponse\">\n" +
                 "<wsdl:part name=\"findByPrimaryKey_TabletypeTypeResponse\" element=\"tns:findByPrimaryKey_TabletypeTypeResponse\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findByPrimaryKey_Tabletype2TypeRequest\">\n" +
                 "<wsdl:part name=\"findByPrimaryKey_Tabletype2TypeRequest\" element=\"tns:findByPrimaryKey_Tabletype2Type\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findByPrimaryKey_Tabletype2TypeResponse\">\n" +
                 "<wsdl:part name=\"findByPrimaryKey_Tabletype2TypeResponse\" element=\"tns:findByPrimaryKey_Tabletype2TypeResponse\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"EmptyResponse\">\n" +
                 "<wsdl:part name=\"emptyResponse\" element=\"tns:EmptyResponse\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findAll_Tabletype2TypeResponse\">\n" +
                 "<wsdl:part name=\"findAll_Tabletype2TypeResponse\" element=\"tns:findAll_Tabletype2TypeResponse\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findAll_TabletypeTypeRequest\">\n" +
                 "<wsdl:part name=\"findAll_TabletypeTypeRequest\" element=\"tns:findAll_TabletypeType\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"delete_TabletypeTypeRequest\">\n" +
                 "<wsdl:part name=\"delete_TabletypeTypeRequest\" element=\"tns:delete_TabletypeType\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:portType name=\"tabletypeService_Interface\">\n" +
                 "<wsdl:operation name=\"update_TabletypeType\">\n" +
                    "<wsdl:input message=\"tns:update_TabletypeTypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output name=\"update_TabletypeTypeEmptyResponse\" message=\"tns:EmptyResponse\">\n" +"</wsdl:output>\n" +
                    "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">\n" +"</wsdl:fault>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"delete_Tabletype2Type\">\n" +
                    "<wsdl:input message=\"tns:delete_Tabletype2TypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output name=\"delete_Tabletype2TypeEmptyResponse\" message=\"tns:EmptyResponse\">\n" +"</wsdl:output>\n" +
                    "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">\n" +"</wsdl:fault>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"create_Tabletype2Type\">\n" +
                    "<wsdl:input message=\"tns:create_Tabletype2TypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output name=\"create_Tabletype2TypeEmptyResponse\" message=\"tns:EmptyResponse\">\n" +"</wsdl:output>\n" +
                    "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">\n" +"</wsdl:fault>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"findAll_Tabletype2Type\">\n" +
                    "<wsdl:input message=\"tns:findAll_Tabletype2TypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output message=\"tns:findAll_Tabletype2TypeResponse\">\n" +"</wsdl:output>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"create_TabletypeType\">\n" +
                    "<wsdl:input message=\"tns:create_TabletypeTypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output name=\"create_TabletypeTypeEmptyResponse\" message=\"tns:EmptyResponse\">\n" +"</wsdl:output>\n" +
                    "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">\n" +"</wsdl:fault>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"findAll_TabletypeType\">\n" +
                    "<wsdl:input message=\"tns:findAll_TabletypeTypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output message=\"tns:findAll_TabletypeTypeResponse\">\n" +"</wsdl:output>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"update_Tabletype2Type\">\n" +
                    "<wsdl:input message=\"tns:update_Tabletype2TypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output name=\"update_Tabletype2TypeEmptyResponse\" message=\"tns:EmptyResponse\">\n" +"</wsdl:output>\n" +
                    "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">\n" +"</wsdl:fault>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"findByPrimaryKey_TabletypeType\">\n" +
                    "<wsdl:input message=\"tns:findByPrimaryKey_TabletypeTypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output message=\"tns:findByPrimaryKey_TabletypeTypeResponse\">\n" +"</wsdl:output>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"findByPrimaryKey_Tabletype2Type\">\n" +
                    "<wsdl:input message=\"tns:findByPrimaryKey_Tabletype2TypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output message=\"tns:findByPrimaryKey_Tabletype2TypeResponse\">\n" +"</wsdl:output>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"delete_TabletypeType\">\n" +
                    "<wsdl:input message=\"tns:delete_TabletypeTypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output name=\"delete_TabletypeTypeEmptyResponse\" message=\"tns:EmptyResponse\">\n" +"</wsdl:output>\n" +
                    "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">\n" +"</wsdl:fault>\n" +
                 "</wsdl:operation>\n" +
             "</wsdl:portType>\n" +
             "<wsdl:binding name=\"tabletypeService_SOAP_HTTP\" type=\"tns:tabletypeService_Interface\">\n" +
                "<soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>\n" +
                "<wsdl:operation name=\"update_TabletypeType\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:update_TabletypeType\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                   "<wsdl:fault name=\"FaultException\">\n" +
                       "<soap:fault name=\"FaultException\" use=\"literal\"/>\n" +
                   "</wsdl:fault>\n" +
                "</wsdl:operation>\n" +
                "<wsdl:operation name=\"delete_Tabletype2Type\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:delete_Tabletype2Type\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                   "<wsdl:fault name=\"FaultException\">\n" +
                      "<soap:fault name=\"FaultException\" use=\"literal\"/>\n" +
                   "</wsdl:fault>\n" +
                "</wsdl:operation>\n" +
                "<wsdl:operation name=\"create_Tabletype2Type\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:create_Tabletype2Type\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                   "<wsdl:fault name=\"FaultException\">\n" +
                      "<soap:fault name=\"FaultException\" use=\"literal\"/>\n" +
                   "</wsdl:fault>\n" +
                "</wsdl:operation>\n" +
                "<wsdl:operation name=\"findAll_Tabletype2Type\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:findAll_Tabletype2Type\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                "</wsdl:operation>\n" +
                "<wsdl:operation name=\"create_TabletypeType\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:create_TabletypeType\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                   "<wsdl:fault name=\"FaultException\">\n" +
                      "<soap:fault name=\"FaultException\" use=\"literal\"/>\n" +
                   "</wsdl:fault>\n" +
                "</wsdl:operation>\n" +
                "<wsdl:operation name=\"findAll_TabletypeType\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:findAll_TabletypeType\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                "</wsdl:operation>\n" +
                "<wsdl:operation name=\"update_Tabletype2Type\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:update_Tabletype2Type\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                   "<wsdl:fault name=\"FaultException\">\n" +
                       "<soap:fault name=\"FaultException\" use=\"literal\"/>\n" +
                   "</wsdl:fault>\n" +
                "</wsdl:operation>\n" +
                "<wsdl:operation name=\"findByPrimaryKey_TabletypeType\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:findByPrimaryKey_TabletypeType\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                "</wsdl:operation>\n" +
                "<wsdl:operation name=\"findByPrimaryKey_Tabletype2Type\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:findByPrimaryKey_Tabletype2Type\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                "</wsdl:operation>\n" +
                "<wsdl:operation name=\"delete_TabletypeType\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:delete_TabletypeType\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                   "<wsdl:fault name=\"FaultException\">\n" +
                      "<soap:fault name=\"FaultException\" use=\"literal\"/>\n" +
                   "</wsdl:fault>\n" +
                "</wsdl:operation>\n" +
             "</wsdl:binding>\n" +
             "<wsdl:service name=\"tabletypeService\">\n" +
                "<wsdl:port name=\"tabletypeServicePort\" binding=\"tns:tabletypeService_SOAP_HTTP\">\n" +
                   "<soap:address location=\"REPLACE_WITH_ENDPOINT_ADDRESS\"/>\n" +
                "</wsdl:port>\n" +
             "</wsdl:service>\n" +
        "</wsdl:definitions>\n";

    protected static final String OR_PROJECT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "\n<orm:entity-mappings xmlns:orm=\"http://www.eclipse.org/eclipselink/xsds/persistence/orm\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.eclipse.org/eclipselink/xsds/persistence/orm org/eclipse/persistence/jpa/eclipselink_orm_2_5.xsd\">" +
        "\n  <orm:entity access=\"VIRTUAL\" class=\"tabletype.Tabletype\">" +
        "\n    <orm:table name=\"TABLETYPE\"/>" +
        "\n    <orm:named-native-query name=\"findByPrimaryKey_TabletypeType\" result-class=\"tabletype.Tabletype\">" +
        "\n      <orm:query>SELECT * FROM TABLETYPE WHERE (ID = ?1)</orm:query>" +
        "\n    </orm:named-native-query>" +
        "\n    <orm:named-native-query name=\"findAll_TabletypeType\" result-class=\"tabletype.Tabletype\">" +
        "\n      <orm:query>SELECT * FROM TABLETYPE</orm:query>" +
        "\n    </orm:named-native-query>" +
        "\n    <orm:named-native-query name=\"update_TabletypeType\">" +
        "\n      <orm:query>UPDATE TABLETYPE SET NAME = ?2, DEPTNO = ?3, DEPTNAME = ?4, SECTION = ?5, SAL = ?6, COMMISSION = ?7, SALES = ?8, BINID = ?9, B = ?10, C = ?11, R = ?12 WHERE (ID = ?1)</orm:query>" +
        "\n    </orm:named-native-query>" +
        "\n    <orm:named-native-query name=\"create_TabletypeType\">" +
        "\n      <orm:query>INSERT INTO TABLETYPE (ID, NAME, DEPTNO, DEPTNAME, SECTION, SAL, COMMISSION, SALES, BINID, B, C, R) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12)</orm:query>" +
        "\n    </orm:named-native-query>" +
        "\n    <orm:named-native-query name=\"delete_TabletypeType\">" +
        "\n      <orm:query>DELETE FROM TABLETYPE WHERE (ID = ?1)</orm:query>" +
        "\n    </orm:named-native-query>" +
        "\n    <orm:attributes>" +
        "\n      <orm:id attribute-type=\"java.math.BigInteger\" name=\"id\">" +
        "\n        <orm:column name=\"ID\"/>" +
        "\n      </orm:id>" +
        "\n      <orm:basic attribute-type=\"java.lang.String\" name=\"name\">" +
        "\n        <orm:column name=\"NAME\"/>" +
        "\n      </orm:basic>" +
        "\n      <orm:basic attribute-type=\"java.math.BigInteger\" name=\"deptno\">" +
        "\n        <orm:column name=\"DEPTNO\"/>" +
        "\n      </orm:basic>" +
        "\n      <orm:basic attribute-type=\"java.lang.String\" name=\"deptname\">" +
        "\n        <orm:column name=\"DEPTNAME\"/>" +
        "\n      </orm:basic>" +
        "\n      <orm:basic attribute-type=\"java.lang.Character\" name=\"section\">" +
        "\n        <orm:column name=\"SECTION\"/>" +
        "\n      </orm:basic>" +
        "\n        <orm:basic attribute-type=\"java.lang.Float\" name=\"sal\">" +
        "\n        <orm:column name=\"SAL\"/>" +
        "\n      </orm:basic>" +
        "\n      <orm:basic attribute-type=\"java.lang.Float\" name=\"commission\">" +
        "\n        <orm:column name=\"COMMISSION\"/>" +
        "\n      </orm:basic>" +
        "\n      <orm:basic attribute-type=\"java.lang.Float\" name=\"sales\">" +
        "\n        <orm:column name=\"SALES\"/>" +
        "\n      </orm:basic>" +
        "\n      <orm:basic attribute-type=\"[B\" name=\"binid\">" +
        "\n        <orm:column name=\"BINID\"/>" +
        "\n      </orm:basic>" +
        "\n      <orm:basic attribute-type=\"[B\" name=\"b\">" +
        "\n        <orm:column name=\"B\"/>" +
        "\n      </orm:basic>" +
        "\n      <orm:basic attribute-type=\"[Ljava.lang.Character;\" name=\"c\">" +
        "\n        <orm:column name=\"C\"/>" +
        "\n      </orm:basic>" +
        "\n      <orm:basic attribute-type=\"[B\" name=\"r\">" +
        "\n        <orm:column name=\"R\"/>" +
        "\n      </orm:basic>" +
        "\n    </orm:attributes>" +
        "\n  </orm:entity>" +
        "\n  <orm:entity access=\"VIRTUAL\" class=\"tabletype.Tabletype2\">" +
        "\n    <orm:table name=\"TABLETYPE2\"/>" +
        "\n    <orm:named-native-query name=\"findByPrimaryKey_Tabletype2Type\" result-class=\"tabletype.Tabletype2\">" +
        "\n      <orm:query>SELECT * FROM TABLETYPE2 WHERE (ID = ?1)</orm:query>" +
        "\n    </orm:named-native-query>" +
        "\n    <orm:named-native-query name=\"findAll_Tabletype2Type\" result-class=\"tabletype.Tabletype2\">" +
        "\n      <orm:query>SELECT * FROM TABLETYPE2</orm:query>" +
        "\n    </orm:named-native-query>" +
        "\n    <orm:named-native-query name=\"delete_Tabletype2Type\">" +
        "\n      <orm:query>DELETE FROM TABLETYPE2 WHERE (ID = ?1)</orm:query>" +
        "\n    </orm:named-native-query>" +
        "\n    <orm:named-native-query name=\"create_Tabletype2Type\">" +
        "\n      <orm:query>INSERT INTO TABLETYPE2 (ID, LR) VALUES (?1, ?2)</orm:query>" +
        "\n    </orm:named-native-query>" +
        "\n    <orm:named-native-query name=\"update_Tabletype2Type\">" +
        "\n      <orm:query>UPDATE TABLETYPE2 SET LR = ?2 WHERE (ID = ?1)</orm:query>" +
        "\n    </orm:named-native-query>" +
        "\n    <orm:attributes>" +
        "\n      <orm:id attribute-type=\"java.math.BigInteger\" name=\"id\">" +
        "\n        <orm:column name=\"ID\"/>" +
        "\n      </orm:id>" +
        "\n      <orm:basic attribute-type=\"[B\" name=\"lr\">" +
        "\n        <orm:column name=\"LR\"/>" +
        "\n      </orm:basic>" +
        "\n    </orm:attributes>" +
        "\n  </orm:entity>" +
        "\n</orm:entity-mappings>";

    protected static final String OX_PROJECT =
        "<?xml version = '1.0' encoding = 'UTF-8'?>" +
        "<xml-bindings-list xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence/oxm\">" +
           "<xml-bindings package-name=\"tabletype\">" +
              "<xml-schema namespace=\"urn:tabletype\" element-form-default=\"QUALIFIED\"/>" +
              "<java-types>" +
                 "<java-type xml-accessor-type=\"FIELD\" name=\"Tabletype\">" +
                    "<xml-type namespace=\"urn:tabletype\" name=\"tabletypeType\"/>" +
                    "<xml-root-element namespace=\"urn:tabletype\" name=\"tabletypeType\"/>" +
                    "<java-attributes>" +
                       "<xml-element xml-path=\"id/text()\" type=\"java.math.BigInteger\" required=\"true\" java-attribute=\"id\">" +
                          "<xml-schema-type name=\"decimal\"/>" +
                       "</xml-element>" +
                       "<xml-element xml-path=\"name/text()\" type=\"java.lang.String\" java-attribute=\"name\">" +
                          "<xml-null-policy is-set-performed-for-absent-node=\"true\" null-representation-for-xml=\"XSI_NIL\" empty-node-represents-null=\"false\" xsi-nil-represents-null=\"true\"/>" +
                          "<xml-schema-type name=\"string\"/>" +
                       "</xml-element>" +
                       "<xml-element xml-path=\"deptno/text()\" type=\"java.math.BigInteger\" java-attribute=\"deptno\">" +
                          "<xml-null-policy is-set-performed-for-absent-node=\"true\" null-representation-for-xml=\"XSI_NIL\" empty-node-represents-null=\"false\" xsi-nil-represents-null=\"true\"/>" +
                          "<xml-schema-type name=\"decimal\"/>" +
                       "</xml-element>" +
                       "<xml-element xml-path=\"deptname/text()\" type=\"java.lang.String\" java-attribute=\"deptname\">" +
                          "<xml-null-policy is-set-performed-for-absent-node=\"true\" null-representation-for-xml=\"XSI_NIL\" empty-node-represents-null=\"false\" xsi-nil-represents-null=\"true\"/>" +
                          "<xml-schema-type name=\"string\"/>" +
                       "</xml-element>" +
                       "<xml-element xml-path=\"section/text()\" type=\"java.lang.Character\" java-attribute=\"section\">" +
                          "<xml-null-policy is-set-performed-for-absent-node=\"true\" null-representation-for-xml=\"XSI_NIL\" empty-node-represents-null=\"false\" xsi-nil-represents-null=\"true\"/>" +
                          "<xml-schema-type name=\"string\"/>" +
                       "</xml-element>" +
                       "<xml-element xml-path=\"sal/text()\" type=\"java.lang.Float\" java-attribute=\"sal\">" +
                          "<xml-null-policy is-set-performed-for-absent-node=\"true\" null-representation-for-xml=\"XSI_NIL\" empty-node-represents-null=\"false\" xsi-nil-represents-null=\"true\"/>" +
                          "<xml-schema-type name=\"double\"/>" +
                       "</xml-element>" +
                       "<xml-element xml-path=\"commission/text()\" type=\"java.lang.Float\" java-attribute=\"commission\">" +
                          "<xml-null-policy is-set-performed-for-absent-node=\"true\" null-representation-for-xml=\"XSI_NIL\" empty-node-represents-null=\"false\" xsi-nil-represents-null=\"true\"/>" +
                          "<xml-schema-type name=\"double\"/>" +
                       "</xml-element>" +
                       "<xml-element xml-path=\"sales/text()\" type=\"java.lang.Float\" java-attribute=\"sales\">" +
                          "<xml-null-policy is-set-performed-for-absent-node=\"true\" null-representation-for-xml=\"XSI_NIL\" empty-node-represents-null=\"false\" xsi-nil-represents-null=\"true\"/>" +
                          "<xml-schema-type name=\"double\"/>" +
                       "</xml-element>" +
                       "<xml-element xml-path=\"binid\" xml-mime-type=\"application/octet-stream\" xml-inline-binary-data=\"true\" type=\"[B\" java-attribute=\"binid\">" +
                          "<xml-null-policy is-set-performed-for-absent-node=\"true\" null-representation-for-xml=\"XSI_NIL\" empty-node-represents-null=\"false\" xsi-nil-represents-null=\"true\"/>" +
                          "<xml-schema-type name=\"base64Binary\"/>" +
                       "</xml-element>" +
                       "<xml-element xml-path=\"b\" xml-mime-type=\"application/octet-stream\" xml-inline-binary-data=\"true\" type=\"[B\" java-attribute=\"b\">" +
                          "<xml-null-policy is-set-performed-for-absent-node=\"true\" null-representation-for-xml=\"XSI_NIL\" empty-node-represents-null=\"false\" xsi-nil-represents-null=\"true\"/>" +
                          "<xml-schema-type name=\"base64Binary\"/>" +
                       "</xml-element>" +
                       "<xml-element xml-path=\"c/text()\" type=\"java.lang.String\" java-attribute=\"c\">" +
                          "<xml-null-policy is-set-performed-for-absent-node=\"true\" null-representation-for-xml=\"XSI_NIL\" empty-node-represents-null=\"false\" xsi-nil-represents-null=\"true\"/>" +
                          "<xml-schema-type name=\"string\"/>" +
                       "</xml-element>" +
                       "<xml-element xml-path=\"r\" xml-mime-type=\"application/octet-stream\" xml-inline-binary-data=\"true\" type=\"[B\" java-attribute=\"r\">" +
                          "<xml-null-policy is-set-performed-for-absent-node=\"true\" null-representation-for-xml=\"XSI_NIL\" empty-node-represents-null=\"false\" xsi-nil-represents-null=\"true\"/>" +
                          "<xml-schema-type name=\"base64Binary\"/>" +
                       "</xml-element>" +
                    "</java-attributes>" +
                 "</java-type>" +
                 "<java-type xml-accessor-type=\"FIELD\" name=\"Tabletype2\">" +
                    "<xml-type namespace=\"urn:tabletype\" name=\"tabletype2Type\"/>" +
                    "<xml-root-element namespace=\"urn:tabletype\" name=\"tabletype2Type\"/>" +
                    "<java-attributes>" +
                       "<xml-element xml-path=\"id/text()\" type=\"java.math.BigInteger\" required=\"true\" java-attribute=\"id\">" +
                          "<xml-schema-type name=\"decimal\"/>" +
                       "</xml-element>" +
                       "<xml-element xml-path=\"lr\" xml-mime-type=\"application/octet-stream\" xml-inline-binary-data=\"true\" type=\"[B\" java-attribute=\"lr\">" +
                          "<xml-null-policy is-set-performed-for-absent-node=\"true\" null-representation-for-xml=\"XSI_NIL\" empty-node-represents-null=\"false\" xsi-nil-represents-null=\"true\"/>" +
                          "<xml-schema-type name=\"base64Binary\"/>" +
                       "</xml-element>" +
                    "</java-attributes>" +
                 "</java-type>" +
              "</java-types>" +
           "</xml-bindings>" +
        "</xml-bindings-list>";

    protected static final String ONE_PERSON_XML =
        REGULAR_XML_HEADER +
        "<tabletypeType xmlns=\"urn:tabletype\">" +
          "<id>1</id>" +
          "<name>mike</name>" +
          "<deptno>99</deptno>" +
          "<deptname>sales</deptname>" +
          "<section>a</section>" +
          "<sal>100000.8</sal>" +
          "<commission>450.8</commission>" +
          "<sales>10000.8</sales>" +
          "<binid>EBA=</binid>" +
          "<b>AQEBAQEBAQEBAQEBAQEB</b>" +
          "<c>aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa</c>" +
          "<r>AQEB</r>" +
        "</tabletypeType>";

    protected static final String UPDATED_PERSON_XML =
        REGULAR_XML_HEADER +
        "<tabletypeType xmlns=\"urn:tabletype\">" +
          "<id>1</id>" +
          "<name>mike</name>" +
          "<deptno>99</deptno>" +
          "<deptname>sales</deptname>" +
          "<section>a</section>" +
          "<sal>112000.99</sal>" +
          "<commission>450.8</commission>" +
          "<sales>10000.8</sales>" +
          "<binid>EBA=</binid>" +
          "<b>AQEBAQEBAQEBAQEBAQEB</b>" +
          "<c>ababababababababababababababab</c>" +
          "<r>AQEB</r>" +
        "</tabletypeType>";


    // TODO: use this 'new person' once round trip binary is sorted out
    /*
    protected static final String NEW_PERSON_XML =
        REGULAR_XML_HEADER +
        "<tabletypeType xmlns=\"urn:tabletype\">" +
          "<id>99</id>" +
          "<name>Joe Black</name>" +
          "<deptno>22</deptno>" +
          "<deptname>Janitor</deptname>" +
          "<section>q</section>" +
          "<sal>19000.0</sal>" +
          "<commission>333.0</commission>" +
          "<sales>1.0</sales>" +
          "<binid>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAACEAE=</binid>" +
          "<b>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPEREBAQEBAQEBAQEBAQEB</b>" +
          "<c>adadadadadadadadadadadadadadad</c>" +
          "<r>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAADEQEB</r>" +
          "<lr>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAJERERAQEBAQEB</lr>" +
        "</tabletypeType>";
    */

    protected static final String NEW_PERSON_XML =
        REGULAR_XML_HEADER +
        "<tabletypeType xmlns=\"urn:tabletype\">" +
            "<id>99</id>" +
            "<name>Joe Black</name>" +
            "<deptno>22</deptno>" +
            "<deptname>Janitor</deptname>" +
            "<section>q</section>" +
            "<sal>19000.0</sal>" +
            "<commission>333.0</commission>" +
            "<sales>1.0</sales>" +
            "<binid xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
            "<b xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
            "<c>adadadadadadadadadadadadadadad</c>" +
            "<r xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
        "</tabletypeType>";

    protected static final String NEW_PERSON2_XML =
        REGULAR_XML_HEADER +
        "<tabletypeType xmlns=\"urn:tabletype\">" +
            "<id>99</id>" +
            "<name>Joe Black</name>" +
            "<deptno>22</deptno>" +
            "<deptname>Janitor</deptname>" +
            "<section>q</section>" +
            "<sal>19000.0</sal>" +
            "<commission>333.0</commission>" +
            "<sales>1.0</sales>" +
            "<binid>ERE=</binid>" +
            "<b>BAQEBAQEBAQEBAQEBAQE</b>" +
            "<c>dddddddddddddddddddddddddddddd</c>" +
            "<r>BAQE</r>" +
        "</tabletypeType>";

    protected static final String ALL_PEOPLE_XML =
        REGULAR_XML_HEADER +
        "<tabletype-collection>" +
            "<tabletypeType xmlns=\"urn:tabletype\">" +
                "<id>1</id>" +
                "<name>mike</name>" +
                "<deptno>99</deptno>" +
                "<deptname>sales</deptname>" +
                "<section>a</section>" +
                "<sal>100000.8</sal>" +
                "<commission>450.8</commission>" +
                "<sales>10000.8</sales>" +
                "<binid>EBA=</binid>" +
                "<b>AQEBAQEBAQEBAQEBAQEB</b>" +
                "<c>aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa</c>" +
                "<r>AQEB</r>" +
            "</tabletypeType>" +
            "<tabletypeType xmlns=\"urn:tabletype\">" +
                "<id>2</id>" +
                "<name>merrick</name>" +
                "<deptno>98</deptno>" +
                "<deptname>delivery</deptname>" +
                "<section>f</section>" +
                "<sal>20000.0</sal>" +
                "<commission>0.0</commission>" +
                "<sales>0.0</sales>" +
                "<binid>AQE=</binid>" +
                "<b>AgICAgICAgICAgICAgIC</b>" +
                "<c>bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb</c>" +
                "<r>AgIC</r>" +
            "</tabletypeType>" +
            "<tabletypeType xmlns=\"urn:tabletype\">" +
                "<id>3</id>" +
                "<name>rick</name>" +
                "<deptno>99</deptno>" +
                "<deptname>sales</deptname>" +
                "<section>b</section>" +
                "<sal>98000.2</sal>" +
                "<commission>150.2</commission>" +
                "<sales>2000.2</sales>" +
                "<binid>ERA=</binid>" +
                "<b>AwMDAwMDAwMDAwMDAwMD</b>" +
                "<c>cccccccccccccccccccccccccccccc</c>" +
                "<r>AwMD</r>" +
            "</tabletypeType>" +
        "</tabletype-collection>";

    protected static final String LONG_RAW_XML =
        REGULAR_XML_HEADER +
        "<tabletype2-collection>" +
           "<tabletype2Type xmlns=\"urn:tabletype\">" +
              "<id>66</id>" +
              "<lr>AQEBAQEBAQEB</lr>" +
           "</tabletype2Type>" +
           "<tabletype2Type xmlns=\"urn:tabletype\">" +
              "<id>67</id>" +
              "<lr>AgICAgICAgIC</lr>" +
           "</tabletype2Type>" +
           "<tabletype2Type xmlns=\"urn:tabletype\">" +
              "<id>68</id>" +
              "<lr>AwMDAwMDAwMD</lr>" +
           "</tabletype2Type>" +
        "</tabletype2-collection>";
}
