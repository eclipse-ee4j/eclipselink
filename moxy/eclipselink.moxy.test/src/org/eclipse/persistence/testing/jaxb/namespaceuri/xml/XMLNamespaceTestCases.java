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
 *     Denise Smith - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.namespaceuri.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XMLNamespaceTestCases extends JAXBWithJSONTestCases{

	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/xml/xmlnamespace.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/namespaceuri/xml/xmlnamespace.json";
	
	public XMLNamespaceTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{Address.class});
    	setControlDocument(XML_RESOURCE);
    	setControlJSON(JSON_RESOURCE);
    	
    	Map<String, String> namespaces = new HashMap<String, String>();
    	namespaces.put("http://www.w3.org/XML/1998/namespace","xml");
    	namespaces.put("myns","ns0");
    	jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_NAMESPACE_PREFIX_MAPPER, namespaces);
	}

	protected JAXBMarshaller getJSONMarshaller() throws Exception {
		JAXBMarshaller jsonMarshaller = (JAXBMarshaller) jaxbContext.createMarshaller();
		jsonMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
		Map<String, String> namespaces = new HashMap<String, String>();
    	namespaces.put("http://www.w3.org/XML/1998/namespace","xml");
    	namespaces.put("myns","ns0");
    	jsonMarshaller.setProperty(MarshallerProperties.NAMESPACE_PREFIX_MAPPER, namespaces);
    	return jsonMarshaller;

	}
	
	@Override
	protected Object getControlObject() {
		Address addr = new Address();
		addr.setStreet("theStreet");
		addr.lang = "blah";
		return addr;	
	}
	
	public void testSchemaGen() throws Exception{
		List<InputStream> controlSchemas = new ArrayList<InputStream>();
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/namespaceuri/xml/xmlnamespace.xsd");

		controlSchemas.add(inputStream);
	
		super.testSchemaGen(controlSchemas);
	}	
}
