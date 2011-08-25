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
 *     Denise Smith - August 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.norootelement;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;

import org.eclipse.persistence.testing.jaxb.json.JSONTestCases;
import org.eclipse.persistence.testing.oxm.OXTestCase;

public class NoRootElementTestCases extends JSONTestCases {    
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/norootelement/address.json";    
    
	public NoRootElementTestCases(String name) throws Exception {
	    super(name);
	    setControlJSON(JSON_RESOURCE);
	    setClasses(new Class[]{Address.class});	    

	}

	public Object getControlObject() {
		Address addr = new Address();
		addr.setId(10);
		addr.setCity("Ottawa");
		addr.setStreet("Main street");
		
		QName name = new QName("");
		JAXBElement jbe = new JAXBElement<Address>(name, Address.class, addr);
		return jbe;
	}
    
   
}

