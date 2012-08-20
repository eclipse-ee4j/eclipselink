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
 *     David McCann - Aug.02, 2012 - 2.4.1 - Initial implementation
 ******************************************************************************/
package dbws.testing.attachedbinary;

import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dbws.testing.DBWSTestSuite;
import static dbws.testing.attachedbinary.AttachedBinaryBuilderTestSuite.CREATE_FUNCTION;
import static dbws.testing.attachedbinary.AttachedBinaryBuilderTestSuite.CREATE_TABLE;
import static dbws.testing.attachedbinary.AttachedBinaryBuilderTestSuite.POPULATE_TABLE;
import static dbws.testing.attachedbinary.AttachedBinaryBuilderTestSuite.DROP_FUNCTION;
import static dbws.testing.attachedbinary.AttachedBinaryBuilderTestSuite.DROP_TABLE;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests binary attachments.
 * 
 */
public class AttachedBinaryServiceTestSuite extends DBWSTestSuite {
	public static final String FIND_ALL = 
		"<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
		"<env:Header />" +
			"<env:Body>" + 
				"<findAll_attachedbinaryType xmlns=\"urn:attachedbinaryService\" />" +
			"</env:Body>" +
		"</env:Envelope>";
    public static final String GET_BLOB_BY_ID =
		"<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
		"<env:Header />" +
			"<env:Body>" +
				"<getBLOBById xmlns=\"urn:attachedbinaryService\">" +
					"<PK>1</PK>" +
				"</getBLOBById>" +
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
	    	runDdl(conn, CREATE_FUNCTION, ddlDebug);
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
            runDdl(conn, DROP_FUNCTION, ddlDebug);
        }
    }

    @Test
    public void testService() {
    	try {
	        QName qname = new QName("urn:attachedbinaryService", "attachedbinaryServicePort");
	        Service service = Service.create(new QName("urn:attachedbinary", "attachedbinaryService"));
	        service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING, "http://" + host + ":" + port + "/attachedbinary/attachedbinary");
	        Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class, Service.Mode.MESSAGE);
	
	        // TEST FINDALL
	        // we expect 3 attachments
			SOAPMessage request = createSOAPMessage(FIND_ALL);
	        SOAPMessage result = sourceDispatch.invoke(request);
			
	        assertTrue("findAll failed:  wrong number of attachments - expected [3] but was [" + result.countAttachments() + "]", result.countAttachments() == 3);
	
			SOAPElement elt = SOAPFactory.newInstance().createElement("b");
			
			// verify cid:ref1
			elt.addTextNode("cid:ref1");
			AttachmentPart ap = result.getAttachment(elt);
			assertNotNull("getBlobById failed:  no attachment for [cid:ref1]", ap);

			byte[] rawBytes = ap.getRawContentBytes() ;
			assertTrue("getBlobById failed:  wrong number of bytes returned - expected [15] but was [" + rawBytes.length + "]", rawBytes.length == 15);
			for (int i = 0; i < rawBytes.length; i++) {
				assertTrue("getBlobById failed:  wrong byte value returned - expected [1] but was [" + rawBytes[i] + "]", rawBytes[i] == 1);
	        }
	        elt.removeContents();
			
			// verify cid:ref2
			elt.addTextNode("cid:ref2");
			ap = result.getAttachment(elt);
			assertNotNull("getBlobById failed:  no attachment for [cid:ref2]", ap);

			rawBytes = ap.getRawContentBytes() ;
			assertTrue("getBlobById failed:  wrong number of bytes returned - expected [15] but was [" + rawBytes.length + "]", rawBytes.length == 15);
	        for (int i = 0; i < rawBytes.length; i++) {
				assertTrue("getBlobById failed:  wrong byte value returned - expected [2] but was [" + rawBytes[i] + "]", rawBytes[i] == 2);
	        }
	        elt.removeContents();
			
			// verify cid:ref3
			elt.addTextNode("cid:ref3");
			ap = result.getAttachment(elt);
			assertNotNull("getBlobById failed:  no attachment for [cid:ref3]", ap);
			
			rawBytes = ap.getRawContentBytes() ;
			assertTrue("getBlobById failed:  wrong number of bytes returned - expected [15] but was [" + rawBytes.length + "]", rawBytes.length == 15);
	        for (int i = 0; i < rawBytes.length; i++) {
				assertTrue("getBlobById failed:  wrong byte value returned - expected [3] but was [" + rawBytes[i] + "]", rawBytes[i] == 3);
	        }
	        elt.removeContents();
	        
			// TEST STORED FUNCTION GETBLOBBYID
			// we expect one attachment
			request = createSOAPMessage(GET_BLOB_BY_ID);
	        result = sourceDispatch.invoke(request);
	        
	        assertTrue("getBlobById failed:  wrong number of attachments - expected [1] but was [" + result.countAttachments() + "]", result.countAttachments() == 1);
	
			elt.addTextNode("cid:ref1");
			ap = result.getAttachment(elt);
			assertNotNull("getBlobById failed:  no attachment for [cid:ref1]", ap);

			rawBytes = ap.getRawContentBytes() ;
			assertTrue("getBlobById failed:  wrong number of bytes returned - expected [15] but was [" + rawBytes.length + "]", rawBytes.length == 15);
	        for (int i = 0; i < rawBytes.length; i++) {
				assertTrue("getBlobById failed:  wrong byte value returned - expected [1] but was [" + rawBytes[i] + "]", rawBytes[i] == 1);
	        }
    	} catch (Exception x) {
    		fail("Service test failed: " + x.getMessage());
    	}
	}
}