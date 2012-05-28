/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CollectionHolderInitializedWithNullsTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderinitializedwithnulls.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderinitializedELwithnulls.json";    																							  
    
    public CollectionHolderInitializedWithNullsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = CollectionHolderInitialized.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, Boolean.TRUE);
    }

    @Override
    protected Object getControlObject() {
    	CollectionHolderInitialized obj = new CollectionHolderInitialized();
    	obj.collection1.add(null);
    	obj.collection1.add(null);
    	obj.collection2.add(null);
    	obj.collection2.add(null);
    	obj.collection3.add(null);
    	obj.collection3.add(null);
    	obj.collection4.add(null);
    	obj.collection4.add(null);
    	obj.collection5.add(null);
    	obj.collection5.add(null);
    	obj.collection6.add(null);
    	obj.collection6.add(null);
    	obj.collection7.add(null);
    	obj.collection7.add(null);
    	obj.collection8.add(null);
    	obj.collection8.add(null);
     	obj.collection9.add(null);
     	obj.collection9.add(null);
    	obj.collection10.put("theKey", null);
     	obj.collection10.put("theKey", null);
   	//   obj.collection11.add(null);
   	//    obj.collection11.add(null);
 	    obj.collection12.add(null);
    	obj.collection12.add(null);
    	obj.collection13.add(null);
    	obj.collection13.add(null);
    	obj.collection14.add(null);
    	obj.collection14.add(null);
    	return obj;
    }
   
    @Override
	public Object getReadControlObject() {
    	CollectionHolderInitialized obj = new CollectionHolderInitialized();
    	obj.collection1.add(null);
    	obj.collection1.add(null);
    	//obj.collection2.add(null);
    	//obj.collection2.add(null);
    	obj.collection3.add(null);
    	obj.collection3.add(null);
//    	obj.collection4.add(null);
    	//obj.collection4.add(null);
    	obj.collection5.add(null);
    	obj.collection5.add(null);
   // 	obj.collection6.add(null);
    //	obj.collection6.add(null);
    	//obj.collection7.add(null);
    	//obj.collection7.add(null);
    	//obj.collection8.add(null);
    	//obj.collection8.add(null);
     	obj.collection9.add(null);
     	obj.collection9.add(null);
    	//obj.collection10.put("theKey", null);
     	//obj.collection10.put("theKey", null);
   	  //  obj.collection11.add(null);
   	   // obj.collection11.add(null);
 	   // obj.collection12.add(null);
    	//obj.collection12.add(null);
    	//obj.collection13.add(null);
    	//obj.collection13.add(null);
     	//obj.collection14.add(null);
    	//obj.collection14.add(null);
    	
    	return obj;
    }
    
    public boolean shouldRemoveWhitespaceFromControlDocJSON(){
    	return false;
    }
    public void testRoundTrip() throws Exception{
    	
    }
}
