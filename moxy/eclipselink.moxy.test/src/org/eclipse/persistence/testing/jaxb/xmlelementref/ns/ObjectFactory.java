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
//  - rbarkhouse - 17 November 2011 - 2.3.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref.ns;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private final static QName _EchoByteArrayRequest_QNAME = new QName("http://missing-uri.org/", "request");

    public ObjectFactory() {
    }

    public EchoByteArray createEchoByteArray() {
        return new EchoByteArray();
    }

    @XmlElementDecl(namespace = "http://missing-uri.org/", name = "request", scope = EchoByteArray.class)
    public JAXBElement<byte[]> createEchoByteArrayRequest(byte[] value) {
        return new JAXBElement<byte[]>(_EchoByteArrayRequest_QNAME, byte[].class, EchoByteArray.class, ((byte[]) value));
    }

}
