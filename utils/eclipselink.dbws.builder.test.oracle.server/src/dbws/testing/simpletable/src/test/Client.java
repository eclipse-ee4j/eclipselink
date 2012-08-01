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
 *     David McCann - July 30, 2012 - 2.4.1 - Initial implementation
 ******************************************************************************/
package dbws.testing.simpletable.src.test;

//javase imports
import java.io.StringReader;
import java.io.StringWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//java eXtension imports
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

//EclipseLink imports
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;

public class Client {

    static XMLComparer comparer = new XMLComparer();
    static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    static XMLParser xmlParser = xmlPlatform.newXMLParser();

    static final String SOAP_FINDBYPK_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findByPrimaryKey_simpletableType xmlns=\"urn:simpletableService\">" +
              "<id>1</id>" +
            "</findByPrimaryKey_simpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_FINDALL_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findAll_simpletableType xmlns=\"urn:simpletableService\" xmlns:urn=\"urn:simpletable\"/>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_UPDATE_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<update_simpletableType xmlns=\"urn:simpletableService\" xmlns:urn=\"urn:simpletable\">" +
              "<theInstance>" +
                "<urn:simpletableType>" +
                  "<urn:id>1</urn:id>" +
                  "<urn:name>mike norman</urn:name>" +
                  "<urn:since>2001-12-25</urn:since>" +
                "</urn:simpletableType>" +
              "</theInstance>" +
            "</update_simpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_CREATE_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<create_simpletableType xmlns=\"urn:simpletableService\" xmlns:urn=\"urn:simpletable\">" +
              "<theInstance>" +
                "<urn:simpletableType>" +
                  "<urn:id>4</urn:id>" +
                  "<urn:name>test</urn:name>" +
                  "<urn:since>2009-03-27</urn:since>" +
                "</urn:simpletableType>" +
              "</theInstance>" +
            "</create_simpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_DELETE_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<delete_simpletableType xmlns=\"urn:simpletableService\" xmlns:urn=\"urn:simpletable\">" +
              "<id>4</id>" +
            "</delete_simpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_UPDATE2_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<update_simpletableType xmlns=\"urn:simpletableService\" xmlns:urn=\"urn:simpletable\">" +
              "<theInstance>" +
                "<urn:simpletableType>" +
                  "<urn:id>1</urn:id>" +
                  "<urn:name>mike</urn:name>" +
                  "<urn:since>2001-12-25</urn:since>" +
                "</urn:simpletableType>" +
              "</theInstance>" +
            "</update_simpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";


    // pass in two args: hostname, port
    public static void main(String[] args) throws SOAPException, TransformerException {
        if (args.length != 2) {
            System.out.println("Client - requires 2 args: hostname, port");
            System.exit(-1);
        }
        String hostname = args[0];
        String port = args[1];

        QName qname = new QName("urn:simpletableService", "simpletableServicePort");
        Service service = Service.create(new QName("urn:simpletable", "simpletableService"));
        service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING,
            "http://" + hostname + ":" + port + "/simpletable/simpletable");
        Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class,
            Service.Mode.MESSAGE);
        SOAPMessage request = createSOAPMessage(SOAP_FINDBYPK_REQUEST);
        SOAPMessage result = sourceDispatch.invoke(request);
        if (result != null) {
            SOAPBody responseBody = result.getSOAPPart().getEnvelope().getBody();
            Document resultDoc = responseBody.extractContentAsDocument();
			removeEmptyTextNodes(resultDoc);
            //Document controlDoc = xmlParser.parse(new StringReader(XML_HEADER + SOAP_FINDBYPK_RESPONSE_PREAMBLE_1 + SOAP_FINDBYPK_RESPONSE));
            Document controlDoc = xmlParser.parse(new StringReader(XML_HEADER + SOAP_FINDBYPK_RESPONSE_PREAMBLE_2 + SOAP_FINDBYPK_RESPONSE));
			removeEmptyTextNodes(controlDoc);
			comparer.setIgnoreOrder(true);
            boolean nodeEqual = comparer.isNodeEqual(controlDoc, resultDoc);
            //if (!(documentToString(resultDoc).equals(documentToString(controlDoc)))) {
            if (!nodeEqual) {
                System.out.println("findByPrimaryKey_simpletableType response not same as control document");
                System.out.println("resultDoc: \n" + documentToString(resultDoc));
                System.out.println("controlDoc: \n" + documentToString(controlDoc));
            }
        }

        request = createSOAPMessage(SOAP_FINDALL_REQUEST);
        result = sourceDispatch.invoke(request);
        if (result != null) {
            SOAPBody responseBody = result.getSOAPPart().getEnvelope().getBody();
            Document resultDoc = responseBody.extractContentAsDocument();
            Document controlDoc = xmlParser.parse(new StringReader(SOAP_FINDALL_RESPONSE));
            boolean nodeEqual = comparer.isNodeEqual(controlDoc, resultDoc);
            if (!nodeEqual) {
                System.out.println("findAll_simpletableType response not same as control document");
                System.out.println("resultDoc: \n" + documentToString(resultDoc));
                System.out.println("controlDoc: \n" + documentToString(controlDoc));
            }
        }

        request = createSOAPMessage(SOAP_UPDATE_REQUEST);
        result = sourceDispatch.invoke(request);
        if (result != null) {
            String localName = result.getSOAPBody().getFirstChild().getLocalName();
            if (!SOAP_UPDATE_RESPONSE_ELEMENTNAME.equals(localName)) {
                System.out.println(SOAP_UPDATE_RESPONSE_ELEMENTNAME + " incorrect");
            }
        }

        request = createSOAPMessage(SOAP_FINDBYPK_REQUEST);
        result = sourceDispatch.invoke(request);
        if (result != null) {
            SOAPBody responseBody = result.getSOAPPart().getEnvelope().getBody();
            Document resultDoc = responseBody.extractContentAsDocument();
            Document controlDoc = xmlParser.parse(new StringReader(SOAP_FINDBYPK_AFTERUPDATE_RESPONSE));
            boolean nodeEqual = comparer.isNodeEqual(controlDoc, resultDoc);
            if (!nodeEqual) {
                System.out.println("findByPrimaryKey_simpletableType response (after update) not same as control document");
                System.out.println("resultDoc: \n" + documentToString(resultDoc));
                System.out.println("controlDoc: \n" + documentToString(controlDoc));
            }
        }

        request = createSOAPMessage(SOAP_CREATE_REQUEST);
        result = sourceDispatch.invoke(request);
        if (result != null) {
            String localName = result.getSOAPBody().getFirstChild().getLocalName();
            if (!SOAP_CREATE_RESPONSE_ELEMENTNAME.equals(localName)) {
                System.out.println(SOAP_CREATE_RESPONSE_ELEMENTNAME + " incorrect");
            }
        }

        request = createSOAPMessage(SOAP_DELETE_REQUEST);
        result = sourceDispatch.invoke(request);
        if (result != null) {
            String localName = result.getSOAPBody().getFirstChild().getLocalName();
            if (!SOAP_DELETE_RESPONSE_ELEMENTNAME.equals(localName)) {
                System.out.println(SOAP_DELETE_RESPONSE_ELEMENTNAME + " incorrect");
            }
        }

        request = createSOAPMessage(SOAP_UPDATE2_REQUEST);
        result = sourceDispatch.invoke(request);
        if (result != null) {
            String localName = result.getSOAPBody().getFirstChild().getLocalName();
            if (!SOAP_UPDATE_RESPONSE_ELEMENTNAME.equals(localName)) {
                System.out.println(SOAP_UPDATE_RESPONSE_ELEMENTNAME + " incorrect");
            }
        }

    }

    static final String XML_HEADER =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    static final String SOAP_FINDBYPK_RESPONSE_PREAMBLE_1 =
        "<srvc:findByPrimaryKey_simpletableTypeResponse xmlns:srvc=\"urn:simpletableService\" xmlns=\"urn:simpletable\" " + 
            "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";

    static final String SOAP_FINDBYPK_RESPONSE_PREAMBLE_2 =
        "<srvc:findByPrimaryKey_simpletableTypeResponse xmlns=\"urn:simpletable\" xmlns:srvc=\"urn:simpletableService\">";

	static final String SOAP_FINDBYPK_RESPONSE = 
            "<srvc:result>" +
                "<simpletableType>" +
                    "<id>1</id>" +
                    "<name>mike</name>" +
                    "<since>2001-12-25</since>" +
                "</simpletableType>" +
            "</srvc:result>" +
        "</srvc:findByPrimaryKey_simpletableTypeResponse>";
    static final String SOAP_FINDALL_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<srvc:findAll_simpletableTypeResponse xmlns=\"urn:simpletable\" xmlns:srvc=\"urn:simpletableService\">" +
            "<srvc:result>" +
                "<simpletableType>" +
                    "<id>1</id>" +
                    "<name>mike</name>" +
                    "<since>2001-12-25</since>" +
                 "</simpletableType>" +
                 "<simpletableType>" +
                    "<id>2</id>" +
                    "<name>blaise</name>" +
                    "<since>2001-12-25</since>" +
                 "</simpletableType>" +
                 "<simpletableType>" +
                    "<id>3</id>" +
                    "<name>rick</name>" +
                    "<since>2001-12-25</since>" +
                 "</simpletableType>" +
             "</srvc:result>" +
        "</srvc:findAll_simpletableTypeResponse>";
    static final String SOAP_UPDATE_RESPONSE_ELEMENTNAME =
        "update_simpletableTypeResponse";
    static final String SOAP_FINDBYPK_AFTERUPDATE_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<srvc:findByPrimaryKey_simpletableTypeResponse xmlns=\"urn:simpletable\" xmlns:srvc=\"urn:simpletableService\">" +
            "<srvc:result>" +
                "<simpletableType>" +
                    "<id>1</id>" +
                    "<name>mike norman</name>" +
                    "<since>2001-12-25</since>" +
                "</simpletableType>" +
            "</srvc:result>" +
        "</srvc:findByPrimaryKey_simpletableTypeResponse>";
    static final String SOAP_CREATE_RESPONSE_ELEMENTNAME =
        "create_simpletableTypeResponse";
    static final String SOAP_DELETE_RESPONSE_ELEMENTNAME =
        "delete_simpletableTypeResponse";

    static SOAPMessage createSOAPMessage(String message) {
        try {
            MessageFactory factory = MessageFactory.newInstance();
            SOAPMessage soapMessage = factory.createMessage();
            soapMessage.getSOAPPart().setContent(
                (Source)new StreamSource(new StringReader(message)));
            soapMessage.saveChanges();
            return soapMessage;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    static Transformer getTransformer() {
        Transformer transformer = null;
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            transformer = tf.newTransformer();
        }
        catch (TransformerConfigurationException e) {
            /* extremely rare, safe to ignore */
        }
        return transformer;
    }

    /**
     * Returns the given org.w3c.dom.Document as a String.
     *
     */
    static String documentToString(Document doc) {
        DOMSource domSource = new DOMSource(doc);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.transform(domSource, result);
            return stringWriter.toString();
        } catch (Exception e) {
            // e.printStackTrace();
            return "<empty/>";
        }
    }

	/**
     * Helper method that removes empty text nodes from a Document.
     * This is typically called prior to comparing two documents
     * for equality.
     *
     */
    public static void removeEmptyTextNodes(Node node) {
        NodeList nodeList = node.getChildNodes();
        Node childNode;
        for (int x = nodeList.getLength() - 1; x >= 0; x--) {
            childNode = nodeList.item(x);
            if (childNode.getNodeType() == Node.TEXT_NODE) {
                if (childNode.getNodeValue().trim().equals("")) {
                    node.removeChild(childNode);
                }
            } else if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                removeEmptyTextNodes(childNode);
            }
        }
    }

}