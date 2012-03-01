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
 * Denise Smith - September 15 /2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.schemagen.scope;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class SchemaGenScopeTestCases extends JAXBWithJSONTestCases{
	protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/scope/scope.xml";
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/schemagen/scope/scope.json";

	public SchemaGenScopeTestCases(String name) throws Exception {
		super(name);
		init();
	}

	public void init() throws Exception {
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		Class[] classes = new Class[2];
		classes[0] = ClassA.class;
		classes[1] = ObjectFactory.class;
		setClasses(classes);
	}

	public  List<InputStream> getControlSchemaFiles(){		
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/schemagen/scope/scope.xsd");		
        List<InputStream> controlSchema = new ArrayList<InputStream>();
        controlSchema.add(instream);
        return controlSchema;
    }
	
 
	public void testSchemaGen() throws Exception {
		testSchemaGen(getControlSchemaFiles());
	}
	
	protected Object getControlObject() {
		ClassA classA = new ClassA();
		classA.setSomeValue("value");
				
		return classA;
	}

}
