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
package dbws.testing.mtom;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

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
import static dbws.testing.mtom.MTOMBuilderTestSuite.CREATE_TABLE;
import static dbws.testing.mtom.MTOMBuilderTestSuite.DROP_TABLE;
import static dbws.testing.mtom.MTOMBuilderTestSuite.POPULATE_TABLE;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Tests MTOM.
 *
 */
public class MTOMServiceTestSuite extends DBWSTestSuite {
    static final String SOAP_FINDBYPK_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findByPrimaryKey_mtomType xmlns=\"urn:mtomService\" xmlns:urn=\"urn:mtom\">" +
              "<id>3</id>" +
            "</findByPrimaryKey_mtomType>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_FINDALL_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findAll_mtomType xmlns=\"urn:mtomService\" xmlns:urn=\"urn:mtom\"/>" +
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
	        QName qname = new QName("urn:mtomService", "mtomServicePort");
	        Service service = Service.create(new QName("urn:mtom", "mtomService"));
	        service.addPort(qname, SOAPBinding.SOAP11HTTP_MTOM_BINDING, "http://" + host + ":" + port + "/mtom/mtom");
	        Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class, Service.Mode.MESSAGE);

	        // TEST FINDALL
	        // we expect 3 attachments
	        SOAPMessage request = createSOAPMessage(SOAP_FINDALL_REQUEST);
	    	SOAPMessage response = sourceDispatch.invoke(request);
	        assertNotNull("findAll failed:  response is null.", response);
	        assertTrue("findAll failed:  wrong number of attachments - expected [3] but was [" + response.countAttachments() + "]", response.countAttachments() == 3);

	        // verify MTOM format, i.e. 
			// <xop:Include xmlns:xop="http://www.w3.org/2004/08/xop/include" href="cid:c060bfb1-82cb-4820-9d87-4f2422b50915"/>
	        for (@SuppressWarnings("unchecked")
			Iterator<AttachmentPart> attachmentsIterator = (Iterator<AttachmentPart>)response.getAttachments(); attachmentsIterator.hasNext();) {
	            AttachmentPart ap = attachmentsIterator.next();

	            SOAPElement elt = SOAPFactory.newInstance().createElement(new QName("http://www.w3.org/2004/08/xop/include", "Include", "xop"));
	    		// content id will be wrapped in angled brackets - need to remove them
	    		String contentId = ap.getContentId();
	    		contentId = contentId.replaceFirst("<", "");
	    		contentId = contentId.replaceFirst(">", "");
	    		elt.addAttribute(new QName("href"), "cid:" + contentId);
	    		
	    		ap = response.getAttachment(elt);
	    		assertNotNull("findAll failed:  no attachment for [cid:" + contentId + "]", ap);
	    		
	    		byte[] rawBytes = ap.getRawContentBytes() ;
	    		assertTrue("findAll failed:  wrong number of bytes returned - expected [15] but was [" + rawBytes.length + "]", rawBytes.length == 15);

	    		// no order is guaranteed, so need to check which attachment we are dealing with
	            byte b = rawBytes[0];
	            assertTrue("findAll failed:  wrong byte value returned - expected [1], [2] or [3] but was [" + b + "]", (b==1 || b==2 || b==3));
                compareBytes(b, rawBytes, "findAll");
	            elt.removeContents();
	        }
	        // TEST FINDBYPK
	        // we expect 1 attachment
	        request = createSOAPMessage(SOAP_FINDBYPK_REQUEST);
	    	response = sourceDispatch.invoke(request);
	    	assertTrue("findByPk failed:  wrong number of attachments - expected [1] but was [" + response.countAttachments() + "]", response.countAttachments() == 1);
	    	
			@SuppressWarnings("rawtypes")
			AttachmentPart ap = (AttachmentPart)((Iterator)response.getAttachments()).next();
	        SOAPElement elt = SOAPFactory.newInstance().createElement(new QName("http://www.w3.org/2004/08/xop/include", "Include", "xop"));
			// content id will be wrapped in angled brackets - need to remove them
			String contentId = ap.getContentId();
			contentId = contentId.replaceFirst("<", "");
			contentId = contentId.replaceFirst(">", "");
			elt.addAttribute(new QName("href"), "cid:" + contentId);
			
			ap = response.getAttachment(elt);
			assertNotNull("findByPk failed:  no attachment for [cid:" + contentId + "]", ap);
			
			byte[] rawBytes = ap.getRawContentBytes() ;
			assertTrue("findByPk failed:  wrong number of bytes returned - expected [15] but was [" + rawBytes.length + "]", rawBytes.length == 15);
	    	compareBytes(3, rawBytes, "findByPk");
    	} catch (Exception x) {
    		fail("Service test failed: " + x.getMessage());
    	}
	}
    
    static void compareBytes(int intVal, byte[] bytes, String testName) {
        for (int i = 0; i < bytes.length; i++) {
        	assertEquals(testName + " failed:  wrong byte value returned - expected [" + intVal + "] but was [" + bytes[i] + "]", bytes[i], intVal);
        }
    }
}