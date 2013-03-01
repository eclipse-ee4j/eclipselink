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
 *     Denise Smith, February 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlinverseref.bindings;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class InverseBiWriteableBindingsTestCases extends JAXBWithJSONTestCases{

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bindings/inverseRefWrite.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bindings/inverseRefWrite.json";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bindings/inverseRefWrite.xsd";
  
    public InverseBiWriteableBindingsTestCases(String name) throws Exception {
		super(name);
		setControlJSON(JSON_RESOURCE);
		setControlDocument(XML_RESOURCE);
		setClasses(new Class[]{Person.class});
	
	}
    
    @Override
	protected Map getProperties() {
    	InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmlinverseref/bindings/inverseref-bi-write-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.xmlinverseref.bindings", new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
		return properties;
    }

	@Override
	protected Object getControlObject() {
		Person p = new Person();
		p.name = "theName";
		Address addr = new Address();
		addr.street = "theStreet";
		addr.owner = p;
		p.addr = addr;
		return addr;
	}
	
	public void testSchemaGen() throws Exception{
		List<InputStream> controlSchemas = new ArrayList<InputStream>();
		controlSchemas.add(getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE));
		super.testSchemaGen(controlSchemas);
	}

}
