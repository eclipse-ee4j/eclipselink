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
 *  - rbarkhouse - 21 October 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.idresolver;

import org.eclipse.persistence.jaxb.IDResolver;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class IDResolverTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/idresolver/fruit.xml";

    private MyIDResolver idResolver = new MyIDResolver();
    
    public IDResolverTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Apple.class, AppleRef.class, Box.class, FruitOrder.class, Orange.class, OrangeRef.class });
        setControlDocument(XML_RESOURCE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        getJAXBUnmarshaller().setProperty(UnmarshallerProperties.ID_RESOLVER, idResolver);
    }

    public void testHitMethods() throws Exception {
        getJAXBUnmarshaller().unmarshal(getControlDocument());

        assertTrue("IDResolver.startDocument() was not called.", idResolver.hitStartDocument);
        assertTrue("IDResolver.endDocument() was not called.", idResolver.hitEndDocument);
        assertTrue("IDResolver.bind(Map) was not called.", idResolver.hitBind);
        assertTrue("IDResolver.resolve(Map) was not called.", idResolver.hitResolve);
        assertTrue("IDResolver.bind(Object) was not called.", idResolver.hitBindSingle);
        assertTrue("IDResolver.resolve(Object) was not called.", idResolver.hitResolveSingle);
        assertTrue("ValidationEventHandler was not set.", idResolver.eventHandlerNotNull);        
    }

    public Object getControlObject() {
        Apple a1 = new Apple();
        a1.id = "a1";
        a1.appleChar = 'M';
        a1.type = "MacIntosh";
        a1.processed = true;
        AppleRef a1Ref = new AppleRef();
        a1Ref.ref = a1;
        Apple a2 = new Apple();
        a2.id = "a1";
        a2.appleChar = 'G';
        a2.type = "Gravenstein";
        a2.processed = true;
        AppleRef a2Ref = new AppleRef();
        a2Ref.ref = a2;
        Orange o1 = new Orange();
        o1.id = "a1";
        o1.orangeCode = 771;
        o1.size = "Small";
        o1.processed = true;
        OrangeRef o1Ref = new OrangeRef();
        o1Ref.ref = o1;

        Box box1 = new Box();
        box1.fruits.add(a1Ref);
        box1.fruits.add(o1Ref);
        box1.fruits.add(a1);
        box1.fruits.add(o1);
        Box box2 = new Box();
        box2.fruits.add(a2Ref);
        box2.fruits.add(a2);

        FruitOrder o = new FruitOrder();
        o.boxes.add(box1);
        o.boxes.add(box2);

        return o;
    }

}