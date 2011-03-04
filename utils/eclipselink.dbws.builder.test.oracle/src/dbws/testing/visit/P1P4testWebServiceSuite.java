package dbws.testing.visit;

import static dbws.testing.visit.DBWSTestHelper.PACKAGE_NAME;
import static dbws.testing.visit.DBWSTestHelper.PROC1;
import static dbws.testing.visit.DBWSTestHelper.PROC1_TEST;
import static dbws.testing.visit.DBWSTestHelper.PROC4;
import static dbws.testing.visit.DBWSTestHelper.PROC4_NAMESPACE;
import static dbws.testing.visit.DBWSTestHelper.PROC4_PORT;
import static dbws.testing.visit.DBWSTestHelper.PROC4_SERVICE;
import static dbws.testing.visit.DBWSTestHelper.PROC4_SERVICE_NAMESPACE;
import static dbws.testing.visit.DBWSTestHelper.PROC4_TEST;
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

import org.eclipse.persistence.tools.dbws.PLSQLProcedureOperationModel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@WebServiceProvider(
    targetNamespace = PROC4_SERVICE_NAMESPACE,
    serviceName = PROC4_SERVICE,
    portName = PROC4_PORT
)
@ServiceMode(MESSAGE)
public class P1P4testWebServiceSuite extends WebServiceTestSuite implements Provider<SOAPMessage> {
    static final String ENDPOINT_ADDRESS = "http://localhost:9999/" + PROC4_TEST;

    @BeforeClass
    public static void setUp() throws WSDLException {
        builder.setProjectName(PROC4_TEST);
        builder.setTargetNamespace(PROC4_NAMESPACE);
        PLSQLProcedureOperationModel p1Model = new PLSQLProcedureOperationModel();
        p1Model.setName(PROC1_TEST);
        p1Model.setReturnType("xsd:int");
        p1Model.setCatalogPattern(PACKAGE_NAME);
        p1Model.setProcedurePattern(PROC1);
        PLSQLProcedureOperationModel p4Model = new PLSQLProcedureOperationModel();
        p4Model.setName(PROC4_TEST);
        p4Model.setReturnType("xsd:int");
        p4Model.setCatalogPattern(PACKAGE_NAME);
        p4Model.setProcedurePattern(PROC4);
        builder.getOperations().add(p1Model);
        builder.getOperations().add(p4Model);
        serviceSetup(ENDPOINT_ADDRESS, new P1P4testWebServiceSuite());
    }

    @PostConstruct
    public void init() {
        super.init();
    }
    
    static final String REQUEST_MSG_1 =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Header/>" +
          "<env:Body>" +
            "<srvc:" + PROC4_TEST + " xmlns:srvc=\"" + PROC4_SERVICE_NAMESPACE + "\" xmlns:ns1=\"" + PROC4_NAMESPACE + "\">" +
              "<srvc:REC>" +
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
              "</srvc:REC>" +
            "</srvc:" + PROC4_TEST + ">" +
          "</env:Body>" +
        "</env:Envelope>";

    static final String REQUEST_MSG_2 =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Header/>" +
          "<env:Body>" +
            "<srvc:" + PROC1_TEST + " xmlns:srvc=\"" + PROC4_SERVICE_NAMESPACE + "\" xmlns:ns1=\"" + PROC4_NAMESPACE + "\">" +
              "<srvc:SIMPLARRAY>" +
                "<ns1:item>" + "this" + "</ns1:item>" +
                "<ns1:item>" + "is" + "</ns1:item>" +
                "<ns1:item>" + "a" + "</ns1:item>" +
              "</srvc:SIMPLARRAY>" +
              "<srvc:FOO>" + "test" + "</srvc:FOO>" +
            "</srvc:" + PROC1_TEST + ">" +
          "</env:Body>" +
        "</env:Envelope>";

    @Test
    public void p1p4TestP1() throws SOAPException, IOException, SAXException,
        ParserConfigurationException, TransformerException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage request = factory.createMessage();
        SOAPPart part = request.getSOAPPart();
        DOMSource domSource = new DOMSource(
            getDocumentBuilder().parse(new InputSource(new StringReader(REQUEST_MSG_2))));
        part.setContent(domSource);
        Dispatch<SOAPMessage> dispatch = testService.createDispatch(portQName, SOAPMessage.class,
            Service.Mode.MESSAGE);
        SOAPMessage response = null;
        try {
            response = dispatch.invoke(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response != null) {
            Source src = response.getSOAPPart().getContent();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMResult result = new DOMResult();
            transformer.transform(src, result);
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

    @Test
    public void p1p4TestP4() throws SOAPException, IOException, SAXException,
        ParserConfigurationException, TransformerException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage request = factory.createMessage();
        SOAPPart part = request.getSOAPPart();
        DOMSource domSource = new DOMSource(
            getDocumentBuilder().parse(new InputSource(new StringReader(REQUEST_MSG_1))));
        part.setContent(domSource);
        Dispatch<SOAPMessage> dispatch = testService.createDispatch(portQName, SOAPMessage.class,
            Service.Mode.MESSAGE);
        SOAPMessage response = null;
        try {
            response = dispatch.invoke(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response != null) {
            Source src = response.getSOAPPart().getContent();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMResult result = new DOMResult();
            transformer.transform(src, result);
            Document resultDoc = (Document)result.getNode();
            Document controlDoc = xmlParser.parse(new StringReader(TEST_RESPONSE_1));
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

    static final String TEST_RESPONSE_1 =
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<SOAP-ENV:Header/>" +
          "<SOAP-ENV:Body>" +
            "<srvc:" + PROC4_TEST + "Response xmlns:srvc=\"" + PROC4_SERVICE_NAMESPACE + "\">" +
              "<srvc:result>1</srvc:result>" +
            "</srvc:" + PROC4_TEST + "Response>" +
          "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";

    static final String TEST_RESPONSE_2 =
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<SOAP-ENV:Header/>" +
          "<SOAP-ENV:Body>" +
            "<srvc:" + PROC1_TEST + "Response xmlns:srvc=\"" + PROC4_SERVICE_NAMESPACE + "\">" +
              "<srvc:result>1</srvc:result>" +
            "</srvc:" + PROC1_TEST + "Response>" +
          "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";
}