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
 *     Denise Smith - 2.4 - April 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.attribute;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class JSONAttributeNoXmlRootElementTestCases extends JAXBWithJSONTestCases {

	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/attribute/address_no_root.json";
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/attribute/address_no_root.xml";

	public JSONAttributeNoXmlRootElementTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{AddressNoRoot.class});
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);	
		jaxbUnmarshaller.setProperty(JAXBUnmarshaller.JSON_INCLUDE_ROOT, false);
	}

	public Class getUnmarshalClass(){		
		return AddressNoRoot.class;
	}
	
	public Object getReadControlObject() {
		JAXBElement jbe = new JAXBElement<AddressNoRoot>(new QName("street"), AddressNoRoot.class, new AddressNoRoot());		
		return jbe;
	}
	
	
	protected Object getJSONReadControlObject() {		
		JAXBElement jbe = new JAXBElement<AddressNoRoot>(new QName(""), AddressNoRoot.class, (AddressNoRoot) getControlObject());		
		return jbe;
	}
	
	protected Object getControlObject() {
		AddressNoRoot add = new AddressNoRoot();
		add.setId(10);
		add.setCity("Ottawa");
		add.setStreet("Main street");

		return add;
	}
	

	
	  public void testXMLToObjectFromURL() throws Exception {
	        if(isUnmarshalTest()) {
	            java.net.URL url = ClassLoader.getSystemResource(resourceName);
	            jaxbUnmarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.MEDIA_TYPE, "application/xml");
	            
	            Object testObject = null;
	            if(getUnmarshalClass() != null){
	               testObject = ((JAXBUnmarshaller)jaxbUnmarshaller).unmarshal(new StreamSource(url.openStream()), getUnmarshalClass());	
	            }else{
	            	testObject = jaxbUnmarshaller.unmarshal(url);
	            }
	            xmlToObjectTest(testObject);
	        }
	    }
	  
	  public void testRoundTrip() throws Exception {}
	  public void testUnmarshallerHandler() throws Exception {}
}
