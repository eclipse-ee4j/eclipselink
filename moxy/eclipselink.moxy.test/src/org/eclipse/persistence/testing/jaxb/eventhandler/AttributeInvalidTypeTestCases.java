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
 *    Denise Smith - May 2012
 ******************************************************************************/ 
package org.eclipse.persistence.testing.jaxb.eventhandler;

import javax.xml.bind.ValidationEvent;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AttributeInvalidTypeTestCases extends JAXBWithJSONTestCases{
	MyEventHandler handler;
	public AttributeInvalidTypeTestCases(String name) throws Exception {
	    super(name);
	    setClasses(new Class[] {MyClass.class});
	    setControlDocument("org/eclipse/persistence/testing/jaxb/eventhandler/attributeWrongType.xml");
	    setControlJSON("org/eclipse/persistence/testing/jaxb/eventhandler/attributeWrongType.json");
	    setWriteControlDocument("org/eclipse/persistence/testing/jaxb/eventhandler/valid.xml");
	    setWriteControlJSON("org/eclipse/persistence/testing/jaxb/eventhandler/valid.json");	        
 
    }

	public void setUp() throws Exception{
		super.setUp();
		handler = new MyEventHandler();
        jaxbUnmarshaller.setEventHandler(handler);
	}
	  
	@Override
	protected Object getControlObject() {
		MyClass myClass = new MyClass();
		myClass.myAttribute =10;
		myClass.myElement = 20;

		return myClass;
	}
	
	public void jsonToObjectTest(Object testObject, Object controlObject) throws Exception {
		assertEquals(ValidationEvent.ERROR, handler.getSeverity());		     
   }
	
    public void xmlToObjectTest(Object testObject) throws Exception {
    	assertEquals(ValidationEvent.ERROR, handler.getSeverity());
    }
    
    public void testRoundTrip(){};
	
    public void testXMLToObjectFromXMLStreamReaderEx(){};
}
