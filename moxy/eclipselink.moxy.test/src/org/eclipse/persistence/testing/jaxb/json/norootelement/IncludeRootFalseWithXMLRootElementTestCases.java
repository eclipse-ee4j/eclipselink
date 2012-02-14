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
 *     Denise Smith - September 2011 - 2.4 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.norootelement;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.w3c.dom.Document;

public class IncludeRootFalseWithXMLRootElementTestCases extends NoRootElementTestCases{

	public IncludeRootFalseWithXMLRootElementTestCases(String name) throws Exception {
		super(name);
		setControlJSON(JSON_RESOURCE);
		setClasses(new Class[]{AddressWithRootElement.class});		
	}
	
	public void setUp() throws Exception{
		super.setUp();
	    jsonMarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_INCLUDE_ROOT, false);	    	   
	    jsonUnmarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_INCLUDE_ROOT, false);
  	}
	
	public Object getControlObject() {
		AddressWithRootElement addr = new AddressWithRootElement();
		addr.setId(10);
		addr.setCity("Ottawa");
		addr.setStreet("Main street");
		
		return addr;
	}
}
