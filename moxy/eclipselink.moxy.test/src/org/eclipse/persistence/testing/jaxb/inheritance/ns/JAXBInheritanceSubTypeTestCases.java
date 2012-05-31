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
 *     Denise Smith - May 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.inheritance.ns;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class JAXBInheritanceSubTypeTestCases extends JAXBTestCases {
	public JAXBInheritanceSubTypeTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[] {SubTypeWithRootElement.class});
		setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/ns/subTypeRoot.xml");
		
	}
	
	public Object getControlObject() {		
		SubTypeWithRootElement subType = new SubTypeWithRootElement();	
		return subType;
	}
}
