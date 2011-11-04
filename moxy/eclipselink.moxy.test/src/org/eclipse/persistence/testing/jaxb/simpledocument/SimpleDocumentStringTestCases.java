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
* mmacivor - April 25/2008 - 1.0M8 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.simpledocument;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import javax.xml.bind.JAXBElement;

/**
 * Tests mapping a simple document containing a single xs:string element to a String object
 * @author mmacivor
 *
 */
public class SimpleDocumentStringTestCases extends JAXBWithJSONTestCases {
		private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/simpledocument/stringroot.xml";
		private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/simpledocument/stringroot.json";

	    public SimpleDocumentStringTestCases(String name) throws Exception {
	        super(name);
	        setControlDocument(XML_RESOURCE);
	        setControlJSON(JSON_RESOURCE);
	        Class[] classes = new Class[1];
	        classes[0] = StringObjectFactory.class;
	        setClasses(classes);
	    }

	    protected Object getControlObject() {
	    	JAXBElement value = new StringObjectFactory().createStringRoot();
	    	value.setValue("StringValue");
	    	return value;      
	    }
	    
	    public Map getProperties(){
	    	Map props = new HashMap();
		    	
	    	Map namespaces = new HashMap();
	    	namespaces.put("ns0", "myns");

	    	props.put(JAXBContext.NAMESPACES, namespaces);
	    	
		    return props;
	}
	    
}
