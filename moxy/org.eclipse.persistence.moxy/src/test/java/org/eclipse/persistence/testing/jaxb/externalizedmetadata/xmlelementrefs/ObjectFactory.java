/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - December 04/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlelementrefs;

@jakarta.xml.bind.annotation.XmlRegistry
public class ObjectFactory {
    @jakarta.xml.bind.annotation.XmlElementDecl(name="root")
    public jakarta.xml.bind.JAXBElement<String> createRoot() {
        return new jakarta.xml.bind.JAXBElement<String>(new javax.xml.namespace.QName("root"), String.class, "");
    }

    @jakarta.xml.bind.annotation.XmlElementDecl(namespace="myns", name="integer-root")
    public jakarta.xml.bind.JAXBElement<Integer> createIntegerRoot() {
        return new jakarta.xml.bind.JAXBElement<Integer>(new javax.xml.namespace.QName("myns", "integer-root"), Integer.class, new Integer(0));
    }

    @jakarta.xml.bind.annotation.XmlElementDecl(name="a")
    public jakarta.xml.bind.JAXBElement<String> createA() {
        return new jakarta.xml.bind.JAXBElement<String>(new javax.xml.namespace.QName("a"), String.class, "");
    }

    @jakarta.xml.bind.annotation.XmlElementDecl(name="b")
    public jakarta.xml.bind.JAXBElement<Integer> createB() {
        return new jakarta.xml.bind.JAXBElement<Integer>(new javax.xml.namespace.QName("b"), java.lang.Integer.class, 0);
    }
}
