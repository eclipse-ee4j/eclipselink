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

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class NamespacesOnContextTestCases extends JSONMarshalUnmarshalTestCases{
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/person.json";

	public NamespacesOnContextTestCases(String name) throws Exception {
		super(name);
		setControlJSON(JSON_RESOURCE);
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
	

	public Map getProperties(){
		Map props = new HashMap();
		props.put(JAXBContext.JSON_ATTRIBUTE_PREFIX, "@");
		
		Map<String, String> namespaceMap = new HashMap<String, String>();
		
		namespaceMap.put("namespace0", "ns0");
		namespaceMap.put("namespace1", "ns1");
		namespaceMap.put("namespace2", "ns2");
		namespaceMap.put("namespace3", "ns3");
		
		
		props.put(JAXBContext.NAMESPACE_PREFIX_MAPPER, namespaceMap);
		return props;
	}


}
