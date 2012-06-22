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
 *     Denise Smith - June 24/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbelement.simple;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.jaxbelement.JAXBElementTestCases;
import org.eclipse.persistence.testing.oxm.xmlroot.Person;

public class JAXBElementBase64TestCases  extends JAXBElementTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/simple/bytearray.xml";

	public JAXBElementBase64TestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);	
		setTargetClass(byte[].class);		
	}

	public Class[] getClasses(){
    	Class[] classes = new Class[1];
        classes[0] = byte[].class;
        return classes;
    }
	
	protected Object getControlObject() {		
		JAXBElement<byte[]> jbe = new JAXBElement<byte[]>(new QName("a", "b"),byte[].class, new byte[] { 0, 1, 2, 3}); 			
		return jbe;		
	}
			
	protected void comparePrimitiveArrays(Object controlValue, Object testValue){	    
	    assertEquals(byte[].class, controlValue.getClass());
	    assertEquals(byte[].class, testValue.getClass());
	    
	    byte[] controlArray = (byte[])controlValue;
	    byte[] testArray = (byte[])testValue;	    
	    
	    assertEquals(controlArray.length, testArray.length);
		for (int i = 0; i < controlArray.length; i++) {
			assertEquals(controlArray[i], testArray[i]);
		}
	}
	
	public void testSchemaGen() throws Exception{
		super.testSchemaGen(new ArrayList<InputStream>());
	}

}
