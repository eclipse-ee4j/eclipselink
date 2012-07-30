/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - June 10 2011, created DDL parser package
 *     David McCann - July 2011, visit tests
 ******************************************************************************/
package dbws.testing.tabletype;

//javase imports
import java.io.StringReader;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Statement;
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
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.internal.xr.XRDynamicEntity;
import org.eclipse.persistence.mappings.transformers.ConstantTransformer;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
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
        Invocation invocation = new Invocation("findByPrimaryKey_tabletypeType");
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
        Invocation invocation = new Invocation("findAll_tabletypeType");
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
        Invocation invocation = new Invocation("findAll_tabletype2Type");
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
        Invocation invocation = new Invocation("update_tabletypeType");
        invocation.setParameter("theInstance", firstPerson);
        Operation op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);

        invocation = new Invocation("findByPrimaryKey_tabletypeType");
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
        invocation = new Invocation("update_tabletypeType");
        invocation.setParameter("theInstance", firstPerson);
        op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);
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

        Invocation invocation = new Invocation("create_tabletypeType");
        invocation.setParameter("theInstance", newPerson);
        Operation op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);
        // verify create call succeeded
        invocation = new Invocation("findByPrimaryKey_tabletypeType");
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
        invocation = new Invocation("delete_tabletypeType");
        invocation.setParameter("id", 99);
        op = xrService.getOperation(invocation.getName());
        op.invoke(xrService, invocation);
        // verify delete succeeded
        invocation = new Invocation("findByPrimaryKey_tabletypeType");
        invocation.setParameter("id", 99);
        op = xrService.getOperation(invocation.getName());
        result = op.invoke(xrService, invocation);
        assertNull("Result is not null after delete call", result);
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
        "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"urn:tabletype\" xmlns=\"urn:tabletype\" elementFormDefault=\"qualified\">\n" +
           "<xsd:complexType name=\"tabletype2Type\">\n" +
              "<xsd:sequence>\n" +
                "<xsd:element name=\"id\" type=\"xsd:decimal\"/>\n" +
                "<xsd:element name=\"lr\" type=\"xsd:base64Binary\" minOccurs=\"0\" nillable=\"true\"/>\n" +
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
                 "<xsd:element name=\"binid\" type=\"xsd:base64Binary\" minOccurs=\"0\" nillable=\"true\"/>\n" +
                 "<xsd:element name=\"b\" type=\"xsd:base64Binary\" minOccurs=\"0\" nillable=\"true\"/>\n" +
                 "<xsd:element name=\"c\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>\n" +
                 "<xsd:element name=\"r\" type=\"xsd:base64Binary\" minOccurs=\"0\" nillable=\"true\"/>\n" +
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
                "<xsd:complexType name=\"findByPrimaryKey_tabletype2TypeResponseType\">\n" +
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
                "<xsd:complexType name=\"create_tabletype2TypeRequestType\">\n" +
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
                "<xsd:complexType name=\"findByPrimaryKey_tabletype2TypeRequestType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"id\" type=\"xsd:decimal\"/>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"findAll_tabletype2TypeRequestType\"/>\n" +
                "<xsd:complexType name=\"findAll_tabletypeTypeRequestType\"/>\n" +
                "<xsd:complexType name=\"delete_tabletype2TypeRequestType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"id\" type=\"xsd:decimal\"/>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"update_tabletypeTypeRequestType\">\n" +
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
                "<xsd:complexType name=\"findAll_tabletypeTypeResponseType\">\n" +
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
                "<xsd:complexType name=\"delete_tabletypeTypeRequestType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"id\" type=\"xsd:decimal\"/>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"findByPrimaryKey_tabletypeTypeResponseType\">\n" +
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
                "<xsd:complexType name=\"findAll_tabletype2TypeResponseType\">\n" +
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
                "<xsd:complexType name=\"update_tabletype2TypeRequestType\">\n" +
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
                "<xsd:complexType name=\"findByPrimaryKey_tabletypeTypeRequestType\">\n" +
                   "<xsd:sequence>\n" +
                      "<xsd:element name=\"id\" type=\"xsd:decimal\"/>\n" +
                   "</xsd:sequence>\n" +
                "</xsd:complexType>\n" +
                "<xsd:complexType name=\"create_tabletypeTypeRequestType\">\n" +
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
                "<xsd:element name=\"update_tabletypeType\" type=\"tns:update_tabletypeTypeRequestType\"/>\n" +
                "<xsd:element name=\"create_tabletype2Type\" type=\"tns:create_tabletype2TypeRequestType\"/>\n" +
                "<xsd:element name=\"findAll_tabletypeTypeResponse\" type=\"tns:findAll_tabletypeTypeResponseType\"/>\n" +
                "<xsd:element name=\"update_tabletype2Type\" type=\"tns:update_tabletype2TypeRequestType\"/>\n" +
                "<xsd:element name=\"findByPrimaryKey_tabletypeType\" type=\"tns:findByPrimaryKey_tabletypeTypeRequestType\"/>\n" +
                "<xsd:element name=\"findAll_tabletype2TypeResponse\" type=\"tns:findAll_tabletype2TypeResponseType\"/>\n" +
                "<xsd:element name=\"delete_tabletype2Type\" type=\"tns:delete_tabletype2TypeRequestType\"/>\n" +
                "<xsd:element name=\"findAll_tabletype2Type\" type=\"tns:findAll_tabletype2TypeRequestType\"/>\n" +
                "<xsd:element name=\"create_tabletypeType\" type=\"tns:create_tabletypeTypeRequestType\"/>\n" +
                "<xsd:element name=\"findAll_tabletypeType\" type=\"tns:findAll_tabletypeTypeRequestType\"/>\n" +
                "<xsd:element name=\"findByPrimaryKey_tabletypeTypeResponse\" type=\"tns:findByPrimaryKey_tabletypeTypeResponseType\"/>\n" +
                "<xsd:element name=\"FaultType\">\n" +
                   "<xsd:complexType>\n" +
                      "<xsd:sequence>\n" +
                         "<xsd:element name=\"faultCode\" type=\"xsd:string\"/>\n" +
                         "<xsd:element name=\"faultString\" type=\"xsd:string\"/>\n" +
                      "</xsd:sequence>\n" +
                   "</xsd:complexType>\n" +
                "</xsd:element>\n" +
                "<xsd:element name=\"findByPrimaryKey_tabletype2Type\" type=\"tns:findByPrimaryKey_tabletype2TypeRequestType\"/>\n" +
                "<xsd:element name=\"EmptyResponse\">\n" +
                   "<xsd:complexType/>\n" +
                "</xsd:element>\n" +
                "<xsd:element name=\"findByPrimaryKey_tabletype2TypeResponse\" type=\"tns:findByPrimaryKey_tabletype2TypeResponseType\"/>\n" +
                "<xsd:element name=\"delete_tabletypeType\" type=\"tns:delete_tabletypeTypeRequestType\"/>\n" +
                "</xsd:schema>\n" +
              "</wsdl:types>\n" +
              "<wsdl:message name=\"delete_tabletype2TypeRequest\">\n" +
                 "<wsdl:part name=\"delete_tabletype2TypeRequest\" element=\"tns:delete_tabletype2Type\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"update_tabletypeTypeRequest\">\n" +
                 "<wsdl:part name=\"update_tabletypeTypeRequest\" element=\"tns:update_tabletypeType\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findByPrimaryKey_tabletypeTypeRequest\">\n" +
                 "<wsdl:part name=\"findByPrimaryKey_tabletypeTypeRequest\" element=\"tns:findByPrimaryKey_tabletypeType\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"create_tabletypeTypeRequest\">\n" +
                 "<wsdl:part name=\"create_tabletypeTypeRequest\" element=\"tns:create_tabletypeType\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findAll_tabletype2TypeRequest\">\n" +
                 "<wsdl:part name=\"findAll_tabletype2TypeRequest\" element=\"tns:findAll_tabletype2Type\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"update_tabletype2TypeRequest\">\n" +
                 "<wsdl:part name=\"update_tabletype2TypeRequest\" element=\"tns:update_tabletype2Type\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"create_tabletype2TypeRequest\">\n" +
                 "<wsdl:part name=\"create_tabletype2TypeRequest\" element=\"tns:create_tabletype2Type\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findAll_tabletypeTypeResponse\">\n" +
                 "<wsdl:part name=\"findAll_tabletypeTypeResponse\" element=\"tns:findAll_tabletypeTypeResponse\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"FaultType\">\n" +
                 "<wsdl:part name=\"fault\" element=\"tns:FaultType\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findByPrimaryKey_tabletypeTypeResponse\">\n" +
                 "<wsdl:part name=\"findByPrimaryKey_tabletypeTypeResponse\" element=\"tns:findByPrimaryKey_tabletypeTypeResponse\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findByPrimaryKey_tabletype2TypeRequest\">\n" +
                 "<wsdl:part name=\"findByPrimaryKey_tabletype2TypeRequest\" element=\"tns:findByPrimaryKey_tabletype2Type\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findByPrimaryKey_tabletype2TypeResponse\">\n" +
                 "<wsdl:part name=\"findByPrimaryKey_tabletype2TypeResponse\" element=\"tns:findByPrimaryKey_tabletype2TypeResponse\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"EmptyResponse\">\n" +
                 "<wsdl:part name=\"emptyResponse\" element=\"tns:EmptyResponse\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findAll_tabletype2TypeResponse\">\n" +
                 "<wsdl:part name=\"findAll_tabletype2TypeResponse\" element=\"tns:findAll_tabletype2TypeResponse\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"findAll_tabletypeTypeRequest\">\n" +
                 "<wsdl:part name=\"findAll_tabletypeTypeRequest\" element=\"tns:findAll_tabletypeType\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:message name=\"delete_tabletypeTypeRequest\">\n" +
                 "<wsdl:part name=\"delete_tabletypeTypeRequest\" element=\"tns:delete_tabletypeType\">\n" +"</wsdl:part>\n" +
              "</wsdl:message>\n" +
              "<wsdl:portType name=\"tabletypeService_Interface\">\n" +
                 "<wsdl:operation name=\"update_tabletypeType\">\n" +
                    "<wsdl:input message=\"tns:update_tabletypeTypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output name=\"update_tabletypeTypeEmptyResponse\" message=\"tns:EmptyResponse\">\n" +"</wsdl:output>\n" +
                    "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">\n" +"</wsdl:fault>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"delete_tabletype2Type\">\n" +
                    "<wsdl:input message=\"tns:delete_tabletype2TypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output name=\"delete_tabletype2TypeEmptyResponse\" message=\"tns:EmptyResponse\">\n" +"</wsdl:output>\n" +
                    "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">\n" +"</wsdl:fault>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"create_tabletype2Type\">\n" +
                    "<wsdl:input message=\"tns:create_tabletype2TypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output name=\"create_tabletype2TypeEmptyResponse\" message=\"tns:EmptyResponse\">\n" +"</wsdl:output>\n" +
                    "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">\n" +"</wsdl:fault>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"findAll_tabletype2Type\">\n" +
                    "<wsdl:input message=\"tns:findAll_tabletype2TypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output message=\"tns:findAll_tabletype2TypeResponse\">\n" +"</wsdl:output>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"create_tabletypeType\">\n" +
                    "<wsdl:input message=\"tns:create_tabletypeTypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output name=\"create_tabletypeTypeEmptyResponse\" message=\"tns:EmptyResponse\">\n" +"</wsdl:output>\n" +
                    "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">\n" +"</wsdl:fault>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"findAll_tabletypeType\">\n" +
                    "<wsdl:input message=\"tns:findAll_tabletypeTypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output message=\"tns:findAll_tabletypeTypeResponse\">\n" +"</wsdl:output>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"update_tabletype2Type\">\n" +
                    "<wsdl:input message=\"tns:update_tabletype2TypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output name=\"update_tabletype2TypeEmptyResponse\" message=\"tns:EmptyResponse\">\n" +"</wsdl:output>\n" +
                    "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">\n" +"</wsdl:fault>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"findByPrimaryKey_tabletypeType\">\n" +
                    "<wsdl:input message=\"tns:findByPrimaryKey_tabletypeTypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output message=\"tns:findByPrimaryKey_tabletypeTypeResponse\">\n" +"</wsdl:output>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"findByPrimaryKey_tabletype2Type\">\n" +
                    "<wsdl:input message=\"tns:findByPrimaryKey_tabletype2TypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output message=\"tns:findByPrimaryKey_tabletype2TypeResponse\">\n" +"</wsdl:output>\n" +
                 "</wsdl:operation>\n" +
                 "<wsdl:operation name=\"delete_tabletypeType\">\n" +
                    "<wsdl:input message=\"tns:delete_tabletypeTypeRequest\">\n" +"</wsdl:input>\n" +
                    "<wsdl:output name=\"delete_tabletypeTypeEmptyResponse\" message=\"tns:EmptyResponse\">\n" +"</wsdl:output>\n" +
                    "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">\n" +"</wsdl:fault>\n" +
                 "</wsdl:operation>\n" +
             "</wsdl:portType>\n" +
             "<wsdl:binding name=\"tabletypeService_SOAP_HTTP\" type=\"tns:tabletypeService_Interface\">\n" +
                "<soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>\n" +
                "<wsdl:operation name=\"update_tabletypeType\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:update_tabletypeType\"/>\n" +
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
                "<wsdl:operation name=\"delete_tabletype2Type\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:delete_tabletype2Type\"/>\n" +
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
                "<wsdl:operation name=\"create_tabletype2Type\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:create_tabletype2Type\"/>\n" +
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
                "<wsdl:operation name=\"findAll_tabletype2Type\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:findAll_tabletype2Type\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                "</wsdl:operation>\n" +
                "<wsdl:operation name=\"create_tabletypeType\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:create_tabletypeType\"/>\n" +
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
                "<wsdl:operation name=\"findAll_tabletypeType\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:findAll_tabletypeType\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                "</wsdl:operation>\n" +
                "<wsdl:operation name=\"update_tabletype2Type\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:update_tabletype2Type\"/>\n" +
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
                "<wsdl:operation name=\"findByPrimaryKey_tabletypeType\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:findByPrimaryKey_tabletypeType\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                "</wsdl:operation>\n" +
                "<wsdl:operation name=\"findByPrimaryKey_tabletype2Type\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:findByPrimaryKey_tabletype2Type\"/>\n" +
                   "<wsdl:input>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:input>\n" +
                   "<wsdl:output>\n" +
                      "<soap:body use=\"literal\"/>\n" +
                   "</wsdl:output>\n" +
                "</wsdl:operation>\n" +
                "<wsdl:operation name=\"delete_tabletypeType\">\n" +
                   "<soap:operation soapAction=\"urn:tabletypeService:delete_tabletypeType\"/>\n" +
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
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<object-persistence xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:eclipselink=\"http://www.eclipse.org/eclipselink/xsds/persistence\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"Eclipse Persistence Services - " + releaseVersion + "\">\n" +
           "<name>tabletype-dbws-or</name>\n" +
           "<class-mapping-descriptors>\n" +
              "<class-mapping-descriptor xsi:type=\"relational-class-mapping-descriptor\">\n" +
                 "<class>tabletype.Tabletype</class>\n" +
                 "<alias>tabletypeType</alias>\n" +
                 "<primary-key>\n" +
                    "<field table=\"TABLETYPE\" name=\"ID\" sql-typecode=\"2\" xsi:type=\"column\"/>\n" +
                 "</primary-key>\n" +
                 "<events/>\n" +
                 "<querying>\n" +
                    "<queries>\n" +
                       "<query name=\"findByPrimaryKey\" xsi:type=\"read-object-query\">\n" +
                          "<criteria operator=\"equal\" xsi:type=\"relation-expression\">\n" +
                             "<left xsi:type=\"field-expression\">\n" +
                                "<field table=\"TABLETYPE\" name=\"ID\" sql-typecode=\"2\" xsi:type=\"column\"/>\n" +
                                "<base xsi:type=\"base-expression\"/>\n" +
                             "</left>\n" +
                             "<right xsi:type=\"parameter-expression\">\n" +
                                "<parameter name=\"id\" xsi:type=\"column\"/>\n" +
                             "</right>\n" +
                          "</criteria>\n" +
                          "<arguments>\n" +
                             "<argument name=\"id\">\n" +
                                "<type>java.lang.Object</type>\n" +
                             "</argument>\n" +
                          "</arguments>\n" +
                          "<reference-class>tabletype.Tabletype</reference-class>\n" +
                       "</query>\n" +
                       "<query name=\"findAll\" xsi:type=\"read-all-query\">\n" +
                          "<reference-class>tabletype.Tabletype</reference-class>\n" +
                          "<container xsi:type=\"list-container-policy\">\n" +
                             "<collection-type>java.util.Vector</collection-type>\n" +
                          "</container>\n" +
                       "</query>\n" +
                    "</queries>\n" +
                 "</querying>\n" +
                 "<attribute-mappings>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>id</attribute-name>\n" +
                       "<field table=\"TABLETYPE\" name=\"ID\" sql-typecode=\"2\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>java.math.BigInteger</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>name</attribute-name>\n" +
                       "<field table=\"TABLETYPE\" name=\"NAME\" sql-typecode=\"12\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>java.lang.String</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>deptno</attribute-name>\n" +
                       "<field table=\"TABLETYPE\" name=\"DEPTNO\" sql-typecode=\"2\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>java.math.BigInteger</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>deptname</attribute-name>\n" +
                       "<field table=\"TABLETYPE\" name=\"DEPTNAME\" sql-typecode=\"12\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>java.lang.String</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>section</attribute-name>\n" +
                       "<field table=\"TABLETYPE\" name=\"SECTION\" sql-typecode=\"1\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>java.lang.Character</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>sal</attribute-name>\n" +
                       "<field table=\"TABLETYPE\" name=\"SAL\" sql-typecode=\"6\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>java.lang.Float</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>commission</attribute-name>\n" +
                       "<field table=\"TABLETYPE\" name=\"COMMISSION\" sql-typecode=\"6\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>java.lang.Float</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>sales</attribute-name>\n" +
                       "<field table=\"TABLETYPE\" name=\"SALES\" sql-typecode=\"6\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>java.lang.Float</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>binid</attribute-name>\n" +
                       "<field table=\"TABLETYPE\" name=\"BINID\" sql-typecode=\"2004\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>[B</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>b</attribute-name>\n" +
                       "<field table=\"TABLETYPE\" name=\"B\" sql-typecode=\"2004\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>[B</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>c</attribute-name>\n" +
                       "<field table=\"TABLETYPE\" name=\"C\" sql-typecode=\"2005\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>[Ljava.lang.Character;</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>r</attribute-name>\n" +
                       "<field table=\"TABLETYPE\" name=\"R\" sql-typecode=\"-3\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>[B</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                 "</attribute-mappings>\n" +
                 "<descriptor-type>independent</descriptor-type>\n" +
                 "<caching>\n" +
                    "<cache-type>weak-reference</cache-type>\n" +
                    "<cache-size>-1</cache-size>\n" +
                 "</caching>\n" +
                 "<remote-caching>\n" +
                    "<cache-type>weak-reference</cache-type>\n" +
                    "<cache-size>-1</cache-size>\n" +
                 "</remote-caching>\n" +
                 "<instantiation/>\n" +
                 "<copying xsi:type=\"instantiation-copy-policy\"/>\n" +
                 "<tables>\n" +
                    "<table name=\"TABLETYPE\"/>\n" +
                 "</tables>\n" +
              "</class-mapping-descriptor>\n" +
              "<class-mapping-descriptor xsi:type=\"relational-class-mapping-descriptor\">\n" +
                 "<class>tabletype.Tabletype2</class>\n" +
                 "<alias>tabletype2Type</alias>\n" +
                 "<primary-key>\n" +
                    "<field table=\"TABLETYPE2\" name=\"ID\" sql-typecode=\"2\" xsi:type=\"column\"/>\n" +
                 "</primary-key>\n" +
                 "<events/>\n" +
                 "<querying>\n" +
                    "<queries>\n" +
                       "<query name=\"findByPrimaryKey\" xsi:type=\"read-object-query\">\n" +
                          "<criteria operator=\"equal\" xsi:type=\"relation-expression\">\n" +
                             "<left xsi:type=\"field-expression\">\n" +
                                "<field table=\"TABLETYPE2\" name=\"ID\" sql-typecode=\"2\" xsi:type=\"column\"/>\n" +
                                "<base xsi:type=\"base-expression\"/>\n" +
                             "</left>\n" +
                             "<right xsi:type=\"parameter-expression\">\n" +
                                "<parameter name=\"id\" xsi:type=\"column\"/>\n" +
                             "</right>\n" +
                          "</criteria>\n" +
                          "<arguments>\n" +
                             "<argument name=\"id\">" +
                                "<type>java.lang.Object</type>\n" +
                             "</argument>\n" +
                          "</arguments>\n" +
                          "<reference-class>tabletype.Tabletype2</reference-class>\n" +
                       "</query>\n" +
                       "<query name=\"findAll\" xsi:type=\"read-all-query\">\n" +
                          "<reference-class>tabletype.Tabletype2</reference-class>\n" +
                          "<container xsi:type=\"list-container-policy\">\n" +
                             "<collection-type>java.util.Vector</collection-type>\n" +
                          "</container>\n" +
                       "</query>\n" +
                    "</queries>\n" +
                 "</querying>\n" +
                 "<attribute-mappings>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>id</attribute-name>\n" +
                       "<field table=\"TABLETYPE2\" name=\"ID\" sql-typecode=\"2\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>java.math.BigInteger</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"direct-mapping\">\n" +
                       "<attribute-name>lr</attribute-name>\n" +
                       "<field table=\"TABLETYPE2\" name=\"LR\" sql-typecode=\"-4\" xsi:type=\"column\"/>\n" +
                       "<attribute-classification>[B</attribute-classification>\n" +
                    "</attribute-mapping>\n" +
                 "</attribute-mappings>\n" +
                 "<descriptor-type>independent</descriptor-type>\n" +
                 "<caching>\n" +
                    "<cache-type>weak-reference</cache-type>\n" +
                    "<cache-size>-1</cache-size>\n" +
                 "</caching>\n" +
                 "<remote-caching>\n" +
                    "<cache-type>weak-reference</cache-type>\n" +
                    "<cache-size>-1</cache-size>\n" +
                 "</remote-caching>\n" +
                 "<instantiation/>\n" +
                 "<copying xsi:type=\"instantiation-copy-policy\"/>\n" +
                 "<tables>\n" +
                    "<table name=\"TABLETYPE2\"/>\n" +
                 "</tables>\n" +
              "</class-mapping-descriptor>\n" +
           "</class-mapping-descriptors>\n" +
           "<login xsi:type=\"database-login\">\n" +
              "<platform-class>org.eclipse.persistence.platform.database.DatabasePlatform</platform-class>\n" +
              "<connection-url></connection-url>\n" +
           "</login>\n" +
        "</object-persistence>";

    protected static final String OX_PROJECT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<object-persistence xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:eclipselink=\"http://www.eclipse.org/eclipselink/xsds/persistence\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"Eclipse Persistence Services - " + releaseVersion + "\">\n" +
           "<name>tabletype-dbws-ox</name>\n" +
           "<class-mapping-descriptors>\n" +
              "<class-mapping-descriptor xsi:type=\"xml-class-mapping-descriptor\">\n" +
                 "<class>tabletype.Tabletype</class>\n" +
                 "<alias>tabletypeType</alias>\n" +
                 "<events/>\n" +
                 "<querying/>\n" +
                 "<attribute-mappings>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>id</attribute-name>\n" +
                       "<field name=\"id/text()\" is-required=\"true\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}decimal</schema-type>\n" +
                       "</field>\n" +
                       "<attribute-classification>java.math.BigInteger</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<empty-node-represents-null>true</empty-node-represents-null>\n" +
                          "<null-representation-for-xml>ABSENT_NODE</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>name</attribute-name>\n" +
                       "<field name=\"name/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}string</schema-type>\n" +
                       "</field>\n" +
                       "<attribute-classification>java.lang.String</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<xsi-nil-represents-null>true</xsi-nil-represents-null>\n" +
                          "<null-representation-for-xml>XSI_NIL</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>deptno</attribute-name>\n" +
                       "<field name=\"deptno/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}decimal</schema-type>\n" +
                       "</field>\n" +
                       "<attribute-classification>java.math.BigInteger</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<xsi-nil-represents-null>true</xsi-nil-represents-null>\n" +
                          "<null-representation-for-xml>XSI_NIL</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>deptname</attribute-name>\n" +
                       "<field name=\"deptname/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}string</schema-type>\n" +
                       "</field>\n" +
                       "<attribute-classification>java.lang.String</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<xsi-nil-represents-null>true</xsi-nil-represents-null>\n" +
                          "<null-representation-for-xml>XSI_NIL</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>section</attribute-name>\n" +
                       "<field name=\"section/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}string</schema-type>\n" +
                       "</field>\n" +
                       "<attribute-classification>java.lang.Character</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<xsi-nil-represents-null>true</xsi-nil-represents-null>\n" +
                          "<null-representation-for-xml>XSI_NIL</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>sal</attribute-name>\n" +
                       "<field name=\"sal/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}double</schema-type>\n" +
                       "</field>\n" +
                       "<attribute-classification>java.lang.Float</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<xsi-nil-represents-null>true</xsi-nil-represents-null>\n" +
                          "<null-representation-for-xml>XSI_NIL</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>commission</attribute-name>\n" +
                       "<field name=\"commission/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}double</schema-type>\n" +
                       "</field>\n" +
                       "<attribute-classification>java.lang.Float</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<xsi-nil-represents-null>true</xsi-nil-represents-null>\n" +
                          "<null-representation-for-xml>XSI_NIL</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>sales</attribute-name>\n" +
                       "<field name=\"sales/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}double</schema-type>\n" +
                       "</field>\n" +
                       "<attribute-classification>java.lang.Float</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<xsi-nil-represents-null>true</xsi-nil-represents-null>\n" +
                          "<null-representation-for-xml>XSI_NIL</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>binid</attribute-name>\n" +
                       "<field name=\"binid/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}base64Binary</schema-type>\n" +
                       "</field>\n" +
                       "<converter xsi:type=\"serialized-object-converter\"/>\n" +
                       "<attribute-classification>[B</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<xsi-nil-represents-null>true</xsi-nil-represents-null>\n" +
                          "<null-representation-for-xml>XSI_NIL</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>b</attribute-name>\n" +
                       "<field name=\"b/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}base64Binary</schema-type>\n" +
                       "</field>\n" +
                       "<converter xsi:type=\"serialized-object-converter\"/>\n" +
                       "<attribute-classification>[B</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<xsi-nil-represents-null>true</xsi-nil-represents-null>\n" +
                          "<null-representation-for-xml>XSI_NIL</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>c</attribute-name>\n" +
                       "<field name=\"c/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}string</schema-type>\n" +
                       "</field>\n" +
                       "<attribute-classification>[Ljava.lang.Character;</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<xsi-nil-represents-null>true</xsi-nil-represents-null>\n" +
                          "<null-representation-for-xml>XSI_NIL</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>r</attribute-name>\n" +
                       "<field name=\"r/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}base64Binary</schema-type>\n" +
                       "</field>\n" +
                       "<converter xsi:type=\"serialized-object-converter\"/>\n" +
                       "<attribute-classification>[B</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<xsi-nil-represents-null>true</xsi-nil-represents-null>\n" +
                          "<null-representation-for-xml>XSI_NIL</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                 "</attribute-mappings>\n" +
                 "<descriptor-type>aggregate</descriptor-type>\n" +
                 "<instantiation/>\n" +
                 "<copying xsi:type=\"instantiation-copy-policy\"/>\n" +
                 "<default-root-element>tabletypeType</default-root-element>\n" +
                 "<default-root-element-field name=\"tabletypeType\"/>\n" +
                 "<namespace-resolver>\n" +
                    "<default-namespace-uri>urn:tabletype</default-namespace-uri>\n" +
                 "</namespace-resolver>\n" +
                 "<schema xsi:type=\"schema-url-reference\">\n" +
                    "<resource></resource>\n" +
                    "<schema-context>/tabletypeType</schema-context>\n" +
                    "<node-type>complex-type</node-type>\n" +
                 "</schema>\n" +
              "</class-mapping-descriptor>\n" +
              "<class-mapping-descriptor xsi:type=\"xml-class-mapping-descriptor\">\n" +
                 "<class>tabletype.Tabletype2</class>\n" +
                 "<alias>tabletype2Type</alias>\n" +
                 "<events/>\n" +
                 "<querying/>\n" +
                 "<attribute-mappings>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>id</attribute-name>\n" +
                       "<field name=\"id/text()\" is-required=\"true\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}decimal</schema-type>\n" +
                       "</field>\n" +
                       "<attribute-classification>java.math.BigInteger</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<empty-node-represents-null>true</empty-node-represents-null>\n" +
                          "<null-representation-for-xml>ABSENT_NODE</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                    "<attribute-mapping xsi:type=\"xml-direct-mapping\">\n" +
                       "<attribute-name>lr</attribute-name>\n" +
                       "<field name=\"lr/text()\" xsi:type=\"node\">\n" +
                          "<schema-type>{http://www.w3.org/2001/XMLSchema}base64Binary</schema-type>\n" +
                       "</field>\n" +
                       "<converter xsi:type=\"serialized-object-converter\"/>\n" +
                       "<attribute-classification>[B</attribute-classification>\n" +
                       "<null-policy xsi:type=\"null-policy\">\n" +
                          "<xsi-nil-represents-null>true</xsi-nil-represents-null>\n" +
                          "<null-representation-for-xml>XSI_NIL</null-representation-for-xml>\n" +
                       "</null-policy>\n" +
                    "</attribute-mapping>\n" +
                 "</attribute-mappings>\n" +
                 "<descriptor-type>aggregate</descriptor-type>\n" +
                 "<instantiation/>\n" +
                 "<copying xsi:type=\"instantiation-copy-policy\"/>\n" +
                 "<default-root-element>tabletype2Type</default-root-element>\n" +
                 "<default-root-element-field name=\"tabletype2Type\"/>\n" +
                 "<namespace-resolver>\n" +
                    "<default-namespace-uri>urn:tabletype</default-namespace-uri>\n" +
                 "</namespace-resolver>\n" +
                 "<schema xsi:type=\"schema-url-reference\">\n" +
                    "<resource></resource>\n" +
                    "<schema-context>/tabletype2Type</schema-context>\n" +
                    "<node-type>complex-type</node-type>\n" +
                 "</schema>\n" +
              "</class-mapping-descriptor>\n" +
           "</class-mapping-descriptors>\n" +
           "<login xsi:type=\"xml-login\">\n" +
              "<platform-class>org.eclipse.persistence.oxm.platform.DOMPlatform</platform-class>\n" +
           "</login>\n" +
        "</object-persistence>";

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
          "<binid>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAACEBA=</binid>" +
          "<b>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAQEBAQEBAQEBAQEBAQEB</b>" +
          "<c>aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa</c>" +
          "<r>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAADAQEB</r>" +
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
          "<binid>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAACEBA=</binid>" +
          "<b>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAQEBAQEBAQEBAQEBAQEB</b>" +
          "<c>ababababababababababababababab</c>" +
          "<r>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAADAQEB</r>" +
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
            "<binid>rO0ABXA=</binid>" +
            "<b>rO0ABXA=</b>" +
            "<c>adadadadadadadadadadadadadadad</c>" +
            "<r>rO0ABXA=</r>" +
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
                "<binid>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAACEBA=</binid>" +
                "<b>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAQEBAQEBAQEBAQEBAQEB</b>" +
                "<c>aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa</c>" +
                "<r>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAADAQEB</r>" +
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
                "<binid>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAACAQE=</binid>" +
                "<b>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAgICAgICAgICAgICAgIC</b>" +
                "<c>bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb</c>" +
                "<r>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAADAgIC</r>" +
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
                "<binid>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAACERA=</binid>" +
                "<b>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAwMDAwMDAwMDAwMDAwMD</b>" +
                "<c>cccccccccccccccccccccccccccccc</c>" +
                "<r>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAADAwMD</r>" +
            "</tabletypeType>" +
        "</tabletype-collection>";

    protected static final String LONG_RAW_XML =
        REGULAR_XML_HEADER +
    	"<tabletype2-collection>" +
    	   "<tabletype2Type xmlns=\"urn:tabletype\">" +
    	      "<id>66</id>" +
    	      "<lr>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAJAQEBAQEBAQEB</lr>" +
    	   "</tabletype2Type>" +
    	   "<tabletype2Type xmlns=\"urn:tabletype\">" +
    	      "<id>67</id>" +
    	      "<lr>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAJAgICAgICAgIC</lr>" +
    	   "</tabletype2Type>" +
    	   "<tabletype2Type xmlns=\"urn:tabletype\">" +
    	      "<id>68</id>" +
    	      "<lr>rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAJAwMDAwMDAwMD</lr>" +
    	   "</tabletype2Type>" +
    	"</tabletype2-collection>";
}