/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - January 2012
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.emptystringns;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

     private final static QName _TestObject_QNAME = new QName("", "testObject");
     private final static QName _Child_QNAME = new QName("", "child");

     public ObjectFactory() {
     }
     public TestObject createTestObject() {
         return new TestObject();
     }

     @XmlElementDecl(namespace = "", name = "testObject")
     public JAXBElement<TestObject> createTestObject(TestObject value) {
         return new JAXBElement<TestObject>(_TestObject_QNAME, TestObject.class, null, value);
     }

     @XmlElementDecl(namespace = "", name = "child")
     public JAXBElement<Object> createChildc(Object value) {
         return new JAXBElement<Object>(_Child_QNAME, Object.class, null, value);
     }
}
