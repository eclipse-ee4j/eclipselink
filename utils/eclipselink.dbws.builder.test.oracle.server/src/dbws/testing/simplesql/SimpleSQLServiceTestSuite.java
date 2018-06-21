/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     David McCann - Aug.15, 2012 - 2.4.1 - Initial implementation
package dbws.testing.simplesql;

import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Statement;

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
import static dbws.testing.simplesql.SimpleSQLBuilderTestSuite.CREATE_TABLE;
import static dbws.testing.simplesql.SimpleSQLBuilderTestSuite.DROP_TABLE;
import static dbws.testing.simplesql.SimpleSQLBuilderTestSuite.POPULATE_TABLE;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests building a web service using SQL.
 *
 */
public class SimpleSQLServiceTestSuite extends DBWSTestSuite {

    static final String SOAP_COUNTINFO_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<count-info xmlns=\"urn:simplesqlService\"/>" +
          "</env:Body>" +
        "</env:Envelope>";

    static final String SOAP_COUNTINFO_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<srvc:count-infoResponse xmlns:srvc=\"urn:simplesqlService\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
          "<srvc:result>" +
            "<simple-sql xsi:type=\"simple-xml-format\">" +
              "<count-info>" +
                "<COUNT>3</COUNT>" +
              "</count-info>" +
            "</simple-sql>" +
          "</srvc:result>" +
        "</srvc:count-infoResponse>";

    // handle case where the xsi namespace decl is on the result element...
    static final String SOAP_COUNTINFO_RESPONSE_2 =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<srvc:count-infoResponse xmlns:srvc=\"urn:simplesqlService\">" +
          "<srvc:result>" +
            "<simple-sql xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"simple-xml-format\">" +
              "<count-info>" +
                "<COUNT>3</COUNT>" +
              "</count-info>" +
            "</simple-sql>" +
          "</srvc:result>" +
        "</srvc:count-infoResponse>";

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
            runDdl(conn, CREATE_TABLE, ddlDebug);
            try {
                Statement stmt = conn.createStatement();
                for (int i = 0; i < POPULATE_TABLE.length; i++) {
                    stmt.addBatch(POPULATE_TABLE[i]);
                }
                stmt.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterClass
    public static void tearDown() {
        if (ddlDrop) {
            runDdl(conn, DROP_TABLE, ddlDebug);
        }
    }

    @Test
    public void testService() {
        try {
            QName qname = new QName("urn:simplesqlService", "simplesqlServicePort");
            Service service = Service.create(new QName("urn:simplesql", "simplesqlService"));
            service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING, "http://" + host + ":" + port + "/simplesql/simplesql");
            Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class, Service.Mode.MESSAGE);
            SOAPMessage request = createSOAPMessage(SOAP_COUNTINFO_REQUEST);
            SOAPMessage response = sourceDispatch.invoke(request);
            assertNotNull("\nTest failed:  response is null.", response);

            SOAPBody responseBody = response.getSOAPPart().getEnvelope().getBody();
            Document resultDoc = responseBody.extractContentAsDocument();

            NodeList elts = resultDoc.getDocumentElement().getElementsByTagNameNS("urn:simplesqlService", "result");
            assertTrue("The wrong number of elements were returned.", ((elts != null && elts.getLength() > 0) && elts.getLength() == 1));
            Node testNode = elts.item(0);
            assertTrue("Didn't find [<srvc:result>] element", testNode.getLocalName().equalsIgnoreCase("result"));
            Document controlDoc = xmlParser.parse(new StringReader(SOAP_COUNTINFO_RESPONSE));
            elts = controlDoc.getDocumentElement().getElementsByTagNameNS("urn:simplesqlService", "result");
            Node ctrlNode = elts.item(0);

            Node ctrlNode2 = null;
            boolean result = comparer.isNodeEqual(ctrlNode, testNode);
            if (!result) {
                // second attempt - depending on the env we sometimes see the xsi namespace decl on the result element
                controlDoc = xmlParser.parse(new StringReader(SOAP_COUNTINFO_RESPONSE_2));
                elts = controlDoc.getDocumentElement().getElementsByTagNameNS("urn:simplesqlService", "result");
                ctrlNode2 = elts.item(0);
                result = comparer.isNodeEqual(ctrlNode2, testNode);
            }
            assertTrue("\nDocument comparison failed.  Expected:\n" + documentToString(ctrlNode) + " OR:\n " + documentToString(ctrlNode2) + "\nbut was:\n" + documentToString(testNode), result);
        } catch (Exception x) {
            x.printStackTrace();
            fail("Service test failed: " + x.getMessage());
        }
    }
}
