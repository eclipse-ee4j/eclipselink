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
 *     Denise Smith - 2011-08-25
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.attribute;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class JSONAttributePrefixEmptyStringTestCases extends JSONMarshalUnmarshalTestCases {
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/attribute/address_empty_string_prefix.json";

	public JSONAttributePrefixEmptyStringTestCases(String name) throws Exception {
		super(name);
		setControlJSON(JSON_RESOURCE);
		setClasses(new Class[]{Address.class});		
	}

	protected Object getControlObject() {
		Address add = new Address();
		add.setId(10);
		add.setCity("Ottawa");
		add.setStreet("Main street");

		return add;
	}
	
	public Map getProperties(){
		Map props = new HashMap();
		props.put(JAXBContext.JSON_ATTRIBUTE_PREFIX, "");
		return props;
	}
		
}
