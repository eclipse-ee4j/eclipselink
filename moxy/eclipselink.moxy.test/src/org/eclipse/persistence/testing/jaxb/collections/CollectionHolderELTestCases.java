/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.collections;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CollectionHolderELTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderemptcollections.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderemptcollectionsEL.json";

    public CollectionHolderELTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = CollectionHolder.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, Boolean.TRUE);        
    }

    @Override
    protected Object getControlObject() {
    	CollectionHolder ch =  new CollectionHolder();
    	ch.collection1 = new ArrayList();
    	ch.collection2 = new ArrayList();
    	ch.collection3 = new ArrayList();
    	ch.collection4 = new ArrayList();
    	ch.collection5 = new ArrayList();
    	ch.collection6 = new ArrayList();
    	ch.collection7 = new ArrayList();
    	ch.collection8 = new ArrayList();
    	ch.collection9 = new ArrayList();
    	ch.collection10 = new HashMap();
    	ch.collection11 = new ArrayList();
    	ch.collection12 = new ArrayList();    	
    	ch.collection13 = new ArrayList();    	
    	return ch;
    }
    
    public Object getReadControlObject(){
    	CollectionHolder ch =  new CollectionHolder();
    	ch.collection2 = new ArrayList();
    	ch.collection12 = new ArrayList();    	
    	return ch;
    }
    
    public Object getJSONReadControlObject(){
    	CollectionHolder ch =  new CollectionHolder();
    	ch.collection1 = new ArrayList();
    	ch.collection2 = new ArrayList();
    	ch.collection3 = new ArrayList();
    	//collection4 is an anycollection and not present in the doc so will be null
    	ch.collection5 = new ArrayList();
    	ch.collection6 = new ArrayList();
    	ch.collection7 = new ArrayList();
    	ch.collection8 = new ArrayList();
    	ch.collection9 = new ArrayList();
    	//collection10 is an anyattribute and not present in the doc so will be null
    	//ch.collection10 = new HashMap();
    	ch.collection11 = new ArrayList();
    	ch.collection12 = new ArrayList();    	
    	ch.collection13 = new ArrayList();    	
    	return ch;
    }

}