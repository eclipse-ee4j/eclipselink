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
 *     Denise Smith - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.nomappings;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class NoMappingsTestCases extends JAXBWithJSONTestCases{
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/nomappings/test.xml";
	private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/nomappings/testWrite.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/nomappings/test.json";
	private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/nomappings/testWrite.json";
	
	public NoMappingsTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{SomeClass.class});
    	setControlDocument(XML_RESOURCE);
    	setControlJSON(JSON_RESOURCE);   
    	setWriteControlDocument(XML_WRITE_RESOURCE);
    	setWriteControlJSON(JSON_WRITE_RESOURCE);
    	jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
    	jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
	}

	@Override
	protected Object getControlObject() {		
		return new SomeClass();		
	}
	
	@Override
	public Object getJSONReadControlObject() {
		JAXBElement jbe = new JAXBElement<SomeClass>(new QName(""), SomeClass.class, new SomeClass());		
		return jbe;
	}	

	@Override
	public Object getReadControlObject() {
		JAXBElement jbe = new JAXBElement<SomeClass>(new QName("someClass"), SomeClass.class, new SomeClass());		
		return jbe;
	}
	
	@Override
	public Class getUnmarshalClass(){
		return SomeClass.class;
	}
	
	public void testUnmarshallerHandler(){};

}
