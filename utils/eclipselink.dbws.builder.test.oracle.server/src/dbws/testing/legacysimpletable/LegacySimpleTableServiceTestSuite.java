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
//     David McCann - Jan.09, 2013 - 2.5.0 - Initial implementation
package dbws.testing.legacysimpletable;

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

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests building a web service from table info.
 *
 * This test is used to verify reading in legacy deployment XML.
 *
 */
public class LegacySimpleTableServiceTestSuite extends DBWSTestSuite {
    public static final String CREATE_TABLE =
        "CREATE TABLE LEGACYSIMPLETABLE (" +
            "id NUMBER NOT NULL," +
            "name VARCHAR2(25)," +
            "since DATE," +
            "PRIMARY KEY (id)" +
        ")";

    public static final String[] POPULATE_TABLE = new String[] {
        "INSERT INTO LEGACYSIMPLETABLE (id, name, since) VALUES (1, 'mike', to_date('2001-12-25','YYYY-MM-DD'))",
        "INSERT INTO LEGACYSIMPLETABLE (id, name, since) VALUES (2, 'blaise',to_date('2001-12-25','YYYY-MM-DD'))",
        "INSERT INTO LEGACYSIMPLETABLE (id, name, since) VALUES (3, 'rick',to_date('2001-12-25','YYYY-MM-DD'))"
    };

    public static final String DROP_TABLE =
        "DROP TABLE LEGACYSIMPLETABLE";

    static final String SOAP_FINDBYPK_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findByPrimaryKey_legacysimpletableType xmlns=\"urn:legacysimpletableService\">" +
              "<id>1</id>" +
            "</findByPrimaryKey_legacysimpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_FINDALL_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findAll_legacysimpletableType xmlns=\"urn:legacysimpletableService\" xmlns:urn=\"urn:legacysimpletable\"/>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_UPDATE_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<update_legacysimpletableType xmlns=\"urn:legacysimpletableService\" xmlns:urn=\"urn:legacysimpletable\">" +
              "<theInstance>" +
                "<urn:legacysimpletableType>" +
                  "<urn:id>1</urn:id>" +
                  "<urn:name>mike norman</urn:name>" +
                  "<urn:since>2001-12-25</urn:since>" +
                "</urn:legacysimpletableType>" +
              "</theInstance>" +
            "</update_legacysimpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_CREATE_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<create_legacysimpletableType xmlns=\"urn:legacysimpletableService\" xmlns:urn=\"urn:legacysimpletable\">" +
              "<theInstance>" +
                "<urn:legacysimpletableType>" +
                  "<urn:id>4</urn:id>" +
                  "<urn:name>test</urn:name>" +
                  "<urn:since>2009-03-27</urn:since>" +
                "</urn:legacysimpletableType>" +
              "</theInstance>" +
            "</create_legacysimpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_DELETE_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<delete_legacysimpletableType xmlns=\"urn:legacysimpletableService\" xmlns:urn=\"urn:legacysimpletable\">" +
              "<id>4</id>" +
            "</delete_legacysimpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_UPDATE2_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<update_legacysimpletableType xmlns=\"urn:legacysimpletableService\" xmlns:urn=\"urn:legacysimpletable\">" +
              "<theInstance>" +
                "<urn:legacysimpletableType>" +
                  "<urn:id>1</urn:id>" +
                  "<urn:name>mike</urn:name>" +
                  "<urn:since>2001-12-25</urn:since>" +
                "</urn:legacysimpletableType>" +
              "</theInstance>" +
            "</update_legacysimpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";

    static final String SOAP_FINDBYPK_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<srvc:findByPrimaryKey_legacysimpletableTypeResponse xmlns=\"urn:legacysimpletable\" xmlns:srvc=\"urn:legacysimpletableService\">" +
            "<srvc:result>" +
                "<legacysimpletableType>" +
                    "<id>1</id>" +
                    "<name>mike</name>" +
                    "<since>2001-12-25</since>" +
                "</legacysimpletableType>" +
            "</srvc:result>" +
        "</srvc:findByPrimaryKey_legacysimpletableTypeResponse>";
    static final String SOAP_FINDALL_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<srvc:findAll_legacysimpletableTypeResponse xmlns=\"urn:legacysimpletable\" xmlns:srvc=\"urn:legacysimpletableService\">" +
            "<srvc:result>" +
                "<legacysimpletableType>" +
                    "<id>1</id>" +
                    "<name>mike</name>" +
                    "<since>2001-12-25</since>" +
                 "</legacysimpletableType>" +
                 "<legacysimpletableType>" +
                    "<id>2</id>" +
                    "<name>blaise</name>" +
                    "<since>2001-12-25</since>" +
                 "</legacysimpletableType>" +
                 "<legacysimpletableType>" +
                    "<id>3</id>" +
                    "<name>rick</name>" +
                    "<since>2001-12-25</since>" +
                 "</legacysimpletableType>" +
             "</srvc:result>" +
        "</srvc:findAll_legacysimpletableTypeResponse>";
    static final String SOAP_UPDATE_RESPONSE_ELEMENTNAME =
        "update_legacysimpletableTypeResponse";
    static final String SOAP_FINDBYPK_AFTERUPDATE_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<srvc:findByPrimaryKey_legacysimpletableTypeResponse xmlns=\"urn:legacysimpletable\" xmlns:srvc=\"urn:legacysimpletableService\">" +
            "<srvc:result>" +
                "<legacysimpletableType>" +
                    "<id>1</id>" +
                    "<name>mike norman</name>" +
                    "<since>2001-12-25</since>" +
                "</legacysimpletableType>" +
            "</srvc:result>" +
        "</srvc:findByPrimaryKey_legacysimpletableTypeResponse>";
    static final String SOAP_CREATE_RESPONSE_ELEMENTNAME =
        "create_legacysimpletableTypeResponse";
    static final String SOAP_DELETE_RESPONSE_ELEMENTNAME =
        "delete_legacysimpletableTypeResponse";

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
            QName qname = new QName("urn:legacysimpletableService", "legacysimpletableServicePort");
            Service service = Service.create(new QName("urn:legacysimpletable", "legacysimpletableService"));
            service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING, "http://" + host + ":" + port + "/legacysimpletable/legacysimpletable");
            Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class, Service.Mode.MESSAGE);

            SOAPMessage request = createSOAPMessage(SOAP_FINDBYPK_REQUEST);
            SOAPMessage response = sourceDispatch.invoke(request);
            assertNotNull("findByPrimaryKey_legacysimpletableType failed:  response is null.", response);
            SOAPBody responseBody = response.getSOAPPart().getEnvelope().getBody();
            Document resultDoc = responseBody.extractContentAsDocument();
            Document controlDoc = xmlParser.parse(new StringReader(SOAP_FINDBYPK_RESPONSE));

            NodeList elts = resultDoc.getDocumentElement().getElementsByTagNameNS("urn:legacysimpletableService", "result");
            assertTrue("The wrong number of elements were returned.", ((elts != null && elts.getLength() > 0) && elts.getLength() == 1));
            Node testNode = elts.item(0);
            assertTrue("Didn't find [<srvc:result>] element", testNode.getLocalName().equalsIgnoreCase("result"));

            elts = controlDoc.getDocumentElement().getElementsByTagNameNS("urn:legacysimpletableService", "result");
            Node ctrlNode = elts.item(0);

            assertTrue("findByPrimaryKey_legacysimpletableType document comparison failed.  Expected:\n" + documentToString(ctrlNode) + "\nbut was:\n" + documentToString(testNode), comparer.isNodeEqual(ctrlNode, testNode));

            request = createSOAPMessage(SOAP_FINDALL_REQUEST);
            response = sourceDispatch.invoke(request);
            assertNotNull("findAll_legacysimpletableType failed:  response is null.", response);
            responseBody = response.getSOAPPart().getEnvelope().getBody();
            resultDoc = responseBody.extractContentAsDocument();
            controlDoc = xmlParser.parse(new StringReader(SOAP_FINDALL_RESPONSE));

            elts = resultDoc.getDocumentElement().getElementsByTagNameNS("urn:legacysimpletableService", "result");
            assertTrue("The wrong number of elements were returned.", ((elts != null && elts.getLength() > 0) && elts.getLength() == 1));
            testNode = elts.item(0);
            assertTrue("Didn't find [<srvc:result>] element", testNode.getLocalName().equalsIgnoreCase("result"));

            elts = controlDoc.getDocumentElement().getElementsByTagNameNS("urn:legacysimpletableService", "result");
            ctrlNode = elts.item(0);

            assertTrue("findAll_legacysimpletableType document comparison failed.  Expected:\n" + documentToString(ctrlNode) + "\nbut was:\n" + documentToString(testNode), comparer.isNodeEqual(ctrlNode, testNode));

            request = createSOAPMessage(SOAP_UPDATE_REQUEST);
            response = sourceDispatch.invoke(request);
            assertNotNull("update_legacysimpletableType failed:  response is null.", response);
            assertTrue(SOAP_UPDATE_RESPONSE_ELEMENTNAME + " incorrect", SOAP_UPDATE_RESPONSE_ELEMENTNAME.equals(response.getSOAPBody().getFirstChild().getLocalName()));

            request = createSOAPMessage(SOAP_FINDBYPK_REQUEST);
            response = sourceDispatch.invoke(request);
            assertNotNull("findByPrimaryKey_legacysimpletableType failed:  response is null.", response);
            responseBody = response.getSOAPPart().getEnvelope().getBody();
            resultDoc = responseBody.extractContentAsDocument();
            controlDoc = xmlParser.parse(new StringReader(SOAP_FINDBYPK_AFTERUPDATE_RESPONSE));
            elts = resultDoc.getDocumentElement().getElementsByTagNameNS("urn:legacysimpletableService", "result");
            assertTrue("The wrong number of elements were returned.", ((elts != null && elts.getLength() > 0) && elts.getLength() == 1));
            testNode = elts.item(0);
            assertTrue("Didn't find [<srvc:result>] element", testNode.getLocalName().equalsIgnoreCase("result"));

            elts = controlDoc.getDocumentElement().getElementsByTagNameNS("urn:legacysimpletableService", "result");
            ctrlNode = elts.item(0);

            assertTrue("findByPrimaryKey_legacysimpletableType (after update) document comparison failed.  Expected:\n" + documentToString(ctrlNode) + "\nbut was:\n" + documentToString(testNode), comparer.isNodeEqual(ctrlNode, testNode));

            request = createSOAPMessage(SOAP_CREATE_REQUEST);
            response = sourceDispatch.invoke(request);
            assertNotNull("create_legacysimpletableType failed:  response is null.", response);
            assertTrue(SOAP_CREATE_RESPONSE_ELEMENTNAME + " incorrect", SOAP_CREATE_RESPONSE_ELEMENTNAME.equals(response.getSOAPBody().getFirstChild().getLocalName()));

            request = createSOAPMessage(SOAP_DELETE_REQUEST);
            response = sourceDispatch.invoke(request);
            assertNotNull("delete_legacysimpletableType failed:  response is null.", response);
            assertTrue(SOAP_DELETE_RESPONSE_ELEMENTNAME + " incorrect", SOAP_DELETE_RESPONSE_ELEMENTNAME.equals(response.getSOAPBody().getFirstChild().getLocalName()));

            request = createSOAPMessage(SOAP_UPDATE2_REQUEST);
            response = sourceDispatch.invoke(request);
            assertNotNull("update_legacysimpletableType (2) failed:  response is null.", response);
            assertTrue(SOAP_UPDATE_RESPONSE_ELEMENTNAME + " incorrect", SOAP_UPDATE_RESPONSE_ELEMENTNAME.equals(response.getSOAPBody().getFirstChild().getLocalName()));
        } catch (Exception x) {
            fail("Service test failed: " + x.getMessage());
        }
    }
}
