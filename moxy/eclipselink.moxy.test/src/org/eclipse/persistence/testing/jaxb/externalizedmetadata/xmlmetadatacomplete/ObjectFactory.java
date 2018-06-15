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
// dmccann - November 09/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmetadatacomplete;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
class ObjectFactory {
    @XmlElementDecl(name="foo")
    public JAXBElement<String> createFoo(String s) {
        return new JAXBElement<String>(new QName("foo"), String.class, s);
    }

    @XmlElementDecl(name="bar")
    public JAXBElement<String> createBar(String s) {
        return new JAXBElement<String>(new QName("bar"), String.class, s);
    }
}
