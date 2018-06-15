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
//     Praba Vijayaratnam - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelementdecl;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlType;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.direct.Team;

//@XmlRootElement.
@XmlRegistry
public class ObjectFactory {

    private final static QName  _Name_QNAME = new QName("", "Name");

    public ObjectFactory() {
    }

    @XmlElementDecl (name = "foo")
    public JAXBElement<String> creatFoo(String s){
        return new JAXBElement<String> (_Name_QNAME ,String.class, null, s);
    }

    public boolean equals(Object object) {
        ObjectFactory obj = ((ObjectFactory)object);

    return true;
    }
}
