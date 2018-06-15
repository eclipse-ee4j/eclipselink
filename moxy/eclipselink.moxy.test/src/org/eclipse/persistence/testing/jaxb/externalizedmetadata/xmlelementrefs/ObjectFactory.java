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
// dmccann - December 04/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs;

@javax.xml.bind.annotation.XmlRegistry
public class ObjectFactory {
    @javax.xml.bind.annotation.XmlElementDecl(name="root")
    public javax.xml.bind.JAXBElement<String> createRoot() {
        return new javax.xml.bind.JAXBElement<String>(new javax.xml.namespace.QName("root"), String.class, "");
    }

    @javax.xml.bind.annotation.XmlElementDecl(namespace="myns", name="integer-root")
    public javax.xml.bind.JAXBElement<Integer> createIntegerRoot() {
        return new javax.xml.bind.JAXBElement<Integer>(new javax.xml.namespace.QName("myns", "integer-root"), Integer.class, new Integer(0));
    }

    @javax.xml.bind.annotation.XmlElementDecl(name="a")
    public javax.xml.bind.JAXBElement<String> createA() {
        return new javax.xml.bind.JAXBElement<String>(new javax.xml.namespace.QName("a"), String.class, "");
    }

    @javax.xml.bind.annotation.XmlElementDecl(name="b")
    public javax.xml.bind.JAXBElement<Integer> createB() {
        return new javax.xml.bind.JAXBElement<Integer>(new javax.xml.namespace.QName("b"), java.lang.Integer.class, 0);
    }
}
