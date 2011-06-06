/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 2008, created DBWS Oracle test package
 ******************************************************************************/
package dbws.testing.visit;

//javase imports
import java.io.IOException;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//java eXtension imports
import javax.annotation.PostConstruct;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;
import static javax.xml.ws.Service.Mode.MESSAGE;

//JUnit4 imports
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

//EclipseLink imports
import org.eclipse.persistence.tools.dbws.PLSQLProcedureOperationModel;

//test imports
import static dbws.testing.visit.DBWSTestHelper.PACKAGE_NAME;
import static dbws.testing.visit.DBWSTestHelper.PROC5;
import static dbws.testing.visit.DBWSTestHelper.PROC5_NAMESPACE;
import static dbws.testing.visit.DBWSTestHelper.PROC5_SERVICE_NAMESPACE;
import static dbws.testing.visit.DBWSTestHelper.PROC5_PORT;
import static dbws.testing.visit.DBWSTestHelper.PROC5_SERVICE;
import static dbws.testing.visit.DBWSTestHelper.PROC5_TEST;

@WebServiceProvider(
    targetNamespace = PROC5_SERVICE_NAMESPACE,
    serviceName = PROC5_SERVICE,
    portName = PROC5_PORT
)
@ServiceMode(MESSAGE)
public class P5testWebServiceSuite extends WebServiceTestSuite implements Provider<SOAPMessage> {

    static final String ENDPOINT_ADDRESS = "http://localhost:9999/" + PROC5_TEST;

    @BeforeClass
    public static void setUp() throws WSDLException {
        builder.setProjectName(PROC5_TEST);
        builder.setTargetNamespace(PROC5_NAMESPACE);
        PLSQLProcedureOperationModel p5Model = new PLSQLProcedureOperationModel();
        p5Model.setName(PROC5_TEST);
        builder.getSchema().getNamespaceResolver().put("ns1", PROC5_NAMESPACE);
        p5Model.setReturnType("ns1:" + "SOMEPACKAGE_ARECORD");
        p5Model.setCatalogPattern(PACKAGE_NAME);
        p5Model.setProcedurePattern(PROC5);
        builder.getOperations().add(p5Model);
        serviceSetup(ENDPOINT_ADDRESS, new P5testWebServiceSuite());
    }

    @PostConstruct
    public void init() {
        super.init();
    }

    static final String REQUEST_MSG =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Header/>" +
          "<env:Body>" +
            "<srvc:" + PROC5_TEST + " xmlns:srvc=\"" + PROC5_SERVICE_NAMESPACE + "\" xmlns:ns1=\"" + PROC5_NAMESPACE + "\">" +
              "<srvc:OLDREC>" +
                "<ns1:t1>" +
                  "<ns1:item>this</ns1:item>" +
                  "<ns1:item>is</ns1:item>" +
                  "<ns1:item>a</ns1:item>" +
                  "<ns1:item>yet another</ns1:item>" +
                  "<ns1:item>list of</ns1:item>" +
                  "<ns1:item>strings</ns1:item>" +
                "</ns1:t1>" +
                "<ns1:t2>" +
                  "<ns1:item>3000.00</ns1:item>" +
                  "<ns1:item>4050.01</ns1:item>" +
                  "<ns1:item>6000.07</ns1:item>" +
                "</ns1:t2>" +
                "<ns1:t3>false</ns1:t3>" +
              "</srvc:OLDREC>" +
            "</srvc:" + PROC5_TEST + ">" +
          "</env:Body>" +
        "</env:Envelope>";

    @Test
    public void p5Test() throws SOAPException, IOException, SAXException,
        ParserConfigurationException, TransformerException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage request = factory.createMessage();
        SOAPPart part = request.getSOAPPart();
        DOMSource domSource = new DOMSource(
            getDocumentBuilder().parse(new InputSource(new StringReader(REQUEST_MSG))));
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
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMResult result = new DOMResult();
            transformer.transform(src, result);
            Document resultDoc = (Document)result.getNode();
            Document controlDoc = xmlParser.parse(new StringReader(TEST_RESPONSE));
            assertTrue("control document not same as instance document",
                comparer.isNodeEqual(controlDoc, resultDoc));
        }
    }

    static final String TEST_RESPONSE =
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<SOAP-ENV:Header/>" +
          "<SOAP-ENV:Body>" +
            "<srvc:" + PROC5_TEST + "Response xmlns=\"" + PROC5_NAMESPACE + "\" xmlns:srvc=\"" + PROC5_SERVICE_NAMESPACE + "\">" +
              "<srvc:result>" +
                "<SOMEPACKAGE_ARECORD>" +
                  "<t1>" +
                    "<item>this</item>" +
                    "<item>is</item>" +
                    "<item>a</item>" +
                    "<item>yet another</item>" +
                    "<item>list of</item>" +
                    "<item>strings</item>" +
                  "</t1>" +
                  "<t2>" +
                    "<item>3000</item>" +
                    "<item>4050.01</item>" +
                    "<item>6000.07</item>" +
                  "</t2>" +
                  "<t3>false</t3>" +
                "</SOMEPACKAGE_ARECORD>" +
              "</srvc:result>" +
            "</srvc:" + PROC5_TEST + "Response>" +
          "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";
}