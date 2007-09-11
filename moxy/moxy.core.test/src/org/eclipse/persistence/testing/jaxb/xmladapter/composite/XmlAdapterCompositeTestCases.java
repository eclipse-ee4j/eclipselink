/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.xmladapter.composite;

import java.util.HashMap;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlAdapterCompositeTestCases extends JAXBTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/composite.xml";
    private final static int ID_1 = 123; 
    private final static int ID_2 = 321;
    private final static String VALUE_1 = "this is a value";
    private final static String VALUE_2 = "this is another value";

    public XmlAdapterCompositeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        Class[] classes = new Class[3];
        classes[0] = MyMap.class;
        classes[1] = MyHashMapType.class;
        classes[2] = MyHashMapEntryType.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        MyMap myMap = new MyMap();
        myMap.hashMap = new HashMap();
        myMap.hashMap.put(ID_2, VALUE_2);
        myMap.hashMap.put(ID_1, VALUE_1);
        return myMap;
    }
}