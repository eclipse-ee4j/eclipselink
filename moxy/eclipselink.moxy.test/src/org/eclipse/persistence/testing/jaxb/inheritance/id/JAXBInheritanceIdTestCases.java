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
 *     Denise Smith - August 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.inheritance.id;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class JAXBInheritanceIdTestCases extends JAXBWithJSONTestCases {
	public JAXBInheritanceIdTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[] {AImpl.class, BImpl.class, Base.class});
		setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/id/a.xml");
		setControlJSON("org/eclipse/persistence/testing/jaxb/inheritance/id/a.json");
				
	}	
	
	public Object getControlObject() {
	    A a = new AImpl("a");
        B b = new BImpl("b");

        a.setB(b);
        b.setA(a);
        
        return a;
	}
		
}
