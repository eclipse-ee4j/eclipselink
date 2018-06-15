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

public class PhoneNumber {
    @XmlAttribute(required=true)
    @XmlPath("@id")
    @XmlID
    public String id;

    @XmlElement(name="value")
    public String number;

    @XmlAttribute(required=true)
    @XmlPath("@type")
    @XmlKey
    public String type;

    public PhoneNumber() {}
    public PhoneNumber(String id, String number, String type) {
        this.id = id;
        this.number = number;
        this.type = type;
    }

    public boolean equals(Object o) {
        PhoneNumber p;
        try {
            p = (PhoneNumber) o;
        } catch (ClassCastException cce) {
            return false;
        }
        try {
            return this.id.equals(p.id) && this.number.equals(p.number) && this.type.equals(p.type);
        } catch (Exception x) {
            return false;
        }
    }
}
