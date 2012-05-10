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
 *     Denise Smith - October 2011 - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbintrospector.elementname;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class JAXBIntrospectorGetElementNameTestCases extends TestCase{

	JAXBContext jaxbContext;
	JAXBIntrospector jaxbIntrospector;
	
	public void setUp() throws Exception{
		Class[] classesToBeBound = new Class[]{TestObject.class};
		jaxbContext = JAXBContextFactory.createContext(classesToBeBound, null);
		jaxbIntrospector = jaxbContext.createJAXBIntrospector();
	}
	public void testGetElementNameEmptyStringUri(){
        TestObject obj = new TestObject();
        QName controlQname = new QName("someUri", "theRoot");
        assertEquals(controlQname, jaxbIntrospector.getElementName(obj));
	}
}
