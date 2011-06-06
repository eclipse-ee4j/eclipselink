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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.platform;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformException;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;
import org.eclipse.persistence.platform.xml.XMLTransformer;

import org.eclipse.persistence.testing.oxm.OXTestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class PlatformTransformerTestCases extends OXTestCase {

	private static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/platform/employee.xsl";
	private static String XML_ERROR_RESOURCE = "org/eclipse/persistence/testing/oxm/platform/employee_error.xsl";

	public PlatformTransformerTestCases(String name) {
		super(name);
	}
	
	private Document getDocument(String root, String fn, String ln) throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();				
		Document doc = factory.newDocumentBuilder().newDocument();
		
		Element empElement = doc.createElement(root);
		
		Element fnElement = doc.createElement(fn);
		Text fnText = doc.createTextNode("Jane");
		fnElement.appendChild(fnText);
		Element lnElement = doc.createElement(ln);
		Text lnText = doc.createTextNode("Doe");
		lnElement.appendChild(lnText);
		
		empElement.appendChild(fnElement);
    empElement.appendChild(lnElement);
		
		doc.appendChild(empElement);
		
		return doc;
	}
	
	private URL getStyleSheet(String resource) throws Exception{
		return ClassLoader.getSystemResource(resource);			
	}
	
	public void testTransformWithStyleSheet() throws Exception{
		
		Document orignalDoc = getDocument("employee", "first-name", "last-name");	
		log("ORIGINAL DOCUMENT");
		log(orignalDoc);	
		
		XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
		XMLTransformer transformer = xmlPlatform.newXMLTransformer();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringElementContentWhitespace(true);
		Document outdoc = factory.newDocumentBuilder().newDocument();
		
		transformer.transform(orignalDoc, outdoc, getStyleSheet(XML_RESOURCE));		
		
		log("\nRESULTING DOCUMENT");
		log(outdoc);	
	
		Document outControlDoc = getDocument("e", "fn", "ln");	
		
		assertXMLIdentical(outdoc, outControlDoc);	
		
	}
	
	/*
	public void testTransformWithInvalidStyleSheet() throws Exception{
		
		try{
			Document orignalDoc = getDocument("employee", "first-name", "last-name");	
			log("ORIGINAL DOCUMENT");
			log(orignalDoc);	
			
			XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
			XMLTransformer transformer = xmlPlatform.newXMLTransformer();
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document outdoc = factory.newDocumentBuilder().newDocument();
			
			transformer.transform(orignalDoc, outdoc, getStyleSheet(XML_ERROR_RESOURCE));		
						
		}catch(XMLPlatformException platformException){
			return;
		}catch(Exception exception){
			fail("An unexpected exception occurred.  Should have been caught an XMLParseException");	
			return;
		}
		fail("An exception should have been caught but wasn't");	
	}
	*/
	
		
	public void testTransformToOutputStream() throws Exception{		
	
		Document orignalDoc = getDocument("employee", "first-name", "last-name");	
		log("ORIGINAL DOCUMENT: " );
		log(orignalDoc);	
		
		XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
		XMLTransformer transformer = xmlPlatform.newXMLTransformer();
		transformer.setFormattedOutput(false);
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();		
		transformer.transform(orignalDoc, outstream);		
			
		log("\noutstream: " + outstream.toString());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		InputStream instream = new ByteArrayInputStream(outstream.toByteArray());
		Document streamDoc = factory.newDocumentBuilder().parse(instream);
			
		log("RESULT DOCUMENT: ");	
		log(streamDoc);	

		assertXMLIdentical(orignalDoc, streamDoc);					
	}
	
	private boolean compareByteArrays(byte[] first, byte[] second)
	{
		if(first.length != second.length){
			return false;
		}

		for(int i=0; i<first.length; i++){
			if (first[i] != second[i]){
				return false;
			}
		}
		return true;
	}
}
