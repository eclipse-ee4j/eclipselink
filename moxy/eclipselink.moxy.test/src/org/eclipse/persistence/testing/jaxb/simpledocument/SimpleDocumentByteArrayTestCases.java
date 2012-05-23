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
* mmacivor - April 25/2008 - 1.0M8 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.simpledocument;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import javax.xml.bind.JAXBElement;

/**
 * Tests mapping a simple document containing a single base64 element to a Byte Array
 * @author mmacivor
 *
 */
public class SimpleDocumentByteArrayTestCases extends JAXBWithJSONTestCases {
		private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/simpledocument/bytearrayroot.xml";
        private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/simpledocument/bytearrayroot.json";

	    public SimpleDocumentByteArrayTestCases(String name) throws Exception {
	        super(name);
	        setControlDocument(XML_RESOURCE);        
            setControlJSON(JSON_RESOURCE);        
	        Class[] classes = new Class[1];
	        classes[0] = ByteArrayObjectFactory.class;
	        setClasses(classes);
	    }

	    protected Object getControlObject() {
	    	JAXBElement value = new ByteArrayObjectFactory().createBase64Root();
	    	value.setValue(new Byte[]{new Byte((byte)1), new Byte((byte)2), new Byte((byte)3), new Byte((byte)4), new Byte((byte)5), new Byte((byte)6), new Byte((byte)7)});
	    	return value;      
	    }
	    
	    protected void compareObjectArrays(Object controlValue, Object testValue){
	    	 Byte[] controlBytes = (Byte[])controlValue;
		     Byte[] testBytes = (Byte[])testValue;
		     assertEquals(controlBytes.length, testBytes.length);
		     for(int i = 0; i < controlBytes.length; i++) {
		     	assertEquals(controlBytes[i].byteValue(), testBytes[i].byteValue());
		     }
	    }		    
}
