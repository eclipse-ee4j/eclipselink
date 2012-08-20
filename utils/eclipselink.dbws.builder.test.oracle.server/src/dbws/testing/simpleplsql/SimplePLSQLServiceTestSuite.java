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
 *     David McCann - Aug.15, 2012 - 2.3.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.simpleplsql;

import java.io.StringReader;

import javax.xml.namespace.QName;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import dbws.testing.DBWSTestSuite;
import static dbws.testing.simpleplsql.SimplePLSQLBuilderTestSuite.CREATE_PACKAGE;
import static dbws.testing.simpleplsql.SimplePLSQLBuilderTestSuite.CREATE_PACKAGE_BODY;
import static dbws.testing.simpleplsql.SimplePLSQLBuilderTestSuite.DROP_PACKAGE;
import static dbws.testing.simpleplsql.SimplePLSQLBuilderTestSuite.DROP_PACKAGE_BODY;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests a simple PL/SQL stored procedure.
 *
 */
public class SimplePLSQLServiceTestSuite extends DBWSTestSuite {
	public static final String TEST = "simpleplsql";
	public static final String TEST_NAMESPACE = "urn:simpleplsql";
	public static final String SERVICE = "simpleplsqlService";
	public static final String SERVICE_NAMESPACE = "urn:simpleplsqlService";
	public static final String SERVICE_PORT = "simpleplsqlServicePort";

    static final String REQUEST_MSG =
      "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
        "<env:Header/>" +
        "<env:Body>" +
          "<simpleplsql xmlns=\"urn:simpleplsqlService\">" +
            "<ARG1>blah</ARG1>" +
          "</simpleplsql>" +
        "</env:Body>" +
      "</env:Envelope>";

    static final String RESPONSE_MSG =
		"<srvc:" + TEST + "Response xmlns:srvc="+ "\"" + SERVICE_NAMESPACE + "\"" + ">" +
          "<srvc:result>" +
            "<simple-xml-format>" +
              "<simple-xml>" +
                "<ARG2>blah</ARG2>" +
              "</simple-xml>" +
            "</simple-xml-format>" +
          "</srvc:result>" +
		"</srvc:simpleplsqlResponse>";

    @BeforeClass
    public static void setUp() {
	    if (conn == null) {
	        try {
	            conn = buildConnection();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    if (ddlCreate) {
        	runDdl(conn, CREATE_PACKAGE, ddlDebug);
        	runDdl(conn, CREATE_PACKAGE_BODY, ddlDebug);
	    }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_PACKAGE_BODY, ddlDebug);
            runDdl(conn, DROP_PACKAGE, ddlDebug);
        }
    }

    @Test
    public void testService() {
    	try {
	        QName qname = new QName(SERVICE_NAMESPACE, SERVICE_PORT);
	        Service service = Service.create(new QName(TEST_NAMESPACE, SERVICE));
	        service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING, "http://" + host + ":" + port + "/simpleplsql/simpleplsql");
	        Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class, Service.Mode.MESSAGE);

	        // Test plsql proc
	        SOAPMessage request = createSOAPMessage(REQUEST_MSG);
	        SOAPMessage response = sourceDispatch.invoke(request);
	        assertNotNull("Test failed:  response is null.", response);

            SOAPBody responseBody = response.getSOAPPart().getEnvelope().getBody();
            Document resultDoc = responseBody.extractContentAsDocument();
            Document controlDoc = xmlParser.parse(new StringReader(RESPONSE_MSG));
            assertTrue("\nDocument comparison failed.  Expected:\n" + documentToString(controlDoc) + "\nbut was:\n" + documentToString(resultDoc), comparer.isNodeEqual(controlDoc, resultDoc));
    	} catch (Exception x) {
			x.printStackTrace();
    		fail("Service test failed: " + x.getMessage());
    	}
	}
}