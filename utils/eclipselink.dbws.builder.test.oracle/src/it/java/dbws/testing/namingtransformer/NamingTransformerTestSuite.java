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
//     David McCann - June 2013 - Initial Implementation
package dbws.testing.namingtransformer;

//javase imports
import java.io.StringReader;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Document;

//java eXtension imports
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.descriptors.TransformerBasedFieldTransformation;
import org.eclipse.persistence.internal.sessions.factories.ObjectPersistenceWorkbenchXMLProject;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.mappings.transformers.ConstantTransformer;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.dbws.BaseDBWSBuilderHelper;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import org.eclipse.persistence.tools.dbws.DefaultNamingConventionTransformer;
import org.eclipse.persistence.tools.dbws.oracle.OracleHelper;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Test use of a custom naming convention transformer.
 *
 */
public class NamingTransformerTestSuite extends DBWSTestSuite {

    static final String CREATE_TABLETYPE_TABLE =
            """
                    CREATE TABLE TABLETYPE (
                    ID NUMERIC(4) NOT NULL,
                    NAME VARCHAR(25),
                    DEPTNO DECIMAL(2,0),
                    DEPTNAME VARCHAR2(40),
                    SECTION CHAR(1),
                    SAL FLOAT,
                    COMMISSION REAL,
                    SALES DOUBLE PRECISION,
                    BINID BLOB,
                    B BLOB,
                    C CLOB,
                    R RAW(3),
                    PRIMARY KEY (ID)
                    )""";
    static final String[] POPULATE_TABLETYPE_TABLE = new String[] {
        "INSERT INTO TABLETYPE (ID, NAME, DEPTNO, DEPTNAME, SECTION, SAL, COMMISSION, SALES, BINID, B, C, R) VALUES (1, 'mike', 99, 'sales', 'a', 100000.80, 450.80, 10000.80, '1010', '010101010101010101010101010101', 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', '010101')",
        "INSERT INTO TABLETYPE (ID, NAME, DEPTNO, DEPTNAME, SECTION, SAL, COMMISSION, SALES, BINID, B, C, R) VALUES (2, 'merrick', 98, 'delivery', 'f', 20000, 0, 0, '0101', '020202020202020202020202020202', 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb', '020202')",
        "INSERT INTO TABLETYPE (ID, NAME, DEPTNO, DEPTNAME, SECTION, SAL, COMMISSION, SALES, BINID, B, C, R) VALUES (3, 'rick', 99, 'sales', 'b', 98000.20, 150.20, 2000.20, '1110', '030303030303030303030303030303', 'cccccccccccccccccccccccccccccc', '030303')"
    };
    static final String DROP_TABLETYPE_TABLE =
        "DROP TABLE TABLETYPE";

    static final String CREATE_MYTYPEX =
            """
                    CREATE OR REPLACE TYPE MYTYPE_X AS OBJECT (
                    id NUMBER,
                    name VARCHAR2(30)
                    )""";
    static final String CREATE_GETMYTYPEX_PROC =
            """
                    CREATE OR REPLACE PROCEDURE GetMyTypeX(RESULT OUT MYTYPE_X) AS
                    BEGIN
                    RESULT := MYTYPE_X(66, 'Steve French');
                    END GetMyTypeX;""";
    static final String DROP_GETMYTYPEX_PROC =
         "DROP PROCEDURE GetMyTypeX";
    static final String DROP_MYTYPEX =
        "DROP TYPE MYTYPE_X";

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
            try {
                Statement stmt = conn.createStatement();
                for (String s : POPULATE_TABLETYPE_TABLE) {
                    stmt.addBatch(s);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                //e.printStackTrace();
            }
            runDdl(conn, CREATE_MYTYPEX, ddlDebug);
            runDdl(conn, CREATE_GETMYTYPEX_PROC, ddlDebug);
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
            "<procedure " +
                "name=\"GetMyTypeXTest\" " +
                "catalogPattern=\"TOPLEVEL\" " +
                "procedurePattern=\"GetMyTypeX\" " +
                "isAdvancedJDBC=\"true\" " +
            "/>" +
        "</dbws-builder>";
        builder = new DBWSBuilder();
        builder.setTopNamingConventionTransformer(new DBWSNamingConventionTransformer());
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
            runDdl(conn, DROP_GETMYTYPEX_PROC, ddlDebug);
            runDdl(conn, DROP_MYTYPEX, ddlDebug);
        }
    }

    @Test
    public void testNamingConventionTransformer() throws WSDLException {
        Invocation invocation = new Invocation("findByPrimaryKey_TabletypeType");
        invocation.setParameter("id", 3);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        removeEmptyTextNodes(doc);
        Document controlDoc = xmlParser.parse(new StringReader(ANOTHER_PERSON_XML));
        removeEmptyTextNodes(controlDoc);
        assertTrue("Control document not same as instance document.  Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String ANOTHER_PERSON_XML =
        REGULAR_XML_HEADER +
        "<TABLETYPExTYPE xmlns=\"urn:tabletype\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" id=\"3\">" +
          "<name>rick</name>" +
        "</TABLETYPExTYPE>";

    @Test
    public void testTransformerOnObject() throws WSDLException {
        Invocation invocation = new Invocation("GetMyTypeXTest");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(MYTYPEX_XML));
        assertTrue("Control document not same as instance document.  Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String MYTYPEX_XML =
        REGULAR_XML_HEADER +
        "<MYTYPE_XxTYPE xmlns=\"urn:tabletype\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" id=\"66\">" +
          "<name>Steve French</name>" +
        "</MYTYPE_XxTYPE>";

    /**
     * Inner class used for testing NamingConventionTransformer
     *
     */
    static class DBWSNamingConventionTransformer extends DefaultNamingConventionTransformer {

        @Override
        public String generateSchemaAlias(String tableName) {
            return super.generateSchemaAlias(tableName +"xTYPE");
        }

        @Override
        public String generateElementAlias(String originalElementName) {
            return super.generateElementAlias(originalElementName.toLowerCase());
        }

        @Override
        public ElementStyle styleForElement(String elementName) {
            if ("id".equalsIgnoreCase(elementName)) {
                return ElementStyle.ATTRIBUTE;
            }
            if ("deptno".equalsIgnoreCase(elementName) ||
                "deptname".equalsIgnoreCase(elementName) ||
                "section".equalsIgnoreCase(elementName) ||
                "sal".equalsIgnoreCase(elementName) ||
                "commission".equalsIgnoreCase(elementName) ||
                "sales".equalsIgnoreCase(elementName) ||
                "binid".equalsIgnoreCase(elementName) ||
                "b".equalsIgnoreCase(elementName) ||
                "c".equalsIgnoreCase(elementName) ||
                "r".equalsIgnoreCase(elementName)) {
                return ElementStyle.NONE;
            }
            return ElementStyle.ELEMENT;
        }
    }

    @Test
    public void validateSchema() {
        Document testDoc = xmlParser.parse(new StringReader(DBWS_SCHEMA_STREAM.toString()));
        Document controlDoc = xmlParser.parse(new StringReader(XSD));
        removeEmptyTextNodes(testDoc);
        removeEmptyTextNodes(controlDoc);
        assertTrue("Schema validation failed:\nExpected:" + documentToString(controlDoc) + "\nbut was:\n" + documentToString(testDoc), comparer.isNodeEqual(controlDoc, testDoc));
    }

    protected static final String XSD =
        "<?xml version = \"1.0\" encoding = \"UTF-8\"?>" +
        "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"urn:tabletype\" xmlns=\"urn:tabletype\" elementFormDefault=\"qualified\">" +
        "   <xsd:complexType name=\"MYTYPE_XxTYPE\">" +
        "      <xsd:sequence>" +
        "         <xsd:element name=\"name\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>" +
        "      </xsd:sequence>" +
        "      <xsd:attribute name=\"id\" type=\"xsd:decimal\"/>" +
        "   </xsd:complexType>" +
        "   <xsd:complexType name=\"TABLETYPExTYPE\">" +
        "      <xsd:sequence>" +
        "         <xsd:element name=\"name\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>" +
        "      </xsd:sequence>" +
        "      <xsd:attribute name=\"id\" type=\"xsd:decimal\" use=\"required\"/>" +
        "   </xsd:complexType>" +
        "   <xsd:element name=\"MYTYPE_XxTYPE\" type=\"MYTYPE_XxTYPE\"/>" +
        "   <xsd:element name=\"TABLETYPExTYPE\" type=\"TABLETYPExTYPE\"/>" +
        "</xsd:schema>";

    @Test
    public void validateWSDL() {
        Document testDoc = xmlParser.parse(new StringReader(DBWS_WSDL_STREAM.toString()));
        Document controlDoc = xmlParser.parse(new StringReader(WSDL));
        removeEmptyTextNodes(testDoc);
        removeEmptyTextNodes(controlDoc);
        assertTrue("WSDL validation failed:\nExpected:" + documentToString(controlDoc) + "\nbut was:\n" + documentToString(testDoc), comparer.isNodeEqual(controlDoc, testDoc));
    }

    protected static final String WSDL =
            """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <wsdl:definitions
                    name="tabletypeService"
                    targetNamespace="urn:tabletypeService"
                    xmlns:ns1="urn:tabletype"
                    xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
                    xmlns:tns="urn:tabletypeService"
                    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                    xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
                    >
                    <wsdl:types>
                    <xsd:schema elementFormDefault="qualified" targetNamespace="urn:tabletypeService" xmlns:tns="urn:tabletypeService"
                    xmlns:xsd="http://www.w3.org/2001/XMLSchema">
                    <xsd:import namespace="urn:tabletype" schemaLocation="eclipselink-dbws-schema.xsd"/>
                    <xsd:complexType name="findAll_TabletypeTypeRequestType"/>
                    <xsd:complexType name="update_TabletypeTypeRequestType">
                    <xsd:sequence>
                    <xsd:element name="theInstance">
                    <xsd:complexType>
                    <xsd:sequence>
                    <xsd:element ref="ns1:TABLETYPExTYPE"/>
                    </xsd:sequence>
                    </xsd:complexType>
                    </xsd:element>
                    </xsd:sequence>
                    </xsd:complexType>
                    <xsd:complexType name="findAll_TabletypeTypeResponseType">
                    <xsd:sequence>
                    <xsd:element name="result">
                    <xsd:complexType>
                    <xsd:sequence>
                    <xsd:element maxOccurs="unbounded" minOccurs="0" ref="ns1:TABLETYPExTYPE"/>
                    </xsd:sequence>
                    </xsd:complexType>
                    </xsd:element>
                    </xsd:sequence>
                    </xsd:complexType>
                    <xsd:complexType name="delete_TabletypeTypeRequestType">
                    <xsd:sequence>
                    <xsd:element name="id" type="xsd:decimal"/>
                    </xsd:sequence>
                    </xsd:complexType>
                    <xsd:complexType name="findByPrimaryKey_TabletypeTypeResponseType">
                    <xsd:sequence>
                    <xsd:element name="result">
                    <xsd:complexType>
                    <xsd:sequence>
                    <xsd:element minOccurs="0" ref="ns1:TABLETYPExTYPE"/>
                    </xsd:sequence>
                    </xsd:complexType>
                    </xsd:element>
                    </xsd:sequence>
                    </xsd:complexType>
                    <xsd:complexType name="findByPrimaryKey_TabletypeTypeRequestType">
                    <xsd:sequence>
                    <xsd:element name="id" type="xsd:decimal"/>
                    </xsd:sequence>
                    </xsd:complexType>
                    <xsd:complexType name="create_TabletypeTypeRequestType">
                    <xsd:sequence>
                    <xsd:element name="theInstance">
                    <xsd:complexType>
                    <xsd:sequence>
                    <xsd:element ref="ns1:TABLETYPExTYPE"/>
                    </xsd:sequence>
                    </xsd:complexType>
                    </xsd:element>
                    </xsd:sequence>
                    </xsd:complexType>
                    <xsd:complexType name="GetMyTypeXTestResponseType">
                    <xsd:sequence>
                    <xsd:element name="result">
                    <xsd:complexType>
                    <xsd:sequence>
                    <xsd:element ref="ns1:MYTYPE_XxTYPE" minOccurs="0"/>
                    </xsd:sequence>
                    </xsd:complexType>
                    </xsd:element>
                    </xsd:sequence>
                    </xsd:complexType>
                    <xsd:complexType name="GetMyTypeXTestRequestType"/>
                    <xsd:element name="update_TabletypeType" type="tns:update_TabletypeTypeRequestType"/>
                    <xsd:element name="findAll_TabletypeTypeResponse" type="tns:findAll_TabletypeTypeResponseType"/>
                    <xsd:element name="findByPrimaryKey_TabletypeType" type="tns:findByPrimaryKey_TabletypeTypeRequestType"/>
                    <xsd:element name="create_TabletypeType" type="tns:create_TabletypeTypeRequestType"/>
                    <xsd:element name="findAll_TabletypeType" type="tns:findAll_TabletypeTypeRequestType"/>
                    <xsd:element name="findByPrimaryKey_TabletypeTypeResponse" type="tns:findByPrimaryKey_TabletypeTypeResponseType"/>
                    <xsd:element name="FaultType">
                    <xsd:complexType>
                    <xsd:sequence>
                    <xsd:element name="faultCode" type="xsd:string"/>
                    <xsd:element name="faultString" type="xsd:string"/>
                    </xsd:sequence>
                    </xsd:complexType>
                    </xsd:element>
                    <xsd:element name="EmptyResponse">
                    <xsd:complexType/>
                    </xsd:element>
                    <xsd:element name="delete_TabletypeType" type="tns:delete_TabletypeTypeRequestType"/>
                    <xsd:element name="GetMyTypeXTestResponse" type="tns:GetMyTypeXTestResponseType"/>
                    <xsd:element name="GetMyTypeXTest" type="tns:GetMyTypeXTestRequestType"/>
                    </xsd:schema>
                    </wsdl:types>
                    <wsdl:message name="update_TabletypeTypeRequest">
                    <wsdl:part name="update_TabletypeTypeRequest" element="tns:update_TabletypeType">
                    </wsdl:part>
                    </wsdl:message>
                    <wsdl:message name="findByPrimaryKey_TabletypeTypeRequest">
                    <wsdl:part name="findByPrimaryKey_TabletypeTypeRequest" element="tns:findByPrimaryKey_TabletypeType">
                    </wsdl:part>
                    </wsdl:message>
                    <wsdl:message name="create_TabletypeTypeRequest">
                    <wsdl:part name="create_TabletypeTypeRequest" element="tns:create_TabletypeType">
                    </wsdl:part>
                    </wsdl:message>
                    <wsdl:message name="findAll_TabletypeTypeResponse">
                    <wsdl:part name="findAll_TabletypeTypeResponse" element="tns:findAll_TabletypeTypeResponse">
                    </wsdl:part>
                    </wsdl:message>
                    <wsdl:message name="FaultType">
                    <wsdl:part name="fault" element="tns:FaultType">
                    </wsdl:part>
                    </wsdl:message>
                    <wsdl:message name="findByPrimaryKey_TabletypeTypeResponse">
                    <wsdl:part name="findByPrimaryKey_TabletypeTypeResponse" element="tns:findByPrimaryKey_TabletypeTypeResponse">
                    </wsdl:part>
                    </wsdl:message>
                    <wsdl:message name="EmptyResponse">
                    <wsdl:part name="emptyResponse" element="tns:EmptyResponse">
                    </wsdl:part>
                    </wsdl:message>
                    <wsdl:message name="findAll_TabletypeTypeRequest">
                    <wsdl:part name="findAll_TabletypeTypeRequest" element="tns:findAll_TabletypeType">
                    </wsdl:part>
                    </wsdl:message>
                    <wsdl:message name="delete_TabletypeTypeRequest">
                    <wsdl:part name="delete_TabletypeTypeRequest" element="tns:delete_TabletypeType">
                    </wsdl:part>
                    </wsdl:message>
                    <wsdl:message name="GetMyTypeXTestRequest">
                    <wsdl:part name="GetMyTypeXTestRequest" element="tns:GetMyTypeXTest"/>
                    </wsdl:message>
                    <wsdl:message name="GetMyTypeXTestResponse">
                    <wsdl:part name="GetMyTypeXTestResponse" element="tns:GetMyTypeXTestResponse"/>
                    </wsdl:message>
                    <wsdl:portType name="tabletypeService_Interface">
                    <wsdl:operation name="update_TabletypeType">
                    <wsdl:input message="tns:update_TabletypeTypeRequest">
                    </wsdl:input>
                    <wsdl:output name="update_TabletypeTypeEmptyResponse" message="tns:EmptyResponse">
                    </wsdl:output>
                    <wsdl:fault name="FaultException" message="tns:FaultType">
                    </wsdl:fault>
                    </wsdl:operation>
                    <wsdl:operation name="create_TabletypeType">
                    <wsdl:input message="tns:create_TabletypeTypeRequest">
                    </wsdl:input>
                    <wsdl:output name="create_TabletypeTypeEmptyResponse" message="tns:EmptyResponse">
                    </wsdl:output>
                    <wsdl:fault name="FaultException" message="tns:FaultType">
                    </wsdl:fault>
                    </wsdl:operation>
                    <wsdl:operation name="findAll_TabletypeType">
                    <wsdl:input message="tns:findAll_TabletypeTypeRequest">
                    </wsdl:input>
                    <wsdl:output message="tns:findAll_TabletypeTypeResponse">
                    </wsdl:output>
                    </wsdl:operation>
                    <wsdl:operation name="findByPrimaryKey_TabletypeType">
                    <wsdl:input message="tns:findByPrimaryKey_TabletypeTypeRequest">
                    </wsdl:input>
                    <wsdl:output message="tns:findByPrimaryKey_TabletypeTypeResponse">
                    </wsdl:output>
                    </wsdl:operation>
                    <wsdl:operation name="delete_TabletypeType">
                    <wsdl:input message="tns:delete_TabletypeTypeRequest">
                    </wsdl:input>
                    <wsdl:output name="delete_TabletypeTypeEmptyResponse" message="tns:EmptyResponse">
                    </wsdl:output>
                    <wsdl:fault name="FaultException" message="tns:FaultType">
                    </wsdl:fault>
                    </wsdl:operation>
                    <wsdl:operation name="GetMyTypeXTest">
                    <wsdl:input message="tns:GetMyTypeXTestRequest"/>
                    <wsdl:output message="tns:GetMyTypeXTestResponse"/>
                    </wsdl:operation>
                    </wsdl:portType>
                    <wsdl:binding name="tabletypeService_SOAP_HTTP" type="tns:tabletypeService_Interface">
                    <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
                    <wsdl:operation name="update_TabletypeType">
                    <soap:operation soapAction="urn:tabletypeService:update_TabletypeType"/>
                    <wsdl:input>
                    <soap:body use="literal"/>
                    </wsdl:input>
                    <wsdl:output>
                    <soap:body use="literal"/>
                    </wsdl:output>
                    <wsdl:fault name="FaultException">
                    <soap:fault name="FaultException" use="literal"/>
                    </wsdl:fault>
                    </wsdl:operation>
                    <wsdl:operation name="create_TabletypeType">
                    <soap:operation soapAction="urn:tabletypeService:create_TabletypeType"/>
                    <wsdl:input>
                    <soap:body use="literal"/>
                    </wsdl:input>
                    <wsdl:output>
                    <soap:body use="literal"/>
                    </wsdl:output>
                    <wsdl:fault name="FaultException">
                    <soap:fault name="FaultException" use="literal"/>
                    </wsdl:fault>
                    </wsdl:operation>
                    <wsdl:operation name="findAll_TabletypeType">
                    <soap:operation soapAction="urn:tabletypeService:findAll_TabletypeType"/>
                    <wsdl:input>
                    <soap:body use="literal"/>
                    </wsdl:input>
                    <wsdl:output>
                    <soap:body use="literal"/>
                    </wsdl:output>
                    </wsdl:operation>
                    <wsdl:operation name="findByPrimaryKey_TabletypeType">
                    <soap:operation soapAction="urn:tabletypeService:findByPrimaryKey_TabletypeType"/>
                    <wsdl:input>
                    <soap:body use="literal"/>
                    </wsdl:input>
                    <wsdl:output>
                    <soap:body use="literal"/>
                    </wsdl:output>
                    </wsdl:operation>
                    <wsdl:operation name="delete_TabletypeType">
                    <soap:operation soapAction="urn:tabletypeService:delete_TabletypeType"/>
                    <wsdl:input>
                    <soap:body use="literal"/>
                    </wsdl:input>
                    <wsdl:output>
                    <soap:body use="literal"/>
                    </wsdl:output>
                    <wsdl:fault name="FaultException">
                    <soap:fault name="FaultException" use="literal"/>
                    </wsdl:fault>
                    </wsdl:operation>
                    <wsdl:operation name="GetMyTypeXTest">
                    <soap:operation soapAction="urn:tabletypeService:GetMyTypeXTest"/>
                    <wsdl:input>
                    <soap:body use="literal"/>
                    </wsdl:input>
                    <wsdl:output>
                    <soap:body use="literal"/>
                    </wsdl:output>
                    </wsdl:operation>
                    </wsdl:binding>
                    <wsdl:service name="tabletypeService">
                    <wsdl:port name="tabletypeServicePort" binding="tns:tabletypeService_SOAP_HTTP">
                    <soap:address location="REPLACE_WITH_ENDPOINT_ADDRESS"/>
                    </wsdl:port>
                    </wsdl:service>
                    </wsdl:definitions>
                    """;

}
