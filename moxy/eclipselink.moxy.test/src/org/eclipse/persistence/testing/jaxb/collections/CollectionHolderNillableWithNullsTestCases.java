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

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CollectionHolderNillableWithNullsTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholdernillablewithnulls.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholdernillablewithnulls.json";

    public CollectionHolderNillableWithNullsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = CollectionHolderNillable.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
    }

    @Override
    protected Object getControlObject() {
        CollectionHolderNillable obj = new CollectionHolderNillable();
    	obj.collection1 = new ArrayList();
    	obj.collection1.add(null);
    	obj.collection1.add(null);
    	obj.collection2 = new ArrayList();
    	obj.collection2.add(null);
    	obj.collection2.add(null);
    	obj.collection3 = new ArrayList();
    	obj.collection3.add(null);
    	obj.collection3.add(null);
    	obj.collection4 = new ArrayList();
    	obj.collection4.add(null);
    	obj.collection4.add(null);
    	obj.collection5 = new ArrayList();
    	obj.collection5.add(null);
    	obj.collection5.add(null);
    	obj.collection6 = new ArrayList();
    	obj.collection6.add(null);
    	obj.collection6.add(null);
   // 	obj.collection7 = new ArrayList();
   // 	obj.collection7.add(null);
    //	obj.collection7.add(null);
    //	obj.collection8 = new ArrayList();
    //	obj.collection8.add(null);
    //	obj.collection8.add(null);
    	obj.collection9 = new ArrayList();
     	obj.collection9.add(null);
     	obj.collection9.add(null);
     	//obj.collection10 = new HashMap());
       //obj.collection10.put(new QName("theKey"), null);
     //	obj.collection10.put(new QName("theKey2"), null);
   
     //	obj.collection11 = new ArrayList();
   	   // obj.collection11.add(null);
   	    //obj.collection11.add(null);
   
   	 return obj;
    }

    public Object getReadControlObject() {
        CollectionHolderNillable obj = new CollectionHolderNillable();
    	obj.collection1 = new ArrayList();
    	obj.collection1.add(null);
    	obj.collection1.add(null);
    	obj.collection2 = new ArrayList();
    	//obj.collection2.add(null);
    	//obj.collection2.add(null);
    	obj.collection3 = new ArrayList();
    	obj.collection3.add(null);
    	obj.collection3.add(null);
    	//obj.collection4 = new ArrayList();
    	//obj.collection4.add(null);
    	//obj.collection4.add(null);
    	obj.collection5 = new ArrayList();
    	obj.collection5.add(null);
    	obj.collection5.add(null);
    	//obj.collection6 = new ArrayList();
    	//obj.collection6.add(null);
    	//obj.collection6.add(null);
    	//obj.collection7 = new ArrayList();
    	//obj.collection7.add(null);
    	//obj.collection7.add(null);
    //	obj.collection8 = new ArrayList();
    	//obj.collection8.add(null);
    	//obj.collection8.add(null);
    	obj.collection9 = new ArrayList();
     	obj.collection9.add(null);
     	obj.collection9.add(null);
     	//obj.collection10 = new HashMap());
       //obj.collection10.put(new QName("theKey"), null);
     //	obj.collection10.put(new QName("theKey2"), null);
   
     //	obj.collection11 = new ArrayList();
   	   // obj.collection11.add(null);
   	    //obj.collection11.add(null);
   
   	 return obj;
    }
    
    public void testRoundTrip() throws Exception{    
    }
}