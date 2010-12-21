/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - March 2, 2010
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmladapter.map;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class JAXBMapWithAdapterTestCases extends JAXBTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/map.xml";
	
    public JAXBMapWithAdapterTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);       
        Class[] classes = new Class[2];
        classes[0] = MyObject.class;
        classes[1] = Person.class;
        setClasses(classes);
    }
    
    protected Object getControlObject() {
        Person person  = new Person();
        
        person.setName("thePerson");
        Map<Integer, String> theMap = new HashMap<Integer, String>();
        theMap.put(1, "first");
        theMap.put(2, "second");
        theMap.put(3, "third");
        person.setMapTest(theMap);
    	
        return person;
    }
    
    
    public void testSchemaGen() throws Exception{
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/xmladapter/map.xsd");
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);
    }
    
}
