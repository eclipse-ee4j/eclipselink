/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//  - rbarkhouse - 29 January 2013 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.sun.idresolver.collection;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.idresolver.collection.Root;
import org.eclipse.persistence.testing.jaxb.idresolver.collection.TestObject;

public class NonELIDResolverTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/idresolver/collection/instance.xml";

    private NonELIDResolver idResolver = new NonELIDResolver();

    public NonELIDResolverTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { Root.class, TestObject.class });
        setControlDocument(XML_RESOURCE);
    }

    @Override
    public String getName() {
        return super.getName() + " Collection";
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
        TestObject o = new TestObject();
        o.name = "FOO"; o.id = 1;

        TestObject s = new TestObject();
        s.name = "SINGLE"; s.id = 4; o.single = s;

        TestObject o2 = new TestObject();
        o2.name = "BAR"; o2.id = 2; o.refs.add(o2);

        TestObject o3 = new TestObject();
        o3.name = "BAZ"; o3.id = 3; o.refs.add(o3);

        // s, o2, and o3 will have been retrieved via
        // IDResolver, so these should be processed=true
        s.processed = true;
        o2.processed = true;
        o3.processed = true;

        Root r = new Root();
        r.testObjects.add(o);
        r.testObjects.add(s);
        r.testObjects.add(o2);
        r.testObjects.add(o3);

        return r;
    }

}
