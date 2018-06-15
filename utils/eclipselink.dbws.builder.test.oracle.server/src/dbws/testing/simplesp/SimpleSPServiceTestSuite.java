/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     David McCann - Aug.15, 2012 - 2.4.1 - Initial implementation
package dbws.testing.simplesp;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dbws.testing.DBWSTestSuite;
import static dbws.testing.simplesp.SimpleSPBuilderTestSuite.CREATE_PROCEDURE;
import static dbws.testing.simplesp.SimpleSPBuilderTestSuite.DROP_PROCEDURE;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests a simple stored procedure.
 *
 */
public class SimpleSPServiceTestSuite extends DBWSTestSuite {

    static final String SOAP_SIMPLESP_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Header/>" +
          "<env:Body>" +
            "<testEcho xmlns=\"urn:simplespService\">" +
              "<T>Hello</T>" +
            "</testEcho>" +
          "</env:Body>" +
       "</env:Envelope>";

    static final String SOAP_SIMPLESP_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<srvc:testEchoResponse xmlns:srvc=\"urn:simplespService\">" +
          "<srvc:result>" +
            "<simple-xml-format>" +
              "<simple-xml>" +
                "<result>test-Hello</result>" +
              "</simple-xml>" +
            "</simple-xml-format>" +
          "</srvc:result>" +
        "</srvc:testEchoResponse>";

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
            runDdl(conn, CREATE_PROCEDURE, ddlDebug);
        }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_PROCEDURE, ddlDebug);
        }
    }

    @Test
    public void testService() {
        try {
            QName qname = new QName("urn:simplespService", "simplespServicePort");
            Service service = Service.create(new QName("urn:simplesp", "simplespService"));
            service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING, "http://" + host + ":" + port + "/simplesp/simplesp");
            Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class, Service.Mode.MESSAGE);
            SOAPMessage request = createSOAPMessage(SOAP_SIMPLESP_REQUEST);
            SOAPMessage response = sourceDispatch.invoke(request);
            assertNotNull("\nTest failed:  response is null.", response);

            SOAPBody responseBody = response.getSOAPPart().getEnvelope().getBody();
            Document resultDoc = responseBody.extractContentAsDocument();

            NodeList elts = resultDoc.getDocumentElement().getElementsByTagNameNS("urn:simplespService", "result");
            assertTrue("The wrong number of elements were returned.", ((elts != null && elts.getLength() > 0) && elts.getLength() == 1));
            Node testNode = elts.item(0);
            assertTrue("Didn't find [<srvc:result>] element", testNode.getLocalName().equalsIgnoreCase("result"));

            Document controlDoc = xmlParser.parse(new StringReader(SOAP_SIMPLESP_RESPONSE));
            elts = controlDoc.getDocumentElement().getElementsByTagNameNS("urn:simplespService", "result");
            Node ctrlNode = elts.item(0);

            assertTrue("\nDocument comparison failed.  Expected:\n" + documentToString(ctrlNode) + "\nbut was:\n" + documentToString(testNode), comparer.isNodeEqual(ctrlNode, testNode));
        } catch (Exception x) {
            fail("Service test failed: " + x.getMessage());
        }
    }
}
