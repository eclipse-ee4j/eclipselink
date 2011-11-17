/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  - rbarkhouse - 17 November 2011 - 2.3.2 - Initial implementation
 ******************************************************************************/
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