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
// dmccann - January 20/2010 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlregistry;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

//@XmlRegistry
public class ObjectFactory2 {
    //@XmlElementDecl(scope=FooBar.class,name="foo")
    public JAXBElement<String> createFooBarFoo(String s) {
        return new JAXBElement<String>(new QName("foo"), String.class, FooBar.class, s);
    }

    //@XmlElementDecl(scope=FooBar.class,name="bar")
    public JAXBElement<String> createFooBarBar(String s) {
        return new JAXBElement<String>(new QName("bar"), String.class, FooBar.class, s);
    }

    //@XmlElementDecl(name="foo")
    public JAXBElement<Integer> createFoos(Integer i) {
        return new JAXBElement<Integer>(new QName("foos"), Integer.class, i);
    }
}
