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

import javax.xml.bind.PropertyException;

import org.eclipse.persistence.jaxb.JAXBContext;

public class JSONAttributePrefixOnMarshallerTestCases extends JSONAttributePrefixOnContextTestCases {

	public JSONAttributePrefixOnMarshallerTestCases(String name) throws Exception {
		super(name);

	}
	public void setUp() throws Exception{
		super.setUp();
		//this should override the properties set on the context in getProperties (called during inherited c-tor setClasses call)
		try{
		    jsonMarshaller.setProperty(JAXBContext.JSON_ATTRIBUTE_PREFIX, "@");
		    jsonUnmarshaller.setProperty(JAXBContext.JSON_ATTRIBUTE_PREFIX, "@");
		}catch (PropertyException e){
			e.printStackTrace();
			fail("an error occurred during setup");
		}
	}

	public Map getProperties(){
		Map props = new HashMap();
		props.put(JAXBContext.JSON_ATTRIBUTE_PREFIX, "CONTEXT_PREFIX"); 
		return props;
	}
}
