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
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref.attachment;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    @XmlElementDecl(name="fooA")
    public JAXBElement<byte[]> createFooA() {
        return new JAXBElement(new QName("fooA"), byte[].class, new byte[]{1, 2, 3, 4, 5});
    }

    @XmlElementDecl(name="fooB")
    public JAXBElement<byte[]> createFooB() {
        return new JAXBElement(new QName("fooB"), Byte[].class, new byte[]{1, 2, 3, 4, 5});
    }

    @XmlElementDecl(name="fooC")
    public JAXBElement<String> createFooC() {
        return new JAXBElement(new QName("fooC"), String.class, "StringValue");
    }
}
