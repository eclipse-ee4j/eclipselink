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
 *     David McCann - Aug.14, 2012 - 2.4.1 - Initial implementation
 ******************************************************************************/
package dbws.testing.inlinebinary;

import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.namespace.QName;

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.NodeList;

import dbws.testing.DBWSTestSuite;
import static dbws.testing.inlinebinary.InlineBinaryBuilderTestSuite.CREATE_TABLE;
import static dbws.testing.inlinebinary.InlineBinaryBuilderTestSuite.DROP_TABLE;
import static dbws.testing.inlinebinary.InlineBinaryBuilderTestSuite.POPULATE_TABLE;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 * Tests inline binary
 *
 */
public class InlineBinaryServiceTestSuite extends DBWSTestSuite {
	static final String b0 = "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAQEBAQEBAQEBAQEBAQEB";
	static final String b1 = "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAgICAgICAgICAgICAgIC";
	static final String b2 = "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAAAPAwMDAwMDAwMDAwMDAwMD";
	
    static final String SOAP_FINDBYPK_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findByPrimaryKey_inlinebinaryType xmlns=\"urn:inlinebinaryService\">" +
              "<id>2</id>" +
            "</findByPrimaryKey_inlinebinaryType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_FINDALL_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findAll_inlinebinaryType xmlns=\"urn:inlinebinaryService\" />" +
          "</env:Body>" +
        "</env:Envelope>";
    
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
	        QName qname = new QName("urn:inlinebinaryService", "inlinebinaryServicePort");
	        Service service = Service.create(new QName("urn:inlinebinary", "inlinebinaryService"));
	        service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING, "http://" + host + ":" + port + "/inlinebinary/inlinebinary");
	        Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class, Service.Mode.MESSAGE);

	        // TEST FINDALL
	        // we expect 3 inline binary elements
	        SOAPMessage request = createSOAPMessage(SOAP_FINDALL_REQUEST);
	    	SOAPMessage response = sourceDispatch.invoke(request);

	    	NodeList elements = response.getSOAPBody().getElementsByTagName("b");
	    	assertTrue("findAll failed:  wrong number of inline binary elements returned - expected [3] but was [" + elements.getLength() + "]", elements.getLength() == 3);

	    	String inlineBinary = elements.item(0).getTextContent();
	    	assertTrue("findAll failed:  unexpected inline binary - expected [" + b0 + "] but was [" + inlineBinary + "]", (inlineBinary != null && inlineBinary.equals(b0)));

	    	inlineBinary = elements.item(1).getTextContent();
	    	assertTrue("findAll failed:  unexpected inline binary - expected [" + b1 + "] but was [" + inlineBinary + "]", (inlineBinary != null && inlineBinary.equals(b1)));

	    	inlineBinary = elements.item(2).getTextContent();
	    	assertTrue("findAll failed:  unexpected inline binary - expected [" + b2 + "] but was [" + inlineBinary + "]", (inlineBinary != null && inlineBinary.equals(b2)));

	        // TEST FINDBYPK
	        request = createSOAPMessage(SOAP_FINDBYPK_REQUEST);
	    	response = sourceDispatch.invoke(request);
	    	elements = response.getSOAPBody().getElementsByTagName("b");
	    	assertTrue("findByPk failed:  wrong number of inline binary elements returned - expected [1] but was [" + elements.getLength() + "]", elements.getLength() == 1);

	    	inlineBinary = elements.item(0).getTextContent();
	    	assertTrue("findByPk failed:  unexpected inline binary - expected [" + b1 + "] but was [" + inlineBinary + "]", (inlineBinary != null && inlineBinary.equals(b1)));
    	} catch (Exception x) {
    		fail("Service test failed: " + x.getMessage());
    	}
	}
}