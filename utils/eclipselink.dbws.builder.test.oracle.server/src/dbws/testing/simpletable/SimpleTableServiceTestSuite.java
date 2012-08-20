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
 *     David McCann - Aug.15, 2012 - 2.4.1 - Initial implementation
 ******************************************************************************/
package dbws.testing.simpletable;

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

import dbws.testing.DBWSTestSuite;
import static dbws.testing.simpletable.SimpleTableBuilderTestSuite.CREATE_TABLE;
import static dbws.testing.simpletable.SimpleTableBuilderTestSuite.DROP_TABLE;
import static dbws.testing.simpletable.SimpleTableBuilderTestSuite.POPULATE_TABLE;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests building a web service from table info.
 *
 */
public class SimpleTableServiceTestSuite extends DBWSTestSuite {
    static final String SOAP_FINDBYPK_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findByPrimaryKey_simpletableType xmlns=\"urn:simpletableService\">" +
              "<id>1</id>" +
            "</findByPrimaryKey_simpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_FINDALL_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findAll_simpletableType xmlns=\"urn:simpletableService\" xmlns:urn=\"urn:simpletable\"/>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_UPDATE_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<update_simpletableType xmlns=\"urn:simpletableService\" xmlns:urn=\"urn:simpletable\">" +
              "<theInstance>" +
                "<urn:simpletableType>" +
                  "<urn:id>1</urn:id>" +
                  "<urn:name>mike norman</urn:name>" +
                  "<urn:since>2001-12-25</urn:since>" +
                "</urn:simpletableType>" +
              "</theInstance>" +
            "</update_simpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_CREATE_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<create_simpletableType xmlns=\"urn:simpletableService\" xmlns:urn=\"urn:simpletable\">" +
              "<theInstance>" +
                "<urn:simpletableType>" +
                  "<urn:id>4</urn:id>" +
                  "<urn:name>test</urn:name>" +
                  "<urn:since>2009-03-27</urn:since>" +
                "</urn:simpletableType>" +
              "</theInstance>" +
            "</create_simpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_DELETE_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<delete_simpletableType xmlns=\"urn:simpletableService\" xmlns:urn=\"urn:simpletable\">" +
              "<id>4</id>" +
            "</delete_simpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_UPDATE2_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<update_simpletableType xmlns=\"urn:simpletableService\" xmlns:urn=\"urn:simpletable\">" +
              "<theInstance>" +
                "<urn:simpletableType>" +
                  "<urn:id>1</urn:id>" +
                  "<urn:name>mike</urn:name>" +
                  "<urn:since>2001-12-25</urn:since>" +
                "</urn:simpletableType>" +
              "</theInstance>" +
            "</update_simpletableType>" +
          "</env:Body>" +
        "</env:Envelope>";

	static final String SOAP_FINDBYPK_RESPONSE = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<srvc:findByPrimaryKey_simpletableTypeResponse xmlns=\"urn:simpletable\" xmlns:srvc=\"urn:simpletableService\">" +
            "<srvc:result>" +
                "<simpletableType>" +
                    "<id>1</id>" +
                    "<name>mike</name>" +
                    "<since>2001-12-25</since>" +
                "</simpletableType>" +
            "</srvc:result>" +
        "</srvc:findByPrimaryKey_simpletableTypeResponse>";
    static final String SOAP_FINDALL_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<srvc:findAll_simpletableTypeResponse xmlns=\"urn:simpletable\" xmlns:srvc=\"urn:simpletableService\">" +
            "<srvc:result>" +
                "<simpletableType>" +
                    "<id>1</id>" +
                    "<name>mike</name>" +
                    "<since>2001-12-25</since>" +
                 "</simpletableType>" +
                 "<simpletableType>" +
                    "<id>2</id>" +
                    "<name>blaise</name>" +
                    "<since>2001-12-25</since>" +
                 "</simpletableType>" +
                 "<simpletableType>" +
                    "<id>3</id>" +
                    "<name>rick</name>" +
                    "<since>2001-12-25</since>" +
                 "</simpletableType>" +
             "</srvc:result>" +
        "</srvc:findAll_simpletableTypeResponse>";
    static final String SOAP_UPDATE_RESPONSE_ELEMENTNAME =
        "update_simpletableTypeResponse";
    static final String SOAP_FINDBYPK_AFTERUPDATE_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<srvc:findByPrimaryKey_simpletableTypeResponse xmlns=\"urn:simpletable\" xmlns:srvc=\"urn:simpletableService\">" +
            "<srvc:result>" +
                "<simpletableType>" +
                    "<id>1</id>" +
                    "<name>mike norman</name>" +
                    "<since>2001-12-25</since>" +
                "</simpletableType>" +
            "</srvc:result>" +
        "</srvc:findByPrimaryKey_simpletableTypeResponse>";
    static final String SOAP_CREATE_RESPONSE_ELEMENTNAME =
        "create_simpletableTypeResponse";
    static final String SOAP_DELETE_RESPONSE_ELEMENTNAME =
        "delete_simpletableTypeResponse";
    
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
	        QName qname = new QName("urn:simpletableService", "simpletableServicePort");
	        Service service = Service.create(new QName("urn:simpletable", "simpletableService"));
	        service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING, "http://" + host + ":" + port + "/simpletable/simpletable");
	        Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class, Service.Mode.MESSAGE);
	        
	        SOAPMessage request = createSOAPMessage(SOAP_FINDBYPK_REQUEST);
	        SOAPMessage response = sourceDispatch.invoke(request);
	        assertNotNull("findByPrimaryKey_simpletableType failed:  response is null.", response);
            SOAPBody responseBody = response.getSOAPPart().getEnvelope().getBody();
            Document resultDoc = responseBody.extractContentAsDocument();
            Document controlDoc = xmlParser.parse(new StringReader(SOAP_FINDBYPK_RESPONSE));
            assertTrue("findByPrimaryKey_simpletableType document comparison failed.  Expected:\n" + documentToString(controlDoc) + "\nbut was:\n" + documentToString(resultDoc), comparer.isNodeEqual(controlDoc, resultDoc));

	        request = createSOAPMessage(SOAP_FINDALL_REQUEST);
	        response = sourceDispatch.invoke(request);
	        assertNotNull("findAll_simpletableType failed:  response is null.", response);
            responseBody = response.getSOAPPart().getEnvelope().getBody();
            resultDoc = responseBody.extractContentAsDocument();
            controlDoc = xmlParser.parse(new StringReader(SOAP_FINDALL_RESPONSE));
            assertTrue("findAll_simpletableType document comparison failed.  Expected:\n" + documentToString(controlDoc) + "\nbut was:\n" + documentToString(resultDoc), comparer.isNodeEqual(controlDoc, resultDoc));

	        request = createSOAPMessage(SOAP_UPDATE_REQUEST);
	        response = sourceDispatch.invoke(request);
	        assertNotNull("update_simpletableType failed:  response is null.", response);
            assertTrue(SOAP_UPDATE_RESPONSE_ELEMENTNAME + " incorrect", SOAP_UPDATE_RESPONSE_ELEMENTNAME.equals(response.getSOAPBody().getFirstChild().getLocalName()));

	        request = createSOAPMessage(SOAP_FINDBYPK_REQUEST);
	        response = sourceDispatch.invoke(request);
	        assertNotNull("findByPrimaryKey_simpletableType failed:  response is null.", response);
	        responseBody = response.getSOAPPart().getEnvelope().getBody();
            resultDoc = responseBody.extractContentAsDocument();
            controlDoc = xmlParser.parse(new StringReader(SOAP_FINDBYPK_AFTERUPDATE_RESPONSE));
            assertTrue("findByPrimaryKey_simpletableType (after update) document comparison failed.  Expected:\n" + documentToString(controlDoc) + "\nbut was:\n" + documentToString(resultDoc), comparer.isNodeEqual(controlDoc, resultDoc));

	        request = createSOAPMessage(SOAP_CREATE_REQUEST);
	        response = sourceDispatch.invoke(request);
	        assertNotNull("create_simpletableType failed:  response is null.", response);
            assertTrue(SOAP_CREATE_RESPONSE_ELEMENTNAME + " incorrect", SOAP_CREATE_RESPONSE_ELEMENTNAME.equals(response.getSOAPBody().getFirstChild().getLocalName()));

	        request = createSOAPMessage(SOAP_DELETE_REQUEST);
	        response = sourceDispatch.invoke(request);
	        assertNotNull("delete_simpletableType failed:  response is null.", response);
	        assertTrue(SOAP_DELETE_RESPONSE_ELEMENTNAME + " incorrect", SOAP_DELETE_RESPONSE_ELEMENTNAME.equals(response.getSOAPBody().getFirstChild().getLocalName()));

	        request = createSOAPMessage(SOAP_UPDATE2_REQUEST);
	        response = sourceDispatch.invoke(request);
	        assertNotNull("update_simpletableType (2) failed:  response is null.", response);
            assertTrue(SOAP_UPDATE_RESPONSE_ELEMENTNAME + " incorrect", SOAP_UPDATE_RESPONSE_ELEMENTNAME.equals(response.getSOAPBody().getFirstChild().getLocalName()));
    	} catch (Exception x) {
    		fail("Service test failed: " + x.getMessage());
    	}
	}
}