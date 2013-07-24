/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - November 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.padding;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.oxm.JSONWithPadding;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;
import org.eclipse.persistence.testing.jaxb.json.JSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.numbers.NumberHolder;

public class JAXBElementListJSONPaddingTestCases extends JSONTestCases{

	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/padding/paddingJAXBElementList.json";

	public JAXBElementListJSONPaddingTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{Simple.class});
		setControlJSON(JSON_RESOURCE);		
	}

	@Override
	protected Object getControlObject() {
		Simple simple = new Simple();
		simple.id = "1111";
		simple.name = "theName";
		JAXBElement<Simple> jbe = new JAXBElement<Simple>(new QName("someUri", "someRootName"), Simple.class, simple);
		
		Simple simple2 = new Simple();
		simple2.id = "2222";
		simple2.name = "theName2";
		JAXBElement<Simple> jbe2 = new JAXBElement<Simple>(new QName("someUri2", "someRootName2"), Simple.class, simple2);
		
		
		List jbes = new ArrayList();
		jbes.add(jbe);
		jbes.add(jbe2);
		JSONWithPadding test = new JSONWithPadding(jbes, "blah");
		return test;
	}
	
	public boolean isUnmarshalTest (){
		return false;
	}
	
	public void testJSONMarshalToBuilderResult() throws Exception{     
    }
	
	public void testJSONMarshalToGeneratorResult() throws Exception{     
    }
}
