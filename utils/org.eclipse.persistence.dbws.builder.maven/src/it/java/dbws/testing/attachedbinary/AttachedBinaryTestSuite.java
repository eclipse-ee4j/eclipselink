/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Mike Norman - May 2008, created DBWS test package
package dbws.testing.attachedbinary;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//java eXtension imports
import javax.activation.DataHandler;
import javax.wsdl.WSDLException;

//JUnit4 imports
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.internal.dbws.SOAPAttachmentHandler;
import org.eclipse.persistence.internal.xr.Invocation;
import org.eclipse.persistence.internal.xr.Operation;
import org.eclipse.persistence.oxm.XMLMarshaller;

//testing imports
import dbws.testing.DBWSTestSuite;

public class AttachedBinaryTestSuite extends DBWSTestSuite {

    static final String CREATE_ATTACHEDBINARY_TABLE =
        "CREATE TABLE IF NOT EXISTS attachedbinary (" +
            "\nID DECIMAL(7,0) NOT NULL," +
            "\nNAME VARCHAR(80)," +
            "\nB BLOB," +
            "\nPRIMARY KEY (ID)" +
        "\n)";
    static final String[] POPULATE_ATTACHEDBINARY_TABLE = new String[] {
        "insert into attachedbinary(ID, NAME, B) values (1, 'one', 0x010101010101010101010101010101)",
        "insert into attachedbinary(ID, NAME, B) values (2, 'two', 0x020202020202020202020202020202)",
        "insert into attachedbinary(ID, NAME, B) values (3, 'three', 0x030303030303030303030303030303)"
    };
    static final String CREATE_GETBLOBBYID_FUNCTION =
        "CREATE FUNCTION getBLOBById(pk numeric(7)) RETURNS BLOB" +
            "\nREADS SQL DATA" +
        "\nBEGIN" +
            "\nDECLARE blb BLOB;" +
            "\nSELECT B into blb FROM attachedbinary WHERE ID=PK;" +
            "\nreturn(blb);" +
        "END";
    static final String DROP_ATTACHEDBINARY_TABLE =
        "DROP TABLE attachedbinary";
    static final String DROP_GETBLOBBYID_FUNCTION =
        "DROP FUNCTION getBLOBById";

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
            runDdl(conn, CREATE_ATTACHEDBINARY_TABLE, ddlDebug);
            runDdl(conn, CREATE_GETBLOBBYID_FUNCTION, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_ATTACHEDBINARY_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_ATTACHEDBINARY_TABLE[i]);
                }
                stmt.executeBatch();
            }
            catch (SQLException e) {
                if (ddlDebug) {
                    e.printStackTrace();
                }
            }
        }
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                "<property name=\"projectName\">attachedbinary</property>" +
                "<property name=\"targetNamespacePrefix\">ab</property>" +
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
                "tableNamePattern=\"attachedbinary\" " +
                ">" +
                "<procedure " +
                  "name=\"getBLOBById\" " +
                  "isCollection=\"false\" " +
                  "returnType=\"ab:attachedbinaryType\" " +
                  "procedurePattern=\"getBLOBById\" " +
                  "binaryAttachment=\"true\" " +
                "/>" +
              "</table>" +
            "</dbws-builder>";
        builder = null;
        DBWSTestSuite.setUp(".");
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_ATTACHEDBINARY_TABLE, ddlDebug);
            runDdl(conn, DROP_GETBLOBBYID_FUNCTION, ddlDebug);
        }
    }

    public static SOAPAttachmentHandler attachmentHandler = new SOAPAttachmentHandler();

    @SuppressWarnings({"rawtypes"})
    @Test
    public void findAll() throws IOException {
        Invocation invocation = new Invocation("findAll_AttachedbinaryType");
        Operation op = xrService.getOperation(invocation.getName());
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        XMLMarshaller marshaller = xrService.getXMLContext().createMarshaller();
        marshaller.setAttachmentMarshaller(attachmentHandler);
        Document doc = xmlPlatform.createDocument();
        Element ec = doc.createElement("attachedbinary-collection");
        doc.appendChild(ec);
        for (Object r : (Vector)result) {
            marshaller.marshal(r, ec);
        }
        Document controlDoc = xmlParser.parse(new StringReader(ATTACHED_BINARY_COLLECTION_XML));
        assertTrue("Control document not same as instance document.\n Expected:\n" + documentToString(controlDoc) + "\nActual:\n" + documentToString(doc), comparer.isNodeEqual(controlDoc, doc));
        // validate WSDL
        Document controlWSDL = xmlParser.parse(new StringReader(WSDL));
        removeEmptyTextNodes(controlWSDL);
        Document testWSDL = xmlParser.parse(new StringReader(DBWS_WSDL_STREAM.toString()));
        removeEmptyTextNodes(testWSDL);
        assertTrue("Control WSDL not same as instance document.\n Expected:\n" + documentToString(controlWSDL) + "\nActual:\n" + documentToString(testWSDL), comparer.isNodeEqual(controlWSDL, testWSDL));
        // validate XSD
        Document controlXSD = xmlParser.parse(new StringReader(XSD));
        removeEmptyTextNodes(controlXSD);
        Document testXSD = xmlParser.parse(new StringReader(DBWS_SCHEMA_STREAM.toString()));
        removeEmptyTextNodes(testXSD);
        assertTrue("Control XSD not same as instance document.\n Expected:\n" + documentToString(controlXSD) + "\nActual:\n" + documentToString(testXSD), comparer.isNodeEqual(controlXSD, testXSD));

        DataHandler dataHandler = attachmentHandler.getAttachments().get("cid:ref1");
        ByteArrayInputStream bais = (ByteArrayInputStream)dataHandler.getInputStream();
        byte[] ref = new byte[bais.available()];
        int count = bais.read(ref);
        assertEquals("wrong number of bytes returned", 15, count);
        for (int i = 0; i < count; i++) {
            assertTrue("wrong byte value returned", 1 == ref[i]);
        }
        dataHandler = attachmentHandler.getAttachments().get("cid:ref2");
        bais = (ByteArrayInputStream)dataHandler.getInputStream();
        ref = new byte[bais.available()];
        count = bais.read(ref);
        assertEquals("wrong number of bytes returned", 15, count);
        for (int i = 0; i < count; i++) {
            assertTrue("wrong byte value returned", 2 == ref[i]);
        }
        dataHandler = attachmentHandler.getAttachments().get("cid:ref3");
        bais = (ByteArrayInputStream)dataHandler.getInputStream();
        ref = new byte[bais.available()];
        count = bais.read(ref);
        assertEquals("wrong number of bytes returned", 15, count);
        for (int i = 0; i < count; i++) {
            assertTrue("wrong byte value returned", 3 == ref[i]);
        }
    }
    public static final String ATTACHED_BINARY_COLLECTION_XML =
        "<?xml version = \"1.0\" encoding = \"UTF-8\"?>" +
        "<attachedbinary-collection>" +
            "<attachedbinaryType xmlns=\"urn:attachedbinary\">" +
                "<id>1</id>" +
                "<name>one</name>" +
                "<b>cid:ref1</b>" +
            "</attachedbinaryType>" +
            "<attachedbinaryType xmlns=\"urn:attachedbinary\">" +
                "<id>2</id>" +
                "<name>two</name>" +
                "<b>cid:ref2</b>" +
            "</attachedbinaryType>" +
            "<attachedbinaryType xmlns=\"urn:attachedbinary\">" +
                "<id>3</id>" +
                "<name>three</name>" +
                "<b>cid:ref3</b>" +
            "</attachedbinaryType>" +
        "</attachedbinary-collection>";

    @Test
    public void getBLOBById() throws IOException {
        Invocation invocation = new Invocation("getBLOBById");
        Operation op = xrService.getOperation(invocation.getName());
        invocation.setParameter("pk", 1);
        Object result = op.invoke(xrService, invocation);
        assertNotNull("result is null", result);
        DataHandler dataHandler = (DataHandler)result;
        ByteArrayInputStream bais = (ByteArrayInputStream)dataHandler.getInputStream();
        byte[] ref = new byte[bais.available()];
        int count = bais.read(ref);
        assertEquals("wrong number of bytes returned", 15, count);
        for (int i = 0; i < count; i++) {
            assertTrue("wrong byte value returned", 1 == ref[i]);
        }
    }

    static final String WSDL =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<wsdl:definitions name=\"attachedbinaryService\" targetNamespace=\"urn:attachedbinaryService\" xmlns:ns1=\"urn:attachedbinary\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:tns=\"urn:attachedbinaryService\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\">" +
        "<wsdl:types>" +
        "<xsd:schema elementFormDefault=\"qualified\" targetNamespace=\"urn:attachedbinaryService\" xmlns:ref=\"http://ws-i.org/profiles/basic/1.1/xsd\" xmlns:tns=\"urn:attachedbinaryService\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><xsd:import namespace=\"urn:attachedbinary\" schemaLocation=\"eclipselink-dbws-schema.xsd\"/><xsd:import namespace=\"http://ws-i.org/profiles/basic/1.1/xsd\" schemaLocation=\"swaref.xsd\"/><xsd:complexType name=\"findAll_AttachedbinaryTypeResponseType\"><xsd:sequence><xsd:element name=\"result\"><xsd:complexType><xsd:sequence><xsd:element maxOccurs=\"unbounded\" minOccurs=\"0\" ref=\"ns1:attachedbinaryType\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType><xsd:complexType name=\"findByPrimaryKey_AttachedbinaryTypeRequestType\"><xsd:sequence><xsd:element name=\"id\" type=\"xsd:decimal\"/></xsd:sequence></xsd:complexType><xsd:complexType name=\"getBLOBByIdResponseType\"><xsd:sequence><xsd:element name=\"result\" type=\"ref:swaRef\"/></xsd:sequence></xsd:complexType><xsd:complexType name=\"findByPrimaryKey_AttachedbinaryTypeResponseType\"><xsd:sequence><xsd:element name=\"result\"><xsd:complexType><xsd:sequence><xsd:element minOccurs=\"0\" ref=\"ns1:attachedbinaryType\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType><xsd:complexType name=\"update_AttachedbinaryTypeRequestType\"><xsd:sequence><xsd:element name=\"theInstance\"><xsd:complexType><xsd:sequence><xsd:element ref=\"ns1:attachedbinaryType\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType><xsd:complexType name=\"getBLOBByIdRequestType\"><xsd:sequence><xsd:element name=\"pk\" type=\"xsd:decimal\"/></xsd:sequence></xsd:complexType><xsd:complexType name=\"create_AttachedbinaryTypeRequestType\"><xsd:sequence><xsd:element name=\"theInstance\"><xsd:complexType><xsd:sequence><xsd:element ref=\"ns1:attachedbinaryType\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType><xsd:complexType name=\"findAll_AttachedbinaryTypeRequestType\"/><xsd:complexType name=\"delete_AttachedbinaryTypeRequestType\"><xsd:sequence><xsd:element name=\"id\" type=\"xsd:decimal\"/></xsd:sequence></xsd:complexType><xsd:element name=\"findByPrimaryKey_AttachedbinaryTypeResponse\" type=\"tns:findByPrimaryKey_AttachedbinaryTypeResponseType\"/><xsd:element name=\"findByPrimaryKey_AttachedbinaryType\" type=\"tns:findByPrimaryKey_AttachedbinaryTypeRequestType\"/><xsd:element name=\"create_AttachedbinaryType\" type=\"tns:create_AttachedbinaryTypeRequestType\"/><xsd:element name=\"findAll_AttachedbinaryTypeResponse\" type=\"tns:findAll_AttachedbinaryTypeResponseType\"/><xsd:element name=\"findAll_AttachedbinaryType\" type=\"tns:findAll_AttachedbinaryTypeRequestType\"/><xsd:element name=\"delete_AttachedbinaryType\" type=\"tns:delete_AttachedbinaryTypeRequestType\"/><xsd:element name=\"FaultType\"><xsd:complexType><xsd:sequence><xsd:element name=\"faultCode\" type=\"xsd:string\"/><xsd:element name=\"faultString\" type=\"xsd:string\"/></xsd:sequence></xsd:complexType></xsd:element><xsd:element name=\"getBLOBById\" type=\"tns:getBLOBByIdRequestType\"/><xsd:element name=\"EmptyResponse\"><xsd:complexType/></xsd:element><xsd:element name=\"update_AttachedbinaryType\" type=\"tns:update_AttachedbinaryTypeRequestType\"/><xsd:element name=\"getBLOBByIdResponse\" type=\"tns:getBLOBByIdResponseType\"/></xsd:schema>" +
        "</wsdl:types>" +
    "<wsdl:message name=\"create_AttachedbinaryTypeRequest\">" +
        "<wsdl:part name=\"create_AttachedbinaryTypeRequest\" element=\"tns:create_AttachedbinaryType\">" +
            "</wsdl:part>" +
        "</wsdl:message>" +
      "<wsdl:message name=\"getBLOBByIdRequest\">" +
        "<wsdl:part name=\"getBLOBByIdRequest\" element=\"tns:getBLOBById\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"findByPrimaryKey_AttachedbinaryTypeResponse\">" +
        "<wsdl:part name=\"findByPrimaryKey_AttachedbinaryTypeResponse\" element=\"tns:findByPrimaryKey_AttachedbinaryTypeResponse\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"delete_AttachedbinaryTypeRequest\">" +
        "<wsdl:part name=\"delete_AttachedbinaryTypeRequest\" element=\"tns:delete_AttachedbinaryType\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"getBLOBByIdResponse\">" +
        "<wsdl:part name=\"getBLOBByIdResponse\" element=\"tns:getBLOBByIdResponse\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"update_AttachedbinaryTypeRequest\">" +
        "<wsdl:part name=\"update_AttachedbinaryTypeRequest\" element=\"tns:update_AttachedbinaryType\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"findAll_AttachedbinaryTypeRequest\">" +
        "<wsdl:part name=\"findAll_AttachedbinaryTypeRequest\" element=\"tns:findAll_AttachedbinaryType\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"EmptyResponse\">" +
        "<wsdl:part name=\"emptyResponse\" element=\"tns:EmptyResponse\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"FaultType\">" +
        "<wsdl:part name=\"fault\" element=\"tns:FaultType\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"findByPrimaryKey_AttachedbinaryTypeRequest\">" +
        "<wsdl:part name=\"findByPrimaryKey_AttachedbinaryTypeRequest\" element=\"tns:findByPrimaryKey_AttachedbinaryType\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"findAll_AttachedbinaryTypeResponse\">" +
        "<wsdl:part name=\"findAll_AttachedbinaryTypeResponse\" element=\"tns:findAll_AttachedbinaryTypeResponse\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:portType name=\"attachedbinaryService_Interface\">" +
        "<wsdl:operation name=\"findByPrimaryKey_AttachedbinaryType\">" +
          "<wsdl:input message=\"tns:findByPrimaryKey_AttachedbinaryTypeRequest\">" +
        "</wsdl:input>" +
          "<wsdl:output message=\"tns:findByPrimaryKey_AttachedbinaryTypeResponse\">" +
        "</wsdl:output>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"create_AttachedbinaryType\">" +
          "<wsdl:input message=\"tns:create_AttachedbinaryTypeRequest\">" +
        "</wsdl:input>" +
          "<wsdl:output name=\"create_AttachedbinaryTypeEmptyResponse\" message=\"tns:EmptyResponse\">" +
        "</wsdl:output>" +
          "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">" +
        "</wsdl:fault>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"delete_AttachedbinaryType\">" +
          "<wsdl:input message=\"tns:delete_AttachedbinaryTypeRequest\">" +
        "</wsdl:input>" +
          "<wsdl:output name=\"delete_AttachedbinaryTypeEmptyResponse\" message=\"tns:EmptyResponse\">" +
        "</wsdl:output>" +
          "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">" +
        "</wsdl:fault>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"findAll_AttachedbinaryType\">" +
          "<wsdl:input message=\"tns:findAll_AttachedbinaryTypeRequest\">" +
        "</wsdl:input>" +
          "<wsdl:output message=\"tns:findAll_AttachedbinaryTypeResponse\">" +
        "</wsdl:output>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"getBLOBById\">" +
          "<wsdl:input message=\"tns:getBLOBByIdRequest\">" +
        "</wsdl:input>" +
          "<wsdl:output message=\"tns:getBLOBByIdResponse\">" +
        "</wsdl:output>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"update_AttachedbinaryType\">" +
          "<wsdl:input message=\"tns:update_AttachedbinaryTypeRequest\">" +
        "</wsdl:input>" +
          "<wsdl:output name=\"update_AttachedbinaryTypeEmptyResponse\" message=\"tns:EmptyResponse\">" +
        "</wsdl:output>" +
          "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">" +
        "</wsdl:fault>" +
        "</wsdl:operation>" +
      "</wsdl:portType>" +
      "<wsdl:binding name=\"attachedbinaryService_SOAP_HTTP\" type=\"tns:attachedbinaryService_Interface\">" +
        "<soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>" +
        "<wsdl:operation name=\"findByPrimaryKey_AttachedbinaryType\">" +
          "<soap:operation soapAction=\"urn:attachedbinaryService:findByPrimaryKey_AttachedbinaryType\"/>" +
          "<wsdl:input>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:input>" +
          "<wsdl:output>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:output>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"create_AttachedbinaryType\">" +
          "<soap:operation soapAction=\"urn:attachedbinaryService:create_AttachedbinaryType\"/>" +
          "<wsdl:input>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:input>" +
          "<wsdl:output>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:output>" +
          "<wsdl:fault name=\"FaultException\">" +
            "<soap:fault name=\"FaultException\" use=\"literal\"/>" +
          "</wsdl:fault>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"delete_AttachedbinaryType\">" +
          "<soap:operation soapAction=\"urn:attachedbinaryService:delete_AttachedbinaryType\"/>" +
          "<wsdl:input>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:input>" +
          "<wsdl:output>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:output>" +
          "<wsdl:fault name=\"FaultException\">" +
            "<soap:fault name=\"FaultException\" use=\"literal\"/>" +
          "</wsdl:fault>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"findAll_AttachedbinaryType\">" +
          "<soap:operation soapAction=\"urn:attachedbinaryService:findAll_AttachedbinaryType\"/>" +
          "<wsdl:input>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:input>" +
          "<wsdl:output>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:output>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"getBLOBById\">" +
          "<soap:operation soapAction=\"urn:attachedbinaryService:getBLOBById\"/>" +
          "<wsdl:input>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:input>" +
          "<wsdl:output>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:output>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"update_AttachedbinaryType\">" +
          "<soap:operation soapAction=\"urn:attachedbinaryService:update_AttachedbinaryType\"/>" +
          "<wsdl:input>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:input>" +
          "<wsdl:output>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:output>" +
          "<wsdl:fault name=\"FaultException\">" +
            "<soap:fault name=\"FaultException\" use=\"literal\"/>" +
          "</wsdl:fault>" +
        "</wsdl:operation>" +
      "</wsdl:binding>" +
      "<wsdl:service name=\"attachedbinaryService\">" +
        "<wsdl:port name=\"attachedbinaryServicePort\" binding=\"tns:attachedbinaryService_SOAP_HTTP\">" +
          "<soap:address location=\"REPLACE_WITH_ENDPOINT_ADDRESS\"/>" +
        "</wsdl:port>" +
      "</wsdl:service>" +
    "</wsdl:definitions>";

    static final String XSD =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<xsd:schema xmlns:ref=\"http://ws-i.org/profiles/basic/1.1/xsd\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xmime=\"http://www.w3.org/2005/05/xmlmime\" targetNamespace=\"urn:attachedbinary\" xmlns=\"urn:attachedbinary\" elementFormDefault=\"qualified\">" +
            "<xsd:import schemaLocation=\"swaref.xsd\" namespace=\"http://ws-i.org/profiles/basic/1.1/xsd\"/>" +
            "<xsd:complexType name=\"attachedbinaryType\">" +
                "<xsd:sequence>" +
                    "<xsd:element name=\"id\" type=\"xsd:decimal\"/>" +
                    "<xsd:element name=\"name\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>" +
                    "<xsd:element name=\"b\" type=\"ref:swaRef\" minOccurs=\"0\" nillable=\"true\" xmime:expectedContentTypes=\"application/octet-stream\"/>" +
                "</xsd:sequence>" +
            "</xsd:complexType>" +
            "<xsd:element name=\"attachedbinaryType\" type=\"attachedbinaryType\"/>" +
        "</xsd:schema>";
}
