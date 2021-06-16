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
