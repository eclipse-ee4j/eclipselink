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

import java.util.ArrayList;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class CollectionHolderPopulatedTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderpopulated.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/collections/emptycollectionholderpopulated.json";

    public CollectionHolderPopulatedTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = CollectionHolderInitialized.class;
        setClasses(classes);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, Boolean.FALSE);
    }

    @Override
    protected Object getControlObject() {
    	CollectionHolderInitialized ch  = new CollectionHolderInitialized();
    	ch.getCollection1().add(10);
    	ch.getCollection1().add(20);
    	
    	ch.getCollection2().add("one");
    	ch.getCollection2().add("two");
    	
    	ch.getCollection3().add(20);
    	ch.getCollection3().add(30);
    	
    	ReferencedObject ref1 = new ReferencedObject();
    	ref1.id ="1";
    	ch.getCollection4().add(ref1);
    	    	
    	ch.getCollection5().add(new CollectionHolderInitialized());
    	
    	ch.getCollection6().add(new JAXBElement<String>(new QName("root"), String.class, "60"));
    	ch.getCollection6().add(new JAXBElement<String>(new QName("root"), String.class, "70"));
    	
    	ReferencedObject ref2 = new ReferencedObject();
    	ref2.id ="2";
    	ReferencedObject ref3 = new ReferencedObject();
    	ref3.id ="3";
    	ch.getCollection7().add(ref2);
    	ch.getCollection7().add(ref3);
    	
    	ReferencedObject ref4 = new ReferencedObject();
    	ref4.id ="4";
    	ReferencedObject ref5 = new ReferencedObject();
    	ref5.id ="5";
    	ch.getCollection8().add(ref4);
    	ch.getCollection8().add(ref5);
    	
    	ch.getCollection9().add(CoinEnum.PENNY);
    	ch.getCollection9().add(CoinEnum.NICKEL);
    	
    	ch.getCollection10().put(new QName("abcQName"), "80");
    	ch.getCollection10().put(new QName("abcQName"), "80");
    	
    	ch.getCollection11().add("abc".getBytes());
    	ch.getCollection11().add("def".getBytes());
    	
    	ch.getCollection12().add("abc");
    	ch.getCollection12().add("def");
    	
    	ch.getCollection13().add(123);
    	ch.getCollection13().add("eee");
    	ch.getCollection13().add(456);
    	
    	ch.collection14.add(123);
    	ch.collection14.add("eee");
    	ch.collection14.add(456);
    	return ch;
    }
    
    public Object getReadControlObject() {
    	CollectionHolderInitialized ch  = new CollectionHolderInitialized();
    	ch.getCollection1().add(10);
    	ch.getCollection1().add(20);
    	
    	ch.getCollection2().add("one");
    	ch.getCollection2().add("two");
    	
    	ch.getCollection3().add(20);
    	ch.getCollection3().add(30);
    	
    	ReferencedObject ref1 = new ReferencedObject();
    	ref1.id ="1";
    	ch.getCollection4().add(ref1);
    	    	
    	ch.getCollection5().add(new CollectionHolderInitialized());
    	
    	ch.getCollection6().add(new JAXBElement<String>(new QName("root"), String.class, "60"));
    	ch.getCollection6().add(new JAXBElement<String>(new QName("root"), String.class, "70"));
    	
    	ReferencedObject ref2 = new ReferencedObject();
    	ref2.id ="2";
    	ReferencedObject ref3 = new ReferencedObject();
    	ref3.id ="3";
    	//ch.getCollection7().add(ref2);
    	//ch.getCollection7().add(ref3);
    	
    	ReferencedObject ref4 = new ReferencedObject();
    	ref4.id ="4";
    	ReferencedObject ref5 = new ReferencedObject();
    	ref5.id ="5";
    //	ch.getCollection8().add(ref4);
    	//ch.getCollection8().add(ref5);
    	//
    	ch.getCollection9().add(CoinEnum.PENNY);
    	ch.getCollection9().add(CoinEnum.NICKEL);
    	
    	ch.getCollection10().put(new QName("abcQName"), "80");
    	ch.getCollection10().put(new QName("abcQName"), "80");
    	
    	ch.getCollection11().add("abc".getBytes());
    	ch.getCollection11().add("def".getBytes());
    	
    	ch.getCollection12().add("abc");
    	ch.getCollection12().add("def");
    	
    	
    	ch.getCollection13().add(123);
    	ch.getCollection13().add("eee");
    	ch.getCollection13().add(456);
    	
    	ch.collection14.add(123);
    	ch.collection14.add("eee");
    	ch.collection14.add(456);    
    	return ch;
    }
    
    protected Object getJSONReadControlObject() {
    	CollectionHolderInitialized obj = (CollectionHolderInitialized)getControlObject();
    	obj.collection5.get(0).collection10.put(new QName("type"), "collectionHolderInitialized");
    	obj.collection7 = new ArrayList();
    	obj.collection8 = new ArrayList();
    	obj.collection13 = new ArrayList();
    	obj.getCollection13().add(123);    	
    	obj.getCollection13().add(456);
    	obj.getCollection13().add("eee");
    	
    	obj.collection14.add(123);    	
    	obj.collection14.add(456);
    	obj.collection14.add("eee");
    	return obj;
    }

    public boolean shouldRemoveWhitespaceFromControlDocJSON(){
    	return false;
    }
    
    //not applicable
    public void testRoundTrip(){
    	
    }
    
}

