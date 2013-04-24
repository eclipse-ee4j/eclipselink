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
 *     Denise Smith   April 2013
 ******************************************************************************/ 
package org.eclipse.persistence.testing.jaxb.xmlenum.xmlvalue;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlEnumXmlValueTestCases extends JAXBWithJSONTestCases{
	
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/xmlenum_xmlvalue.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlenum/xmlenum_xmlvalue.json";
	
	
	public XmlEnumXmlValueTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);  
        setControlJSON(JSON_RESOURCE);
        
        Class[] classes = new Class[2];
        classes[0] = Root.class;
        classes[1] = Coin.class;
        setClasses(classes);
	}

	@Override
	protected Object getControlObject() {
		Root r =new Root();
		r.coin = Coin.dime;
		return r;
	}

}
