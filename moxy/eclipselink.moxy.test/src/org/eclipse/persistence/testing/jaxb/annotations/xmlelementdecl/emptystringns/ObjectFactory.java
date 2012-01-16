/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - January 2012
 ******************************************************************************/
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
