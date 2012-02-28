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
 *  - rbarkhouse - 27 February - 2.3.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.idresolver;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class NonELIDResolverTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/idresolver/fruit2.xml";
    
    private NonELIDResolver idResolver = new NonELIDResolver();
    
    public NonELIDResolverTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Melon.class, MelonRef.class, Box.class, FruitOrder.class });
        setControlDocument(XML_RESOURCE);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        getJAXBUnmarshaller().setProperty("com.sun.xml.bind.IDResolver", idResolver);
    }

    public void testHitMethods() throws Exception {
        getJAXBUnmarshaller().unmarshal(getControlDocument());

        assertTrue("IDResolver.startDocument() was not called.", idResolver.hitStartDocument);
        assertTrue("IDResolver.endDocument() was not called.", idResolver.hitEndDocument);
        assertTrue("IDResolver.bind(Object) was not called.", idResolver.hitBind);
        assertTrue("IDResolver.resolve(Object) was not called.", idResolver.hitResolve);
        assertTrue("ValidationEventHandler was not set.", idResolver.eventHandlerNotNull);        
    }

    public Object getControlObject() {
        Melon m1 = new Melon();
        m1.id = "M1";
        m1.type = "Watermelon";
        m1.processed = true;
        MelonRef m1Ref = new MelonRef();
        m1Ref.ref = m1;
        Melon m2 = new Melon();
        m2.id = "M2";
        m2.type = "Cantaloupe";
        m2.processed = true;
        MelonRef m2Ref = new MelonRef();
        m2Ref.ref = m2;
        Melon m3 = new Melon();
        m3.id = "M3";
        m3.type = "Honeydew";
        m3.processed = true;
        MelonRef m3Ref = new MelonRef();
        m3Ref.ref = m3;
        

        Box box1 = new Box();
        box1.fruits.add(m1Ref);
        box1.fruits.add(m2Ref);
        box1.fruits.add(m1);
        box1.fruits.add(m2);
        Box box2 = new Box();
        box2.fruits.add(m3Ref);
        box2.fruits.add(m3);

        FruitOrder o = new FruitOrder();
        o.boxes.add(box1);
        o.boxes.add(box2);

        return o;
    }

}