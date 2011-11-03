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
 *     David McCann - 2.3 - Initial implementation
 ******************************************************************************/
package dbws.testing.sqlascollection;

//javase imports
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static javax.xml.ws.Service.Mode.MESSAGE;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.wsdl.WSDLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import dbws.testing.DBWSTestProviderHelper;

@WebServiceProvider(
    targetNamespace = SQLAsCollectionTestSuite.TARGET_SERVICE_NAMESPACE,
    serviceName = SQLAsCollectionTestSuite.SERVICE_NAME,
    portName = SQLAsCollectionTestSuite.SERVICE_PORT
)
@ServiceMode(MESSAGE)
public class SQLAsCollectionTestSuite extends DBWSTestProviderHelper implements Provider<SOAPMessage> {

	static final String TARGET = "sqlAsCollection";
	static final String TARGET_NAMESPACE = "urn:" + TARGET;
	static final String TARGET_SERVICE_NAMESPACE = TARGET_NAMESPACE + "Service";
	static final String SERVICE_NAME = TARGET + "Service";
	static final String SERVICE_PORT = SERVICE_NAME + "Port";
	static final String ENDPOINT_ADDRESS = "http://localhost:9999/" + TARGET + "Test";

    // Default constructor required by servlet/jax-ws spec
    public SQLAsCollectionTestSuite() {
        super();
    }

    @PostConstruct
    public void init() {
        super.init();
    }
    
    @BeforeClass
    public static void setUp() throws WSDLException {
        String username = System.getProperty(DBWSTestProviderHelper.DATABASE_USERNAME_KEY, DEFAULT_DATABASE_USERNAME);
        String password = System.getProperty(DBWSTestProviderHelper.DATABASE_PASSWORD_KEY, DEFAULT_DATABASE_PASSWORD);
        String url = System.getProperty(DBWSTestProviderHelper.DATABASE_URL_KEY, DEFAULT_DATABASE_URL);
        String driver = System.getProperty(DBWSTestProviderHelper.DATABASE_DRIVER_KEY, DEFAULT_DATABASE_DRIVER);
        String platform = System.getProperty(DBWSTestProviderHelper.DATABASE_PLATFORM_KEY, DEFAULT_DATABASE_PLATFORM);

        String builderString = DBWS_BUILDER_XML_USERNAME + username + DBWS_BUILDER_XML_PASSWORD +
        password + DBWS_BUILDER_XML_URL + url + DBWS_BUILDER_XML_DRIVER + driver +
        DBWS_BUILDER_XML_PLATFORM + platform + DBWS_BUILDER_XML_MAIN;
        
        serviceSetup(builderString, ENDPOINT_ADDRESS, new SQLAsCollectionTestSuite());
    }
    
    static final String GETDATA_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<srvc:"+SERVICE_NAME+" xmlns:srvc=\"" + TARGET_SERVICE_NAMESPACE + "\" xmlns=\"" + TARGET_NAMESPACE + "\"/>" +
          "</env:Body>" +
        "</env:Envelope>";
    
    @Test
    public void getData() throws SOAPException, ParserConfigurationException, SAXException, IOException, TransformerException {
        MessageFactory factory = MessageFactory.newInstance();
        SOAPMessage request = factory.createMessage();
        SOAPPart part = request.getSOAPPart();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        DOMSource domSource = new DOMSource(db.parse(new InputSource(new StringReader(GETDATA_REQUEST))));
        part.setContent(domSource);
        Dispatch<SOAPMessage> dispatch = testService.createDispatch(portQName, SOAPMessage.class, Service.Mode.MESSAGE);
        BindingProvider bp = (BindingProvider)dispatch;
        Map<String, Object> rc = bp.getRequestContext();
        rc.put(ENDPOINT_ADDRESS_PROPERTY, ENDPOINT_ADDRESS);
        SOAPMessage response = null;
        try {
            response = dispatch.invoke(request);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred: " + e.getMessage());
        }
        if (response != null) {
            Source src = response.getSOAPPart().getContent();
            DOMResult result = new DOMResult();
            getTransformer().transform(src, result);
            Document resultDoc = (Document)result.getNode();
            String resultString = documentToString(resultDoc);
            //System.out.println(resultString);
            Document controlDoc = xmlParser.parse(new StringReader(GETDATA_RESPONSE));
            String controlString = documentToString(controlDoc);
            //System.out.println(controlString);
            assertTrue("Control document not same as instance document.\n Expected:\n" + controlString + "\nActual:\n" + resultString, controlString.equals(resultString));
        }
    }

    static final String DBWS_BUILDER_XML_USERNAME =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
          "<properties>" +
              "<property name=\"projectName\">" + TARGET + "</property>" +
              "<property name=\"logLevel\">off</property>" +
              "<property name=\"username\">";
    static final String DBWS_BUILDER_XML_PASSWORD =
              "</property><property name=\"password\">";
    static final String DBWS_BUILDER_XML_URL =
              "</property><property name=\"url\">";
    static final String DBWS_BUILDER_XML_DRIVER =
              "</property><property name=\"driver\">";
    static final String DBWS_BUILDER_XML_PLATFORM =
              "</property><property name=\"platformClassname\">";
    static final String DBWS_BUILDER_XML_MAIN =
              "</property>" +
          "</properties>" +
          "<sql " +
              "name=\""+SERVICE_NAME+"\" " +
              "returnType=\"sRecord\" " +
              "isCollection=\"true\">" +
              "<statement><![CDATA[select * from sqlascollection]]></statement>" +
              "<build-statement><![CDATA[select * from sqlascollection where 0=1]]></build-statement>" +
          "</sql>" +
        "</dbws-builder>";
    static final String GETDATA_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
        "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "<SOAP-ENV:Header/>\n" +
            "<SOAP-ENV:Body>\n" +
                "<srvc:"+SERVICE_NAME+"Response xmlns=\"" + TARGET_NAMESPACE + "\" xmlns:srvc=\"" + TARGET_SERVICE_NAMESPACE + "\">\n" +
                    "<srvc:result>\n" +
                        "<sRecord>\n" +
                            "<id>1</id>\n" +
                            "<name>mike</name>\n" +
                            "<since>2001-12-25</since>\n" +
                        "</sRecord>\n" +
                        "<sRecord>\n" +
                            "<id>2</id>\n" +
                            "<name xsi:nil=\"true\"/>\n" +
                            "<since>2001-12-25</since>\n" +
                        "</sRecord>\n" +
                        "<sRecord>\n" +
                            "<id>3</id>\n" +
                            "<name>rick</name>\n" +
                            "<since xsi:nil=\"true\"/>\n" +
                        "</sRecord>\n" +
                    "</srvc:result>\n" +
                "</srvc:"+SERVICE_NAME+"Response>\n" +
            "</SOAP-ENV:Body>\n" +
        "</SOAP-ENV:Envelope>";
}