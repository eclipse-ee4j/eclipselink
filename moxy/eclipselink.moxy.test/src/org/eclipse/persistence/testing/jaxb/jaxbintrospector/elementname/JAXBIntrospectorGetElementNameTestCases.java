/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - October 2011 - 2.3
package org.eclipse.persistence.testing.jaxb.jaxbintrospector.elementname;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.JAXBContextFactory;

import junit.framework.TestCase;

public class JAXBIntrospectorGetElementNameTestCases extends TestCase {

    JAXBContext jaxbContext;
    JAXBIntrospector jaxbIntrospector;

    public void setUp() throws Exception {
        Class[] classesToBeBound = new Class[] { TestObject.class,
                ElementDeclObject.class, ObjectFactory.class };
        jaxbContext = JAXBContextFactory.createContext(classesToBeBound, null);
        jaxbIntrospector = jaxbContext.createJAXBIntrospector();
    }

    public void testIsElement_XmlRootElement() {
        TestObject testObject = new TestObject();
        assertTrue(jaxbIntrospector.isElement(testObject));
    }

    public void testGetElementName_XmlRootElement() {
        TestObject obj = new TestObject();
        QName controlQname = new QName("someUri", "theRoot");
        assertEquals(controlQname, jaxbIntrospector.getElementName(obj));
    }

    public void testIsElement_XmlElementDecl() {
        ElementDeclObject elementDeclObject = new ElementDeclObject();
        assertFalse(jaxbIntrospector.isElement(elementDeclObject));
    }

    public void testGetElementName_XmlElementDecl() {
        ElementDeclObject elementDeclObject = new ElementDeclObject();
        assertNull(jaxbIntrospector.getElementName(elementDeclObject));
    }

    public void testIsElement_JAXBElement() {
        ElementDeclObject elementDeclObject = new ElementDeclObject();
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<ElementDeclObject> jaxbElement = objectFactory
                .createElementDeclObject(elementDeclObject);
        assertTrue(jaxbIntrospector.isElement(jaxbElement));
    }

    public void testGetElementName_JAXBElement() {
        ElementDeclObject elementDeclObject = new ElementDeclObject();
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<ElementDeclObject> jaxbElement = objectFactory
                .createElementDeclObject(elementDeclObject);
        QName controlQName = new QName("edo");
        assertEquals(controlQName, jaxbIntrospector.getElementName(jaxbElement));
    }

}
