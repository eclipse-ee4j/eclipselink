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
import static dbws.testing.visit.DBWSTestHelper.PROC6;
import static dbws.testing.visit.DBWSTestHelper.PROC6_NAMESPACE;
import static dbws.testing.visit.DBWSTestHelper.PROC6_SERVICE_NAMESPACE;
import static dbws.testing.visit.DBWSTestHelper.PROC6_PORT;
import static dbws.testing.visit.DBWSTestHelper.PROC6_SERVICE;
import static dbws.testing.visit.DBWSTestHelper.PROC6_TEST;

@WebServiceProvider(
    targetNamespace = PROC6_SERVICE_NAMESPACE,
    serviceName = PROC6_SERVICE,
    portName = PROC6_PORT
)
@ServiceMode(MESSAGE)
public class P6testWebServiceSuite extends WebServiceTestSuite implements Provider<SOAPMessage> {

    static final String ENDPOINT_ADDRESS = "http://localhost:9999/" + PROC6_TEST;

    @BeforeClass
    public static void setUp() throws WSDLException {
        builder.setProjectName(PROC6_TEST);
        builder.setTargetNamespace(PROC6_NAMESPACE);
        PLSQLProcedureOperationModel p6Model = new PLSQLProcedureOperationModel();
        p6Model.setName(PROC6_TEST);
        p6Model.setReturnType("xsd:int");
        p6Model.setCatalogPattern(PACKAGE_NAME);
        p6Model.setProcedurePattern(PROC6);
        builder.getOperations().add(p6Model);
        serviceSetup(ENDPOINT_ADDRESS, new P6testWebServiceSuite());
    }

    @PostConstruct
    public void init() {
        super.init();
    }

    static final String REQUEST_MSG =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Header/>" +
          "<env:Body>" +
            "<srvc:" + PROC6_TEST + " xmlns:srvc=\"" + PROC6_SERVICE_NAMESPACE + "\" xmlns:ns1=\"" + PROC6_NAMESPACE + "\">" +
              "<srvc:BAR>" +
                "<ns1:item>" +
                  "<ns1:item>1</ns1:item>" +
                  "<ns1:item>2</ns1:item>" +
                  "<ns1:item>3</ns1:item>" +
                "</ns1:item>" +
                "<ns1:item>" +
                  "<ns1:item>1</ns1:item>" +
                  "<ns1:item>4</ns1:item>" +
                  "<ns1:item>9</ns1:item>" +
                "</ns1:item>" +
                "<ns1:item>" +
                  "<ns1:item>1</ns1:item>" +
                  "<ns1:item>8</ns1:item>" +
                  "<ns1:item>27</ns1:item>" +
                "</ns1:item>" +
              "</srvc:BAR>" +
            "</srvc:" + PROC6_TEST + ">" +
          "</env:Body>" +
        "</env:Envelope>";

    @Test
    public void p6Test() throws SOAPException, IOException, SAXException,
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
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<SOAP-ENV:Header/>" +
          "<SOAP-ENV:Body>" +
            "<srvc:" + PROC6_TEST + "Response xmlns:srvc=\"" + PROC6_SERVICE_NAMESPACE + "\">" +
              "<srvc:result>1</srvc:result>" +
            "</srvc:" + PROC6_TEST + "Response>" +
          "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";
}