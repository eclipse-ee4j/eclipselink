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
 * dmccann - 2.3 - Initial implementation
 ******************************************************************************/
package dbws.testing.visit;

//javase imports
import static dbws.testing.visit.DBWSTestHelper.PACKAGE_NAME;
import static dbws.testing.visit.DBWSTestHelper.PROC8;
import static dbws.testing.visit.DBWSTestHelper.PROC8_NAMESPACE;
import static dbws.testing.visit.DBWSTestHelper.PROC8_PORT;
import static dbws.testing.visit.DBWSTestHelper.PROC8_SERVICE;
import static dbws.testing.visit.DBWSTestHelper.PROC8_SERVICE_NAMESPACE;
import static dbws.testing.visit.DBWSTestHelper.PROC8_TEST;
import static javax.xml.ws.Service.Mode.MESSAGE;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import javax.annotation.PostConstruct;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.eclipse.persistence.tools.dbws.PLSQLProcedureOperationModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@WebServiceProvider(
    targetNamespace = PROC8_SERVICE_NAMESPACE,
    serviceName = PROC8_SERVICE,
    portName = PROC8_PORT
)
@ServiceMode(MESSAGE)
public class OverloadedPLSQLStoredProcedureSimpleArgsTestSuite extends WebServiceTestSuite implements Provider<SOAPMessage> {

    static final String ENDPOINT_ADDRESS = "http://localhost:9999/" + PROC8_TEST;
    
    @BeforeClass
    public static void setUp() throws WSDLException {
        builder.setProjectName(PROC8_TEST);
        builder.setTargetNamespace(PROC8_NAMESPACE);
        PLSQLProcedureOperationModel p1Model = new PLSQLProcedureOperationModel();
        p1Model.setName(PROC8_TEST);
        p1Model.setReturnType("xsd:int");
        p1Model.setOverload(2);
        p1Model.setSchemaPattern("TLUSER");
        p1Model.setCatalogPattern(PACKAGE_NAME);
        p1Model.setProcedurePattern(PROC8);
        builder.getOperations().add(p1Model);
        serviceSetup(ENDPOINT_ADDRESS, new OverloadedPLSQLStoredProcedureSimpleArgsTestSuite());
    }

    @PostConstruct
    public void init() {
        super.init();
    }

    static final String REQUEST_MSG =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Header/>" +
          "<env:Body>" +
            "<srvc:" + PROC8_TEST + "1 xmlns:srvc=\"" + PROC8_SERVICE_NAMESPACE + "\" xmlns:ns1=\"" + PROC8_NAMESPACE + "\">" +
              "<srvc:FOO>" + "test" + "</srvc:FOO>" +
            "</srvc:" + PROC8_TEST + "1>" +
          "</env:Body>" +
        "</env:Envelope>";
    
    static final String REQUEST_MSG_2 =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Header/>" +
          "<env:Body>" +
            "<srvc:" + PROC8_TEST + "2 xmlns:srvc=\"" + PROC8_SERVICE_NAMESPACE + "\" xmlns:ns1=\"" + PROC8_NAMESPACE + "\">" +
              "<srvc:FOO>" + "second" + "</srvc:FOO>" +
              "<srvc:BAR>" + "test" + "</srvc:BAR>" +
            "</srvc:" + PROC8_TEST + "2>" +
          "</env:Body>" +
        "</env:Envelope>";

    @Test
    public void p8Test1() throws SOAPException, IOException, SAXException,
        ParserConfigurationException, TransformerException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage request = factory.createMessage();
        SOAPPart part = request.getSOAPPart();
        DOMSource domSource = new DOMSource(getDocumentBuilder().parse(
            new InputSource(new StringReader(REQUEST_MSG))));
        part.setContent(domSource);
        Dispatch<SOAPMessage> dispatch = testService.createDispatch(portQName, SOAPMessage.class,
            Service.Mode.MESSAGE);
        SOAPMessage response = null;
        try {
            response = dispatch.invoke(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (response != null) {
            Source src = response.getSOAPPart().getContent();
            DOMResult result = new DOMResult();
            getTransformer().transform(src, result);
            Document resultDoc = (Document)result.getNode();
            Document controlDoc = xmlParser.parse(new StringReader(TEST_RESPONSE));
            /*
            System.out.println("\n---- Control Document ----");
            System.out.println(DBWSTestHelper.documentToString(controlDoc));
            System.out.println("---- Result Document ----");
            System.out.println(DBWSTestHelper.documentToString(resultDoc));
            System.out.println("\n");
            */
            assertTrue("control document not same as instance document",
                comparer.isNodeEqual(controlDoc, resultDoc));
        }
    }

    @Test
    public void p8Test2() throws SOAPException, IOException, SAXException,
        ParserConfigurationException, TransformerException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage request = factory.createMessage();
        SOAPPart part = request.getSOAPPart();
        DOMSource domSource = new DOMSource(getDocumentBuilder().parse(
            new InputSource(new StringReader(REQUEST_MSG_2))));
        part.setContent(domSource);
        Dispatch<SOAPMessage> dispatch = testService.createDispatch(portQName, SOAPMessage.class,
            Service.Mode.MESSAGE);
        SOAPMessage response = null;
        try {
            response = dispatch.invoke(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (response != null) {
            Source src = response.getSOAPPart().getContent();
            DOMResult result = new DOMResult();
            getTransformer().transform(src, result);
            Document resultDoc = (Document)result.getNode();
            Document controlDoc = xmlParser.parse(new StringReader(TEST_RESPONSE_2));
            /*
            System.out.println("\n---- Control Document ----");
            System.out.println(DBWSTestHelper.documentToString(controlDoc));
            System.out.println("---- Result Document ----");
            System.out.println(DBWSTestHelper.documentToString(resultDoc));
            System.out.println("\n");
            */
            assertTrue("control document not same as instance document",
                comparer.isNodeEqual(controlDoc, resultDoc));
        }
    }

    static final String TEST_RESPONSE =
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<SOAP-ENV:Header/>" +
          "<SOAP-ENV:Body>" +
            "<srvc:" + PROC8_TEST + "1Response xmlns:srvc=\"" + PROC8_SERVICE_NAMESPACE + "\">" +
              "<srvc:result>1</srvc:result>" +
            "</srvc:" + PROC8_TEST + "1Response>" +
          "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";

    static final String TEST_RESPONSE_2 =
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<SOAP-ENV:Header/>" +
          "<SOAP-ENV:Body>" +
            "<srvc:" + PROC8_TEST + "2Response xmlns:srvc=\"" + PROC8_SERVICE_NAMESPACE + "\">" +
              "<srvc:result>1</srvc:result>" +
            "</srvc:" + PROC8_TEST + "2Response>" +
          "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";
}