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
 *     Denise Smith  February, 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.map;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.map.namespaces.foo.Foo;
import org.eclipse.persistence.testing.jaxb.map.namespaces.bar.Bar;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class MapNamespaceFooTestCases extends JAXBWithJSONTestCases{
	
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/map/foo.xml";
	private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/map/foo.xsd";
	private final static String XSD_RESOURCE2 = "org/eclipse/persistence/testing/jaxb/map/bar.xsd";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/map/foo.json";
	
	public MapNamespaceFooTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{Foo.class, Bar.class});
    	setControlDocument(XML_RESOURCE);
    	setControlJSON(JSON_RESOURCE);	
    }
	
	public void testSchemaGen() throws Exception{
		List<InputStream> controlSchemas = new ArrayList<InputStream>();
		controlSchemas.add(getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE));
		controlSchemas.add(getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE2));
		super.testSchemaGen(controlSchemas);
	}

	@Override
	protected Object getControlObject() {
	    Foo foo = new Foo();
	    foo.map.put("A", "a");   
    	return foo;
	}
	
}
