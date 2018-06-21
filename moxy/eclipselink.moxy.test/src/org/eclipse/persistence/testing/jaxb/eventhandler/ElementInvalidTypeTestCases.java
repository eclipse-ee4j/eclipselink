/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//    Denise Smith - May 2012
package org.eclipse.persistence.testing.jaxb.eventhandler;

import javax.xml.bind.ValidationEvent;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ElementInvalidTypeTestCases extends JAXBWithJSONTestCases{
    MyEventHandler handler;
    public ElementInvalidTypeTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {MyClass.class});
        setControlDocument("org/eclipse/persistence/testing/jaxb/eventhandler/elementWrongType.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/eventhandler/elementWrongType.json");
        setWriteControlDocument("org/eclipse/persistence/testing/jaxb/eventhandler/valid.xml");
        setWriteControlJSON("org/eclipse/persistence/testing/jaxb/eventhandler/valid.json");

    }

    public void setUp() throws Exception{
        super.setUp();
        handler = new MyEventHandler();
        jaxbUnmarshaller.setEventHandler(handler);
    }

    @Override
    protected Object getControlObject() {
        MyClass myClass = new MyClass();
        myClass.myAttribute =10;
        myClass.myElement = 20;

        return myClass;
    }

    public void jsonToObjectTest(Object testObject, Object controlObject) throws Exception {
        assertEquals(ValidationEvent.ERROR, handler.getSeverity());
   }

    public void xmlToObjectTest(Object testObject) throws Exception {
        assertEquals(ValidationEvent.ERROR, handler.getSeverity());
    }

    public void testRoundTrip(){};

}
