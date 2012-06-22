/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 2008, created DBWS test package
 ******************************************************************************/
package dbws.testing.attachedbinary;

//javase imports
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

//java eXtension imports
import javax.activation.DataHandler;
import javax.wsdl.WSDLException;

//JUnit4 imports
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

    @BeforeClass
    public static void setUp() throws WSDLException {
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
        DBWSTestSuite.setUp(".");
    }

    public static SOAPAttachmentHandler attachmentHandler = new SOAPAttachmentHandler();

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void findAll() {
        Invocation invocation = new Invocation("findAll_attachedbinaryType");
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
        /*
        String resultString = DBWSTestProviderHelper.documentToString(doc);
        System.out.println(resultString);
        String controlString = DBWSTestProviderHelper.documentToString(controlDoc);
        System.out.println(controlString);
        */        
        assertTrue("control document not same as instance document",
            comparer.isNodeEqual(controlDoc, doc));
        
        // validate WSDL
        Document controlWSDL = xmlParser.parse(new StringReader(WSDL));
        removeEmptyTextNodes(controlWSDL);
        Document testWSDL = xmlParser.parse(new StringReader(DBWS_WSDL_STREAM.toString()));
        removeEmptyTextNodes(testWSDL);
        assertTrue("control WSDL not same as instance document", comparer.isNodeEqual(controlWSDL, testWSDL));
        // validate XSD
        Document controlXSD = xmlParser.parse(new StringReader(XSD));
        removeEmptyTextNodes(controlXSD);
        Document testXSD = xmlParser.parse(new StringReader(DBWS_SCHEMA_STREAM.toString()));
        removeEmptyTextNodes(testXSD);
        assertTrue("control XSD not same as instance document", comparer.isNodeEqual(controlXSD, testXSD));
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
    public void getAttachments() throws IOException {
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
    		"<xsd:schema elementFormDefault=\"qualified\" targetNamespace=\"urn:attachedbinaryService\" xmlns:ref=\"http://ws-i.org/profiles/basic/1.1/xsd\" xmlns:tns=\"urn:attachedbinaryService\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><xsd:import namespace=\"urn:attachedbinary\" schemaLocation=\"eclipselink-dbws-schema.xsd\"/><xsd:import namespace=\"http://ws-i.org/profiles/basic/1.1/xsd\" schemaLocation=\"swaref.xsd\"/><xsd:complexType name=\"findAll_attachedbinaryTypeResponseType\"><xsd:sequence><xsd:element name=\"result\"><xsd:complexType><xsd:sequence><xsd:element maxOccurs=\"unbounded\" minOccurs=\"0\" ref=\"ns1:attachedbinaryType\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType><xsd:complexType name=\"findByPrimaryKey_attachedbinaryTypeRequestType\"><xsd:sequence><xsd:element name=\"id\" type=\"xsd:decimal\"/></xsd:sequence></xsd:complexType><xsd:complexType name=\"getBLOBByIdResponseType\"><xsd:sequence><xsd:element name=\"result\" type=\"ref:swaRef\"/></xsd:sequence></xsd:complexType><xsd:complexType name=\"findByPrimaryKey_attachedbinaryTypeResponseType\"><xsd:sequence><xsd:element name=\"result\"><xsd:complexType><xsd:sequence><xsd:element minOccurs=\"0\" ref=\"ns1:attachedbinaryType\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType><xsd:complexType name=\"update_attachedbinaryTypeRequestType\"><xsd:sequence><xsd:element name=\"theInstance\"><xsd:complexType><xsd:sequence><xsd:element ref=\"ns1:attachedbinaryType\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType><xsd:complexType name=\"getBLOBByIdRequestType\"><xsd:sequence><xsd:element name=\"pk\" type=\"xsd:decimal\"/></xsd:sequence></xsd:complexType><xsd:complexType name=\"create_attachedbinaryTypeRequestType\"><xsd:sequence><xsd:element name=\"theInstance\"><xsd:complexType><xsd:sequence><xsd:element ref=\"ns1:attachedbinaryType\"/></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType><xsd:complexType name=\"findAll_attachedbinaryTypeRequestType\"/><xsd:complexType name=\"delete_attachedbinaryTypeRequestType\"><xsd:sequence><xsd:element name=\"id\" type=\"xsd:decimal\"/></xsd:sequence></xsd:complexType><xsd:element name=\"findByPrimaryKey_attachedbinaryTypeResponse\" type=\"tns:findByPrimaryKey_attachedbinaryTypeResponseType\"/><xsd:element name=\"findByPrimaryKey_attachedbinaryType\" type=\"tns:findByPrimaryKey_attachedbinaryTypeRequestType\"/><xsd:element name=\"create_attachedbinaryType\" type=\"tns:create_attachedbinaryTypeRequestType\"/><xsd:element name=\"findAll_attachedbinaryTypeResponse\" type=\"tns:findAll_attachedbinaryTypeResponseType\"/><xsd:element name=\"findAll_attachedbinaryType\" type=\"tns:findAll_attachedbinaryTypeRequestType\"/><xsd:element name=\"delete_attachedbinaryType\" type=\"tns:delete_attachedbinaryTypeRequestType\"/><xsd:element name=\"FaultType\"><xsd:complexType><xsd:sequence><xsd:element name=\"faultCode\" type=\"xsd:string\"/><xsd:element name=\"faultString\" type=\"xsd:string\"/></xsd:sequence></xsd:complexType></xsd:element><xsd:element name=\"getBLOBById\" type=\"tns:getBLOBByIdRequestType\"/><xsd:element name=\"EmptyResponse\"><xsd:complexType/></xsd:element><xsd:element name=\"update_attachedbinaryType\" type=\"tns:update_attachedbinaryTypeRequestType\"/><xsd:element name=\"getBLOBByIdResponse\" type=\"tns:getBLOBByIdResponseType\"/></xsd:schema>" +
    	"</wsdl:types>" +
    	"<wsdl:message name=\"create_attachedbinaryTypeRequest\">" +
    		"<wsdl:part name=\"create_attachedbinaryTypeRequest\" element=\"tns:create_attachedbinaryType\">" +
    		"</wsdl:part>" +
        "</wsdl:message>" +
      "<wsdl:message name=\"getBLOBByIdRequest\">" +
        "<wsdl:part name=\"getBLOBByIdRequest\" element=\"tns:getBLOBById\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"findByPrimaryKey_attachedbinaryTypeResponse\">" +
        "<wsdl:part name=\"findByPrimaryKey_attachedbinaryTypeResponse\" element=\"tns:findByPrimaryKey_attachedbinaryTypeResponse\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"delete_attachedbinaryTypeRequest\">" +
        "<wsdl:part name=\"delete_attachedbinaryTypeRequest\" element=\"tns:delete_attachedbinaryType\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"getBLOBByIdResponse\">" +
        "<wsdl:part name=\"getBLOBByIdResponse\" element=\"tns:getBLOBByIdResponse\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"update_attachedbinaryTypeRequest\">" +
        "<wsdl:part name=\"update_attachedbinaryTypeRequest\" element=\"tns:update_attachedbinaryType\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"findAll_attachedbinaryTypeRequest\">" +
        "<wsdl:part name=\"findAll_attachedbinaryTypeRequest\" element=\"tns:findAll_attachedbinaryType\">" +
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
      "<wsdl:message name=\"findByPrimaryKey_attachedbinaryTypeRequest\">" +
        "<wsdl:part name=\"findByPrimaryKey_attachedbinaryTypeRequest\" element=\"tns:findByPrimaryKey_attachedbinaryType\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:message name=\"findAll_attachedbinaryTypeResponse\">" +
        "<wsdl:part name=\"findAll_attachedbinaryTypeResponse\" element=\"tns:findAll_attachedbinaryTypeResponse\">" +
        "</wsdl:part>" +
      "</wsdl:message>" +
      "<wsdl:portType name=\"attachedbinaryService_Interface\">" +
        "<wsdl:operation name=\"findByPrimaryKey_attachedbinaryType\">" +
          "<wsdl:input message=\"tns:findByPrimaryKey_attachedbinaryTypeRequest\">" +
        "</wsdl:input>" +
          "<wsdl:output message=\"tns:findByPrimaryKey_attachedbinaryTypeResponse\">" +
        "</wsdl:output>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"create_attachedbinaryType\">" +
          "<wsdl:input message=\"tns:create_attachedbinaryTypeRequest\">" +
        "</wsdl:input>" +
          "<wsdl:output name=\"create_attachedbinaryTypeEmptyResponse\" message=\"tns:EmptyResponse\">" +
        "</wsdl:output>" +
          "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">" +
        "</wsdl:fault>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"delete_attachedbinaryType\">" +
          "<wsdl:input message=\"tns:delete_attachedbinaryTypeRequest\">" +
        "</wsdl:input>" +
          "<wsdl:output name=\"delete_attachedbinaryTypeEmptyResponse\" message=\"tns:EmptyResponse\">" +
        "</wsdl:output>" +
          "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">" +
        "</wsdl:fault>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"findAll_attachedbinaryType\">" +
          "<wsdl:input message=\"tns:findAll_attachedbinaryTypeRequest\">" +
        "</wsdl:input>" +
          "<wsdl:output message=\"tns:findAll_attachedbinaryTypeResponse\">" +
        "</wsdl:output>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"getBLOBById\">" +
          "<wsdl:input message=\"tns:getBLOBByIdRequest\">" +
        "</wsdl:input>" +
          "<wsdl:output message=\"tns:getBLOBByIdResponse\">" +
        "</wsdl:output>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"update_attachedbinaryType\">" +
          "<wsdl:input message=\"tns:update_attachedbinaryTypeRequest\">" +
        "</wsdl:input>" +
          "<wsdl:output name=\"update_attachedbinaryTypeEmptyResponse\" message=\"tns:EmptyResponse\">" +
        "</wsdl:output>" +
          "<wsdl:fault name=\"FaultException\" message=\"tns:FaultType\">" +
        "</wsdl:fault>" +
        "</wsdl:operation>" +
      "</wsdl:portType>" +
      "<wsdl:binding name=\"attachedbinaryService_SOAP_HTTP\" type=\"tns:attachedbinaryService_Interface\">" +
        "<soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>" +
        "<wsdl:operation name=\"findByPrimaryKey_attachedbinaryType\">" +
          "<soap:operation soapAction=\"urn:attachedbinaryService:findByPrimaryKey_attachedbinaryType\"/>" +
          "<wsdl:input>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:input>" +
          "<wsdl:output>" +
            "<soap:body use=\"literal\"/>" +
          "</wsdl:output>" +
        "</wsdl:operation>" +
        "<wsdl:operation name=\"create_attachedbinaryType\">" +
          "<soap:operation soapAction=\"urn:attachedbinaryService:create_attachedbinaryType\"/>" +
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
        "<wsdl:operation name=\"delete_attachedbinaryType\">" +
          "<soap:operation soapAction=\"urn:attachedbinaryService:delete_attachedbinaryType\"/>" +
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
        "<wsdl:operation name=\"findAll_attachedbinaryType\">" +
          "<soap:operation soapAction=\"urn:attachedbinaryService:findAll_attachedbinaryType\"/>" +
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
        "<wsdl:operation name=\"update_attachedbinaryType\">" +
          "<soap:operation soapAction=\"urn:attachedbinaryService:update_attachedbinaryType\"/>" +
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
    	"<xsd:schema xmlns:ref=\"http://ws-i.org/profiles/basic/1.1/xsd\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"urn:attachedbinary\" xmlns=\"urn:attachedbinary\" elementFormDefault=\"qualified\">" +
    		"<xsd:import schemaLocation=\"swaref.xsd\" namespace=\"http://ws-i.org/profiles/basic/1.1/xsd\"/>" +
    		"<xsd:complexType name=\"attachedbinaryType\">" +
    			"<xsd:sequence>" +
    				"<xsd:element name=\"id\" type=\"xsd:decimal\"/>" +
    				"<xsd:element name=\"name\" type=\"xsd:string\" minOccurs=\"0\" nillable=\"true\"/>" +
    				"<xsd:element name=\"b\" type=\"ref:swaRef\" minOccurs=\"0\" nillable=\"true\" xmlns:ns0=\"http://www.w3.org/2005/05/xmlmime\" ns0:expectedContentTypes=\"application/octet-stream\"/>" +
    			"</xsd:sequence>" +
    		"</xsd:complexType>" +
    		"<xsd:element name=\"attachedbinaryType\" type=\"attachedbinaryType\"/>" +
    	"</xsd:schema>";
}
