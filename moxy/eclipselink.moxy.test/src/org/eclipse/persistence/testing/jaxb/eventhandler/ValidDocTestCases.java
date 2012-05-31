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

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ValidDocTestCases extends JAXBWithJSONTestCases{
	MyEventHandler handler;
	public ValidDocTestCases(String name) throws Exception {
	    super(name);
	    setClasses(new Class[] {MyClass.class});
	    setControlDocument("org/eclipse/persistence/testing/jaxb/eventhandler/valid.xml");
	    setControlJSON("org/eclipse/persistence/testing/jaxb/eventhandler/valid.json");	        
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
		//myClass.myEnumAttribute = MyEnum.A;
		//myClass.myEnumElement = MyEnum.B;
		return myClass;
	}
	
}
