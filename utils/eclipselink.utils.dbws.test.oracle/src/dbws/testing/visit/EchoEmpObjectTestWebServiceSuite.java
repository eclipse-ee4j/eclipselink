/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import javax.xml.transform.TransformerException;
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
import org.eclipse.persistence.tools.dbws.ProcedureOperationModel;

// test imports
import static dbws.testing.visit.DBWSTestHelper.ADVJDBC_ECHO_EMPOBJECT;
import static dbws.testing.visit.DBWSTestHelper.ADVJDBC_ECHO_EMPOBJECT_NAMESPACE;
import static dbws.testing.visit.DBWSTestHelper.ADVJDBC_ECHO_EMPOBJECT_SERVICE;
import static dbws.testing.visit.DBWSTestHelper.ADVJDBC_ECHO_EMPOBJECT_PORT;
import static dbws.testing.visit.DBWSTestHelper.ADVJDBC_ECHO_EMPOBJECT_SERVICE_NAMESPACE;
import static dbws.testing.visit.DBWSTestHelper.ADVJDBC_ECHO_EMPOBJECT_TEST;
import static dbws.testing.visit.DBWSTestHelper.ADVJDBC_PACKAGE_NAME;

@WebServiceProvider(
    targetNamespace = ADVJDBC_ECHO_EMPOBJECT_SERVICE_NAMESPACE,
    serviceName = ADVJDBC_ECHO_EMPOBJECT_SERVICE,
    portName = ADVJDBC_ECHO_EMPOBJECT_PORT
)
@ServiceMode(MESSAGE)
public class EchoEmpObjectTestWebServiceSuite extends WebServiceTestSuite implements Provider<SOAPMessage> {

    static final String ENDPOINT_ADDRESS = "http://localhost:9999/" + ADVJDBC_ECHO_EMPOBJECT_TEST;

    @BeforeClass
    public static void setUp() throws WSDLException {
        builder.setProjectName(ADVJDBC_ECHO_EMPOBJECT_TEST);
        builder.setTargetNamespace(ADVJDBC_ECHO_EMPOBJECT_NAMESPACE);
        ProcedureOperationModel pModel = new ProcedureOperationModel();
        pModel.setName(ADVJDBC_ECHO_EMPOBJECT_TEST);
        pModel.setIsAdvancedJDBCProcedureOperation(true);
        pModel.setCatalogPattern(ADVJDBC_PACKAGE_NAME);
        pModel.setProcedurePattern(ADVJDBC_ECHO_EMPOBJECT);
        pModel.setReturnType("emp_objectType");
        builder.getOperations().add(pModel);
        serviceSetup(ENDPOINT_ADDRESS, new EchoEmpObjectTestWebServiceSuite());
    }

    @PostConstruct
    public void init() {
        super.init();
    }

    static final String REQUEST_MSG = 
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Header/>" +
          "<env:Body>" +
            "<srvc:" + ADVJDBC_ECHO_EMPOBJECT_TEST + " xmlns:srvc=\"" + 
                ADVJDBC_ECHO_EMPOBJECT_SERVICE_NAMESPACE + 
                "\" xmlns=\"" + ADVJDBC_ECHO_EMPOBJECT_NAMESPACE + "\">" +
              "<srvc:ANEMPOBJECT>" + 
                  "<employee_id>55</employee_id>" +
                  "<address>" +
                     "<street>20 Pinetrail Cres.</street>" +
                     "<suburb>Centrepointe</suburb>" +
                     "<addr_region>" +
                        "<reg_id>5</reg_id>" +
                        "<reg_name>this is a test</reg_name>" +
                     "</addr_region>" +
                     "<postcode>12</postcode>" +
                  "</address>" +
                  "<employee_name>Mike Norman</employee_name>" +
                  "<date_of_hire>2009-05-01</date_of_hire>" +
              "</srvc:ANEMPOBJECT>" +
            "</srvc:" + ADVJDBC_ECHO_EMPOBJECT_TEST + ">" +
          "</env:Body>" +
        "</env:Envelope>";
    @Test
    public void echoEmpObjectTest() throws SOAPException, IOException, SAXException,
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
            assertTrue("control document not same as instance document",
                comparer.isNodeEqual(controlDoc, resultDoc));
        }
    }

    static final String TEST_RESPONSE = 
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<SOAP-ENV:Header/>" +
          "<SOAP-ENV:Body>" +          
             "<srvc:" + ADVJDBC_ECHO_EMPOBJECT_TEST + "Response xmlns=\"" + ADVJDBC_ECHO_EMPOBJECT_NAMESPACE +"\" xmlns:srvc=\"" + ADVJDBC_ECHO_EMPOBJECT_SERVICE_NAMESPACE + "\">" +
                 "<srvc:result>" +
                     "<emp_objectType xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                         "<employee_id>55</employee_id>" +
                         "<address>" +
                            "<street>20 Pinetrail Cres.</street>" +
                            "<suburb>Centrepointe</suburb>" +
                            "<addr_region>" +
                                "<reg_id>5</reg_id>" +
                                "<reg_name>this is a test</reg_name>" +
                            "</addr_region>" +
                            "<postcode>12</postcode>" +
                         "</address>" +
                         "<employee_name>Mike Norman</employee_name>" +
                         "<date_of_hire>2009-05-01</date_of_hire>" +
                     "</emp_objectType>" +
                 "</srvc:result>" +
             "</srvc:" + ADVJDBC_ECHO_EMPOBJECT_TEST + "Response>" +
          "</SOAP-ENV:Body>" +
        "</SOAP-ENV:Envelope>";
}