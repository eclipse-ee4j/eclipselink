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
//    Denise Smith - June 2013
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class FooObjectFactory {

    private final static QName _Things_QNAME = new QName("", "things");

    public FooObjectFactory() {
    }

    @XmlElementDecl(namespace = "", name = "things")
    public JAXBElement<List<byte[]>> createThings(List<byte[]> value) {
       return new JAXBElement<List<byte[]>>(_Things_QNAME, ((Class) List.class),((List<byte[]> ) value));
    }
}
