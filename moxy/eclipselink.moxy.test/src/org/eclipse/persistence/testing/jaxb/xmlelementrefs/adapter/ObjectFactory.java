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
//     Denise Smith - April 2013
package org.eclipse.persistence.testing.jaxb.xmlelementrefs.adapter;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

 private final static QName _Foo_QNAME = new QName("", "foo");
 private final static QName _FooE1_QNAME = new QName("", "e1");
 private final static QName _FooE2_QNAME = new QName("", "e2");

 /**
  * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: test.s2j20.LocalElements
  *
  */
 public ObjectFactory() {
 }

 /**
  * Create an instance of {@link Foo }
  *
  */
 public Foo createFoo() {
     return new Foo();
 }

 /**
  * Create an instance of {@link JAXBElement }{@code <}{@link Foo }{@code >}}
  *
  */
 @XmlElementDecl(namespace = "", name = "foo")
 public JAXBElement<Foo> createFoo(Foo value) {
     return new JAXBElement<Foo>(_Foo_QNAME, Foo.class, null, value);
 }

 /**
  * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
  *
  */
 @XmlElementDecl(namespace = "", name = "e1", scope = Foo.class)
 @XmlJavaTypeAdapter(HexBinaryAdapter.class)
 public JAXBElement<byte[]> createFooE1(byte[] value) {
     return new JAXBElement<byte[]>(_FooE1_QNAME, byte[].class, Foo.class, ((byte[]) value));
 }

 /**
  * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
  *
  */
 @XmlElementDecl(namespace = "", name = "e2", scope = Foo.class)
 public JAXBElement<byte[]> createFooE2(byte[] value) {
     return new JAXBElement<byte[]>(_FooE2_QNAME, byte[].class, Foo.class, ((byte[]) value));
 }

}
