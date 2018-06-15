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
// dmccann - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;

import org.eclipse.persistence.oxm.annotations.XmlKey;
import org.eclipse.persistence.oxm.annotations.XmlPath;

public class Address {
    @XmlAttribute(required=true)
    @XmlPath("@id")
    @XmlID
    public String id;

    @XmlElement(name="value")
    public String address;

    @XmlAttribute(required=true)
    @XmlPath("@type")
    @XmlKey
    public String type;

    public Address() {}
    public Address(String id, String address, String type) {
        this.id = id;
        this.address = address;
        this.type = type;
    }

    public boolean equals(Object o) {
        Address a;
        try {
            a = (Address) o;
        } catch (ClassCastException cce) {
            return false;
        }
        try {
            return this.id.equals(a.id) && this.address.equals(a.address) && this.type.equals(a.type);
        } catch (Exception x) {
            return false;
        }
    }
}
