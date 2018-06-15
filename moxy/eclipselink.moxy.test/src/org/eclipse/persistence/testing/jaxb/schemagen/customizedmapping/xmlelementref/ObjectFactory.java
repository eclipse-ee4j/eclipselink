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
// mmacivor - June 09/2009 - 1.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlelementref;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
    public WrappedByteArray createWrappedByteArray() {
        return new WrappedByteArray();
    }

    @XmlElementDecl(name = "inByteArray")
    public JAXBElement<byte[]> createByteArrayElement(byte[] value) {
        return new JAXBElement<byte[]>(new QName("myNs", "someValue"), byte[].class, null, value);
    }
}
