/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Denise Smith - January 2012
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.emptystringns;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
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
