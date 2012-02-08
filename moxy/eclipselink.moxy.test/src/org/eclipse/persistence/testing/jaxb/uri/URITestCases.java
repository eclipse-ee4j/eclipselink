/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.4 - February 2012 
 ******************************************************************************/

package org.eclipse.persistence.testing.jaxb.uri;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class URITestCases extends JAXBTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/uri/uri.xml";
	//private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/uri.xml";
		
	public URITestCases(String name) throws Exception {
	    super(name);
		setClasses(new Class[]{});
		setControlDocument(XML_RESOURCE);
		//setWriteControlDocument(XML_WRITE_RESOURCE);
	}
		
	public Object getControlObject(){
		
		JAXBElement<URI> jbe = null;
		try {
			jbe = new JAXBElement<URI>(new QName("foo"), URI.class, new URI("http://foo/bar"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();	
		}
		
		return jbe;
	}
	
	public void testXMLToObjectFromInputStream() throws Exception {	}
    public void testRoundTrip() throws Exception{}
    public void testXMLToObjectFromURL() throws Exception {}
    public void testXMLToObjectFromXMLStreamReader() throws Exception {}
    public void testXMLToObjectFromNode() throws Exception {}
    public void testXMLToObjectFromXMLStreamReaderEx() throws Exception {}
    public void testXMLToObjectFromXMLEventReader() throws Exception {}
    public void testUnmarshallerHandler() throws Exception {}

}
