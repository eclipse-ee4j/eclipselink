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
//     David McCann - September 23, 2011 - 2.4 - Initial implementation
package dbws.testing.plsqlcollection;

//javase imports
import java.io.StringReader;
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
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;

//test imports
import dbws.testing.DBWSTestSuite;

/**
 * Tests PL/SQL collections with simple arguments.
 *
 */
public class PLSQLCollectionTestSuite extends DBWSTestSuite {

    static final String CREATE_PACKAGE2_PACKAGE =
        "CREATE OR REPLACE PACKAGE PACKAGE2 AS" +
            "\nTYPE tab1 IS TABLE OF VARCHAR2(111) INDEX BY BINARY_INTEGER;" +
            "\nTYPE ORECORD IS RECORD (" +
                "\nO1 VARCHAR2(10)," +
                "\nO2 DECIMAL(7,2)" +
            "\n);" +
            "\nTYPE TAB2 IS TABLE OF ORECORD INDEX BY BINARY_INTEGER;" +
            "\nTYPE TAB3 IS TABLE OF BOOLEAN INDEX BY BINARY_INTEGER;" +
            "\nPROCEDURE COPYTABLE(OLDTAB IN tab1, NEWTAB OUT tab1);" +
            "\nPROCEDURE SETRECORD(INREC IN ORECORD, NEWTAB OUT TAB2);" +
            "\nPROCEDURE COPYBOOLEANTABLE(OLDTAB IN TAB3, NEWTAB OUT TAB3);" +
            "\nPROCEDURE BOOLTOVARCHAR(OLDTAB IN TAB3, NEWTAB OUT tab1);" +
            "\nFUNCTION COPYTABLE2(OLDTAB IN tab1) RETURN tab1;" +
            "\nFUNCTION SETRECORD2(INREC IN ORECORD) RETURN TAB2;" +
        "\nEND PACKAGE2;";
    static final String CREATE_PACKAGE2_BODY =
        "CREATE OR REPLACE PACKAGE BODY PACKAGE2 AS" +
            "\nPROCEDURE COPYTABLE(OLDTAB IN tab1, NEWTAB OUT tab1) AS" +
            "\nBEGIN" +
                "\nNEWTAB := OLDTAB;" +
            "\nEND COPYTABLE;" +
            "\nPROCEDURE COPYBOOLEANTABLE(OLDTAB IN TAB3, NEWTAB OUT TAB3) AS" +
            "\nBEGIN" +
                "\nNEWTAB := OLDTAB;" +
            "\nEND COPYBOOLEANTABLE;" +
            "\nPROCEDURE BOOLTOVARCHAR(OLDTAB IN TAB3, NEWTAB OUT tab1) AS" +
            "\nBEGIN" +
                "\nIF OLDTAB.COUNT > 0 THEN" +
                    "\nFOR I IN OLDTAB.FIRST..OLDTAB.LAST LOOP" +
                        "\nIF OLDTAB(I) = TRUE THEN" +
                            "\nNEWTAB(I + 1 - OLDTAB.FIRST) := 'true';" +
                        "\nELSE" +
                            "\nNEWTAB(I + 1 - OLDTAB.FIRST) := 'false';" +
                        "\nEND IF;"+
                    "\nEND LOOP;" +
                "\nEND IF;" +
            "\nEND BOOLTOVARCHAR;" +
            "\nPROCEDURE SETRECORD(INREC IN ORECORD, NEWTAB OUT TAB2) AS" +
            "\nBEGIN" +
                "\nNEWTAB(0) := INREC;" +
            "\nEND SETRECORD;" +
            "\nFUNCTION COPYTABLE2(OLDTAB IN tab1) RETURN tab1 IS NEWTAB tab1;" +
            "\nBEGIN" +
                "\nNEWTAB := OLDTAB;" +
                "\nRETURN NEWTAB;" +
            "\nEND COPYTABLE2;" +
            "\nFUNCTION SETRECORD2(INREC IN ORECORD) RETURN TAB2 IS NEWTAB TAB2;" +
            "\nBEGIN" +
                "\nNEWTAB(0) := INREC;" +
                "\nRETURN NEWTAB;" +
            "\nEND SETRECORD2;" +
        "\nEND PACKAGE2;";
    static final String CREATE_PACKAGE2_TAB1_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE2_tab1 AS TABLE OF VARCHAR2(111)";
    static final String CREATE_PACKAGE2_ORECORD_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE2_ORECORD AS OBJECT (" +
            "\nO1 VARCHAR2(10)," +
            "\nO2 DECIMAL(7,2)" +
        "\n)";
    static final String CREATE_PACKAGE2_TAB2_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE2_TAB2 AS TABLE OF PACKAGE2_ORECORD";
    static final String CREATE_PACKAGE2_TAB3_TYPE =
        "CREATE OR REPLACE TYPE PACKAGE2_TAB3 AS TABLE OF INTEGER";
    static final String DROP_PACKAGE2_PACKAGE =
        "DROP PACKAGE PACKAGE2";
    static final String DROP_PACKAGE2_TAB1_TYPE =
        "DROP TYPE PACKAGE2_tab1";
    static final String DROP_PACKAGE2_TAB2_TYPE =
        "DROP TYPE PACKAGE2_TAB2";
    static final String DROP_PACKAGE2_TAB3_TYPE =
        "DROP TYPE PACKAGE2_TAB3";
    static final String DROP_PACKAGE2_ORECORD_TYPE =
        "DROP TYPE PACKAGE2_ORECORD";

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
            runDdl(conn, CREATE_PACKAGE2_PACKAGE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE2_BODY, ddlDebug);
            runDdl(conn, CREATE_PACKAGE2_TAB1_TYPE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE2_ORECORD_TYPE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE2_TAB2_TYPE, ddlDebug);
            runDdl(conn, CREATE_PACKAGE2_TAB3_TYPE, ddlDebug);
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">PLSQLCollection</property>" +
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
                  "name=\"CopyTableTest\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"COPYTABLE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyTableTest2\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"COPYTABLE2\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"CopyBoolTableTest\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"COPYBOOLEANTABLE\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"BoolTabToVarcharTabTest\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"BOOLTOVARCHAR\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"SetRecordTest\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"SETRECORD\" " +
              "/>" +
              "<plsql-procedure " +
                  "name=\"SetRecordTest2\" " +
                  "catalogPattern=\"PACKAGE2\" " +
                  "procedurePattern=\"SETRECORD2\" " +
                  "returnType=\"package2_tab2Type\" " +  // note that returnType is not required
              "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        String ddlDrop = System.getProperty(DATABASE_DDL_DROP_KEY, DEFAULT_DATABASE_DDL_DROP);
        if ("true".equalsIgnoreCase(ddlDrop)) {
            runDdl(conn, DROP_PACKAGE2_PACKAGE, ddlDebug);
            runDdl(conn, DROP_PACKAGE2_TAB1_TYPE, ddlDebug);
            runDdl(conn, DROP_PACKAGE2_TAB2_TYPE, ddlDebug);
            runDdl(conn, DROP_PACKAGE2_TAB3_TYPE, ddlDebug);
            runDdl(conn, DROP_PACKAGE2_ORECORD_TYPE, ddlDebug);
        }
    }

    @Test
    public void copyTableTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab1 = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Invocation invocation = new Invocation("CopyTableTest");
        invocation.setParameter("OLDTAB", inputTab1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String TABLE_XML =
        STANDALONE_XML_HEADER +
        "<package2_tab1Type xmlns=\"urn:PLSQLCollection\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>BLAH</item>" +
        "</package2_tab1Type>";

    @Test
    public void copyTableTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab1 = unmarshaller.unmarshal(new StringReader(TABLE_XML));
        Invocation invocation = new Invocation("CopyTableTest2");
        invocation.setParameter("OLDTAB", inputTab1);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    @Test
    public void copyBoolTableTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab = unmarshaller.unmarshal(new StringReader(TABLE3_XML));
        Invocation invocation = new Invocation("CopyBoolTableTest");
        invocation.setParameter("OLDTAB", inputTab);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TABLE3_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String TABLE3_XML =
        STANDALONE_XML_HEADER +
        "<package2_tab3Type xmlns=\"urn:PLSQLCollection\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>1</item>" +
          "<item>0</item>" +
          "<item>1</item>" +
          "<item>0</item>" +
        "</package2_tab3Type>";

    @Test
    public void boolTabToVarcharTabTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputTab = unmarshaller.unmarshal(new StringReader(TABLE3_XML));
        Invocation invocation = new Invocation("BoolTabToVarcharTabTest");
        invocation.setParameter("OLDTAB", inputTab);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(TABLE4_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String TABLE4_XML =
        STANDALONE_XML_HEADER +
        "<package2_tab1Type xmlns=\"urn:PLSQLCollection\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>true</item>" +
          "<item>false</item>" +
          "<item>true</item>" +
          "<item>false</item>" +
        "</package2_tab1Type>";

    /**
     * StoredProcedure test.
     */
    @Test
    public void setRecordTest() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputORec = unmarshaller.unmarshal(new StringReader(INPUTORECORD_XML));
        Invocation invocation = new Invocation("SetRecordTest");
        invocation.setParameter("INREC", inputORec);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTTABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }
    public static final String INPUTORECORD_XML =
        STANDALONE_XML_HEADER +
        "<package2_orecordType xmlns=\"urn:PLSQLCollection\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<o1>somedata</o1>" +
          "<o2>66.6</o2>" +
        "</package2_orecordType>";
    public static final String OUTPUTTABLE_XML =
        STANDALONE_XML_HEADER +
        "<package2_tab2Type xmlns=\"urn:PLSQLCollection\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<item>" +
            "<o1>somedata</o1>" +
            "<o2>66.6</o2>" +
          "</item>" +
        "</package2_tab2Type>";

    /**
     * StoredFunction test.
     */
    @Test
    public void setRecordTest2() {
        XMLUnmarshaller unmarshaller = xrService.getXMLContext().createUnmarshaller();
        Object inputORec = unmarshaller.unmarshal(new StringReader(INPUTORECORD_XML));
        Invocation invocation = new Invocation("SetRecordTest2");
        invocation.setParameter("INREC", inputORec);
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        Document doc = xmlPlatform.createDocument();
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.marshal(result, doc);
        Document controlDoc = xmlParser.parse(new StringReader(OUTPUTTABLE_XML));
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    /**
     * WSDL generation test.
     */
    @Test
    public void testWSDLGeneration() {
        assertNotNull("No WSDL was generated", DBWS_WSDL_STREAM);
        Document doc = xmlParser.parse(new StringReader(DBWS_WSDL_STREAM.toString()));
        removeEmptyTextNodes(doc);
        Document controlDoc = xmlParser.parse(new StringReader(WSDL_XML));
        removeEmptyTextNodes(controlDoc);
        assertTrue("Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
    }

    public static final String WSDL_XML =
        STANDALONE_XML_HEADER +
        "<wsdl:definitions name=\"PLSQLCollectionService\" targetNamespace=\"urn:PLSQLCollectionService\" xmlns:ns1=\"urn:PLSQLCollection\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:tns=\"urn:PLSQLCollectionService\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\">" +
        "<wsdl:types>" +
          "<xsd:schema xmlns:tns=\"urn:PLSQLCollectionService\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"urn:PLSQLCollectionService\" elementFormDefault=\"qualified\">" +
            "<xsd:import schemaLocation=\"eclipselink-dbws-schema.xsd\" namespace=\"urn:PLSQLCollection\"/>" +
            "<xsd:complexType name=\"SetRecordTestRequestType\"><xsd:sequence><xsd:element name=\"INREC\" type=\"ns1:package2_orecordType\"/></xsd:sequence></xsd:complexType>" +
            "<xsd:complexType name=\"CopyBoolTableTestResponseType\"><xsd:sequence><xsd:element name=\"result\"><xsd:complexType><xsd:sequence><xsd:element minOccurs=\"0\" ref=\"ns1:package2_tab3Type\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType>" +
            "<xsd:complexType name=\"CopyTableTestResponseType\"><xsd:sequence><xsd:element name=\"result\"><xsd:complexType><xsd:sequence><xsd:element ref=\"ns1:package2_tab1Type\" minOccurs=\"0\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType>" +
            "<xsd:complexType name=\"BoolTabToVarcharTabTestRequestType\"><xsd:sequence><xsd:element name=\"OLDTAB\" type=\"ns1:package2_tab3Type\"/></xsd:sequence></xsd:complexType>" +
            "<xsd:complexType name=\"CopyTableTest2ResponseType\"><xsd:sequence><xsd:element name=\"result\"><xsd:complexType><xsd:sequence><xsd:element ref=\"ns1:package2_tab1Type\" minOccurs=\"0\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType>" +
            "<xsd:complexType name=\"SetRecordTest2RequestType\"><xsd:sequence><xsd:element name=\"INREC\" type=\"ns1:package2_orecordType\"/></xsd:sequence></xsd:complexType>" +
            "<xsd:complexType name=\"CopyTableTestRequestType\"><xsd:sequence><xsd:element name=\"OLDTAB\" type=\"ns1:package2_tab1Type\"/></xsd:sequence></xsd:complexType>" +
            "<xsd:complexType name=\"BoolTabToVarcharTabTestResponseType\"><xsd:sequence><xsd:element name=\"result\"><xsd:complexType><xsd:sequence><xsd:element minOccurs=\"0\" ref=\"ns1:package2_tab1Type\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType>" +
            "<xsd:complexType name=\"SetRecordTestResponseType\"><xsd:sequence><xsd:element name=\"result\"><xsd:complexType><xsd:sequence><xsd:element ref=\"ns1:package2_tab2Type\" minOccurs=\"0\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType>" +
            "<xsd:complexType name=\"CopyTableTest2RequestType\"><xsd:sequence><xsd:element name=\"OLDTAB\" type=\"ns1:package2_tab1Type\"/></xsd:sequence></xsd:complexType>" +
            "<xsd:complexType name=\"SetRecordTest2ResponseType\"><xsd:sequence><xsd:element name=\"result\"><xsd:complexType><xsd:sequence><xsd:element ref=\"ns1:package2_tab2Type\" minOccurs=\"0\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType>" +
            "<xsd:complexType name=\"CopyBoolTableTestRequestType\"><xsd:sequence><xsd:element name=\"OLDTAB\" type=\"ns1:package2_tab3Type\"/></xsd:sequence></xsd:complexType>" +
            "<xsd:element name=\"CopyTableTestResponse\" type=\"tns:CopyTableTestResponseType\"/>" +
            "<xsd:element name=\"CopyBoolTableTestResponse\" type=\"tns:CopyBoolTableTestResponseType\"/>" +
            "<xsd:element name=\"CopyTableTest\" type=\"tns:CopyTableTestRequestType\"/>" +
            "<xsd:element name=\"SetRecordTest\" type=\"tns:SetRecordTestRequestType\"/>" +
            "<xsd:element name=\"CopyTableTest2Response\" type=\"tns:CopyTableTest2ResponseType\"/>" +
            "<xsd:element name=\"SetRecordTest2\" type=\"tns:SetRecordTest2RequestType\"/>" +
            "<xsd:element name=\"BoolTabToVarcharTabTestResponse\" type=\"tns:BoolTabToVarcharTabTestResponseType\"/>" +
            "<xsd:element name=\"SetRecordTest2Response\" type=\"tns:SetRecordTest2ResponseType\"/>" +
            "<xsd:element name=\"SetRecordTestResponse\" type=\"tns:SetRecordTestResponseType\"/>" +
            "<xsd:element name=\"BoolTabToVarcharTabTest\" type=\"tns:BoolTabToVarcharTabTestRequestType\"/>" +
            "<xsd:element name=\"CopyBoolTableTest\" type=\"tns:CopyBoolTableTestRequestType\"/>" +
            "<xsd:element name=\"CopyTableTest2\" type=\"tns:CopyTableTest2RequestType\"/>" +
          "</xsd:schema>" +
        "</wsdl:types>" +
        "<wsdl:message name=\"SetRecordTest2Response\">" +
          "<wsdl:part name=\"SetRecordTest2Response\" element=\"tns:SetRecordTest2Response\">" +
          "</wsdl:part>" +
        "</wsdl:message>" +
        "<wsdl:message name=\"BoolTabToVarcharTabTestResponse\">" +
          "<wsdl:part element=\"tns:BoolTabToVarcharTabTestResponse\" name=\"BoolTabToVarcharTabTestResponse\">" +
          "</wsdl:part>" +
        "</wsdl:message>" +
        "<wsdl:message name=\"SetRecordTestRequest\">" +
          "<wsdl:part name=\"SetRecordTestRequest\" element=\"tns:SetRecordTest\">" +
          "</wsdl:part>" +
        "</wsdl:message>" +
        "<wsdl:message name=\"BoolTabToVarcharTabTestRequest\">" +
          "<wsdl:part element=\"tns:BoolTabToVarcharTabTest\" name=\"BoolTabToVarcharTabTestRequest\">" +
          "</wsdl:part>" +
        "</wsdl:message>" +
        "<wsdl:message name=\"SetRecordTestResponse\">" +
          "<wsdl:part name=\"SetRecordTestResponse\" element=\"tns:SetRecordTestResponse\">" +
          "</wsdl:part>" +
        "</wsdl:message>" +
        "<wsdl:message name=\"CopyBoolTableTestResponse\">" +
          "<wsdl:part element=\"tns:CopyBoolTableTestResponse\" name=\"CopyBoolTableTestResponse\">" +
          "</wsdl:part>" +
        "</wsdl:message>" +
        "<wsdl:message name=\"CopyTableTestRequest\">" +
          "<wsdl:part name=\"CopyTableTestRequest\" element=\"tns:CopyTableTest\">" +
          "</wsdl:part>" +
        "</wsdl:message>" +
        "<wsdl:message name=\"CopyTableTestResponse\">" +
          "<wsdl:part name=\"CopyTableTestResponse\" element=\"tns:CopyTableTestResponse\">" +
          "</wsdl:part>" +
        "</wsdl:message>" +
        "<wsdl:message name=\"CopyTableTest2Response\">" +
          "<wsdl:part name=\"CopyTableTest2Response\" element=\"tns:CopyTableTest2Response\">" +
          "</wsdl:part>" +
        "</wsdl:message>" +
        "<wsdl:message name=\"CopyTableTest2Request\">" +
          "<wsdl:part name=\"CopyTableTest2Request\" element=\"tns:CopyTableTest2\">" +
          "</wsdl:part>" +
        "</wsdl:message>" +
        "<wsdl:message name=\"CopyBoolTableTestRequest\">" +
          "<wsdl:part element=\"tns:CopyBoolTableTest\" name=\"CopyBoolTableTestRequest\">" +
          "</wsdl:part>" +
        "</wsdl:message>" +
        "<wsdl:message name=\"SetRecordTest2Request\">" +
          "<wsdl:part name=\"SetRecordTest2Request\" element=\"tns:SetRecordTest2\">" +
          "</wsdl:part>" +
        "</wsdl:message>" +
        "<wsdl:portType name=\"PLSQLCollectionService_Interface\">" +
          "<wsdl:operation name=\"CopyTableTest\">" +
            "<wsdl:input message=\"tns:CopyTableTestRequest\">" +
          "</wsdl:input>" +
            "<wsdl:output message=\"tns:CopyTableTestResponse\">" +
          "</wsdl:output>" +
          "</wsdl:operation>" +
          "<wsdl:operation name=\"SetRecordTest\">" +
            "<wsdl:input message=\"tns:SetRecordTestRequest\">" +
          "</wsdl:input>" +
            "<wsdl:output message=\"tns:SetRecordTestResponse\">" +
          "</wsdl:output>" +
          "</wsdl:operation>" +
          "<wsdl:operation name=\"SetRecordTest2\">" +
            "<wsdl:input message=\"tns:SetRecordTest2Request\">" +
          "</wsdl:input>" +
            "<wsdl:output message=\"tns:SetRecordTest2Response\">" +
          "</wsdl:output>" +
          "</wsdl:operation>" +
          "<wsdl:operation name=\"BoolTabToVarcharTabTest\">" +
            "<wsdl:input message=\"tns:BoolTabToVarcharTabTestRequest\">" +
            "</wsdl:input>" +
            "<wsdl:output message=\"tns:BoolTabToVarcharTabTestResponse\">" +
            "</wsdl:output>" +
          "</wsdl:operation>" +
          "<wsdl:operation name=\"CopyBoolTableTest\">" +
            "<wsdl:input message=\"tns:CopyBoolTableTestRequest\">" +
            "</wsdl:input>" +
            "<wsdl:output message=\"tns:CopyBoolTableTestResponse\">" +
            "</wsdl:output>" +
          "</wsdl:operation>" +
          "<wsdl:operation name=\"CopyTableTest2\">" +
            "<wsdl:input message=\"tns:CopyTableTest2Request\">" +
          "</wsdl:input>" +
            "<wsdl:output message=\"tns:CopyTableTest2Response\">" +
          "</wsdl:output>" +
          "</wsdl:operation>" +
        "</wsdl:portType>" +
        "<wsdl:binding name=\"PLSQLCollectionService_SOAP_HTTP\" type=\"tns:PLSQLCollectionService_Interface\">" +
          "<soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>" +
          "<wsdl:operation name=\"CopyTableTest\">" +
            "<soap:operation soapAction=\"urn:PLSQLCollectionService:CopyTableTest\"/>" +
            "<wsdl:input>" +
              "<soap:body use=\"literal\"/>" +
            "</wsdl:input>" +
            "<wsdl:output>" +
              "<soap:body use=\"literal\"/>" +
            "</wsdl:output>" +
          "</wsdl:operation>" +
          "<wsdl:operation name=\"SetRecordTest\">" +
            "<soap:operation soapAction=\"urn:PLSQLCollectionService:SetRecordTest\"/>" +
            "<wsdl:input>" +
              "<soap:body use=\"literal\"/>" +
            "</wsdl:input>" +
            "<wsdl:output>" +
              "<soap:body use=\"literal\"/>" +
            "</wsdl:output>" +
          "</wsdl:operation>" +
          "<wsdl:operation name=\"SetRecordTest2\">" +
            "<soap:operation soapAction=\"urn:PLSQLCollectionService:SetRecordTest2\"/>" +
            "<wsdl:input>" +
              "<soap:body use=\"literal\"/>" +
            "</wsdl:input>" +
            "<wsdl:output>" +
              "<soap:body use=\"literal\"/>" +
            "</wsdl:output>" +
          "</wsdl:operation>" +
          "<wsdl:operation name=\"BoolTabToVarcharTabTest\">" +
            "<soap:operation soapAction=\"urn:PLSQLCollectionService:BoolTabToVarcharTabTest\"/>" +
            "<wsdl:input>" +
              "<soap:body use=\"literal\"/>" +
            "</wsdl:input>" +
            "<wsdl:output>" +
              "<soap:body use=\"literal\"/>" +
            "</wsdl:output>" +
          "</wsdl:operation>" +
          "<wsdl:operation name=\"CopyBoolTableTest\">" +
            "<soap:operation soapAction=\"urn:PLSQLCollectionService:CopyBoolTableTest\"/>" +
            "<wsdl:input>" +
              "<soap:body use=\"literal\"/>" +
            "</wsdl:input>" +
            "<wsdl:output>" +
              "<soap:body use=\"literal\"/>" +
            "</wsdl:output>" +
          "</wsdl:operation>" +
          "<wsdl:operation name=\"CopyTableTest2\">" +
            "<soap:operation soapAction=\"urn:PLSQLCollectionService:CopyTableTest2\"/>" +
            "<wsdl:input>" +
              "<soap:body use=\"literal\"/>" +
            "</wsdl:input>" +
            "<wsdl:output>" +
              "<soap:body use=\"literal\"/>" +
            "</wsdl:output>" +
          "</wsdl:operation>" +
        "</wsdl:binding>" +
        "<wsdl:service name=\"PLSQLCollectionService\">" +
          "<wsdl:port name=\"PLSQLCollectionServicePort\" binding=\"tns:PLSQLCollectionService_SOAP_HTTP\">" +
            "<soap:address location=\"REPLACE_WITH_ENDPOINT_ADDRESS\"/>" +
          "</wsdl:port>" +
        "</wsdl:service>" +
      "</wsdl:definitions>";


}
