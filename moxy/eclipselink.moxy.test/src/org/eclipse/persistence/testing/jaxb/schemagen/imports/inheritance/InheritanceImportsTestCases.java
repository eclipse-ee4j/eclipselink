/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith- 2.4 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.imports.inheritance;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.schemagen.imports.inheritance.child.Child;

public class InheritanceImportsTestCases extends JAXBTestCases{
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/imports/inheritance/child.xml";
	
	public InheritanceImportsTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{Child.class});
		setControlDocument(XML_RESOURCE);
	}

	protected Object getControlObject() {
		Child c = new Child();
		c.childThing = "childValue"; 			
		c.parentThing = "parentValue";
		return c;
	}
	
	public void testSchemaGen() throws Exception{
		List<InputStream> controlSchemas = new ArrayList<InputStream>();
		InputStream is1 =getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/imports/inheritance/schema.xsd");
		InputStream is2 = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/imports/inheritance/schema2.xsd");
		controlSchemas.add(is1);
		controlSchemas.add(is2);
		super.testSchemaGen(controlSchemas);
	}
  
	
}
