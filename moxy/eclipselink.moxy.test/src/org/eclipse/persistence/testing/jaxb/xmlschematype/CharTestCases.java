/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Denise Smith - March 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlschematype;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CharTestCases extends JAXBWithJSONTestCases{
	
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschematype/chars.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschematype/chars.json";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlschematype/chars.xsd";

	public CharTestCases(String name) throws Exception {
	    super(name);
	    setControlDocument(XML_RESOURCE);  
	    setControlJSON(JSON_RESOURCE);
	    Class[] classes = new Class[1];
	    classes[0] = CharHolder.class;
	    setClasses(classes);
	}

	@Override
	protected Object getControlObject() {			
		CharHolder holder = new CharHolder();
		holder.testIntString = 11;
		holder.testIntDefault = 12;
		holder.testIntWithunsignedShort =13;
		
		holder.testCharDefault = 'a';
		holder.testCharWithDecimal = 'b';
		holder.testCharWithInteger = 'c';
		holder.testCharWithString = 'd';
		holder.testCharWithunsignedShort = 'e';
		holder.testOtherChar = '5';
		holder.testCharZero = '0';
		return holder;		
	}
	
	public void testSchemaGen() throws Exception{
		List<InputStream> controlSchemas = new ArrayList<InputStream>();
		controlSchemas.add(getClass().getClassLoader().getResourceAsStream(XSD_RESOURCE));
		super.testSchemaGen(controlSchemas);
	}	
}
