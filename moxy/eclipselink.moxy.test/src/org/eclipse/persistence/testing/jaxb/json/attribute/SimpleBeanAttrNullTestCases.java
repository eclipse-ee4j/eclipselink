/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.5 - September 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.attribute;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class SimpleBeanAttrNullTestCases extends JAXBWithJSONTestCases {

	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/attribute/simplebean.json";
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/attribute/simplebean.xml";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/attribute/simplebean_write.json";
	
	public SimpleBeanAttrNullTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{SimpleBean.class});
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);	
		setWriteControlJSON(JSON_WRITE_RESOURCE);
		jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
	}

	public Class getUnmarshalClass(){		
		return SimpleBean.class;
	}
	
	
	public Object getReadControlObject() {
		JAXBElement jbe = new JAXBElement<SimpleBean>(new QName("simpleBean"), SimpleBean.class, new SimpleBean());		
		return jbe;
	}
	
	
	protected Object getJSONReadControlObject() {		
		JAXBElement jbe = new JAXBElement<SimpleBean>(new QName(""), SimpleBean.class,  new SimpleBean());		
		return jbe;
	}
	
	protected Object getControlObject() {
	    SimpleBean sb = new SimpleBean();
		sb.setAttr2("");
		sb.elem2 = "";
		return sb;
	}
	
    public void testUnmarshallerHandler() throws Exception {}
	
}
