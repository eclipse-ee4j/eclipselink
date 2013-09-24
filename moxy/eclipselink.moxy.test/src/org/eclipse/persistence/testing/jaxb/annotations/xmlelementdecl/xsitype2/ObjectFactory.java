/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - September 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.xsitype2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

	 public final static QName _FOO_QNAME = new QName("", "foo");
	 public final static QName _BAR_QNAME = new QName("", "bar");

	 public ObjectFactory() {
	 }
	
	 @XmlElementDecl(namespace = "", name = "foo")
	 public JAXBElement<Foo> createFoo(Foo value) {
	     return new JAXBElement<Foo>(_FOO_QNAME, Foo.class, null, value);
	 }

	 @XmlElementDecl(namespace = "", name = "bar")
	 public JAXBElement<Bar> createBar(Bar value) {
	     return new JAXBElement<Bar>(_BAR_QNAME, Bar.class, null, value);
	 }
}