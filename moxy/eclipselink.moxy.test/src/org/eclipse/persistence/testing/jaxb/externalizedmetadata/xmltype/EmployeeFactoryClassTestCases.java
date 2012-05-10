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
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class EmployeeFactoryClassTestCases extends XmlTypeTestCases {

	public EmployeeFactoryClassTestCases(String name) throws Exception {
		super(name);
	}
    
    public Map getProperties(){
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/employee-factory-class-oxm.xml");

		HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
		metadataSourceMap.put("org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype", new StreamSource(inputStream));
		Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
		properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);		
	        
	    return properties;
	}
    
    public void xmlToObjectTest(Object testObject) throws Exception{
    	super.xmlToObjectTest(testObject);
        assertNotNull("Unmarshalled object is null.", testObject);
        Employee empObj = (Employee)testObject;
        assertTrue("Factory method was not called as expected", empObj.fromFactoryMethod);    
    }
}
