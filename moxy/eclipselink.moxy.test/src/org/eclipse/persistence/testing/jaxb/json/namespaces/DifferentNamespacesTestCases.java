/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.jaxb.json.namespaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.PropertyException;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class DifferentNamespacesTestCases extends JSONMarshalUnmarshalTestCases{
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/person.json";
	private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/person_different.json";

	public DifferentNamespacesTestCases(String name) throws Exception {
		super(name);
		setControlJSON(JSON_RESOURCE);
		setWriteControlJSON(JSON_WRITE_RESOURCE);
		setClasses(new Class[]{Person.class});
	}

	protected Object getControlObject() {
		Person p = new Person();
		p.setId(10);
		p.setFirstName("Jill");
		p.setLastName("MacDonald");
		
		List<String> middleNames = new ArrayList<String>();
		middleNames.add("Jane");
		middleNames.add("Janice");
		p.setMiddleNames(middleNames);
		
		Address addr = new Address();
		addr.setStreet("The Street");
		addr.setCity("Ottawa");
		p.setAddress(addr);
		
		return p;
	}
	

	public void setUp(){
		super.setUp();
		
        Map<String, String> marshalNamespaceMap = new HashMap<String, String>();		
        marshalNamespaceMap.put("aaa", "namespace0");
        marshalNamespaceMap.put("bbb", "namespace1");
        marshalNamespaceMap.put("ccc", "namespace2");
        marshalNamespaceMap.put("ddd", "namespace3");
		
		Map<String, String> unmarshalNamespaceMap = new HashMap<String, String>();		
		unmarshalNamespaceMap.put("ns0", "namespace0");
		unmarshalNamespaceMap.put("ns1", "namespace1");
		unmarshalNamespaceMap.put("ns2", "namespace2");
		unmarshalNamespaceMap.put("ns3", "namespace3");
				
		try{
		    jsonMarshaller.setProperty(JAXBContext.NAMESPACES, marshalNamespaceMap);
		    jsonUnmarshaller.setProperty(JAXBContext.NAMESPACES, unmarshalNamespaceMap);
		}catch(PropertyException e){
			e.printStackTrace();
			fail("An error occurred setting properties during setup.");
		}
	}
	
	
	public Map getProperties(){
		Map props = new HashMap();
		props.put(JAXBContext.ATTRIBUTE_PREFIX, "@");
		return props;
	}


}
