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
//  - rbarkhouse - 17 November 2011 - 2.3.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref.ns;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
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
