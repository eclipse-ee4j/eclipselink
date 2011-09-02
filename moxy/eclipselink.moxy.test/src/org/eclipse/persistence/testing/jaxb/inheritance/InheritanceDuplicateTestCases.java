/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.inheritance;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class InheritanceDuplicateTestCases extends JAXBTestCases{

	public InheritanceDuplicateTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{Child.class});
		setControlDocument("org/eclipse/persistence/testing/jaxb/inheritance/child.xml");
	}

	protected Object getControlObject() {
		Child c = new Child();
		c.setChildThing("childValue");
		c.setSomeThing("someValue");
		return c;
 	}
	
	public void testSchemaGen() throws Exception{
		List<InputStream> controlSchemas = new ArrayList<InputStream>();
		InputStream is = getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/inheritance/child.xsd");
		controlSchemas.add(is);
		super.testSchemaGen(controlSchemas);
	}

}
