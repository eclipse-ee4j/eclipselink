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
package org.eclipse.persistence.testing.jaxb.json.xmlvalue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XMLValuePropTestCases extends JAXBWithJSONTestCases {

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/xmlvalue/person.json";    
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/xmlvalue/person.xml";
    
	public XMLValuePropTestCases(String name) throws Exception {
		super(name);
		setClasses(new Class[]{Person.class});
		setControlJSON(JSON_RESOURCE);
		setControlDocument(XML_RESOURCE);
	}

	public Map getProperties(){
		Map props = new HashMap();		
		props.put(JAXBContext.VALUE_WRAPPER, "value");
		return props;		   
	}
	
	@Override
	protected Object getControlObject() {
		Person p = new Person();
		p.setFirstName("Sally");
		p.setLastName("Smith");
		p.setMiddleNames(new ArrayList());
		PhoneNumber phone = new PhoneNumber();
		phone.number = "1234567";
		p.setPhoneNumber(phone);
		
		Address addr = new Address();
		List<String> addressInfos = new ArrayList<String>();
		addressInfos.add("someStreet");
		addressInfos.add("someCity");
		addressInfos.add("somePostalCode");
		addr.setAddressInfo(addressInfos);
		p.setAddress(addr);
		
		return p;
	}

}
