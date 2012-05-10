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
 *     Denise Smith - 2011-08-25
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.attribute;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.PropertyException;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class JSONAttributePrefixOnContextTestCases extends JSONMarshalUnmarshalTestCases {
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/attribute/address_prefix.json";

	public JSONAttributePrefixOnContextTestCases(String name) throws Exception {
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
		props.put(JAXBContextProperties.JSON_ATTRIBUTE_PREFIX, "@");
		return props;
	}
	
	public void testingTurnAttributesOnOff() throws Exception{
		try{
		    jsonMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "");
		}catch (PropertyException e){
			e.printStackTrace();
			fail("an error occurred during setup");
		}
		
		StringWriter sw = new StringWriter();
		jsonMarshaller.marshal(getWriteControlObject(), sw);

		//since the prefix got set to "" then there should not be any @ symbols marshalled
	    assertFalse(sw.getBuffer().toString().indexOf('@')>-1);
				
		try{
		    jsonMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
		}catch (PropertyException e){
			e.printStackTrace();
			fail("an error occurred during setup");
		}
		testJSONMarshalToOutputStream();
				
	}

}
