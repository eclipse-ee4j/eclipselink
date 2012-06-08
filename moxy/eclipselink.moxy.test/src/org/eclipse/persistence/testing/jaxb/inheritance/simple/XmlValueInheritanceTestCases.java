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
 *    Denise Smith - June 2012
 ******************************************************************************/  

package org.eclipse.persistence.testing.jaxb.inheritance.simple;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;


public class XmlValueInheritanceTestCases extends JAXBTestCases{

	public XmlValueInheritanceTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[] { Simple.class, Complex.class});
		setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/simple/xmlvalue.xml");

	}
	public Object getControlObject() {
		Complex complex = new Complex();
		complex.foo = "aaa";
		complex.bar = "bbb";
		return complex;
	}
	
	public void testSchemaGen() throws Exception{
		List<InputStream> controlSchemas = new ArrayList<InputStream>();
    	InputStream instream1 = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/inheritance/simple/schema.xsd");
    	controlSchemas.add(instream1);
		super.testSchemaGen(controlSchemas);
	}
	
}
