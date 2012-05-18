/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 27 January 2012 - 2.3.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.collections;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CollectionHolderWrappersPopulatedTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderwrapperspopulated.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderwrapperspopulated.json";

    public CollectionHolderWrappersPopulatedTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);

        Class[] classes = new Class[1];
        classes[0] = CollectionHolderWrappers.class;
        setClasses(classes);

    }

    @Override
    protected Object getControlObject() {
    	CollectionHolderWrappers obj  = new CollectionHolderWrappers();
    	List<Integer> numbers = new ArrayList<Integer>();
    	obj.collection1 = new ArrayList<Integer>();
    	obj.collection1.add(10);
    	obj.collection1.add(20);
    	
    	obj.collection2 = new ArrayList<Object>(obj.collection1);
    	
    	//obj.collection3 = new ArrayList<Object>();
    //	obj.collection3.add(new CollectionHolderWrappers());
    	    	
    	obj.collection4 = new ArrayList<CollectionHolderWrappers>();    	    	    
    	obj.collection4.add(new CollectionHolderWrappers());
    	
    	obj.collection5 = new ArrayList<JAXBElement<String>>();
    	obj.collection5.add(new JAXBElement<String>(new QName("root"), String.class, "abcvalue"));
    	
    	obj.collection6 = new ArrayList<CoinEnum>();
    	obj.collection6.add(CoinEnum.DIME);
    	
    	obj.collection7 = new ArrayList<byte[]>();
    	obj.collection7.add(new String("abc").getBytes());
    	obj.collection7.add(new String("def").getBytes());
    	return obj;
    }

    public boolean shouldRemoveWhitespaceFromControlDocJSON(){
    	return false;
    }
}

